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

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.io.Closeables;
import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.CommentTemplate;
import sonia.scm.jira.CommentTemplateHandler;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraConfigurationResolver;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.jira.JiraIssueHandler;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.jira.JiraIssueRequestFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PermissionType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.security.RepositoryPermission;
import sonia.scm.security.Role;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Class to resubmit all comments saved from the {@link MessageProblemHandler}.
 * These comments could not be sent due to network problems or other exceptions.
 */
public class ResubmitCommentsHandler
{

  /** Field description */
  private static final String ENV_AUTHOR = "author";

  /** Field description */
  private static final String ENV_CREATED = "created";

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(ResubmitCommentsHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new ResubmitCommentsHandler.
   *
   * @param requestFactory jira request factory
   * @param templateHandler template handler
   * @param messageProblemHandler message problem handler
   * @param context jira global context
   * @param repositoryManager repository manager
   * @param repositoryServiceFactory repository service factory
   */
  @Inject
  public ResubmitCommentsHandler(JiraIssueRequestFactory requestFactory,
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
   * Resends the comment with the given id back to the jira server.
   *
   *
   * @param commentId id of the comment
   *
   * @throws IOException
   * @throws RepositoryException
   */
  public void resubmit(String commentId) throws IOException, RepositoryException
  {
    CommentData commentData = messageProblemHandler.getComment(commentId);

    if (commentData != null)
    {
      //J-
      SecurityUtils.getSubject().checkPermission(
        new RepositoryPermission(commentData.getRepositoryId(), PermissionType.OWNER)
      );
      //J+
      resubmit(commentData);
    }
    else
    {
      // TODO custom exception type?
      throw new IllegalArgumentException("id does not exists");
    }
  }

  /**
   * Resend all comments to jira.
   *
   * @throws IOException
   * @throws RepositoryException
   */
  public void resubmitAll() throws IOException, RepositoryException
  {
    SecurityUtils.getSubject().checkRole(Role.ADMIN);

    resubmit(messageProblemHandler.getAllComments());
  }

  /**
   * Resends all comments which are associated with the given repository id back
   * to the jira server.
   *
   * @param repositoryId repository id
   *
   * @throws IOException
   * @throws RepositoryException
   */
  public void resubmitAllFromRepository(String repositoryId)
    throws IOException, RepositoryException
  {
    //J-
    SecurityUtils.getSubject().checkPermission(
      new RepositoryPermission(repositoryId, PermissionType.OWNER)
    );
    //J+

    resubmit(messageProblemHandler.getAllCommentsByRepository(repositoryId));
  }

  private String createContent(JiraIssueRequest request, Changeset changeset,
    CommentData commentData)
    throws IOException
  {
    Map<String, Object> env = templateHandler.createBaseEnvironment(request,
                                changeset);

    env.put(ENV_AUTHOR, commentData.getAuthor());
    env.put(ENV_CREATED, format(commentData.getCreated()));

    return templateHandler.render(CommentTemplate.RESEND, env);
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

  private String format(Calendar calendar)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    return sdf.format(calendar.getTime());
  }

  private void resubmit(List<CommentData> comments)
    throws IOException, RepositoryException
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
   * @throws RepositoryException
   */
  private void resubmit(CommentData commentData)
    throws IOException, RepositoryException
  {
    logger.info("resubmit comment with id {} for issue {}", commentData.getId(),
      commentData.getIssueId());

    // used stored configuration instead of global one
    JiraIssueRequest request = createRequest(commentData);
    Changeset changeset = getChangeset(request.getRepository(),
                            commentData.getChangesetId());

    // todo handle npe for changeset

    //J-
    try 
    { 
      String content = createContent(request, changeset, commentData);
      new JiraIssueHandler(
        messageProblemHandler, 
        templateHandler,
        request
      ).updateIssue(changeset, commentData.getIssueId(), content);
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
