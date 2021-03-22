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

package sonia.scm.jira.soap;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.Comment;
import sonia.scm.jira.Comments;
import sonia.scm.jira.Compareables;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraException;
import sonia.scm.jira.JiraExceptions;
import sonia.scm.jira.JiraHandler;

import java.rmi.RemoteException;

/**
 * Implementation of the {@link JiraHandler} which uses the SOAP protocol to
 * communicate with Jira.
 *
 * @author Sebastian Sdorra
 */
public class SoapJiraHandler implements JiraHandler
{

  /** id for the default close action */
  public static final String ACTION_DEFAULT_CLOSE = "2";

  /** the logger for SoapJiraHandler */
  private static final Logger logger = LoggerFactory.getLogger(SoapJiraHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new SoapJiraHandler.
   *
   *
   * @param service jira soap service
   * @param request jira issure request
   * @param token authentication token
   * @param username connection username
   */
  public SoapJiraHandler(JiraSoapService service, JiraConfiguration request, String token, String username)
  {
    this.service = service;
    this.request = request;
    this.token = token;
    this.username = username;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public void addComment(String issueId, Comment comment) throws JiraException
  {
    if (logger.isInfoEnabled())
    {
      logger.info("add comment to issue {}", issueId);
    }

    RemoteComment remoteComment = new RemoteComment();

    remoteComment.setAuthor(username);
    remoteComment.setCreated(comment.getCreated());
    remoteComment.setBody(Comments.prepareComment(request, issueId, comment));

    if (!Strings.isNullOrEmpty(comment.getBody()))
    {
      remoteComment.setRoleLevel(comment.getRoleLevel());
    }

    try
    {
      service.addComment(token, issueId, remoteComment);
    }
    catch (RemoteException ex)
    {
      throw JiraExceptions.propagate(ex, "Failed to add comment to issue ".concat(issueId));
    }
  }

  /**
   * {@inheritDoc}
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

      String mappedAcw = request.getMappedAutoCloseWord(autoCloseWord);

      for (RemoteNamedObject rnm : rnms)
      {
        if (Compareables.contains(rnm.getName(), mappedAcw) || rnm.getId().equals(mappedAcw))
        {
          id = rnm.getId();

          break;
        }
      }

      service.progressWorkflowAction(token, issueId, id, new RemoteFieldValue[] {});
    }
    catch (RemoteException ex)
    {
      throw JiraExceptions.propagate(ex, "Failed to close issue ".concat(issueId));
    }
  }

  /**
   * {@inheritDoc}
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
      throw JiraExceptions.propagate(ex, "Failed to logout");
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCommentAlreadyExists(String issueId, String... contains) throws JiraException
  {
    boolean result = false;

    try
    {
      RemoteComment[] comments = service.getComments(token, issueId);

      for (RemoteComment comment : comments)
      {
        if (Compareables.contains(comment.getBody(), contains))
        {
          result = true;

          break;
        }
      }
    }
    catch (RemoteException ex)
    {
      throw JiraExceptions.propagate(ex, "could not check for jira comment at issue ".concat(issueId));
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** jira issue request */
  private final JiraConfiguration request;

  /** jira soap service */
  private final JiraSoapService service;

  /** authentication token */
  private final String token;

  /** connection username */
  private final String username;
}
