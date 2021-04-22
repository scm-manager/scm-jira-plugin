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

package sonia.scm.jira.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.jira.config.JiraGlobalConfiguration;
import sonia.scm.store.ConfigurationStore;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JiraV3GlobalConfigMigrationStepTest {

  private TestingConfigurationStoreFactory storeFactory;

  private JiraV3GlobalConfigMigrationStep step;

  @BeforeEach
  void setUp() {
    storeFactory = new TestingConfigurationStoreFactory();
    step = new JiraV3GlobalConfigMigrationStep(storeFactory);
  }

  @Test
  void shouldMigrate() {
    storeFactory.add("jira", CONFIG_V2.getBytes(StandardCharsets.UTF_8));

    step.doUpdate();

    JiraGlobalConfiguration config = getStore().get();
    assertThat(config.getUrl()).isEqualTo("https://jira.hitchhiker.com");
    assertThat(config.getFilter()).isEqualTo("SCM");
    assertThat(config.isUpdateIssues()).isTrue();
    assertThat(config.getRoleLevel()).isEqualTo("Vogons");
    assertThat(config.getUsername()).isEqualTo("trillian");
    assertThat(config.isAutoClose()).isTrue();
    assertThat(config.getAutoCloseWords())
      .containsEntry("fix", "done")
      .containsEntry("reopen", "reopen and start progress")
      .containsEntry("start", "start progress");
    assertThat(config.isDisableRepositoryConfiguration()).isFalse();
  }

  private ConfigurationStore<JiraGlobalConfiguration> getStore() {
    return storeFactory.withType(JiraGlobalConfiguration.class).withName("jira").build();
  }

  @Test
  void shouldDoNothingWithoutV2Config() {
    step.doUpdate();

    assertThat(getStore().getOptional()).isEmpty();
  }

  private static final String CONFIG_V2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
    "<jira-global-configuration>\n" +
    "    <auto-close>true</auto-close>\n" +
    "    <auto-close-words>fix=done, reopen=reopen and start progress, start=start progress</auto-close-words>\n" +
    "    <mail-error-address>trillian@hitchhiker.com</mail-error-address>\n" +
    "    <password></password>\n" +
    "    <rest-api-enabled>true</rest-api-enabled>\n" +
    "    <comment-prefix>[SCM]</comment-prefix>\n" +
    "    <filter>SCM</filter>\n" +
    "    <resubmission>true</resubmission>\n" +
    "    <role-level>Vogons</role-level>\n" +
    "    <update-issues>true</update-issues>\n" +
    "    <url>https://jira.hitchhiker.com</url>\n" +
    "    <username>trillian</username>\n" +
    "    <comment-wrap>{quote}</comment-wrap>\n" +
    "    <comment-monospace>true</comment-monospace>\n" +
    "    <disable-repository-configuration>false</disable-repository-configuration>\n" +
    "</jira-global-configuration>\n";

}
