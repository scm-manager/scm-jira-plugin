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

package sonia.scm.jira.update;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.update.V1PropertyDaoTestUtil;
import sonia.scm.update.V1PropertyDaoTestUtil.PropertiesForRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

    V2JiraConfiguration actualConfiguration = storeFactory
      .withType(V2JiraConfiguration.class)
      .withName("jira")
      .forRepository("repo")
      .build()
      .get();

    assertThat(actualConfiguration)
      .hasFieldOrPropertyWithValue("url", "http://hitchhiker.com")
      .hasFieldOrPropertyWithValue("autoClose", true)
      .hasFieldOrPropertyWithValue("username", "master")
      .hasFieldOrPropertyWithValue("password", "{enc}lsR6NYxeb1agGdOblwQfOkTI40JsrmnK")
      .hasFieldOrPropertyWithValue("updateIssues", true)
      .hasFieldOrPropertyWithValue("filter", "good-ones")
      .hasFieldOrPropertyWithValue("roleLevel", "vogons");

    Map<String, String> autoCloseWords = actualConfiguration.getAutoCloseWords();
    assertThat(autoCloseWords).containsEntry("fix", "done")
      .containsEntry("reopen", "reopen and start progress")
      .containsEntry("start", "start progress");
  }

  @Test
  void shouldSkipRepositoriesWithoutJiraConfig() {
    Map<String, String> mockedValues = ImmutableMap.of("any", "value");

    testUtil.mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    JiraConfiguration actualConfiguration = storeFactory.withType(JiraConfiguration.class).withName("jira").build().get();

    assertThat(actualConfiguration).isNull();
  }
}
