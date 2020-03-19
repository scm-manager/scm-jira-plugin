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
