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

package sonia.scm.jira.config;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.JiraPermissions;
import sonia.scm.repository.Repository;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

/**
 * Global jira context.
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class JiraConfigurationStore
{

  /** configuration store name */
  private static final String NAME = "jira";

  /**
   * the logger for JiraGlobalContext
   */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraConfigurationStore.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraGlobalContext
   *
   *
   * @param storeFactory store factory
   */
  @Inject
  public JiraConfigurationStore(ConfigurationStoreFactory storeFactory)
  {
    this.storeFactory = storeFactory;
    this.store = storeFactory.withType(JiraGlobalConfiguration.class).withName(NAME).build();
  }


  //~--- get methods ----------------------------------------------------------
  /**
   * Returns the global jira configuration.
   *
   *
   * @return global jira configuration
   */
  public JiraGlobalConfiguration getGlobalConfiguration()
  {
    return store
      .getOptional()
      .orElse(new JiraGlobalConfiguration());
  }

  public JiraConfiguration getConfiguration(Repository repository) {
    return getRepositoryStore(repository)
      .getOptional()
      .orElse(new JiraConfiguration());
  }


  //~--- set methods ----------------------------------------------------------
  /**
   * Sets and stores the global jira configuration.
   *
   *
   * @param configuration global jira configuration
   */
  public void setGlobalConfiguration(JiraGlobalConfiguration configuration)
  {
    JiraPermissions.checkWriteGlobalConfig();
    logger.debug("store jira configuration");
    this.store.set(configuration);
  }

  public void setConfiguration(JiraConfiguration configuration, Repository repository) {
    JiraPermissions.checkWriteRepositoryConfig(repository);
    getRepositoryStore(repository)
      .set(configuration);
  }

  private ConfigurationStore<JiraConfiguration> getRepositoryStore(Repository repository) {
    return storeFactory
      .withType(JiraConfiguration.class)
      .withName(NAME)
      .forRepository(repository)
      .build();
  }

  //~--- fields ---------------------------------------------------------------
  /** global configuration store */
  private final ConfigurationStore<JiraGlobalConfiguration> store;
  private final ConfigurationStoreFactory storeFactory;
}
