package sonia.scm.jira.commitmessagechecker;

import com.cloudogu.scm.commitmessagechecker.Context;
import com.cloudogu.scm.commitmessagechecker.InvalidCommitMessageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraGlobalConfiguration;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static sonia.scm.jira.commitmessagechecker.CommitMessageIssueKeyValidator.CommitMessageIssueKeyValidatorConfig;

@ExtendWith(MockitoExtension.class)
class CommitMessageIssueKeyValidatorTest {

  private static final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private JiraGlobalContext jiraGlobalContext;

  @InjectMocks
  private CommitMessageIssueKeyValidator validator;

  @Test
  void shouldValidateSuccessfully() {
    mockJiraConfig("");
    CommitMessageIssueKeyValidatorConfig config = new CommitMessageIssueKeyValidatorConfig();
    validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature HOG-42 DONE");
  }

  @Test
  void shouldValidateSuccessfullyWithMultipleFilters() {
    mockJiraConfig("SCM,HOG,API");
    CommitMessageIssueKeyValidatorConfig config = new CommitMessageIssueKeyValidatorConfig();
    validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature API-21 DONE");
  }

  @Test
  void shouldFailOnInvalidIssueKey() {
    mockJiraConfig("");
    CommitMessageIssueKeyValidatorConfig config = new CommitMessageIssueKeyValidatorConfig();
    Assertions.assertThrows(InvalidCommitMessageException.class,
      () -> validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature 42 DONE"));
  }

  @Test
  void shouldFailIfIssueKeyDoNotMatchFilter() {
    mockJiraConfig("SCM");
    CommitMessageIssueKeyValidatorConfig config = new CommitMessageIssueKeyValidatorConfig();
    Assertions.assertThrows(InvalidCommitMessageException.class,
      () -> validator.validate(new Context(REPOSITORY, "master", config), "Trillian added some feature HOG-42 DONE"));
  }

  private void mockJiraConfig(String filter) {
    JiraGlobalConfiguration jiraGlobalConfiguration = new JiraGlobalConfiguration();
    jiraGlobalConfiguration.setDisableRepositoryConfiguration(false);
    jiraGlobalConfiguration.setFilter(filter);
    when(jiraGlobalContext.getGlobalConfiguration()).thenReturn(jiraGlobalConfiguration);
    JiraConfiguration jiraConfiguration = new JiraConfiguration();
    jiraConfiguration.setFilter(filter);
    lenient().when(jiraGlobalContext.getConfiguration(REPOSITORY)).thenReturn(jiraConfiguration);
  }
}
