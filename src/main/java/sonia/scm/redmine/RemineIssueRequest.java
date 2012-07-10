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

import sonia.scm.repository.Repository;

//~--- JDK imports ------------------------------------------------------------

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra
 */
public class RemineIssueRequest implements Closeable
{

  /**
   * Constructs ...
   *
   *
   *
   * @param handlerFactory
   * @param username
   * @param password
   * @param configuration
   * @param repository
   */
  public RemineIssueRequest(RemineHandlerFactory handlerFactory,
                              String username, String password,
                              RemineConfiguration configuration,
                              Repository repository)
  {
    this.handlerFactory = handlerFactory;
    this.username = username;
    this.password = password;
    this.configuration = configuration;
    this.repository = repository;
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
    if (handler != null)
    {
      try
      {
        handler.logout();
      }
      catch (RemineException ex)
      {
        throw new IOException("could not logout", ex);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws RemineConnectException
   */
  public RemineHandler createJiraHandler() throws RemineConnectException
  {
    if (handler == null)
    {
      handler = handlerFactory.createJiraHandler(configuration.getUrl(),
              username, password);
    }

    return handler;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public RemineConfiguration getConfiguration()
  {
    return configuration;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Repository getRepository()
  {
    return repository;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUsername()
  {
    return username;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private RemineConfiguration configuration;

  /** Field description */
  private RemineHandler handler;

  /** Field description */
  private RemineHandlerFactory handlerFactory;

  /** Field description */
  private String password;

  /** Field description */
  private Repository repository;

  /** Field description */
  private String username;
}
