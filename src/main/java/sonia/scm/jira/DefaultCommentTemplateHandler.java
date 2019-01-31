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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.issuetracker.TemplateBasedHandler;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class DefaultCommentTemplateHandler extends TemplateBasedHandler implements CommentTemplateHandler {

  private static final String ENV_COMMENTWRAP_PRE = "commentWrapPre";
  private static final String ENV_COMMENTWRAP_POST = "commentWrapPost";

  //~--- constructors ---------------------------------------------------------

  @Inject
  public DefaultCommentTemplateHandler(
    TemplateEngineFactory templateEngineFactory, LinkHandler linkHandler)
  {
    super(templateEngineFactory, linkHandler);
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public Map<String, Object> createBaseEnvironment(JiraIssueRequest request,
    Changeset changeset)
  {
    Repository repository = request.getRepository();
    IssueRequest issueRequest = new IssueRequest(repository, changeset, Collections.emptyList(), request.getCommitter());


    Map<String, Object> model = createModel(issueRequest, null);


    final JiraConfiguration configuration = request.getConfiguration();
    final boolean commentMonospace = configuration.getCommentMonospace();
    final String commentWrap = Strings.nullToEmpty(configuration.getCommentWrap());

    final String commentWrapPre = (commentMonospace ? "{{" : "") + commentWrap;
    final String commentWrapPost = commentWrap + (commentMonospace ? "}}" : "");

    model.put(ENV_COMMENTWRAP_PRE, commentWrapPre);
    model.put(ENV_COMMENTWRAP_POST, commentWrapPost);

    return model;
  }

  @Override
  public String render(Object environment)
  {
    return createComment(environment);
  }

  @Override
  protected Template loadTemplate(TemplateEngine engine) throws IOException {
    return engine.getTemplate(createTemplate().getResource());
  }

  abstract CommentTemplate createTemplate();
}
