/**
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

import sonia.scm.jira.AutoCloseWords;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.version.Version;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;
import static sonia.scm.update.V1PropertyReader.REPOSITORY_PROPERTY_READER;
import static sonia.scm.version.Version.parse;

@Extension
public class JiraV2ConfigMigrationUpdateStep implements UpdateStep {

  private final V1PropertyDAO v1PropertyDAO;
  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public JiraV2ConfigMigrationUpdateStep(V1PropertyDAO v1PropertyDAO, ConfigurationStoreFactory storeFactory) {
    this.v1PropertyDAO = v1PropertyDAO;
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    v1PropertyDAO
      .getProperties(REPOSITORY_PROPERTY_READER)
      .havingAnyOf(
        "jira.url",
        "jira.auto-close",
        "jira.comment-monospace",
        "jira.username",
        "jira.password",
        "jira.comment-prefix",
        "jira.update-issues",
        "jira.auto-close-words",
        "jira.rest-api-enabled",
        "jira.mail-error-address",
        "jira.comment-wrap",
        "jira.filter",
        "jira.role-level",
        "jira.resubmission")
      .forEachEntry((key, properties) -> setConfiguration(buildConfig(key, properties), key));
  }

  private void setConfiguration(JiraConfiguration config, String repositoryId) {
    storeFactory
      .withType(JiraConfiguration.class)
      .withName("jira")
      .forRepository(repositoryId)
      .build()
      .set(config);
  }

  private JiraConfiguration buildConfig(String key, V1Properties properties) {
    JiraConfiguration v2JiraConfig = new JiraConfiguration();
    v2JiraConfig.setUrl(properties.get("jira.url"));
    properties.getBoolean("jira.auto-close").ifPresent(v2JiraConfig::setAutoClose);
    properties.getBoolean("jira.comment-monospace").ifPresent(v2JiraConfig::setCommentMonospace);
    v2JiraConfig.setUsername(properties.get("jira.username"));
    v2JiraConfig.setPassword(properties.get("jira.password"));
    v2JiraConfig.setCommentPrefix(properties.get("jira.comment-prefix"));
    properties.getBoolean("jira.update-issues").ifPresent(v2JiraConfig::setUpdateIssues);
    ofNullable(properties.get("jira.auto-close-words")).map(AutoCloseWords::parse).ifPresent(v2JiraConfig::setAutoCloseWordsForMapping);
    properties.getBoolean("jira.rest-api-enabled").ifPresent(v2JiraConfig::setRestApiEnabled);
    v2JiraConfig.setMailAddress(properties.get("jira.mail-error-address"));
    v2JiraConfig.setCommentWrap(properties.get("jira.comment-wrap"));
    v2JiraConfig.setFilter(properties.get("jira.filter"));
    v2JiraConfig.setRoleLevel(properties.get("jira.role-level"));
    properties.getBoolean("jira.resubmission").ifPresent(v2JiraConfig::setResubmission);
    return v2JiraConfig;
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.jira.config.repository.xml";
  }
}
