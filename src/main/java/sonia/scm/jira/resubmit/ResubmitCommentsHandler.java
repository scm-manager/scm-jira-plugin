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



package sonia.scm.jira.resubmit;

import com.google.common.io.Closeables;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.CommentTemplate;
import sonia.scm.jira.CommentTemplateHandler;
import sonia.scm.jira.CommentTemplateHandlerFactory;
import sonia.scm.jira.Comments;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraConfigurationResolver;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.jira.JiraIssueHandler;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.jira.JiraIssueRequestFactory;
import sonia.scm.jira.JiraPermissions;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

/**
 * Class to resubmit all comments saved from the {@link MessageProblemHandler}.
 * These comments could not be sent due to network problems or other exceptions.
 */
public class ResubmitCommentsHandler
{

  private static final String ENV_CREATED = "created";

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(ResubmitCommentsHandler.class);

  //~--- constructors ---------------------------------------------------------

  @Inject
  public ResubmitCommentsHandler(JiraIssueRequestFactory requestFactory,
    CommentTemplateHandlerFactory commentTemplateHandlerFactory,
    MessageProblemHandler messageProblemHandler, JiraGlobalContext context,
    RepositoryManager repositoryManager,
    RepositoryServiceFactory repositoryServiceFactory)
  {
    this.requestFactory = requestFactory;
    this.commentTemplateHandlerFactory = commentTemplateHandlerFactory;
    this.messageProblemHandler = messageProblemHandler;
    this.context = context;
    this.repositoryManager = repositoryManager;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Resends the comment with the given id back to the jira server.
   *
   *
   * @param commentId id of the comment
   *
   * @throws IOException
   */
  public void resubmit(String commentId) throws IOException
  {
    resubmit(getCommentChecked(commentId));
  }

  private CommentData getCommentChecked(String commentId)
  {
    CommentData commentData = messageProblemHandler.getComment(commentId);

    if (commentData != null)
    {
      new JiraPermissions().checkWriteRepositoryConfig(commentData.getRepositoryId());
      return commentData;
    }
    else
    {
      throw notFound(entity("jira comment", commentId));
    }
  }

  /**
   * Resend all comments to jira.
   *
   * @throws IOException
   */
  public void resubmitAll() throws IOException
  {
    new JiraPermissions().checkWriteGlobalConfig();

    resubmit(messageProblemHandler.getAllComments());
  }

  /**
   * Resends all comments which are associated with the given repository id back
   * to the jira server.
   *
   * @param repositoryId repository id
   *
   * @throws IOException
   */
  public void resubmitAllFromRepository(String repositoryId)
    throws IOException
  {
    new JiraPermissions().checkWriteRepositoryConfig(repositoryId);

    resubmit(messageProblemHandler.getAllCommentsByRepository(repositoryId));
  }

  public void removeComment(String commentId) {
    new JiraPermissions().checkWriteGlobalConfig();
    messageProblemHandler.deleteComment(commentId);
  }

  private String createContent(JiraIssueRequest request, Changeset changeset,
    CommentData commentData)
    throws IOException
  {
    CommentTemplateHandler resendTemplateHandler = commentTemplateHandlerFactory.create(CommentTemplate.RESEND);
    Map<String, Object> env = resendTemplateHandler.createBaseEnvironment(request,
                                changeset);

    env.put(ENV_CREATED, Comments.format(commentData.getCreated()));

    return resendTemplateHandler.render(env);
  }

  /**
   * Creates a request from the given comment data.
   *
   *
   * @param commentData comment data
   *
   * @return jira issue request
   */
  private JiraIssueRequest createRequest(CommentData commentData) throws IOException {
    Repository repository =
      repositoryManager.get(commentData.getRepositoryId());

    // todo handle npe for repository

    JiraConfiguration cfg = JiraConfigurationResolver.resolve(context,
                              repository);

    Changeset changeset = getChangeset(repository,
                            commentData.getChangesetId());
    // todo handle npe for changeset
    return requestFactory.createRequest(cfg, repository, changeset,
      Optional.ofNullable(commentData.getCommitter()), commentData.getCreated());
  }

  private void resubmit(List<CommentData> comments)
    throws IOException
  {
    logger.trace("resubmit {} comments", comments.size());

    for (CommentData comment : comments)
    {
      resubmit(comment);
    }
  }

  /**
   * Send the given comment to jira and delete it from the store.
   *
   * @param commentData The data to use for resent.
   *
   * @throws IOException
   */
  private void resubmit(CommentData commentData)
    throws IOException
  {
    logger.info("resubmit comment with id {} for issue {}", commentData.getId(),
      commentData.getIssueId());

    // used stored configuration instead of global one
    JiraIssueRequest request = createRequest(commentData);
    Changeset changeset = request.getChangeset();

    // todo handle npe for changeset

    String content = createContent(request, changeset, commentData);
    //J-
    try (
      JiraIssueHandler jiraIssueHandler = new JiraIssueHandler(
        messageProblemHandler,
        commentTemplateHandlerFactory,
        request
      ))
    {
      jiraIssueHandler.updateIssue(changeset, commentData.getIssueId(), content);
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
   */
  private Changeset getChangeset(Repository repository, String changesetId)
    throws IOException
  {
    Changeset changeset;
    RepositoryService service = null;

    try
    {
      service = repositoryServiceFactory.create(repository);
      changeset = service.getLogCommand()
                         .setDisablePreProcessors(true)
                         .getChangeset(changesetId);
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
  private final CommentTemplateHandlerFactory commentTemplateHandlerFactory;
}
