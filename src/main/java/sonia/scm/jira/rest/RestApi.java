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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import sonia.scm.jira.JiraException;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;
import sonia.scm.net.ahc.BaseHttpRequest;
import sonia.scm.util.HttpUtil;

import java.io.IOException;
import java.util.Collection;

/**
 * This class represents an api of a Jira instance.
 */
@Slf4j
public class RestApi {

  private final AdvancedHttpClient client;
  private final JiraConfiguration configuration;
  private final String baseUrl;

  public RestApi(AdvancedHttpClient client, JiraConfiguration configuration) {
    this.client = client;
    this.configuration = configuration;
    this.baseUrl = createBaseUrl(configuration);
  }

  /**
   * @param issueId Usually an abbreviation and a number; e.g. <tt>RTT-1</tt>
   * @param comment Prepared {@link RestComment} instance.
   * @throws IOException In case of unexpected request failures.
   */
  public void addComment(String issueId, RestComment comment) throws IOException {
    log.info("add comment to issue {}", issueId);
    AdvancedHttpResponse response = authenticate(client.post(commentUrl(issueId))
      .spanKind("Jira")
    )
      .jsonContent(comment)
      .request();

    if (!response.isSuccessful()) {
      fail("failed to add comment to %s", issueId, response);
    } else {
      log.debug("successfully added comment to issue {}", issueId);
    }
  }

  /**
   * @param issueId      Usually an abbreviation and a number; e.g. <tt>RTT-1</tt>
   * @param transitionId <tt>Done</tt>, <tt>in progress</tt>, etc.
   * @throws IOException In case of unexpected request failures.
   */
  public void changeState(String issueId, String transitionId) throws IOException {
    log.info("attempting to change state of issue {}", issueId);
    AdvancedHttpResponse response = authenticate(client.post(transitionsUrl(issueId))
      .spanKind("Jira")
    )
      .jsonContent(new RestDoTransition(transitionId))
      .request();

    if (!response.isSuccessful()) {
      fail("failed to change state of issue %s", issueId, response);
    } else {
      log.debug("successfully changed state of issue {} to {}", issueId, transitionId);
    }
  }

  /**
   * @param issueId Usually an abbreviation and a number; e.g. <tt>RTT-1</tt>
   * @throws IOException In case of unexpected request failures.
   */
  public Collection<RestTransition> getTransitions(String issueId) throws IOException {
    log.debug("get transitions for issue {}", issueId);
    AdvancedHttpResponse response = authenticate(client.get(transitionsUrl(issueId))
      .spanKind("Jira")
    )
      .request();

    if (!response.isSuccessful()) {
      fail("failed to retrieve transitions from %s", issueId, response);
    }

    return response.contentFromJson(RestTransitions.class)
      .getTransitions();
  }

  private <R extends BaseHttpRequest<?>> R authenticate(R request) {
    if (configuration.isUseAccessToken()) {
      log.trace("Using access token for Jira connection");
      return (R) request.header("Authorization", "Bearer " + configuration.getAccessToken());
    } else {
      log.trace("Using basic auth for Jira connection");
      return (R) request.basicAuth(configuration.getUsername(), configuration.getPassword());
    }
  }

  private String createBaseUrl(JiraConfiguration configuration) {
    return HttpUtil.concatenate(configuration.getUrl(), "rest", "api", "2", "issue");
  }

  private String commentUrl(String issueId) {
    return HttpUtil.concatenate(baseUrl, issueId, "comment");
  }

  private String transitionsUrl(String issueId) {
    return HttpUtil.concatenate(baseUrl, issueId, "transitions");
  }

  private void fail(String message, String issueId, AdvancedHttpResponse response) throws JiraException {
    throw new JiraException(String.format(message, issueId) + ", return code " + response.getStatus());
  }
}
