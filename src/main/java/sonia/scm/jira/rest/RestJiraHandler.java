/**
 * Copyright (c) 2014, Sebastian Sdorra 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided with the distribution. 3. Neither the
 * name of SCM-Manager; nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira.rest;

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
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 * Implementation of the {@link JiraHandler} which uses the Jira Rest v2 protocol to
 * communicate with Jira.
 *
 * @author Sebastian Sdorra
 */
public class RestJiraHandler implements JiraHandler
{

  /**
   * the logger for RestJiraHandler
   */
  private static final Logger logger = LoggerFactory.getLogger(RestJiraHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new {@link RestJiraHandler}.
   *
   *
   * @param client advanced http client
   * @param request jira issue request
   * @param baseUrl rest base url
   * @param username jira username
   * @param password jira password
   */
  public RestJiraHandler(AdvancedHttpClient client, JiraIssueRequest request, String baseUrl, String username,
    String password)
  {
    this.client = client;
    this.request = request;
    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public void addComment(String issueId, Comment comment) throws JiraException
  {
    logger.info("add comment to issue {}", issueId);

    String body = Comments.prepareComment(request, issueId, comment);
    RestComment restComment = new RestComment(body, Strings.emptyToNull(comment.getRoleLevel()));

    try
    {
      //J-
      AdvancedHttpResponse response = client.post(baseUrl.concat(issueId).concat("/comment"))
                                            .basicAuth(username, password)
                                            .jsonContent(restComment)
                                            .request();
      if (!response.isSuccessful())
      {
        throw new JiraException("failed to add comment to issue ".concat(issueId));
      } 
      else 
      {
        logger.debug("user {} successfully added comment to issue {}", username, issueId);
      }
      //J+
    }
    catch (IOException ex)
    {
      throw JiraExceptions.propagate(ex, "failed to add comment to issue ".concat(issueId));
    }
  }

  @Override
  public void close(String issueId, String autoCloseWord) throws JiraException
  {
    logger.info("try to close issue {}, with auto close word {}", issueId, autoCloseWord);

    String url = baseUrl.concat(issueId).concat("/transitions");

    try
    {
      //J-
      RestTransitions transitions = client.get(url)
                                          .basicAuth(username, password)
                                          .request()
                                          .contentFromJson(RestTransitions.class);
      
      String id = null;
      
      for (RestTransition transition : transitions) 
      {
        if (Compareables.contains(transition.getName(), autoCloseWord))
        {
          id = transition.getId();
          break;
        }
      }
      
      if (!Strings.isNullOrEmpty(id)){
        AdvancedHttpResponse response = client.post(url)
                                              .basicAuth(username, password)
                                              .jsonContent(new RestDoTransition(id))
                                              .request();
        
        if (!response.isSuccessful())
        {
          throw new JiraException("failed to change transition on issue ".concat(issueId));
        } 
        else 
        {
          logger.debug("user {} successfully changed transition to issue {}", username, issueId);
        }
        
      } 
      else 
      {
        // could not find transition, throw exception?
      }
      //J+
    }
    catch (IOException ex)
    {
      throw JiraExceptions.propagate(ex, "failed to handle transitions from issue ".concat(issueId));
    }
  }

  @Override
  public void logout() throws JiraException
  {

    // we need no logout
  }

  //~--- get methods ----------------------------------------------------------

  @Override
  public boolean isCommentAlreadyExists(String issueId, String... contains) throws JiraException
  {
    boolean result = false;

    try
    {
      //J-
      RestComments comments = client.get(baseUrl.concat(issueId).concat("/comment"))
                                    .basicAuth(username, password)
                                    .request()
                                    .contentFromJson(RestComments.class);
      
      for (RestComment comment : comments) 
      {
        if (Compareables.contains(comment.getBody(), contains))
        {
          result = true;

          break;
        }
      }
      //J+
    }
    catch (IOException ex)
    {
      throw JiraExceptions.propagate(ex, "failed to get comments from issue ".concat(issueId));
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** jira rest base url */
  private final String baseUrl;

  /** advanced http client */
  private final AdvancedHttpClient client;

  /** jira password */
  private final String password;

  /** jira issue request */
  private final JiraIssueRequest request;

  /** jira username */
  private final String username;
}
