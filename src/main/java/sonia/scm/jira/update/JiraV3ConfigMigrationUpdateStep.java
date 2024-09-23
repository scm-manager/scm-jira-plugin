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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.migration.RepositoryUpdateContext;
import sonia.scm.migration.RepositoryUpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.StoreException;
import sonia.scm.version.Version;

import jakarta.inject.Inject;
import java.util.Optional;

import static java.util.Optional.empty;

@Extension
public class JiraV3ConfigMigrationUpdateStep implements RepositoryUpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(JiraV3ConfigMigrationUpdateStep.class);

  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public JiraV3ConfigMigrationUpdateStep(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate(RepositoryUpdateContext repositoryUpdateContext) {
    String repositoryId = repositoryUpdateContext.getRepositoryId();
    readV2JiraConfiguration(repositoryId)
      .ifPresent(
        v2JiraConfig -> {
          JiraConfiguration v3JiraConfig = new JiraConfiguration();
          v2JiraConfig.copyTo(v3JiraConfig);
          storeFactory.withType(JiraConfiguration.class)
            .withName("jira")
            .forRepository(repositoryId)
            .build()
            .set(v3JiraConfig);
        }
      );
  }

  private Optional<V2JiraConfiguration> readV2JiraConfiguration(String repositoryId) {
    try {
      return storeFactory.withType(V2JiraConfiguration.class)
        .withName("jira")
        .forRepository(repositoryId)
        .build()
        .getOptional();
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
    return "sonia.scm.jira.config.repository.xml";
  }

}
