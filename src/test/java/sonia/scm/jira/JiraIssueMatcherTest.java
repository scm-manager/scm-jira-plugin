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

import com.google.common.base.Joiner;
import org.junit.jupiter.api.Test;
import sonia.scm.jira.config.JiraConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class JiraIssueMatcherTest {

  @Test
  void shouldMatchAllWithFilter() {
    JiraIssueMatcher matcher = matcher();
    findMatch(matcher, "Match SCM-2 as the key", "SCM-2");
    findMatch(matcher, "TST-2 ", "TST-2");
    findMatch(matcher, "TST-21 and SCM-42 ", "TST-21", "SCM-42");
  }

  @Test
  void shouldMatchOnlyFiltered() {
    JiraIssueMatcher matcher = matcher("SCM", "TST");
    findMatch(matcher, "Match SCM-2 as the key", "SCM-2");
    findMatch(matcher, "TST-2 ", "TST-2");
    findMatch(matcher, "OLM-1 TST-21 SMT-3 SCM-42 and OTR-1", "TST-21", "SCM-42");
  }

  private void findMatch(JiraIssueMatcher matcher, String message, String... expected) {
    Pattern p = matcher.getKeyPattern();
    Matcher m = p.matcher(message);
    for (String expectedMatch : expected) {
      assertThat(m.find()).isTrue();
      String issueKey = matcher.getKey(m);
      assertThat(issueKey).isEqualTo(expectedMatch);
    }
  }

  private JiraIssueMatcher matcher(String... filter) {
    JiraConfiguration configuration = new JiraConfiguration();
    if (filter.length > 0) {
      configuration.setFilter(Joiner.on(',').join(filter));
    }
    return new JiraIssueMatcher(configuration);
  }

}
