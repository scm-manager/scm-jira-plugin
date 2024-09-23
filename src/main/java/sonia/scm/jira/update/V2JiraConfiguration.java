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

package sonia.scm.jira.update;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.jira.config.JiraConfiguration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "jira-configuration")
public class V2JiraConfiguration {

  private static final Map<String,String> DEFAULT_AUTO_CLOSE_WORDS = ImmutableMap.of(
    "fix", "done",
    "reopen", "reopen and start progress",
    "start", "start progress"
  );

  private String url;
  @XmlElement(name = "filter")
  private String filter;
  @XmlElement(name = "update-issues")
  private boolean updateIssues;
  private String username;
  @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
  private String password;
  @XmlElement(name = "role-level")
  private String roleLevel;
  @XmlElement(name = "auto-close")
  private boolean autoClose;
  @XmlElement(name = "auto-close-words")
  @XmlJavaTypeAdapter(XmlStringMapAdapter.class)
  private Map<String,String> autoCloseWords = new HashMap<>(DEFAULT_AUTO_CLOSE_WORDS);

  public void copyTo(JiraConfiguration v3JiraConfig) {
    v3JiraConfig.setUrl(url);
    v3JiraConfig.setFilter(filter);
    v3JiraConfig.setUpdateIssues(updateIssues);
    v3JiraConfig.setUsername(username);
    v3JiraConfig.setPassword(password);
    v3JiraConfig.setRoleLevel(roleLevel);
    v3JiraConfig.setAutoClose(autoClose);
    v3JiraConfig.setAutoCloseWords(autoCloseWords);
  }

}
