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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.secure.MessageProblemHandler;
import sonia.scm.jira.soap.JiraSoapService;
import sonia.scm.jira.soap.RemoteComment;
import sonia.scm.jira.soap.RemoteFieldValue;
import sonia.scm.jira.soap.RemoteNamedObject;
import sonia.scm.repository.EscapeUtil;

//~--- JDK imports ------------------------------------------------------------


import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 *
 * @author Sebastian Sdorra
 */
public class SoapJiraHandler implements JiraHandler
{

  /** Field description */
  public static final String ACTION_DEFAULT_CLOSE = "2";

  /** the logger for SoapJiraHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(SoapJiraHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param service
   * @param jiraUrl
   * @param token
   * @param username
   */
  public SoapJiraHandler(JiraSoapService service, String jiraUrl, String token,
    String username)
  {
    this.service = service;
    this.jiraUrl = jiraUrl;
    this.token = token;
    this.username = username;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   *
   * @throws JiraException
   */
  @Override
  public void addComment(String issueId, Comment comment, JiraIssueRequest request) throws JiraException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("add comment to issue {}", issueId);
    }

    RemoteComment remoteComment = new RemoteComment();

    remoteComment.setAuthor(username);
    remoteComment.setCreated(new GregorianCalendar());
    remoteComment.setBody(prepareComment(issueId, comment));

    if (!Strings.isNullOrEmpty(comment.getBody()))
    {
      remoteComment.setRoleLevel(comment.getRoleLevel());
    }

    try
    {
      service.addComment(token, issueId, remoteComment);
    }
    catch (Exception ex)
    {
        // Send mail and save comment information
        String mailAddress = request.getConfiguration().getMailAddress();
        String mailHost = request.getConfiguration().getMailHost();
        String jiraUrl = request.getConfiguration().getUrl();
        MessageProblemHandler messageProblemHandler = new MessageProblemHandler(mailAddress, mailHost);
        messageProblemHandler.handleMessageProblem(token, issueId, remoteComment, jiraUrl);
        
      throw new JiraException("add comment failed", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param issueId
   * @param autoCloseWord
   *
   * @throws JiraException
   */
  @Override
  public void close(String issueId, String autoCloseWord) throws JiraException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("try to close issue {}", issueId);
    }

    try
    {
      RemoteNamedObject[] rnms = service.getAvailableActions(token, issueId);
      String id = ACTION_DEFAULT_CLOSE;

      for (RemoteNamedObject rnm : rnms)
      {
        if (contains(rnm.getName(), autoCloseWord))
        {
          id = rnm.getId();

          break;
        }
      }

      service.progressWorkflowAction(token, issueId, id,
        new RemoteFieldValue[] {});
    }
    catch (Exception ex)
    {
      throw new JiraException("close issue failed", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @throws JiraException
   */
  @Override
  public void logout() throws JiraException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("logout from jira");
    }

    try
    {
      service.logout(token);
    }
    catch (RemoteException ex)
    {
      throw new JiraException("logout failed", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   *
   * @return
   */
  public String prepareComment(String issueId, Comment comment)
  {
    String body = Strings.nullToEmpty(comment.getBody());

    return removeIssueLink(issueId, body);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param contains
   *
   * @return
   *
   * @throws JiraException
   */
  @Override
  public boolean isCommentAlreadyExists(String issueId, String... contains)
    throws JiraException
  {
    boolean result = false;

    try
    {
      RemoteComment[] comments = service.getComments(token, issueId);

      for (RemoteComment comment : comments)
      {
        if (contains(comment, contains))
        {
          result = true;

          break;
        }
      }
    }
    catch (Exception ex)
    {
      throw new JiraException("could not check for jira comment", ex);
    }

    return result;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param text
   * @param value
   *
   * @return
   */
  private boolean contains(String text, String value)
  {
    return toLowerCase(text).contains(toLowerCase(value));
  }

  /**
   * Method description
   *
   *
   * @param comment
   * @param contains
   *
   * @return
   */
  @VisibleForTesting
  boolean contains(RemoteComment comment, String... contains)
  {
    boolean result = false;
    String body = comment.getBody();

    if (!Strings.isNullOrEmpty(body))
    {
      result = true;
      for (String c : contains)
      {
        if (!body.contains(c))
        {
          result = false;

          break;

        }

      }
    }

    return result;
  }

  /**
   * Remove issue self reference link.
   * {@see https://bitbucket.org/sdorra/scm-manager/issue/337/jira-comment-contains-unneccessary-link}.
   *
   * TODO: The preprocessor order on hooks should be fixed in the core.
   *
   *
   * @param issueId
   * @param body
   *
   * @return
   */
  private String removeIssueLink(String issueId, String body)
  {
    //J-
    String link = MessageFormat.format(
      JiraChangesetPreProcessorFactory.REPLACEMENT_LINK,
      jiraUrl
    ).replaceAll(Matcher.quoteReplacement("$0"), issueId);
    //J+

    body = body.replaceAll(link, issueId);
    body = body.replaceAll(EscapeUtil.escape(link), issueId);

    return body;
  }

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   */
  private String toLowerCase(String value)
  {
    return Strings.nullToEmpty(value).toLowerCase(Locale.ENGLISH);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String jiraUrl;

  /** Field description */
  private JiraSoapService service;

  /** Field description */
  private String token;

  /** Field description */
  private String username;
}
