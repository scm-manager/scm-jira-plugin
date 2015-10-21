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



package sonia.scm.jira.soap;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.Comment;
import sonia.scm.jira.Comments;
import sonia.scm.jira.Compareables;
import sonia.scm.jira.JiraException;
import sonia.scm.jira.JiraExceptions;
import sonia.scm.jira.JiraHandler;
import sonia.scm.jira.JiraIssueRequest;

//~--- JDK imports ------------------------------------------------------------

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
  public SoapJiraHandler(JiraSoapService service, JiraIssueRequest request, String token, String username)
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

      for (RemoteNamedObject rnm : rnms)
      {
        if (Compareables.contains(rnm.getName(), autoCloseWord))
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
  private final JiraIssueRequest request;

  /** jira soap service */
  private final JiraSoapService service;

  /** authentication token */
  private final String token;

  /** connection username */
  private final String username;
}
