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

package sonia.scm.jira.config;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import sonia.scm.Validateable;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
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

  /** comment role level */
  @XmlElement(name = "role-level")
  private String roleLevel;

  /** auto close */
  @XmlElement(name = "auto-close")
  private boolean autoClose;

  /** set of auto close words */
  @XmlElement(name = "auto-close-words")
  @XmlJavaTypeAdapter(XmlStringMapAdapter.class)
  private Map<String,String> autoCloseWords = DEFAULT_AUTO_CLOSE_WORDS;

  /**
   * Returns {@code true} if the configuration is valid.
   *
   *
   * @return {@code true} if the configuration is valid
   */
  @Override
  public boolean isValid() {
    return Util.isNotEmpty(url) && Util.isNotEmpty(username) && Util.isNotEmpty(password);
  }

  public Map<String, String> getAutoCloseWords() {
    if (autoCloseWords == null) {
      return Collections.emptyMap();
    }
    return autoCloseWords;
  }
}
