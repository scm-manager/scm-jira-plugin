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

import org.junit.jupiter.api.Test;
import sonia.scm.jira.config.JiraConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class JiraIssueLinkFactoryTest {

  @Test
  void shouldCreateJiraIssueLink() {
    JiraConfiguration configuration = new JiraConfiguration();
    configuration.setUrl("https://jira.hitchhiker.com");
    JiraIssueLinkFactory linkFactory = new JiraIssueLinkFactory(configuration);

    String link = linkFactory.createLink("SCM-42");
    assertThat(link).isEqualTo("https://jira.hitchhiker.com/browse/SCM-42");
  }

}
