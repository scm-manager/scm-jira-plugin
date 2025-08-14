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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.jira.rest.RestComment;
import sonia.scm.jira.rest.RestVisibility;
import sonia.scm.jira.rest.RestVisibilityType;
import sonia.scm.jira.rest.property.RestInternalProperty;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JiraCommentatorTest {

  @Mock
  private RestApi api;

  private JiraConfiguration configuration;

  private JiraCommentator commentator;

  @Captor
  private ArgumentCaptor<RestComment> commentCaptor;

  @BeforeEach
  void setUp() {
    configuration = new JiraConfiguration();
    commentator = new JiraCommentator(api, configuration);
  }

  @Test
  void shouldDelegateToRestApi() throws IOException {
    commentator.comment("SCM-42", "Awesome");

    RestComment comment = verifyComment("SCM-42");
    assertThat(comment.getBody()).isEqualTo("Awesome");
  }

  @Test
  void shouldNotSetVisibility() throws IOException {
    RestComment comment = commentAndVerify("SCM-21", "Incredible");
    assertThat(comment.getVisibility()).isNull();
  }

  @Test
  void shouldSetVisibilityRole() throws IOException {
    configuration.setRoleLevel("vogon");

    RestComment comment = commentAndVerify("SCM-4121", "Awesome Shit");
    assertThat(comment.getVisibility()).isNotNull().satisfies(restVisibility -> {
      assertThat(restVisibility.getType()).isEqualTo(RestVisibilityType.ROLE);
      assertThat(restVisibility.getValue()).isEqualTo("vogon");
    });
  }

  @Test
  void shouldSetInternalPropertyByDefault() throws IOException {
    RestComment comment = commentAndVerify("SCM-36", "RIP");
    assertThat(comment.getProperties()).containsExactly(new RestInternalProperty());
  }

  private RestComment commentAndVerify(String issueKey, String content) throws IOException {
    commentator.comment(issueKey, content);
    return verifyComment(issueKey);
  }

  private RestComment verifyComment(String issueKey) throws IOException {
    verify(api).addComment(eq(issueKey), commentCaptor.capture());
    return commentCaptor.getValue();
  }

}
