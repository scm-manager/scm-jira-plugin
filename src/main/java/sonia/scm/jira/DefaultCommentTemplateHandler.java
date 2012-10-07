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
   */
  public DefaultCommentTemplateHandler(
    TemplateEngineFactory templateEngineFactory,
    @Named(UrlProviderFactory.TYPE_WUI) UrlProvider wuiUrlProvider,
    @Named(UrlProviderFactory.TYPE_RESTAPI_XML) UrlProvider restUrlProvider)
  {
    this.templateEngineFactory = templateEngineFactory;
    this.wuiUrlProvider = wuiUrlProvider;
    this.restUrlProvider = restUrlProvider;
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
    env.put(ENV_DIFFURL, getDiffUrl(repository, changeset));
    env.put(ENV_DIFFRESTURL, getDiffRestUrl(repository, changeset));
    env.put(ENV_REPOSITORYURL, getRepositoryUrl(repository));

    TemplateEngine engine = templateEngineFactory.getDefaultEngine();
    Template template = engine.getTemplate(tpl.getResource());

    StringWriter writer = new StringWriter();

    template.execute(writer, env);

    return writer.toString();
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   * @param changeset
   *
   * @return
   */
  private String getDiffRestUrl(Repository repository, Changeset changeset)
  {
    return restUrlProvider.getRepositoryUrlProvider().getDiffUrl(
      repository.getId(), changeset.getId());
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param changeset
   *
   * @return
   */
  private String getDiffUrl(Repository repository, Changeset changeset)
  {
    return wuiUrlProvider.getRepositoryUrlProvider().getDiffUrl(
      repository.getId(), changeset.getId());
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  private String getRepositoryUrl(Repository repository)
  {
    return wuiUrlProvider.getRepositoryUrlProvider().getDetailUrl(
      repository.getId());
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private UrlProvider restUrlProvider;

  /** Field description */
  private TemplateEngineFactory templateEngineFactory;

  /** Field description */
  private UrlProvider wuiUrlProvider;
}
