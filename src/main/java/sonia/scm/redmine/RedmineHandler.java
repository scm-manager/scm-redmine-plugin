/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
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
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
 */


package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Preconditions;
import com.taskadapter.redmineapi.RedmineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.issuetracker.TemplateBasedHandler;
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

  protected final RedmineConfiguration configuration;
  protected final IssueRequest request;
  private RedmineManager manager;

  public RedmineHandler(TemplateEngineFactory templateEngineFactory,
                        LinkHandler linkHandler, RedmineConfiguration configuration,
                        IssueRequest request) {
    super(templateEngineFactory, linkHandler);
    this.configuration = configuration;
    this.request = request;
  }


  public void close() {

    // do nothing
  }


  public RedmineManager getManager() {
    if (manager == null) {
      String username = configuration.getUsername();
      String password = configuration.getPassword();

      manager = new RedmineManager(configuration.getUrl(), username, password);
    }

    return manager;
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
