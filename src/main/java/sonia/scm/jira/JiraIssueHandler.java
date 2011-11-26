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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Changeset;

/**
 *
 * @author Sebastian Sdorra
 */
public class JiraIssueHandler
{

  /** the logger for JiraIssueHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraIssueHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param templateHandler
   * @param request
   */
  public JiraIssueHandler(TemplateHandler templateHandler,
                          JiraIssueRequest request)
  {
    this.templateHandler = templateHandler;
    this.request = request;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param changeset
   */
  public void handleIssue(String issueId, Changeset changeset)
  {
    if (request.getConfiguration().isAutoCloseEnabled())
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("check changeset {} for auto-close of issue",
                     changeset.getId(), issueId);
      }

      String autoCloseWord = searchAutoCloseWord(changeset);

      if (autoCloseWord != null)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("found auto close word {} for issue {}", autoCloseWord,
                       issueId);
        }

        closeIssue(changeset, issueId, autoCloseWord);
      }
      else
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("found no auto close word");
        }

        updateIssue(changeset, issueId);
      }
    }
    else
    {
      updateIssue(changeset, issueId);
    }
  }

  /**
   * Method description
   *
   *
   * @param changeset
   * @param issueId
   * @param autoCloseWord
   */
  private void closeIssue(Changeset changeset, String issueId,
                          String autoCloseWord)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("try to close issue {} because of changeset {}", issueId,
                   changeset.getId());
    }

    try
    {
      JiraHandler handler = request.createJiraHandler();
      String comment = templateHandler.render(Template.CLOSE_SIMPLE, request,
                         changeset, autoCloseWord);

      handler.close(issueId, autoCloseWord);
      handler.addComment(issueId, comment);
    }
    catch (TemplateException ex)
    {
      logger.error("could render template", ex);
    }
    catch (JiraException ex)
    {
      logger.error("could not close jira issue", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param changeset
   *
   * @return
   */
  private String searchAutoCloseWord(Changeset changeset)
  {
    String description = changeset.getDescription();
    String autoCloseWord = null;
    String[] words = description.split("\\s");

    for (String w : words)
    {
      for (String acw : request.getConfiguration().getAutoCloseWords())
      {
        acw = acw.trim();

        if (w.equalsIgnoreCase(acw))
        {
          autoCloseWord = w;

          break;
        }
      }

      if (autoCloseWord != null)
      {
        break;
      }
    }

    return autoCloseWord;
  }

  /**
   * Method description
   *
   *
   * @param changeset
   * @param issueId
   */
  private void updateIssue(Changeset changeset, String issueId)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("try to update issue {} because of changeset {}", issueId,
                   changeset.getId());
    }

    try
    {
      JiraHandler handler = request.createJiraHandler();
      String comment = templateHandler.render(Template.UPDATE_SIMPLE, request,
                         changeset);

      handler.addComment(issueId, comment);
    }
    catch (TemplateException ex)
    {
      logger.error("could render template", ex);
    }
    catch (JiraException ex)
    {
      logger.error("could not close jira issue", ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private JiraIssueRequest request;

  /** Field description */
  private TemplateHandler templateHandler;
}
