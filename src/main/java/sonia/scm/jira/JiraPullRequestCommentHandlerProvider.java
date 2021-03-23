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
import sonia.scm.issuetracker.PullRequestCommentHandler;
import sonia.scm.issuetracker.PullRequestCommentHandlerProvider;
import sonia.scm.issuetracker.RequestData;
import sonia.scm.jira.rest.RestJiraHandlerFactory;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.jira.soap.SoapJiraHandlerFactory;
import sonia.scm.net.ahc.AdvancedHttpClient;

import javax.inject.Inject;
import javax.inject.Provider;

public class JiraPullRequestCommentHandlerProvider implements PullRequestCommentHandlerProvider {

  private static final Logger LOG = LoggerFactory.getLogger(JiraPullRequestCommentHandlerProvider.class);

  private final JiraGlobalContext context;
  private final MessageProblemHandler messageProblemHandler;
  private final Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider;
  private final AdvancedHttpClient advancedHttpClient;

  @Inject
  public JiraPullRequestCommentHandlerProvider(JiraGlobalContext context, MessageProblemHandler messageProblemHandler, Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider, AdvancedHttpClient advancedHttpClient) {
    this.context = context;
    this.messageProblemHandler = messageProblemHandler;
    this.templateHandlerFactoryProvider = templateHandlerFactoryProvider;
    this.advancedHttpClient = advancedHttpClient;
  }

  @Override
  public PullRequestCommentHandler getCommentHandler(RequestData data) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, data.getRepository());
    JiraHandlerFactory handlerFactory = createJiraHandlerFactory(configuration);
    return new JiraPullRequestCommentHandler(context, templateHandlerFactoryProvider.get(), handlerFactory, data);
  }

  private JiraHandlerFactory createJiraHandlerFactory(JiraConfiguration configuration)
  {
    JiraHandlerFactory factory;

    String username = configuration.getUsername();
    String password = configuration.getPassword();

    if (configuration.isRestApiEnabled())
    {
      LOG.debug("use rest api for jira communication");
      factory = new RestJiraHandlerFactory(advancedHttpClient, username, password);
    }
    else
    {
      LOG.debug("use soap api for jira communication");
      factory = new SoapJiraHandlerFactory(username, password);
    }

    return factory;
  }
}
