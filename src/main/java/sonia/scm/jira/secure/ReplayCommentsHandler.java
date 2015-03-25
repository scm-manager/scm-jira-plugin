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



package sonia.scm.jira.secure;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.CommentTemplateHandler;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraConfigurationResolver;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.jira.JiraIssueHandler;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.jira.JiraIssueRequestFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 * Class to replay all comments saved from the {@link MessageProblemHandler}.
 * These comments could not be sent due to network problems or other exceptions.
 */
public class ReplayCommentsHandler
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(ReplayCommentsHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new ReplayCommentsHandler.
   *
   * @param requestFactory jira request factory
   * @param templateHandler template handler
   * @param messageProblemHandler message problem handler
   * @param context
   * @param repositoryManager
   * @param repositoryServiceFactory
   */
  @Inject
  public ReplayCommentsHandler(JiraIssueRequestFactory requestFactory,
    CommentTemplateHandler templateHandler,
    MessageProblemHandler messageProblemHandler, JiraGlobalContext context,
    RepositoryManager repositoryManager,
    RepositoryServiceFactory repositoryServiceFactory)
  {
    this.requestFactory = requestFactory;
    this.templateHandler = templateHandler;
    this.messageProblemHandler = messageProblemHandler;
    this.context = context;
    this.repositoryManager = repositoryManager;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Resend comment with the given id to jira.
   *
   *
   * @param id comment id
   *
   * @throws IOException
   * @throws RepositoryException
   */
  public void replay(String id) throws IOException, RepositoryException
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(id),
      "id is null or empty");

    CommentData commentData = messageProblemHandler.getComment(id);

    if (commentData == null)
    {

      // TODO custom exception type?
      throw new IllegalArgumentException("id does not exists");
    }

    replayComment(commentData);
  }

  /**
   * Resend all comments to jira.
   *
   * @throws IOException
   * @throws RepositoryException
   */
  public void replayAll() throws IOException, RepositoryException
  {

    // send comments to jira
    for (CommentData commentData : messageProblemHandler.getAllComments())
    {
      replayComment(commentData);
    }
  }

  /**
   * Creates a request from the given comment data.
   *
   *
   * @param commentData comment data
   *
   * @return jira issue request
   */
  private JiraIssueRequest createRequest(CommentData commentData)
  {
    Repository repository =
      repositoryManager.get(commentData.getRepositoryId());

    // todo handle npe for repository

    JiraConfiguration cfg = JiraConfigurationResolver.resolve(context,
                              repository);

    // todo handle npe for changeset
    return requestFactory.createRequest(cfg, repository,
      commentData.getCreated());
  }

  /**
   * Send the given comment to jira and delete it from the store.
   *
   * @param commentData The data to use for resent.
   *
   * @throws IOException
   * @throws RepositoryException
   */
  private void replayComment(CommentData commentData)
    throws IOException, RepositoryException
  {
    logger.info("replay comment with id {} for issue {}", commentData.getId(),
      commentData.getIssueId());

    // used stored configuration instead of global one
    JiraIssueRequest request = createRequest(commentData);
    Changeset changeset = getChangeset(request.getRepository(),
                            commentData.getChangesetId());

    // todo handle npe for changeset

    //J-
    try 
    {    
      new JiraIssueHandler(
        messageProblemHandler, 
        templateHandler,
        request
      ).handleIssue(commentData.getIssueId(), changeset);
    } 
    finally 
    {
      // delete the comment
      messageProblemHandler.deleteComment(commentData.getId());
    }
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the {@link Changeset} with the given id.
   *
   *
   * @param repository repository
   * @param changesetId changeset id
   *
   * @return {@link Changeset} with the given id
   *
   * @throws IOException
   * @throws RepositoryException
   */
  private Changeset getChangeset(Repository repository, String changesetId)
    throws IOException, RepositoryException
  {
    Changeset changeset = null;
    RepositoryService service = null;

    try
    {
      service = repositoryServiceFactory.create(repository);
      changeset = service.getLogCommand().getChangeset(changesetId);
    }
    finally
    {
      Closeables.close(service, true);
    }

    return changeset;
  }

  //~--- fields ---------------------------------------------------------------

  /** global jira context */
  private final JiraGlobalContext context;

  /** message problem handler */
  private final MessageProblemHandler messageProblemHandler;

  /** repository manager */
  private final RepositoryManager repositoryManager;

  /** repository service factory */
  private final RepositoryServiceFactory repositoryServiceFactory;

  /** request factory */
  private final JiraIssueRequestFactory requestFactory;

  /** template handler */
  private final CommentTemplateHandler templateHandler;
}
