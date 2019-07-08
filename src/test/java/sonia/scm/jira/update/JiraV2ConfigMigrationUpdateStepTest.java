package sonia.scm.jira.update;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.update.V1PropertyDaoTestUtil;
import sonia.scm.update.V1PropertyDaoTestUtil.PropertiesForRepository;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JiraV2ConfigMigrationUpdateStepTest {

  V1PropertyDaoTestUtil testUtil = new V1PropertyDaoTestUtil();
  ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();

  private JiraV2ConfigMigrationUpdateStep updateStep;

  @BeforeEach
  void initUpdateStep() {
    updateStep = new JiraV2ConfigMigrationUpdateStep(testUtil.getPropertyDAO(), storeFactory);
  }

  @Test
  void shouldMigrateRepositoryConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.<String, String>builder()
        .put("jira.url", "http://hitchhiker.com")
        .put("jira.auto-close", "true")
        .put("jira.comment-monospace", "true")
        .put("jira.username", "master")
        .put("jira.password", "{enc}lsR6NYxeb1agGdOblwQfOkTI40JsrmnK")
        .put("jira.comment-prefix", "POST")
        .put("jira.update-issues", "true")
        .put("jira.auto-close-words", "fix=done, reopen=reopen and start progress, start=start progress")
        .put("jira.rest-api-enabled", "true")
        .put("jira.mail-error-address", "dent@hitchhiker,com")
        .put("jira.comment-wrap", "(quote)")
        .put("jira.filter", "good-ones")
        .put("jira.role-level", "vogons")
        .put("jira.resubmission", "true")
        .build();

    testUtil.mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    JiraConfiguration actualConfiguration = storeFactory
      .withType(JiraConfiguration.class)
      .withName("jira")
      .forRepository("repo")
      .build()
      .get();

    assertThat(actualConfiguration)
      .hasFieldOrPropertyWithValue("url", "http://hitchhiker.com")
      .hasFieldOrPropertyWithValue("autoClose", true)
      .hasFieldOrPropertyWithValue("commentMonospace", true)
      .hasFieldOrPropertyWithValue("username", "master")
      .hasFieldOrPropertyWithValue("password", "{enc}lsR6NYxeb1agGdOblwQfOkTI40JsrmnK")
      .hasFieldOrPropertyWithValue("commentPrefix", "POST")
      .hasFieldOrPropertyWithValue("updateIssues", true)
      .hasFieldOrPropertyWithValue("restApiEnabled", true)
      .hasFieldOrPropertyWithValue("mailAddress", "dent@hitchhiker,com")
      .hasFieldOrPropertyWithValue("commentWrap", "(quote)")
      .hasFieldOrPropertyWithValue("filter", "good-ones")
      .hasFieldOrPropertyWithValue("roleLevel", "vogons")
      .hasFieldOrPropertyWithValue("resubmission", true)
    ;
    assertThat(actualConfiguration.getMappedAutoCloseWord("fix")).isEqualTo("done");
    assertThat(actualConfiguration.getMappedAutoCloseWord("reopen")).isEqualTo("reopen and start progress");
    assertThat(actualConfiguration.getMappedAutoCloseWord("start")).isEqualTo("start progress");
  }

  @Test
  void shouldSkipRepositoriesWithoutJiraConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "any", "value"
      );

    testUtil.mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    JiraConfiguration actualConfiguration = storeFactory.withType(JiraConfiguration.class).withName("jira").build().get();

    assertThat(actualConfiguration).isNull();
  }
}
