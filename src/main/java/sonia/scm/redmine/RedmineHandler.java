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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.issuetracker.TemplateBasedHandler;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class RedmineHandler extends TemplateBasedHandler
  implements Closeable {

  private static final Logger logger = LoggerFactory.getLogger(RedmineHandler.class);

  private static final String TEMPLATE_BASE_PATH = "scm/template/";

  private final RedmineConfiguration configuration;
  private final AdvancedHttpClient advancedHttpClient;
  private RedmineRestApiService apiService;

  public RedmineHandler(TemplateEngineFactory templateEngineFactory,
                        LinkHandler linkHandler,
                        RedmineConfiguration configuration,
                        AdvancedHttpClient advancedHttpClient) {
    super(templateEngineFactory, linkHandler);
    this.configuration = configuration;
    this.advancedHttpClient = advancedHttpClient;
  }


  public void close() {

    // do nothing
  }


  public RedmineRestApiService getService() {
    if (apiService == null) {
      String username = configuration.getUsername();
      String password = configuration.getPassword();

      apiService = new RedmineRestApiService(advancedHttpClient, configuration.getUrl(), username, password);
    }

    return apiService;
  }

  @Override
  protected Template loadTemplate(TemplateEngine engine) throws IOException {
    String templatePath = buildTemplatePath();
    logger.debug("load template {}", templatePath);
    return engine.getTemplate(templatePath);
  }

  private String buildTemplatePath() {
    String type = configuration.getTextFormatting().name().toLowerCase(Locale.ENGLISH);
    return TEMPLATE_BASE_PATH + type + "/" + getTemplateName();
  }

  protected abstract String getTemplateName();


}
