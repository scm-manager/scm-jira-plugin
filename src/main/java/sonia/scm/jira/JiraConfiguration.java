/**
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

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import sonia.scm.Validateable;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

//~--- JDK imports ------------------------------------------------------------

/**
 * Local or per repository jira configuration.
 *
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "jira-configuration")
public class JiraConfiguration implements Validateable
{

  /** default comment prefix */
  public static final String DEFAULT_COMMENT_PREFIX = "[SCM]";
  public static final String DEFAULT_AUTO_CLOSE_WORDS = "fix=done, reopen=reopen and start progress, start=start progress";
  public static final String DEFAULT_COMMENT_WRAP = "";

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("squid:S2068") // we have no password here
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("url", url)
                  .add("updateIssues", updateIssues)
                  .add("commentPrefix", commentPrefix)
                  .add("filter", filter)
                  .add("roleLevel", roleLevel)
                  .add("autoClose", autoClose)
                  .add("autoCloseWords", autoCloseWords)
                  .add("username", username)
                  .add("password", "xxx")
                  .add("mailAddress", mailAddress)
                  .add("resubmission", resubmission)
                  .add("restApiEnabled", restApiEnabled)
                  .add("commentWrap", commentWrap)
                  .add("commentMonospace", commentMonospace)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns a set of auto close words.
   *
   *
   * @return set of auto close words
   */
  public Set<String> getAutoCloseWords()
  {
    return autoCloseWords.keySet();
  }
  
  /**
   * Returns a mapped auto close word.
   *
   * @param acw auto close word
   * 
   * @return mapped auto close word
   */
  public String getMappedAutoCloseWord(String acw)
  {
    return autoCloseWords.get(acw);
  }

  /**
   * Returns a prefix for the jira comments. Default is
   * {@link #DEFAULT_COMMENT_PREFIX} used.
   *
   *
   * @return comment prefix
   */
  public String getCommentPrefix()
  {
    return commentPrefix;
  }
  
  public String getFilter()
  {
    return filter;
  }

  /**
   * Mail address which is used in case the comment could not be attached to the
   * jira issue.
   *
   *
   * @return error notification mail address
   */
  public String getMailAddress()
  {
    return mailAddress;
  }

  /**
   * Returns password which is used for the connection to the jira server.
   *
   *
   * @return connection password
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Returns the comment role level. The role level defines which type of jira
   * user is able to see the comments.
   *
   *
   * @return comment role level
   */
  public String getRoleLevel()
  {
    return roleLevel;
  }

  /**
   * Returns the jira server url.
   *
   *
   * @return jira server url
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * Returns the username which is used for the connection to the jira server.
   *
   *
   * @return connection username
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * Returns {@code true} if the configuration is valid, updating and auto
   * closing issues is enabled.
   *
   *
   * @return {@code true} if the configuration is valid, updating and auto
   *   closing issues is enabled
   */
  public boolean isAutoCloseEnabled()
  {
    return isUpdateIssuesEnabled() && autoClose && Util.isNotEmpty(autoCloseWords);
  }

  /**
   * Returns {@code true} if the jira rest api v2 is enabled.
   *
   * @return {@code true} if the jira rest api
   */
  public boolean isRestApiEnabled()
  {
    return restApiEnabled;
  }

  /**
   * Returns {@code true} if the resubmission should be used. Resubmission means
   * that comments are stored for a later resubmission in case of an error.
   *
   *
   * @return {@code true} if resubmission is enabled
   */
  public boolean isResubmission()
  {
    return resubmission;
  }

  /**
   * Returns {@code true} if the configuration is valid and updating issues is
   * enabled.
   *
   *
   * @return {@code true} if updating issues is enabled and the configuration is
   *   valid
   */
  public boolean isUpdateIssuesEnabled()
  {
    return isValid() && updateIssues;
  }

  /**
   * Returns {@code true} if the configuration is valid.
   *
   *
   * @return {@code true} if the configuration is valid
   */
  @Override
  public boolean isValid()
  {
    return Util.isNotEmpty(url) && Util.isNotEmpty(username) && Util.isNotEmpty(password);
  }


  public String getCommentWrap() 
  {
    return commentWrap;
  }

  public boolean getCommentMonospace() 
  { 
    return commentMonospace; 
  }

  public void setAutoCloseWordsForMapping(Map<String, String> autoCloseWords) {
    this.autoCloseWords = autoCloseWords;
  }

  Map<String, String> getAutoCloseWordsForMapping() {
    return this.autoCloseWords;
  }

  public boolean isAutoClose() {
    return autoClose;
  }

  public void setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
  }

  public boolean isUpdateIssues() {
    return updateIssues;
  }

  public void setUpdateIssues(boolean updateIssues) {
    this.updateIssues = updateIssues;
  }

  public void setMailAddress(String mailAddress) {
    this.mailAddress = mailAddress;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRestApiEnabled(boolean restApiEnabled) {
    this.restApiEnabled = restApiEnabled;
  }

  public void setCommentPrefix(String commentPrefix) {
    this.commentPrefix = commentPrefix;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public void setResubmission(boolean resubmission) {
    this.resubmission = resubmission;
  }

  public void setRoleLevel(String roleLevel) {
    this.roleLevel = roleLevel;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setCommentWrap(String commentWrap) {
    this.commentWrap = commentWrap;
  }

  public void setCommentMonospace(boolean commentMonospace) {
    this.commentMonospace = commentMonospace;
  }

  //~--- fields ---------------------------------------------------------------

  /** auto close */
  @XmlElement(name = "auto-close")
  private boolean autoClose;

  /** set of auto close words */
  @XmlElement(name = "auto-close-words")
  @XmlJavaTypeAdapter(XmlStringMapAdapter.class)
  private Map<String,String> autoCloseWords = AutoCloseWords.parse(DEFAULT_AUTO_CLOSE_WORDS);

  /** Address to send Error-Message to */
  @XmlElement(name = "mail-error-address")
  private String mailAddress;

  /** connection password */
  @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
  private String password;

  /** use jira rest api */
  @XmlElement(name = "rest-api-enabled")
  private boolean restApiEnabled = false;

  /** comment prefix */
  @XmlElement(name = "comment-prefix")
  private String commentPrefix = DEFAULT_COMMENT_PREFIX;
  
  /** filter */
  @XmlElement(name = "filter")
  private String filter;

  /** use resubmission */
  @XmlElement(name = "resubmission")
  private boolean resubmission;

  /** comment role level */
  @XmlElement(name = "role-level")
  private String roleLevel;

  /** update jira issues */
  @XmlElement(name = "update-issues")
  private boolean updateIssues;

  /** jira server url */
  private String url;

  /** connection username */
  private String username;

  @XmlElement(name = "comment-wrap")
  private String commentWrap = DEFAULT_COMMENT_WRAP;

  @XmlElement(name = "comment-monospace")
  private boolean commentMonospace;
}
