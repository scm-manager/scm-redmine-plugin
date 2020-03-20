/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.ChangeStateHandler;
import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.issuetracker.DataStoreBasedIssueTracker;
import sonia.scm.issuetracker.IssueLinkFactory;
import sonia.scm.issuetracker.IssueMatcher;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.template.TemplateEngineFactory;

import java.util.Optional;

/**
 * @author Sebastian Sdorra
 */
@Singleton
@Extension
public class RedmineIssueTracker extends DataStoreBasedIssueTracker {

  private static final String NAME = "redmine";

  private static final Logger logger =
    LoggerFactory.getLogger(RedmineIssueTracker.class);

  private final RedmineConfigStore configStore;

  private final Provider<LinkHandler> linkHandlerProvider;
  private final TemplateEngineFactory templateEngineFactory;


  @Inject
  public RedmineIssueTracker(RedmineConfigStore configStore, DataStoreFactory dataStoreFactory,
                             TemplateEngineFactory templateEngineFactory, Provider<LinkHandler> linkHandlerProvider) {
    super(NAME, dataStoreFactory);
    this.configStore = configStore;
    this.templateEngineFactory = templateEngineFactory;
    this.linkHandlerProvider = linkHandlerProvider;
  }


  @Override
  protected ChangeStateHandler getChangeStateHandler(IssueRequest request) {
    ChangeStateHandler changeStateHandler = null;
    RedmineConfiguration cfg = resolveConfiguration(request.getRepository());

    if ((cfg != null) && cfg.isAutoCloseEnabled()) {
      changeStateHandler = new RedmineChangeStateHandler(templateEngineFactory,
        linkHandlerProvider.get(), cfg, request);
    } else {
      logger.debug("configuration is not valid or change state is disabled");
    }

    return changeStateHandler;
  }

  @Override
  protected CommentHandler getCommentHandler(IssueRequest request) {
    CommentHandler commentHandler = null;
    RedmineConfiguration cfg = resolveConfiguration(request.getRepository());

    if ((cfg != null) && cfg.isUpdateIssuesEnabled()) {
      commentHandler = new RedmineCommentHandler(templateEngineFactory,
        linkHandlerProvider.get(), cfg, request);
    } else {
      logger.debug("configuration is not valid or update is disabled");
    }

    return commentHandler;
  }


  @Override
  public Optional<IssueMatcher> createMatcher(Repository repository) {
    IssueMatcher matcher = null;
    RedmineConfiguration config = resolveConfiguration(repository);

    if (config != null) {
      matcher = new RedmineIssueMatcher();
    }

    return Optional.ofNullable(matcher);
  }

  @Override
  public Optional<IssueLinkFactory> createLinkFactory(Repository repository) {
    RedmineConfiguration configuration = resolveConfiguration(repository);
    String redmineUrl = configuration.getUrl();
    if (redmineUrl == null) {
      return Optional.empty();
    } else {
      return Optional.of(new RedmineIssueLinkFactory(redmineUrl));
    }
  }


  public RedmineConfiguration resolveConfiguration(Repository repository) {
    RedmineConfiguration cfg = configStore.getConfiguration(repository);

    if (!cfg.isValid()) {
      logger.debug("repository config for {} is not valid",
        repository.getName());
      cfg = getGlobalConfiguration();
    }

    if (!cfg.isValid()) {
      logger.debug("no valid configuration for repository {} found",
        repository.getName());
      cfg = null;
    }
    return cfg;
  }

  public void setGlobalConfiguration(RedmineGlobalConfiguration updatedConfig) {
    configStore.storeConfiguration(updatedConfig);
  }

  public void setRepositoryConfiguration(RedmineConfiguration updatedConfig, Repository repository) {
    configStore.storeConfiguration(updatedConfig, repository);
  }

  public RedmineConfiguration getRepositoryConfiguration(Repository repository) {
    return configStore.getConfiguration(repository);
  }

  public RedmineConfiguration getRepositoryConfigurationOrEmpty(Repository repository) {
    RedmineConfiguration configuration = configStore.getConfiguration(repository);
    if (configuration == null) {
      return new RedmineConfiguration();
    }
    return configuration;
  }

  public RedmineGlobalConfiguration getGlobalConfiguration() {
    return configStore.getConfiguration();
  }


}
