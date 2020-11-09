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

package sonia.scm.jira.rest;

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
import sonia.scm.net.ahc.ContentTransformerException;

import java.io.IOException;

/**
 * Implementation of the {@link JiraHandler} which uses the Jira Rest v2 protocol to
 * communicate with Jira.
 *
 * @author Sebastian Sdorra
 */
public class RestJiraHandler implements JiraHandler {

  /**
   * the logger for RestJiraHandler
   */
  private static final Logger logger = LoggerFactory.getLogger(RestJiraHandler.class);

  private final String baseUrl;
  private final AdvancedHttpClient client;
  private final String password;
  private final JiraIssueRequest request;
  private final String username;

  /**
   * Constructs a new {@link RestJiraHandler}.
   *
   * @param client   advanced http client
   * @param request  jira issue request
   * @param baseUrl  rest base url
   * @param username jira username
   * @param password jira password
   */
  public RestJiraHandler(AdvancedHttpClient client, JiraIssueRequest request, String baseUrl, String username,
                         String password) {
    this.client = client;
    this.request = request;
    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
  }

  @Override
  public void addComment(String issueId, Comment comment) throws JiraException {
    logger.info("add comment to issue {}", issueId);

    String body = Comments.prepareComment(request, issueId, comment);
    RestComment restComment = new RestComment(body, Strings.emptyToNull(comment.getRoleLevel()));

    try {
      //J-
      AdvancedHttpResponse response = client.post(baseUrl.concat(issueId).concat("/comment"))
        .spanKind("Jira")
        .basicAuth(username, password)
        .jsonContent(restComment)
        .request();
      if (!response.isSuccessful()) {
        throw new JiraException("failed to add comment to issue ".concat(issueId));
      } else {
        logger.debug("user {} successfully added comment to issue {}", username, issueId);
      }
      //J+
    } catch (IOException ex) {
      throw JiraExceptions.propagate(ex, "failed to add comment to issue ".concat(issueId));
    }
  }

  @Override
  public void close(String issueId, String autoCloseWord) throws JiraException {
    logger.info("try to close issue {}, with auto close word {}", issueId, autoCloseWord);

    String url = baseUrl.concat(issueId).concat("/transitions");

    try {
      //J-
      RestTransitions transitions = client.get(url)
        .spanKind("Jira")
        .basicAuth(username, password)
        .request()
        .contentFromJson(RestTransitions.class);
      //J+

      String id = null;

      String mappedAcw = request.getConfiguration().getMappedAutoCloseWord(autoCloseWord);

      for (RestTransition transition : transitions) {
        if (Compareables.contains(transition.getName(), mappedAcw) || transition.getId().equals(mappedAcw)) {
          id = transition.getId();
          break;
        }
      }

      if (!Strings.isNullOrEmpty(id)) {
        //J-
        AdvancedHttpResponse response = client.post(url)
          .spanKind("Jira")
          .basicAuth(username, password)
          .jsonContent(new RestDoTransition(id))
          .request();
        //J+

        if (!response.isSuccessful()) {
          throw new JiraException("failed to change transition on issue ".concat(issueId));
        } else {
          logger.debug("user {} successfully changed transition to issue {}", username, issueId);
        }

      } else {
        //J-
        throw new JiraException(
          String.format("could not find transition/close word %s on issue %s", autoCloseWord, issueId)
        );
        //J+
      }
    } catch (IOException ex) {
      throw JiraExceptions.propagate(ex, "failed to handle transitions from issue ".concat(issueId));
    }
  }

  @Override
  public void logout() throws JiraException {

    // we need no logout for rest api
  }

  @Override
  public boolean isCommentAlreadyExists(String issueId, String... contains) throws JiraException {
    boolean result = false;

    try {
      //J-
      RestComments comments = client.get(baseUrl.concat(issueId).concat("/comment"))
        .spanKind("Jira")
        .basicAuth(username, password)
        .request()
        .contentFromJson(RestComments.class);
      //J+

      for (RestComment comment : comments) {
        if (Compareables.contains(comment.getBody(), contains)) {
          result = true;

          break;
        }
      }
    } catch (ContentTransformerException ex) {
      throw JiraExceptions.propagate(ex, "failed to transform response from ".concat(issueId));
    } catch (IOException ex) {
      throw JiraExceptions.propagate(ex, "failed to get comments from issue ".concat(issueId));
    }

    return result;
  }
}
