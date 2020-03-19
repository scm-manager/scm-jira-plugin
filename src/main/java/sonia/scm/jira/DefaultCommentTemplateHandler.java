/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
