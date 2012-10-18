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

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;
import sonia.scm.url.UrlProvider;
import sonia.scm.url.UrlProviderFactory;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.StringWriter;

import java.util.Map;

/**
 *
 * @author Sebastian Sdorra
 */
public class DefaultCommentTemplateHandler implements CommentTemplateHandler
{

  /** Field description */
  private static final String ENV_AUTOCLOSEWORD = "autoCloseWord";

  /** Field description */
  private static final String ENV_CHANGESET = "changeset";

  /** Field description */
  private static final String ENV_DIFFRESTURL = "diffRestUrl";

  /** Field description */
  private static final String ENV_DIFFURL = "diffUrl";

  /** Field description */
  private static final String ENV_REPOSITORY = "repository";

  /** Field description */
  private static final String ENV_REPOSITORYURL = "repositoryUrl";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param templateEngineFactory
   * @param wuiUrlProvider
   * @param restUrlProvider
   * @param linkHandler
   */
  @Inject
  public DefaultCommentTemplateHandler(
    TemplateEngineFactory templateEngineFactory, LinkHandler linkHandler)
  {
    this.templateEngineFactory = templateEngineFactory;
    this.linkHandler = linkHandler;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param template
   * @param request
   * @param changeset
   *
   * @return
   *
   * @throws IOException
   */
  @Override
  public String render(CommentTemplate template, JiraIssueRequest request,
    Changeset changeset)
    throws IOException
  {
    return render(template, request, changeset, null);
  }

  /**
   * Method description
   *
   *
   * @param tpl
   * @param request
   * @param changeset
   * @param autoCloseWord
   *
   * @return
   *
   * @throws IOException
   */
  @Override
  public String render(CommentTemplate tpl, JiraIssueRequest request,
    Changeset changeset, String autoCloseWord)
    throws IOException
  {
    Repository repository = request.getRepository();
    Map<String, Object> env = Maps.newHashMap();

    env.put(ENV_REPOSITORY, repository);
    env.put(ENV_CHANGESET, changeset);
    env.put(ENV_AUTOCLOSEWORD, Util.nonNull(autoCloseWord));
    env.put(ENV_DIFFURL, linkHandler.getDiffUrl(repository, changeset));
    env.put(ENV_DIFFRESTURL, linkHandler.getDiffRestUrl(repository, changeset));
    env.put(ENV_REPOSITORYURL, linkHandler.getRepositoryUrl(repository));

    TemplateEngine engine = templateEngineFactory.getDefaultEngine();
    Template template = engine.getTemplate(tpl.getResource());

    StringWriter writer = new StringWriter();

    template.execute(writer, env);

    return writer.toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private LinkHandler linkHandler;

  /** Field description */
  private TemplateEngineFactory templateEngineFactory;
}
