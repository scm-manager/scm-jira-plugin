/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import sonia.scm.PropertiesAware;
import sonia.scm.Validateable;
import sonia.scm.issuetracker.EncryptionUtil;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//~--- JDK imports ------------------------------------------------------------

/**
 * Local or per repository jira configuration.
 *
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JiraConfiguration implements Validateable
{

  /** default comment prefix */
  public static final String DEFAULT_COMMENT_PREFIX = "[SCM]";

  public static final String DEFAULT_COMMENT_WRAP = "";

  /** auto close property */
  public static final String PROPERTY_AUTOCLOSE = "jira.auto-close";

  /** auto close words property */
  public static final String PROPERTY_AUTOCLOSEWORDS = "jira.auto-close-words";

  /** comment prefix property */
  public static final String PROPERTY_COMMENT_PREFIX = "jira.comment-prefix";
  
  /** filter property */
  public static final String PROPERTY_FILTER = "jira.filter";

  /** Address used in case of error */
  public static final String PROPERTY_ERROR_MAIL = "jira.mail-error-address";

  /** url property */
  public static final String PROPERTY_JIRA_URL = "jira.url";

  /** password property */
  public static final String PROPERTY_PASSWORD = "jira.password";

  /** resubmission */
  public static final String PROPERTY_RESUBMISSION = "jira.resubmission";
  
  /** use jira rest api v2 */
  public static final String PROPERTY_REST_API = "jira.rest-api-enabled";

  /** role level property */
  public static final String PROPERTY_ROLELEVEL = "jira.role-level";

  /** update issues property */
  public static final String PROPERTY_UPDATEISSUES = "jira.update-issues";

  /** username property */
  public static final String PROPERTY_USERNAME = "jira.username";

  /** wrap property */
  public static final String PROPERTY_WRAP_FORMAT = "jira.comment-wrap";

  /** monospace property */
  public static final String PROPERTY_MONOSPACE = "jira.comment-monospace";

  /** separator for set properties */
  public static final String SEPARATOR = ",";

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
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
    return Util.isNotEmpty(url);
  }


  public String getCommentWrap() 
  {
    return commentWrap;
  }

  public boolean getCommentMonospace() 
  { 
    return commentMonospace; 
  }


  private boolean getBooleanProperty(PropertiesAware repository, String key)
  {
    boolean result = false;
    String value = repository.getProperty(key);

    if (Util.isNotEmpty(value))
    {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }

  private String getEncryptedProperty(PropertiesAware repository, String key)
  {
    String value = repository.getProperty(key);

    if (EncryptionUtil.isEncrypted(value))
    {
      value = EncryptionUtil.decrypt(value);
    }

    return value;
  }

  private Map<String,String> getAutoCloseWordProperty(PropertiesAware repository, String key)
  {
    Map<String,String> map;
    String value = repository.getProperty(key);

    if (!Strings.isNullOrEmpty(value))
    {
      map = AutoCloseWords.parse(value);
    }
    else
    {
      map = Collections.EMPTY_MAP;
    }

    return map;
  }

  void setAutoCloseWordsForMapping(Map<String, String> autoCloseWords) {
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

  //~--- fields ---------------------------------------------------------------

  /** auto close */
  @XmlElement(name = "auto-close")
  private boolean autoClose;

  /** set of auto close words */
  @XmlElement(name = "auto-close-words")
  @XmlJavaTypeAdapter(XmlStringMapAdapter.class)
  private Map<String,String> autoCloseWords;

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
