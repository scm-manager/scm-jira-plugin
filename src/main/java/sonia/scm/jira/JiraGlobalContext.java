/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
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
