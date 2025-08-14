/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.jira;

import sonia.scm.issuetracker.api.IssueTracker;
import sonia.scm.issuetracker.spi.IssueTrackerBuilder;
import sonia.scm.issuetracker.spi.IssueTrackerProvider;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.config.JiraConfigurationResolver;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Optional;

@Extension
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

    if (configuration.isUpdateIssues()) {
      RestApi restApi = new RestApi(httpClient.get(), configuration);
      IssueTrackerBuilder.ChangeStateStage changeStateStage = readStage.commenting(repository, new JiraCommentator(restApi, configuration))
        .template("/sonia/scm/jira/{0}_reference.mustache");
      if (configuration.isAutoClose()) {
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
