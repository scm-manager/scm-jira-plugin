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
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.JiraException;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;
import sonia.scm.util.HttpUtil;

import java.io.IOException;

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

  public void addComment(String issueId, String comment) throws IOException {
    LOG.info("add comment to issue {}", issueId);

    RestComment restComment = new RestComment(comment, Strings.emptyToNull(configuration.getRoleLevel()));

    AdvancedHttpResponse response = client.post(commentUrl(issueId))
      .spanKind("Jira")
      .basicAuth(configuration.getUsername(), configuration.getPassword())
      .jsonContent(restComment)
      .request();

    if (!response.isSuccessful()) {
      throw new JiraException("failed to add comment to issue " + issueId);
    } else {
      LOG.debug("successfully added comment to issue {}", issueId);
    }
  }

  public RestTransitions getTransitions(String issueId) throws IOException {
    return client.get(transitionsUrl(issueId))
      .spanKind("Jira")
      .basicAuth(configuration.getUsername(), configuration.getPassword())
      .request()
      .contentFromJson(RestTransitions.class);
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
      throw new JiraException("failed to change transition on issue ".concat(issueId));
    } else {
      LOG.debug("successfully changed transition on issue {}", issueId);
    }
  }
}
