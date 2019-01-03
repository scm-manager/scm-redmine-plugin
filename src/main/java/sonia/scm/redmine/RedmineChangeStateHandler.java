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


import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
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
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
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
    int issueId = parseIssueId(issueIdString);

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
    return Iterables.transform(getStatusList(), new Function<IssueStatus, String>() {
      @Override
      public String apply(IssueStatus input) {
        return input.getName();
      }
    });
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
