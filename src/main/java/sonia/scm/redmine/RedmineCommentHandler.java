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


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.template.TemplateEngineFactory;


/**
 * @author Sebastian Sdorra
 */
public class RedmineCommentHandler extends RedmineHandler
  implements CommentHandler {

  private static final String TEMPLATE_NAME = "update.mustache";
  private static final Logger logger =
    LoggerFactory.getLogger(RedmineCommentHandler.class);


  public RedmineCommentHandler(TemplateEngineFactory templateEngineFactory,
                               LinkHandler linkHandler, RedmineConfiguration configuration,
                               IssueRequest request, AdvancedHttpClient advancedHttpClient) {
    super(templateEngineFactory, linkHandler, configuration, request, advancedHttpClient);
  }


  @Override
  public void commentIssue(String issueIdString) {
    int issueId = Ids.parseAsInt(issueIdString);

    try {
      String comment = createComment(request);

      if (!Strings.isNullOrEmpty(comment)) {
        logger.info("add comment to issue {}", issueId);

        ObjectNode issue = getService().getIssueById(issueId);

        issue.put("notes", comment);
        getService().updateIssue(issueId, issue);
      } else {
        logger.warn("generate comment is null or empty");
      }

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  protected String getTemplateName() {
    return TEMPLATE_NAME;
  }
}
