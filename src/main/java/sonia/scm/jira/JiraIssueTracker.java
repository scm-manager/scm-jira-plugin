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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.issuetracker.DataStoreBasedIssueTracker;
import sonia.scm.issuetracker.IssueLinkFactory;
import sonia.scm.issuetracker.IssueMatcher;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.text.MessageFormat;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Extension
@EagerSingleton
public class JiraIssueTracker extends DataStoreBasedIssueTracker {

  public static final String JIRA_ISSUE_TRACKER_NAME = "jira";
  public static final String JIRA_URL_TEMPLATE = "{0}/browse/{1}";

  private final JiraGlobalContext context;
  private final MessageProblemHandler messageProblemHandler;
  private final Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider;
  private final JiraIssueRequestFactory requestFactory;
  private final JiraMatcherProvider matcherProvider;

  @Inject
  public JiraIssueTracker(JiraGlobalContext context,
                          DataStoreFactory storeFactory,
                          MessageProblemHandler messageProblemHandler,
                          Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider,
                          JiraIssueRequestFactory requestFactory,
                          JiraMatcherProvider matcherProvider) {
    super(JIRA_ISSUE_TRACKER_NAME, storeFactory);
    this.context = context;
    this.messageProblemHandler = messageProblemHandler;
    this.templateHandlerFactoryProvider = templateHandlerFactoryProvider;
    this.requestFactory = requestFactory;
    this.matcherProvider = matcherProvider;
  }

  @Override
  public Optional<IssueMatcher> createMatcher(Repository repository) {
    return matcherProvider.createMatcher(repository);
  }

  @Override
  public Optional<IssueLinkFactory> createLinkFactory(Repository repository) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, repository);
    String jiraUrl = configuration.getUrl();
    if (jiraUrl == null) {
      return empty();
    } else {
      return of(key -> MessageFormat.format(JIRA_URL_TEMPLATE, jiraUrl, key));
    }
  }

  @Override
  protected CommentHandler getCommentHandler(IssueRequest request) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, request.getRepository());
    JiraIssueRequest jiraIssueRequest = requestFactory.createRequest(configuration, request.getRepository(), request.getChangeset(), request.getCommitter());
    return new JiraIssueHandler(messageProblemHandler, templateHandlerFactoryProvider.get(), jiraIssueRequest);
  }
}
