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

import sonia.scm.issuetracker.api.IssueTracker;
import sonia.scm.issuetracker.spi.IssueTrackerBuilder;
import sonia.scm.issuetracker.spi.IssueTrackerProvider;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.config.JiraConfigurationResolver;
import sonia.scm.jira.config.JiraStateChanger;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

public class JiraIssueTrackerProvider implements IssueTrackerProvider {

  private static final String NAME = "jira";

  private final JiraConfigurationResolver resolver;
  private final Provider<AdvancedHttpClient> httpClient;

  @Inject
  public JiraIssueTrackerProvider(JiraConfigurationResolver resolver, Provider<AdvancedHttpClient> httpClient) {
    this.resolver = resolver;
    this.httpClient = httpClient;
  }

  @Override
  public Optional<IssueTracker> create(IssueTrackerBuilder builder, Repository repository) {
    return resolver.resolve(repository).map(configuration -> create(builder, configuration, repository));
  }

  private IssueTracker create(IssueTrackerBuilder builder, JiraConfiguration configuration, Repository repository) {
    IssueTrackerBuilder.ReadStage readStage = builder.start(
      NAME, new JiraIssueMatcher(configuration), new JiraIssueLinkFactory(configuration)
    );

    if (configuration.isUpdateIssuesEnabled()) {
      RestApi restApi = new RestApi(httpClient.get(), configuration);
      IssueTrackerBuilder.ChangeStateStage changeStateStage = readStage.commenting(repository, new JiraCommentator(restApi))
        .template("/sonia/scm/jira/{0}_reference.mustache");
      if (configuration.isAutoCloseEnabled()) {
        return changeStateStage.stateChanging(new JiraStateChanger(restApi, configuration))
          .template("/sonia/scm/jira/{0}_statechange.mustache")
          .build();
      } else {
        return changeStateStage.build();
      }
    } else {
      return readStage.build();
    }
  }
}
