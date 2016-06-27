/***
 * Copyright (c) 2015, Sebastian Sdorra
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
 * https://bitbucket.org/sdorra/scm-manager
 * 
 */

package sonia.scm.redmine.config;

import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.redmine.RedmineIssueTracker;
import sonia.scm.resources.ResourceHandler;
import sonia.scm.resources.ResourceType;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class RepositoryConfigResourceHandler implements ResourceHandler {

  /** Field description */
  public static final String PATH = "/sonia/scm/redmine/sonia.redmine.repositoryconfigpanel.js";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param tracker
   */
  @Inject
  public RepositoryConfigResourceHandler(RedmineIssueTracker tracker)
  {
    this.tracker = tracker;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String getName()
  {
    return PATH;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public InputStream getResource()
  {
    InputStream content;

    if (!tracker.getGlobalConfiguration().isDisableRepositoryConfiguration())
    {
      content = RepositoryConfigResourceHandler.class.getResourceAsStream(PATH);
    }
    else
    {

      // create dummy stream
      content = new ByteArrayInputStream(new byte[0]);
    }

    return content;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public ResourceType getType()
  {
    return ResourceType.SCRIPT;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final RedmineIssueTracker tracker;
}
