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

//~--- JDK imports ------------------------------------------------------------

import java.io.Closeable;
import java.io.IOException;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
   * @param username connection username
   * @param password connection password
   * @param configuration jira configuration
   * @param repository modified repository
   * @param author name of user which has done the push/commit
   * @param creation creation time
   */
  public JiraIssueRequest(JiraHandlerFactory handlerFactory, String username,
    String password, JiraConfiguration configuration, Repository repository,
    Changeset changeset, String author, Calendar creation)
  {
    this.handlerFactory = handlerFactory;
    this.username = username;
    this.password = password;
    this.configuration = configuration;
    this.repository = repository;
    this.author = author;
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
      handler = handlerFactory.createJiraHandler(this, username, password);
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
                  .add("username", username)
                  .add("password", "xxx")
                  .add("configuration", configuration)
                  .add("repository", repository)
                  .add("author", author)
                  .add("creation", creation)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns name of the user which has done the push/commit, if the author is 
   * {@code null} the method will return the username.
   *
   *
   * @return author name
   */
  public String getAuthor()
  {
    return MoreObjects.firstNonNull(author, username);
  }

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
   * Returns the password which is used for the connection.
   *
   *
   * @return connection password
   */
  public String getPassword()
  {
    return password;
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

  /**
   * Returns the username which is used for the connection.
   *
   *
   * @return connection username
   */
  public String getUsername()
  {
    return username;
  }

  //~--- fields ---------------------------------------------------------------

  /** the user which has done the push/commit */
  private final String author;

  /** jira configuration */
  private final JiraConfiguration configuration;

  /** creation time */
  private final Calendar creation;

  /** jira handler factory */
  private final JiraHandlerFactory handlerFactory;

  /** connection password */
  private final String password;

  /** changed repository */
  private final Repository repository;

  private final Changeset changeset;

  /** connection username */
  private final String username;

  /** jira handler */
  private JiraHandler handler;
}
