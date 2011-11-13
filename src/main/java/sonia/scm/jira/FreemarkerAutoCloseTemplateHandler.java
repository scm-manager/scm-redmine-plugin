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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra
 */
public class FreemarkerAutoCloseTemplateHandler
        implements AutoCloseTemplateHandler
{

  /** Field description */
  public static final String ENV_AUTOCLOSEWORD = "autoCloseWord";

  /** Field description */
  public static final String ENV_CHANGESET = "changeset";

  /** Field description */
  public static final String ENV_DIFFURL = "diffUrl";

  /** Field description */
  public static final String ENV_REPOSITORY = "repository";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public FreemarkerAutoCloseTemplateHandler()
  {
    configuration = new Configuration();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param name
   * @param reader
   * @param repository
   * @param changeset
   * @param autoCloseWord
   *
   * @return
   *
   *
   * @throws AutoCloseTemplateException
   */
  @Override
  public String render(String name, Reader reader, Repository repository,
                       Changeset changeset, String autoCloseWord)
          throws AutoCloseTemplateException
  {
    StringWriter writer = null;

    try
    {
      Template template = new Template(name, reader, configuration);

      writer = new StringWriter();

      Map<String, Object> env = new HashMap<String, Object>();

      env.put(ENV_REPOSITORY, repository);
      env.put(ENV_CHANGESET, changeset);
      env.put(ENV_AUTOCLOSEWORD, autoCloseWord);

      // todo create diff url
      env.put(ENV_DIFFURL, Util.EMPTY_STRING);
      template.process(env, writer);
    }
    catch (Exception ex)
    {
      throw new AutoCloseTemplateException("could not render template", ex);
    }

    return writer.toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Configuration configuration;
}
