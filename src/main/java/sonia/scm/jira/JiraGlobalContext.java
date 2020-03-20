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

package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Repository;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

/**
 * Global jira context.
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class JiraGlobalContext
{

  /** configuration store name */
  private static final String NAME = "jira";

  /**
   * the logger for JiraGlobalContext
   */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraGlobalContext.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraGlobalContext
   *
   *
   * @param storeFactory store factory
   * @param permissions
   */
  @Inject
  public JiraGlobalContext(ConfigurationStoreFactory storeFactory, JiraPermissions permissions)
  {
    this.storeFactory = storeFactory;
    this.store = storeFactory.withType(JiraGlobalConfiguration.class).withName(NAME).build();
    this.permissions = permissions;
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
    permissions.checkWriteGlobalConfig();
    logger.debug("store jira configuration");
    this.store.set(configuration);
  }

  public void setConfiguration(JiraConfiguration configuration, Repository repository) {
    permissions.checkWriteRepositoryConfig(repository);
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
  private final JiraPermissions permissions;
  private final ConfigurationStoreFactory storeFactory;
}
