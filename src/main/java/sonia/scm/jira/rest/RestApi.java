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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.JiraException;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;
import sonia.scm.util.HttpUtil;

import java.io.IOException;
import java.util.Collection;

public class RestApi {

  private static final Logger LOG = LoggerFactory.getLogger(RestApi.class);

  private final AdvancedHttpClient client;
  private final JiraConfiguration configuration;
  private final String baseUrl;

  public RestApi(AdvancedHttpClient client, JiraConfiguration configuration) {
    this.client = client;
    this.configuration = configuration;
    this.baseUrl = createBaseUrl(configuration);
  }

  private String createBaseUrl(JiraConfiguration configuration) {
    return HttpUtil.concatenate(configuration.getUrl(), "rest", "api", "2", "issue");
  }

  public void addComment(String issueId, RestComment comment) throws IOException {
    LOG.info("add comment to issue {}", issueId);
    AdvancedHttpResponse response = client.post(commentUrl(issueId))
      .spanKind("Jira")
      .basicAuth(configuration.getUsername(), configuration.getPassword())
      .jsonContent(comment)
      .request();

    if (!response.isSuccessful()) {
      fail("failed to add comment to %s", issueId, response);
    } else {
      LOG.debug("successfully added comment to issue {}", issueId);
    }
  }

  public Collection<RestTransition> getTransitions(String issueId) throws IOException {
    AdvancedHttpResponse response = client.get(transitionsUrl(issueId))
      .spanKind("Jira")
      .basicAuth(configuration.getUsername(), configuration.getPassword())
      .request();

    if (!response.isSuccessful()) {
      fail("failed to retrieve transitions from %s", issueId, response);
    }

    return response.contentFromJson(RestTransitions.class)
      .getTransitions();
  }

  private String commentUrl(String issueId) {
    return HttpUtil.concatenate(baseUrl, issueId, "comment");
  }

  private String transitionsUrl(String issueId) {
    return HttpUtil.concatenate(baseUrl, issueId, "transitions");
  }

  public void changeState(String issueId, String transitionId) throws IOException {
    AdvancedHttpResponse response = client.post(transitionsUrl(issueId))
      .spanKind("Jira")
      .basicAuth(configuration.getUsername(), configuration.getPassword())
      .jsonContent(new RestDoTransition(transitionId))
      .request();

    if (!response.isSuccessful()) {
      fail("failed to change transition on issue %s", issueId, response);
    } else {
      LOG.debug("successfully changed transition on issue {}", issueId);
    }
  }

  private void fail(String message, String issueId, AdvancedHttpResponse response) throws JiraException {
    throw new JiraException(String.format(message, issueId) + ", return code " + response.getStatus());
  }
}
