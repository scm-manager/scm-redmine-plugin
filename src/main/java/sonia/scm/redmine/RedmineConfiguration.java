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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sonia.scm.Validateable;
import sonia.scm.repository.Repository;
import sonia.scm.util.Util;

/**
 *
 * @author Marvin Froeder marvin_at_marvinformatics_dot_com
 */
public class RedmineConfiguration implements Validateable
{

  /** Field description */
  public static final String PROPERTY_AUTOCLOSE = "redmine.auto-close";

  /** Field description */
  public static final String PROPERTY_AUTOCLOSEWORDS = "redmine.auto-close-words";

  /** Field description */
  public static final String PROPERTY_REDMINE_URL = "redmine.url";

  /** Field description */
  public static final String PROPERTY_UPDATEISSUES = "redmine.update-issues";

  /** Field description */
  public static final String PROPERTY_USERNAMETRANSFORMER =
    "redmine.auto-close-username-transformer";

  /** Field description */
  public static final String SEPARATOR = ",";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param repository
   */
  public RedmineConfiguration(Repository repository)
  {
    url = repository.getProperty(PROPERTY_REDMINE_URL);
    updateIssues = getBooleanProperty(repository, PROPERTY_UPDATEISSUES);
    autoClose = getBooleanProperty(repository, PROPERTY_AUTOCLOSE);
    autoCloseWords = getListProperty(repository, PROPERTY_AUTOCLOSEWORDS);
    usernameTransformPattern =
      repository.getProperty(PROPERTY_USERNAMETRANSFORMER);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<String> getAutoCloseWords()
  {
    return autoCloseWords;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUsernameTransformPattern()
  {
    return usernameTransformPattern;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isAutoCloseEnabled()
  {
    return isUpdateIssuesEnabled() && autoClose
           && Util.isNotEmpty(autoCloseWords);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isUpdateIssuesEnabled()
  {
    return isValid() && updateIssues;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public boolean isValid()
  {
    return Util.isNotEmpty(url);
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param key
   *
   * @return
   */
  private boolean getBooleanProperty(Repository repository, String key)
  {
    boolean result = false;
    String value = repository.getProperty(key);

    if (Util.isNotEmpty(value))
    {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param key
   *
   * @return
   */
  private List<String> getListProperty(Repository repository, String key)
  {
    List<String> values = null;
    String valueString = repository.getProperty(key);

    if (Util.isNotEmpty(valueString))
    {
      values = Arrays.asList(valueString.split(SEPARATOR));
    }
    else
    {
      values = Collections.emptyList();
    }

    return values;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private boolean autoClose;

  /** Field description */
  private List<String> autoCloseWords;

  /** Field description */
  private boolean updateIssues;

  /** Field description */
  private String url;

  /** Field description */
  private String usernameTransformPattern;
}
