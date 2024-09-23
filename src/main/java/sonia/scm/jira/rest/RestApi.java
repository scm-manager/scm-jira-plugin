/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
