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
