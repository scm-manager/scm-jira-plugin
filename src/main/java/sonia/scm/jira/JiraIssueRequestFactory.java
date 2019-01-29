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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.shiro.SecurityUtils;
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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

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
   * @return new {@link JiraIssueRequest}
   */
  public JiraIssueRequest createRequest(JiraConfiguration configuration, Repository repository, Changeset changeset)
  {
    return createRequest(configuration, repository, changeset, getCommitter().map(User::getDisplayName), null);
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
  public JiraIssueRequest createRequest(JiraConfiguration configuration, Repository repository, Changeset changeset, Optional<String> committer,
    Calendar creation)
  {
    logger.debug("create jira issue request");

    return new JiraIssueRequest(createJiraHandlerFactory(configuration), committer, configuration, repository,
      changeset, creation);
  }

  private static Optional<User> getCommitter() {
    try {
      return ofNullable(SecurityUtils.getSubject().getPrincipals().oneByType(User.class));
    } catch (Exception e) {
      // reading the logged in user should not let the comment fail
      logger.info("could not read current user from SecurityUtils", e);
      return empty();
    }
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
