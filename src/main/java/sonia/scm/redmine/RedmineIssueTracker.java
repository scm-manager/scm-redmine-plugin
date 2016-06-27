/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.shiro.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.ChangeStateHandler;
import sonia.scm.issuetracker.CommentHandler;

import sonia.scm.issuetracker.DataStoreBasedIssueTrack;
import sonia.scm.issuetracker.IssueMatcher;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.security.Role;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.Store;
import sonia.scm.store.StoreFactory;
import sonia.scm.template.TemplateEngineFactory;

/**
 *
 * @author Sebastian Sdorra
 */
@Singleton
@Extension
public class RedmineIssueTracker extends DataStoreBasedIssueTrack
{

  /** Field description */
  private static final String NAME = "redmine";

  /**
   * the logger for RedmineIssueTracker
   */
  private static final Logger logger =
    LoggerFactory.getLogger(RedmineIssueTracker.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param storeFactory
   * @param dataStoreFactory
   * @param templateEngineFactory
   * @param linkHandlerProvider
   */
  @Inject
  public RedmineIssueTracker(StoreFactory storeFactory, DataStoreFactory dataStoreFactory,
    TemplateEngineFactory templateEngineFactory, Provider<LinkHandler> linkHandlerProvider)
  {
    super(NAME, dataStoreFactory);
    this.globalConfigurationStore = storeFactory.getStore(RedmineGlobalConfiguration.class, NAME);
    this.templateEngineFactory = templateEngineFactory;
    this.linkHandlerProvider = linkHandlerProvider;
  }
  
  
  /**
   * Method description
   *
   *
   * @param request
   *
   * @return
   */
  @Override
  protected ChangeStateHandler getChangeStateHandler(IssueRequest request)
  {
    ChangeStateHandler changeStateHandler = null;
    RedmineConfiguration cfg = resolveConfiguration(request.getRepository());

    if ((cfg != null) && cfg.isAutoCloseEnabled())
    {
      changeStateHandler = new RedmineChangeStateHandler(templateEngineFactory,
        linkHandlerProvider.get(), cfg, request);
    }
    else
    {
      logger.debug("configuration is not valid or change state is disabled");
    }

    return changeStateHandler;
  }

  /**
   * Method description
   *
   *
   * @param request
   *
   * @return
   */
  @Override
  protected CommentHandler getCommentHandler(IssueRequest request)
  {
    CommentHandler commentHandler = null;
    RedmineConfiguration cfg = resolveConfiguration(request.getRepository());

    if ((cfg != null) && cfg.isUpdateIssuesEnabled())
    {
      commentHandler = new RedmineCommentHandler(templateEngineFactory,
        linkHandlerProvider.get(), cfg, request);
    }
    else
    {
      logger.debug("configuration is not valid or update is disabled");
    }

    return commentHandler;
  }


  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  @Override
  public IssueMatcher createMatcher(Repository repository)
  {
    IssueMatcher matcher = null;
    RedmineConfiguration config = resolveConfiguration(repository);

    if (config != null)
    {
      matcher = new RedmineIssueMatcher(config);
    }

    return matcher;
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  public RedmineConfiguration resolveConfiguration(Repository repository)
  {
    RedmineConfiguration cfg = new RedmineConfiguration(repository);

    if (!cfg.isValid())
    {
      logger.debug("repository config for {} is not valid",
        repository.getName());
      cfg = getGlobalConfiguration();
    }

    if (!cfg.isValid())
    {
      logger.debug("no valid configuration for repository {} found",
        repository.getName());
      cfg = null;
    }

    return cfg;
  }

  public void setGlobalConfiguration(RedmineGlobalConfiguration updatedConfig) {
    SecurityUtils.getSubject().checkRole(Role.ADMIN);
    globalConfigurationStore.set(updatedConfig);
  }
  
  public RedmineGlobalConfiguration getGlobalConfiguration() {
    RedmineGlobalConfiguration configuration = globalConfigurationStore.get();
    if (configuration == null){
      configuration = new RedmineGlobalConfiguration();
    }
    return configuration;
  }

  //~--- fields ---------------------------------------------------------------

  private final Store<RedmineGlobalConfiguration> globalConfigurationStore;
  
  /** Field description */
  private final Provider<LinkHandler> linkHandlerProvider;

  /** Field description */
  private final TemplateEngineFactory templateEngineFactory;


}
