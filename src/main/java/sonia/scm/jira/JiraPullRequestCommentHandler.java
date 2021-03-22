/*
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.PullRequestCommentHandler;
import sonia.scm.issuetracker.PullRequestIssueRequestData;

import java.util.GregorianCalendar;

public class JiraPullRequestCommentHandler implements PullRequestCommentHandler {

  private static final Logger LOG = LoggerFactory.getLogger(JiraPullRequestCommentHandler.class);

  private final JiraGlobalContext context;
  private final CommentTemplateHandlerFactory commentTemplateHandlerFactory;
  private final JiraHandlerFactory handlerFactory;

  private final PullRequestIssueRequestData data;

  public JiraPullRequestCommentHandler(JiraGlobalContext context,
                                       CommentTemplateHandlerFactory commentTemplateHandlerFactory,
                                       JiraHandlerFactory handlerFactory, PullRequestIssueRequestData data) {
    this.context = context;
    this.commentTemplateHandlerFactory = commentTemplateHandlerFactory;
    this.handlerFactory = handlerFactory;
    this.data = data;
  }

  @Override
  public void mentionedInTitleOrDescription(String issueId) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, data.getRepository());
    try {
      CommentTemplateHandler commentTemplateHandler = commentTemplateHandlerFactory.create(CommentTemplate.PR);
      String comment = commentTemplateHandler.render(data);

      if (!Strings.isNullOrEmpty(comment)) {
        LOG.info("add comment to issue {}", issueId);
        JiraHandler jiraHandler = handlerFactory.createJiraHandler(configuration);
        jiraHandler.addComment(issueId, Comments.createComment(configuration, new GregorianCalendar(), comment));
      } else {
        LOG.warn("generate comment is null or empty");
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
