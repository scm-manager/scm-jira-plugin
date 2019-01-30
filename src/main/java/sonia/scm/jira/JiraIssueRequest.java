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

import com.google.common.base.MoreObjects;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.user.User;

//~--- JDK imports ------------------------------------------------------------

import java.io.Closeable;
import java.io.IOException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

/**
 * The JiraIssueRequest contains all informations which are required to create a
 * connection to the jira server and is able to create {@link JiraHandler}.
 *
 * @author Sebastian Sdorra
 */
public class JiraIssueRequest implements Closeable
{

  /**
   * Constructs a new JiraIssueRequest.
   *
   * @param handlerFactory jira handler factory
   * @param committer optional user which has done the push/commit
   * @param configuration jira configuration
   * @param repository modified repository
   * @param creation creation time
   */
  public JiraIssueRequest(JiraHandlerFactory handlerFactory, Optional<User> committer,
    JiraConfiguration configuration, Repository repository,
    Changeset changeset, Calendar creation)
  {
    this.handlerFactory = handlerFactory;
    this.committer = committer;
    this.configuration = configuration;
    this.repository = repository;
    this.creation = creation;
    this.changeset = changeset;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException
  {
    if (handler != null)
    {
      try
      {
        handler.logout();
      }
      catch (JiraException ex)
      {
        throw new IOException("could not logout", ex);
      }
    }
  }

  /**
   * Creates a new {@link JiraHandler} for the configured jira server.
   *
   *
   * @return new {@link JiraHandler}
   *
   * @throws JiraConnectException
   */
  public JiraHandler createJiraHandler() throws JiraConnectException
  {
    if (handler == null)
    {
      handler = handlerFactory.createJiraHandler(this);
    }

    return handler;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("squid:S2068") // we have no password here
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("handlerFactory", handlerFactory.getClass())
                  .add("committer", committer)
                  .add("password", "xxx")
                  .add("configuration", configuration)
                  .add("repository", repository)
                  .add("creation", creation)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns jira configuration.
   *
   *
   * @return jira configuration
   */
  public JiraConfiguration getConfiguration()
  {
    return configuration;
  }

  /**
   * Returns the creation time. If the creation time is {@code null} the method
   * will create a new creation time on every call.
   *
   *
   * @return creation time
   */
  public Calendar getCreation()
  {
    return (creation != null)
      ? creation
      : new GregorianCalendar();
  }

  /**
   * Returns the changed repository.
   *
   *
   * @return changed repository
   */
  public Repository getRepository()
  {
    return repository;
  }

  public Changeset getChangeset()
  {
    return changeset;
  }

  public Optional<User> getCommitter() {
    return committer;
  }
//~--- fields ---------------------------------------------------------------

  /** the user which has done the push/commit */
  private final Optional<User> committer;

  /** jira configuration */
  private final JiraConfiguration configuration;

  /** creation time */
  private final Calendar creation;

  /** jira handler factory */
  private final JiraHandlerFactory handlerFactory;

  /** changed repository */
  private final Repository repository;

  private final Changeset changeset;

  /** jira handler */
  private JiraHandler handler;
}
