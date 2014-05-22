/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
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
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;

import com.taskadapter.redmineapi.RedmineManager;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import sonia.scm.issuetracker.Credentials;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.issuetracker.TemplateBasedHandler;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.security.Role;
import sonia.scm.template.TemplateEngineFactory;
import sonia.scm.user.User;

//~--- JDK imports ------------------------------------------------------------

import java.io.Closeable;
import java.io.IOException;

import java.text.MessageFormat;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class RedmineHandler extends TemplateBasedHandler
  implements Closeable
{

  /**
   * Constructs ...
   *
   *
   * @param templateEngineFactory
   * @param linkHandler
   * @param configuration
   * @param request
   */
  public RedmineHandler(TemplateEngineFactory templateEngineFactory,
    LinkHandler linkHandler, RedmineConfiguration configuration,
    IssueRequest request)
  {
    super(templateEngineFactory, linkHandler);
    this.configuration = configuration;
    this.request = request;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Override
  public void close() throws IOException
  {

    // do nothing
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public RedmineManager getManager()
  {
    if (manager == null)
    {

      // username and password from configuration ???
      String username = getUsername();
      String password = Credentials.current().getPassword();

      manager = new RedmineManager(configuration.getUrl(), username, password);
    }

    return manager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   */
  protected int parseIssueId(String id)
  {
    return Integer.parseInt(id);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private String getUsername()
  {
    Subject subject = SecurityUtils.getSubject();

    subject.checkRole(Role.USER);

    String username = null;

    User user = subject.getPrincipals().oneByType(User.class);

    if (user != null)
    {
      String transformPattern = configuration.getUsernameTransformPattern();

      if (!Strings.isNullOrEmpty(transformPattern))
      {
        username = user.getName();
      }
      else
      {
        username = MessageFormat.format(transformPattern, user.getName(),
          user.getMail(), user.getDisplayName());
      }
    }
    else
    {
      throw new RuntimeException("could not find current user");
    }

    return username;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  protected final RedmineConfiguration configuration;

  /** Field description */
  protected final IssueRequest request;

  /** Field description */
  private RedmineManager manager;
}
