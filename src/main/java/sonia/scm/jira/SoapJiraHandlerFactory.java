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

import sonia.scm.jira.soap.JiraSoapService;
import sonia.scm.jira.soap.JiraSoapServiceServiceLocator;

//~--- JDK imports ------------------------------------------------------------

import java.net.URL;

/**
 *
 * @author Sebastian Sdorra
 */
public class SoapJiraHandlerFactory implements JiraHandlerFactory
{

  /** the logger for SoapJiraHandlerFactory */
  private static final Logger logger =
    LoggerFactory.getLogger(SoapJiraHandlerFactory.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param urlString
   * @param username
   * @param password
   *
   * @return
   *
   * @throws JiraConnectException
   */
  @Override
  public JiraHandler createJiraHandler(String urlString, String username,
          String password)
          throws JiraConnectException
  {
    JiraHandler handler = null;

    try
    {
      URL url = createSoapUrl(urlString);

      if (logger.isDebugEnabled())
      {
        logger.debug("connect to jira {} as user {}", url, username);
      }

      JiraSoapService service =
        new JiraSoapServiceServiceLocator().getJirasoapserviceV2(url);
      String token = service.login(username, password);

      handler = new SoapJiraHandler(service, token);
    }
    catch (Exception ex)
    {
      throw new JiraConnectException(
          "could not connect to jira instance at ".concat(urlString), ex);
    }

    return handler;
  }

  /**
   * Method description
   *
   *
   * @param url
   *
   * @return
   */
  private URL createSoapUrl(String url)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
