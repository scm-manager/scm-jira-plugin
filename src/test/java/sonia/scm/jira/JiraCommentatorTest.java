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
      assertThat(restVisibility.getType()).isEqualTo("role");
      assertThat(restVisibility.getValue()).isEqualTo("vogon");
    });
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
