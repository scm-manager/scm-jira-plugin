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

package sonia.scm.jira;

import com.google.common.base.Strings;
import sonia.scm.issuetracker.spi.Commentator;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.jira.rest.RestComment;

import java.io.IOException;

public class JiraCommentator implements Commentator {

  private final RestApi restApi;
  private final JiraConfiguration configuration;

  JiraCommentator(RestApi restApi, JiraConfiguration configuration) {
    this.restApi = restApi;
    this.configuration = configuration;
  }

  @Override
  public void comment(String issueKey, String content) throws IOException {
    restApi.addComment(issueKey, comment(content));
  }

  private RestComment comment(String content) {
    return new RestComment(content, Strings.emptyToNull(configuration.getRoleLevel()));
  }
}
