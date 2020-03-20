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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.rest.RestJiraHandlerFactory;
import sonia.scm.jira.soap.SoapJiraHandlerFactory;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.user.User;

import java.util.Calendar;
import java.util.Optional;

/**
 * The JiraIssueRequestFactory is able to create {@link JiraIssueRequest}
 * instances.
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class JiraIssueRequestFactory
{

  /**
   * the logger for JiraIssueRequestFactory
   */
  private static final Logger logger = LoggerFactory.getLogger(JiraIssueRequestFactory.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraIssueRequestFactory.
   *
   * @param ahcProvider provider for {@link AdvancedHttpClient}
   */
  @Inject
  public JiraIssueRequestFactory(Provider<AdvancedHttpClient> ahcProvider)
  {
    this.ahcProvider = ahcProvider;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Creates a new {@link JiraIssueRequest}.
   *
   * @param configuration jira configuration
   * @param repository changed repository
   *
   * @param committer
   * @return new {@link JiraIssueRequest}
   */
  public JiraIssueRequest createRequest(JiraConfiguration configuration, Repository repository, Changeset changeset, Optional<User> committer)
  {
    return createRequest(configuration, repository, changeset, committer, null);
  }

  /**
   * Creates a new {@link JiraIssueRequest}.
   *
   * @param configuration jira configuration
   * @param repository changed repository
   * @param committer name of user which has done the push/commit
   * @param creation creation time
   *
   * @return new {@link JiraIssueRequest}
   */
  public JiraIssueRequest createRequest(JiraConfiguration configuration, Repository repository, Changeset changeset, Optional<User> committer,
    Calendar creation)
  {
    logger.debug("create jira issue request");

    return new JiraIssueRequest(createJiraHandlerFactory(configuration), committer, configuration, repository,
      changeset, creation);
  }

  private JiraHandlerFactory createJiraHandlerFactory(JiraConfiguration configuration)
  {
    JiraHandlerFactory factory;

    String username = configuration.getUsername();
    String password = configuration.getPassword();

    if (configuration.isRestApiEnabled())
    {
      logger.debug("use rest api for jira communication");
      factory = new RestJiraHandlerFactory(ahcProvider.get(), username, password);
    }
    else
    {
      logger.debug("use soap api for jira communication");
      factory = new SoapJiraHandlerFactory(username, password);
    }

    return factory;
  }

  //~--- fields ---------------------------------------------------------------

  /** provider for AdvancedHttpClient */
  private final Provider<AdvancedHttpClient> ahcProvider;
}
