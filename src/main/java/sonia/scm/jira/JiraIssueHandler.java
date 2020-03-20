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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.repository.Changeset;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Map;

/**
 * The JiraIssueHandler updates or closes the jira issues based on the
 * {@link Changeset} description.
 *
 * @author Sebastian Sdorra
 */
public class JiraIssueHandler implements CommentHandler
{

  /** env variable auto close word */
  private static final String ENV_AUTOCLOSEWORD = "autoCloseWord";

  /** the logger for JiraIssueHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraIssueHandler.class);

  //~--- constructors ---------------------------------------------------------

  public JiraIssueHandler(MessageProblemHandler problemHandler,
                          CommentTemplateHandlerFactory commentTemplateHandlerFactory, JiraIssueRequest request)
  {
    this.problemHandler = problemHandler;
    this.updateTemplateHandler = commentTemplateHandlerFactory.create(CommentTemplate.UPADTE);
    this.autoCloseTemplateHandler = commentTemplateHandlerFactory.create(CommentTemplate.AUTOCLOSE);
    this.request = request;
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public void close() {
    // nothing to do here
  }

  /**
   * Updates or closes the jira issue with the given id.
   *
   *
   * @param issueId jira issue id
   */
  @Override
  public void commentIssue(String issueId)
  {
    Changeset changeset = request.getChangeset();
    if (request.getConfiguration().isAutoCloseEnabled())
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("check changeset {} for auto-close of issue {}",
          changeset.getId(), issueId);
      }

      String autoCloseWord = searchAutoCloseWord(changeset);

      if (autoCloseWord != null)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("found auto close word {} for issue {}", autoCloseWord,
            issueId);
        }

        closeIssue(changeset, issueId, autoCloseWord);
      }
      else
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("found no auto close word");
        }

        updateIssue(changeset, issueId);
      }
    }
    else
    {
      updateIssue(changeset, issueId);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("request", request)
                  .add("problemHandler", updateTemplateHandler)
                  .toString();
    //J+
  }

  /**
   * Updates the jira issue with the given comment.
   *
   *
   * @param changeset changeset
   * @param issueId issue id
   * @param comment comment content
   */
  public void updateIssue(Changeset changeset, String issueId, String comment)
  {
    logger.debug("try to update issue {} because of changeset {}", issueId,
      changeset.getId());

    try
    {

      JiraHandler handler = request.createJiraHandler();

      if (!handler.isCommentAlreadyExists(issueId, changeset.getId(),
        changeset.getDescription()))
      {

        //J-
        handler.addComment(
          issueId, 
          Comments.createComment(request, comment)
        );
        //J+

      }
      else if (logger.isInfoEnabled())
      {
        logger.info("comment for changeset {} already exists at issue {}",
          changeset.getId(), issueId);
      }

    }
    catch (JiraException ex)
    {
      logger.error("could not add comment to jira issue", ex);
      handleException(issueId, comment, changeset);
    }
  }

  private void closeIssue(Changeset changeset, String issueId,
    String autoCloseWord)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("try to close issue {} because of changeset {}", issueId,
        changeset.getId());
    }

    String comment = null;

    try
    {
      JiraHandler handler = request.createJiraHandler();
      Map<String, Object> env = updateTemplateHandler.createBaseEnvironment(request,
                                  changeset);

      env.put(ENV_AUTOCLOSEWORD, Strings.nullToEmpty(autoCloseWord));

      comment = autoCloseTemplateHandler.render(env);

      handler.close(issueId, autoCloseWord);
      //J-
      handler.addComment(
        issueId, 
        Comments.createComment(request, comment)
      );
      //J+
    }
    catch (IOException ex)
    {

      // TODO use problem handler
      logger.error("could not render template", ex);
    }
    catch (JiraException ex)
    {
      logger.error("could not close jira issue", ex);
      handleException(issueId, comment, changeset);
    }
  }

  private void handleException(String issueId, String comment,
    Changeset changeset)
  {
    if (request.getConfiguration().isResubmission())
    {
      logger.trace(
        "call problem handler to store comment and send notification");

      try
      {
        storeCommentAndSentNotification(issueId, comment, changeset);
      }
      catch (IOException e)
      {

        // do something useful
        throw Throwables.propagate(e);
      }
    }
    else
    {
      logger.trace("resubmission is disabled");
    }
  }
  
  @VisibleForTesting
  String searchAutoCloseWord(Changeset changeset)
  {
    String autoCloseWord = null;
    String description = changeset.getDescription();
    for (String acw : request.getConfiguration().getAutoCloseWords())
    {
      if ( AutoCloseWords.find(description, acw) ){
          autoCloseWord = acw;
          break;
      }
    }

    return autoCloseWord;
  }

  private void storeCommentAndSentNotification(String issueId, String comment,
    Changeset changeset)
    throws IOException
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(issueId),
      "issue id is null or empty");
    Preconditions.checkNotNull(changeset, "changeset is null");

    JiraConfiguration configuration = request.getConfiguration();

    if (Strings.isNullOrEmpty(comment))
    {
      Map<String, Object> env = updateTemplateHandler.createBaseEnvironment(request,
                                  changeset);

      // use always update?
      comment = updateTemplateHandler.render(env);
    }

    //J-
    String body = Comments.prepareComment(
      configuration.getUrl(), issueId, Comments.createComment(request, comment)
    );
    //J+

    // Send mail and save comment information
    problemHandler.handleMessageProblem(request, issueId, body, changeset);
  }

  private void updateIssue(Changeset changeset, String issueId)
  {
    try
    {

      Map<String, Object> env = updateTemplateHandler.createBaseEnvironment(request,
                                  changeset);

      String comment = updateTemplateHandler.render(env);

      updateIssue(changeset, issueId, comment);
    }
    catch (IOException ex)
    {

      // TODO Case of rendering mistake (IO)
      logger.error("could render not template", ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** message problem handler */
  private final MessageProblemHandler problemHandler;

  /** jira issue request */
  private final JiraIssueRequest request;

  /** comment template handler */
  private final CommentTemplateHandler updateTemplateHandler;
  private final CommentTemplateHandler autoCloseTemplateHandler;
}
