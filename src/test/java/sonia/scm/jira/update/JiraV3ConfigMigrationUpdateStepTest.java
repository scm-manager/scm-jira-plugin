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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.migration.RepositoryUpdateContext;
import sonia.scm.store.ConfigurationStore;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JiraV3ConfigMigrationUpdateStepTest {

  private static final String REPOSITORY_ID = "42";

  private TestingConfigurationStoreFactory storeFactory;

  private JiraV3ConfigMigrationUpdateStep step;

  @BeforeEach
  void setUp() {
    storeFactory = new TestingConfigurationStoreFactory();
    step = new JiraV3ConfigMigrationUpdateStep(storeFactory);
  }

  @Test
  void shouldMigrate() {
    storeFactory.add("jira", CONFIG_V2.getBytes(StandardCharsets.UTF_8));

    step.doUpdate(new RepositoryUpdateContext(REPOSITORY_ID));

    JiraConfiguration config = getStore().get();
    assertThat(config.getUrl()).isEqualTo("https://hitchhiker.com/jira");
    assertThat(config.getFilter()).isEqualTo("CORE");
    assertThat(config.isUpdateIssues()).isTrue();
    assertThat(config.getRoleLevel()).isEqualTo("Admins");
    assertThat(config.getUsername()).isEqualTo("dent");
    assertThat(config.isAutoClose()).isTrue();
    assertThat(config.getAutoCloseWords())
      .containsEntry("fixes", "done")
      .containsEntry("open", "reopen and start progress")
      .containsEntry("begin", "start progress");
  }

  private ConfigurationStore<JiraConfiguration> getStore() {
    return storeFactory.withType(JiraConfiguration.class)
      .withName("jira")
      .forRepository(REPOSITORY_ID)
      .build();
  }

  @Test
  void shouldDoNothingWithoutV2Config() {
    step.doUpdate(new RepositoryUpdateContext(REPOSITORY_ID));

    assertThat(getStore().getOptional()).isEmpty();
  }

  private static final String CONFIG_V2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
    "<jira-configuration>\n" +
    "    <auto-close>true</auto-close>\n" +
    "    <auto-close-words>fixes=done, open=reopen and start progress, begin=start progress</auto-close-words>\n" +
    "    <mail-error-address>dent@hitchhiker.com</mail-error-address>\n" +
    "    <password></password>\n" +
    "    <rest-api-enabled>true</rest-api-enabled>\n" +
    "    <comment-prefix>[SCM]</comment-prefix>\n" +
    "    <filter>CORE</filter>\n" +
    "    <resubmission>true</resubmission>\n" +
    "    <role-level>Admins</role-level>\n" +
    "    <update-issues>true</update-issues>\n" +
    "    <url>https://hitchhiker.com/jira</url>\n" +
    "    <username>dent</username>\n" +
    "    <comment-wrap>{noformat}</comment-wrap>\n" +
    "    <comment-monospace>true</comment-monospace>\n" +
    "</jira-configuration>\n";
}
