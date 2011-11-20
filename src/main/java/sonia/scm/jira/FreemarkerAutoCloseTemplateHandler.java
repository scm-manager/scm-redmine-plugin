/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import freemarker.template.Configuration;
import freemarker.template.Template;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.url.UrlProvider;
import sonia.scm.url.UrlProviderFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.Reader;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra
 */
public class FreemarkerAutoCloseTemplateHandler
        implements AutoCloseTemplateHandler
{

  /** Field description */
  public static final String ENV_AUTOCLOSEWORD = "autoCloseWord";

  /** Field description */
  public static final String ENV_CHANGESET = "changeset";

  /** Field description */
  public static final String ENV_DIFFRESTURL = "diffRestUrl";

  /** Field description */
  public static final String ENV_DIFFURL = "diffUrl";

  /** Field description */
  public static final String ENV_REPOSITORY = "repository";

  /** Field description */
  public static final String ENV_REPOSITORYURL = "repositoryUrl";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param wuiUrlProvider
   * @param restUrlProvider
   */
  @Inject
  public FreemarkerAutoCloseTemplateHandler(
          @Named(
            UrlProviderFactory
              .TYPE_WUI) Provider<UrlProvider> wuiUrlProvider, @Named(
                UrlProviderFactory
                  .TYPE_RESTAPI_XML) Provider<UrlProvider> restUrlProvider)
  {
    this.wuiUrlProvider = wuiUrlProvider;
    this.restUrlProvider = restUrlProvider;
    configuration = new Configuration();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param name
   * @param reader
   * @param request
   * @param changeset
   * @param autoCloseWord
   *
   * @return
   *
   *
   * @throws AutoCloseTemplateException
   */
  @Override
  public String render(String name, Reader reader,
                       JiraAutoCloseRequest request, Changeset changeset,
                       String autoCloseWord)
          throws AutoCloseTemplateException
  {
    StringWriter writer = null;

    try
    {
      Template template = new Template(name, reader, configuration);

      writer = new StringWriter();

      Repository repository = request.getRepository();
      Map<String, Object> env = new HashMap<String, Object>();
      UrlProvider wuiUrl = wuiUrlProvider.get();
      UrlProvider restUrl = restUrlProvider.get();

      env.put(ENV_REPOSITORY, repository);
      env.put(ENV_CHANGESET, changeset);
      env.put(ENV_AUTOCLOSEWORD, autoCloseWord);
      env.put(ENV_DIFFURL,
              wuiUrl.getRepositoryUrlProvider().getDiffUrl(repository.getId(),
                changeset.getId()));
      env.put(ENV_DIFFRESTURL,
              restUrl.getRepositoryUrlProvider().getDiffUrl(repository.getId(),
                changeset.getId()));
      env.put(
          ENV_REPOSITORYURL,
          wuiUrl.getRepositoryUrlProvider().getDetailUrl(repository.getId()));
      template.process(env, writer);
    }
    catch (Exception ex)
    {
      throw new AutoCloseTemplateException("could not render template", ex);
    }

    return writer.toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Configuration configuration;

  /** Field description */
  private Provider<UrlProvider> restUrlProvider;

  /** Field description */
  private Provider<UrlProvider> wuiUrlProvider;
}
