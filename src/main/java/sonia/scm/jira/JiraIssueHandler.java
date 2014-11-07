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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.secure.MessageProblemHandler;
import sonia.scm.repository.Changeset;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.GregorianCalendar;

/**
 *
 * @author Sebastian Sdorra
 */
public class JiraIssueHandler
{

  /** the logger for JiraIssueHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraIssueHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param problemHandler
   * @param templateHandler
   * @param request
   */
  public JiraIssueHandler(MessageProblemHandler problemHandler,
    CommentTemplateHandler templateHandler, JiraIssueRequest request)
  {
    this.problemHandler = problemHandler;
    this.templateHandler = templateHandler;
    this.request = request;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param changeset
   */
  public void handleIssue(String issueId, Changeset changeset)
  {
    if (request.getConfiguration().isAutoCloseEnabled())
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("check changeset {} for auto-close of issue",
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
   * Method description
   *
   *
   * @return
   */
  @Override
  public String toString()
  {
    //J-
    return Objects.toStringHelper(this)
                  .add("request", request)
                  .add("problemHandler", templateHandler)
                  .toString();
    //J+
  }

  /**
   * Method description
   *
   *
   * @param changeset
   * @param issueId
   * @param autoCloseWord
   */
  private void closeIssue(Changeset changeset, String issueId,
    String autoCloseWord)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("try to close issue {} because of changeset {}", issueId,
        changeset.getId());
    }

    try
    {
      JiraHandler handler = request.createJiraHandler();
      String comment = templateHandler.render(CommentTemplate.AUTOCLOSE,
                         request, changeset, autoCloseWord);

      handler.close(issueId, autoCloseWord);
      handler.addComment(issueId, createComment(comment), request);
    }
    catch (IOException ex)
    {

      // TODO use problem handler
      logger.error("could not render template", ex);
    }
    catch (JiraException ex)
    {

      // TODO use problem handler
      logger.error("could not close jira issue", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param body
   *
   * @return
   */
  private Comment createComment(String body)
  {
    String prefix = request.getConfiguration().getCommentPrefix();

    if (!Strings.isNullOrEmpty(prefix))
    {
      prefix = prefix.concat(" ");
    }

    //J-
    return new Comment(
      Strings.nullToEmpty(prefix).concat(Strings.nullToEmpty(body)),
      request.getConfiguration().getRoleLevel()
    );
    //J+
  }

  /**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   * @param changeset
   *
   * @throws IOException
   */
  private void handleException(String issueId, String comment,
    Changeset changeset)
    throws IOException
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(issueId),
      "issue id is null");

    /*
     * Preconditions.checkArgument(!Strings.isNullOrEmpty(comment),
     * "comment is null");
     */
    Preconditions.checkNotNull(changeset, "changeset is null");

    JiraConfiguration configuration = request.getConfiguration();

    if (comment == null)
    {
      comment = templateHandler.render(CommentTemplate.UPADTE, request,
        changeset);
    }

    CommentPreparation commentPreparation =
      new CommentPreparation(configuration.getUrl());
    String body = commentPreparation.prepareComment(issueId,
                    createComment(comment));

    // Send mail and save comment information
    //J-
    problemHandler.handleMessageProblem(
      configuration,
      issueId, 
      request.getUsername(), 
      body, 
      new GregorianCalendar(),
      changeset, 
      request.getRepository()
    );
    //J+
  }

  /**
   * Method description
   *
   *
   * @param changeset
   *
   * @return
   */
  private String searchAutoCloseWord(Changeset changeset)
  {
    String description = changeset.getDescription();
    String autoCloseWord = null;
    String[] words = description.split("\\s");

    for (String w : words)
    {
      for (String acw : request.getConfiguration().getAutoCloseWords())
      {
        acw = acw.trim();

        if (w.equalsIgnoreCase(acw))
        {
          autoCloseWord = w;

          break;
        }
      }

      if (autoCloseWord != null)
      {
        break;
      }
    }

    return autoCloseWord;
  }

  /**
   * Method description
   *
   *
   * @param changeset
   * @param issueId
   */
  private void updateIssue(Changeset changeset, String issueId)
  {
    logger.debug("try to update issue {} because of changeset {}", issueId,
      changeset.getId());

    String comment = null;

    try
    {
      JiraHandler handler = request.createJiraHandler();

      if (!handler.isCommentAlreadyExists(issueId, changeset.getId(),
        changeset.getDescription()))
      {
        comment = templateHandler.render(CommentTemplate.UPADTE, request,
          changeset);

        handler.addComment(issueId, createComment(comment), request);
      }
      else if (logger.isInfoEnabled())
      {
        logger.info("comment for changeset {} already exists at issue {}",
          changeset.getId(), issueId);
      }
    }
    catch (IOException ex)
    {
      logger.error("could render template", ex);
    }
    catch (JiraException ex)
    {

      // TODO: Save comment in case of login-error or existence check
      // Possibly remove saving from soap jira handler

      // TODO Case of rendering mistake (IO)
      logger.error("could not add comment to jira issue", ex);

      try
      {
        handleException(issueId, comment, changeset);
      }
      catch (IOException e)
      {

        // do something useful
        throw Throwables.propagate(e);
      }

    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final MessageProblemHandler problemHandler;

  /** Field description */
  private final JiraIssueRequest request;

  /** Field description */
  private final CommentTemplateHandler templateHandler;
}
