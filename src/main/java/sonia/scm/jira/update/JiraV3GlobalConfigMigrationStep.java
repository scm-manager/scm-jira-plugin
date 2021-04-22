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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.config.JiraGlobalConfiguration;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.StoreException;
import sonia.scm.version.Version;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.empty;

@Extension
public class JiraV3GlobalConfigMigrationStep implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(JiraV3GlobalConfigMigrationStep.class);

  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public JiraV3GlobalConfigMigrationStep(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    readV2JiraConfiguration()
      .ifPresent(
        v2JiraConfig -> {
          JiraGlobalConfiguration v3JiraConfig = new JiraGlobalConfiguration();
          v2JiraConfig.copyTo(v3JiraConfig);
          storeFactory.withType(JiraGlobalConfiguration.class).withName("jira").build().set(v3JiraConfig);
        }
      );
  }

  private Optional<V2JiraGlobalConfiguration> readV2JiraConfiguration() {
    try {
      return storeFactory.withType(V2JiraGlobalConfiguration.class).withName("jira").build().getOptional();
    } catch (StoreException e) {
      LOG.debug("could not read existing jira configuration store; assume that it already is a v3 store", e);
      return empty();
    }
  }

  @Override
  public Version getTargetVersion() {
    return Version.parse("3.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.jira.config.global.xml";
  }

}
