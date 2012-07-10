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



package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

/**
 *
 * @author Marvin Froeder marvin_at_marvinformatics_dot_com
 */
public class RestRedmineHandler implements RedmineHandler
{

  /** Field description */
  public static final String ACTION_DEFAULT_CLOSE = "2";

  /** the logger for RestRedmineHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(RestRedmineHandler.class);

  private final RedmineManager redmineManager;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param redmineManager
   * @param username
   */
  public RestRedmineHandler( RedmineManager mgr )
  {
      this.redmineManager = mgr;
  }

  //~--- methods --------------------------------------------------------------


/**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   *
   * @throws RedmineException
   */
  @Override
  public void addComment(Integer issueId, String comment) throws RedmineException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("add comment to issue {}", issueId);
    }

    Issue issue = redmineManager.getIssueById( issueId );
    issue.setNotes( comment );
    redmineManager.update( issue );
  }

  /**
   * Method description
   *
   *
   * @param issueId
   * @param autoCloseWords
   *
   * @throws RedmineException
   */
  @Override
  public void close(Integer issueId, String autoCloseWords) throws RedmineException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("close issue {}", issueId);
    }

    Issue issue = redmineManager.getIssueById( issueId );
    issue.setStatusId( 5 );
    issue.setNotes( autoCloseWords );
    redmineManager.update( issue );
  }

  /**
   * Method description
   *
   *
   * @throws RedmineException
   */
  @Override
  public void logout() throws RedmineException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("logout from redmine");
    }

    redmineManager.shutdown();
  }

}
