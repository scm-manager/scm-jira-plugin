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

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.Store;
import sonia.scm.store.StoreFactory;

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
   * @param dataStoreFactory data store factory
   */
  @Inject
  public JiraGlobalContext(StoreFactory storeFactory,
    DataStoreFactory dataStoreFactory)
  {
    store = storeFactory.getStore(JiraGlobalConfiguration.class, NAME);
    dataStore = dataStoreFactory.getStore(JiraData.class, NAME);
    configuration = store.get();

    if (configuration == null)
    {
      configuration = new JiraGlobalConfiguration();
    }
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Marks an changeset as handled.
   *
   *
   * @param repository changed repository
   * @param changeset changeset
   */
  public void markAsHandled(Repository repository, Changeset changeset)
  {
    logger.debug("mark changeset {} of repository {} as handled",
      changeset.getId(), repository.getId());

    JiraData data = getData(repository);

    data.getHandledChangesets().add(changeset.getId());
    dataStore.put(repository.getId(), data);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the global jira configuration.
   *
   *
   * @return global jira configuration
   */
  public JiraGlobalConfiguration getConfiguration()
  {
    return configuration;
  }

  /**
   * Returns {@code true} if the changeset is already handled.
   *
   *
   * @param repository changed repository
   * @param changeset changeset
   *
   * @return {@code true} if the changeset is already handled
   */
  public boolean isHandled(Repository repository, Changeset changeset)
  {
    JiraData data = getData(repository);

    return data.getHandledChangesets().contains(changeset.getId());
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Sets and stores the global jira configuration.
   *
   *
   * @param configuration global jira configuration
   */
  public void setConfiguration(JiraGlobalConfiguration configuration)
  {
    logger.debug("store jira configuration");
    this.configuration = configuration;
    this.store.set(configuration);
  }

  //~--- get methods ----------------------------------------------------------

  private JiraData getData(Repository repository)
  {
    JiraData data = dataStore.get(repository.getId());

    if (data == null)
    {
      data = new JiraData();
    }

    return data;
  }

  //~--- fields ---------------------------------------------------------------

  /** global jira configuration */
  private JiraGlobalConfiguration configuration;

  /** data store */
  private final DataStore<JiraData> dataStore;

  /** global configuration store */
  private final Store<JiraGlobalConfiguration> store;
}
