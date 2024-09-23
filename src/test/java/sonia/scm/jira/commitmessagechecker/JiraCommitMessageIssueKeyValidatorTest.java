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

package sonia.scm.jira.commitmessagechecker;

import com.cloudogu.scm.commitmessagechecker.Context;
import com.cloudogu.scm.commitmessagechecker.InvalidCommitMessageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.config.JiraConfigurationResolver;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sonia.scm.jira.commitmessagechecker.JiraCommitMessageIssueKeyValidator.JiraCommitMessageIssueKeyValidatorConfig;

@ExtendWith(MockitoExtension.class)
class JiraCommitMessageIssueKeyValidatorTest {

  private static final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private JiraConfigurationResolver configResolver;

  @InjectMocks
  private JiraCommitMessageIssueKeyValidator validator;

  @Test
  void shouldValidateSuccessfully() {
    mockJiraConfig("");
    JiraCommitMessageIssueKeyValidatorConfig config = new JiraCommitMessageIssueKeyValidatorConfig();
    validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature HOG-42 DONE");
  }

  @Test
  void shouldValidateSuccessfullyWithMultipleFilters() {
    mockJiraConfig("SCM,HOG,API");
    JiraCommitMessageIssueKeyValidatorConfig config = new JiraCommitMessageIssueKeyValidatorConfig();
    validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature API-21 DONE");
  }

  @Test
  void shouldFailOnInvalidIssueKey() {
    mockJiraConfig("");
    JiraCommitMessageIssueKeyValidatorConfig config = new JiraCommitMessageIssueKeyValidatorConfig();
    Context context = new Context(REPOSITORY, "master", config);
    Assertions.assertThrows(InvalidCommitMessageException.class,
      () -> validator.validate(context, "Trillian added some feature 42 DONE"));
  }

  @Test
  void shouldFailIfIssueKeyDoNotMatchFilter() {
    mockJiraConfig("SCM");
    JiraCommitMessageIssueKeyValidatorConfig config = new JiraCommitMessageIssueKeyValidatorConfig();
    Context context = new Context(REPOSITORY, "master", config);
    Assertions.assertThrows(InvalidCommitMessageException.class,
      () -> validator.validate(context, "Trillian added some feature HOG-42 DONE"));
  }

  private void mockJiraConfig(String filter) {
    JiraConfiguration configuration = new JiraConfiguration();
    configuration.setFilter(filter);
    when(configResolver.resolve(any())).thenReturn(Optional.of(configuration));
  }
}
