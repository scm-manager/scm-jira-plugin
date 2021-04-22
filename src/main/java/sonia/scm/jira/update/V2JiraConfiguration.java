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

package sonia.scm.jira.update;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.jira.config.JiraConfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
