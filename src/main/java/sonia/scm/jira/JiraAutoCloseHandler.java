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
import sonia.scm.repository.Repository;
import sonia.scm.security.CipherUtil;
import sonia.scm.util.AssertUtil;

//~--- JDK imports ------------------------------------------------------------

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Sebastian Sdorra
 */
public class JiraAutoCloseHandler
{

  /** Field description */
  public static final String SCM_CREDENTIALS = "SCM_CREDENTIALS";

  /** the logger for JiraAutoCloseHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraAutoCloseHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param request
   * @param repository
   * @param url
   * @param autoCloseWords
   */
  public JiraAutoCloseHandler(HttpServletRequest request,
                              Repository repository, String url,
                              String[] autoCloseWords)
  {
    this.request = request;
    this.repository = repository;
    this.url = url;
    this.autoCloseWords = autoCloseWords;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param changeset
   */
  public void handleAutoClose(String issueId, Changeset changeset)
  {
    if (logger.isTraceEnabled())
    {
      logger.trace("check changeset {} for auto-close of issue",
                   changeset.getId(), issueId);
    }

    String autoCloseWord = searchAutoCloseWord(changeset);

    if (autoCloseWord != null)
    {
      closeIssue(repository, changeset, issueId, autoCloseWord);
    }

    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param changeset
   * @param issueId
   * @param autoCloseWord
   */
  private void closeIssue(Repository repository, Changeset changeset,
                          String issueId, String autoCloseWord)
  {
    HttpSession session = request.getSession();

    AssertUtil.assertIsNotNull(session);

    String credentialsString = (String) session.getAttribute(SCM_CREDENTIALS);

    AssertUtil.assertIsNotEmpty(credentialsString);
    credentialsString = CipherUtil.getInstance().decode(credentialsString);

    String[] credentialsArray = credentialsString.split(":");

    if (credentialsArray.length < 2)
    {
      throw new RuntimeException("credentials empty");
    }

    try
    {
      JiraHandler handler = JiraUtil.createJiraHandler(url,
                              credentialsArray[0], credentialsArray[1]);

      // TODO add comment
      handler.close(issueId);
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
      for (String acw : autoCloseWords)
      {
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

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String[] autoCloseWords;

  /** Field description */
  private Repository repository;

  /** Field description */
  private HttpServletRequest request;

  /** Field description */
  private String url;
}
