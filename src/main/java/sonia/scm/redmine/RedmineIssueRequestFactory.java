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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Repository;
import sonia.scm.security.CipherUtil;
import sonia.scm.user.User;
import sonia.scm.util.AssertUtil;
import sonia.scm.util.SecurityUtil;
import sonia.scm.util.Util;
import sonia.scm.web.security.WebSecurityContext;

//~--- JDK imports ------------------------------------------------------------

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marvin Froeder marvin_at_marvinformatics_dot_com
 */
@Singleton
public class RedmineIssueRequestFactory
{

  /** Field description */
  public static final String SCM_CREDENTIALS = "SCM_CREDENTIALS";

  /** the logger for RedmineIssueRequestFactory */
  private static final Logger logger =
    LoggerFactory.getLogger(RedmineIssueRequestFactory.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param handlerFactory
   * @param requestProvider
   * @param securityContextProvider
   */
  @Inject
  public RedmineIssueRequestFactory(
          RedmineHandlerFactory handlerFactory,
          Provider<HttpServletRequest> requestProvider,
          Provider<WebSecurityContext> securityContextProvider)
  {
    this.handlerFactory = handlerFactory;
    this.requestProvider = requestProvider;
    this.securityContextProvider = securityContextProvider;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param configuration
   * @param repository
   *
   * @return
   */
  public RedmineIssueRequest createRequest(RedmineConfiguration configuration,
          Repository repository)
  {
    String username = getUsername(configuration);
    String password = getPassword();

    return new RedmineIssueRequest(handlerFactory, username, password,
                                    configuration, repository);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private String getPassword()
  {
    HttpSession session = requestProvider.get().getSession();

    AssertUtil.assertIsNotNull(session);

    String credentialsString = (String) session.getAttribute(SCM_CREDENTIALS);

    AssertUtil.assertIsNotEmpty(credentialsString);
    credentialsString = CipherUtil.getInstance().decode(credentialsString);

    String[] credentialsArray = credentialsString.split(":");

    if (credentialsArray.length < 2)
    {
      throw new RuntimeException("credentials empty");
    }

    return credentialsArray[1];
  }

  /**
   * Method description
   *
   *
   *
   *
   * @param configuration
   * @return
   */
  private String getUsername(RedmineConfiguration configuration)
  {
    String username = null;
    User user = SecurityUtil.getCurrentUser(securityContextProvider);

    if (user != null)
    {
      String transformPattern = configuration.getUsernameTransformPattern();

      if (Util.isEmpty(transformPattern))
      {
        username = user.getName();
      }
      else
      {
        username = MessageFormat.format(transformPattern, user.getName(),
                                        user.getMail(), user.getDisplayName());
      }
    }
    else if (logger.isErrorEnabled())
    {
      logger.error("could not find current user");
    }

    return username;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private RedmineHandlerFactory handlerFactory;

  /** Field description */
  private Provider<HttpServletRequest> requestProvider;

  /** Field description */
  private Provider<WebSecurityContext> securityContextProvider;
}
