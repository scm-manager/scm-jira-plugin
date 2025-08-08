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

package sonia.scm.jira.config;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import sonia.scm.Validateable;
import sonia.scm.util.Util;
import sonia.scm.xml.XmlEncryptionAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Local or per repository jira configuration.
 *
 * @author Sebastian Sdorra
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "jira-configuration")
public class JiraConfiguration implements Validateable {

  private static final Map<String,String> DEFAULT_AUTO_CLOSE_WORDS = ImmutableMap.of(
    "fix", "done",
    "reopen", "reopen and start progress",
    "start", "start progress"
  );

  /** jira server url */
  private String url;

  /** filter */
  @XmlElement(name = "filter")
  private String filter;

  /** update jira issues */
  @XmlElement(name = "update-issues")
  private boolean updateIssues;

  /** connection username */
  private String username;

  /** connection password */
  @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
  private String password;

  /** connection access token */
  @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
  private String accessToken;

  /** flag whether to use the access token or username/password */
  @XmlElement(name = "use-access-token")
  private boolean useAccessToken;

  /** comment role level */
  @XmlElement(name = "role-level")
  private String roleLevel;

  /** auto close */
  @XmlElement(name = "auto-close")
  private boolean autoClose;

  /** set of auto close words */
  @XmlElement(name = "auto-close-words")
  private Map<String,String> autoCloseWords = new HashMap<>(DEFAULT_AUTO_CLOSE_WORDS);

  /** disable state change for commits */
  @XmlElement(name = "disable-state-change-by-commit")
  private boolean disableStateChangeByCommit;

  /**
   * Returns {@code true} if the configuration is valid.
   *
   *
   * @return {@code true} if the configuration is valid
   */
  @Override
  public boolean isValid() {
    return Util.isNotEmpty(url);
  }

  public Map<String, String> getAutoCloseWords() {
    if (autoCloseWords == null) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(autoCloseWords);
  }
}
