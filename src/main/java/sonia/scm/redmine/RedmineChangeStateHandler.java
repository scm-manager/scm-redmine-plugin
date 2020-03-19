/**
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


import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.issuetracker.ChangeStateHandler;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.template.TemplateEngineFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Sdorra
 */
public class RedmineChangeStateHandler extends RedmineHandler
  implements ChangeStateHandler {

  @Override
  protected String getTemplateName() {
    return TEMPLATE_NAME;
  }

  private List<IssueStatus> statusList;

  private static final String TEMPLATE_NAME = "changeState.mustache";

  private static final Logger logger =
    LoggerFactory.getLogger(RedmineChangeStateHandler.class);


  public RedmineChangeStateHandler(TemplateEngineFactory templateEngineFactory,
                                   LinkHandler linkHandler, RedmineConfiguration configuration,
                                   IssueRequest request) {
    super(templateEngineFactory, linkHandler, configuration, request);
  }

  @Override
  public void changeState(String issueIdString, String keyword) {
    int issueId = Ids.parseAsInt(issueIdString);

    try {
      String comment = createComment(request, keyword);

      if (!Strings.isNullOrEmpty(comment)) {
        logger.info("change state of issue {} by keyword {}", issueId, keyword);

        Issue issue = getManager().getIssueById(issueId);

        IssueStatus status = getIssueStatusByKeyword(keyword);
        if (status != null) {
          issue.setStatusId(status.getId());
        } else {
          logger.warn("could not find keyword {} in issue status list", keyword);
        }
        issue.setNotes(comment);
        getManager().update(issue);
      } else {
        logger.warn("generated comment for change state attempt is null or empty");
      }

    } catch (RedmineException ex) {
      throw new RuntimeException(ex);
    }
  }

  private IssueStatus getIssueStatusByKeyword(String keyword) {
    IssueStatus status = null;
    for (IssueStatus s : getStatusList()) {
      if (s.getName().equalsIgnoreCase(keyword)) {
        status = s;
        break;
      }
    }
    return status;
  }

  @Override
  public Iterable<String> getKeywords() {
    return Iterables.transform(getStatusList(), IssueStatus::getName);
  }

  private List<IssueStatus> getStatusList() {
    if (statusList == null) {
      try {
        statusList = getManager().getStatuses();
      } catch (RedmineException ex) {
        logger.warn("failed to fetch issue status list", ex);
        return Collections.emptyList();
      }
    }
    return statusList;
  }

}
