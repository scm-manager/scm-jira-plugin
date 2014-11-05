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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import sonia.scm.Validateable;
import sonia.scm.repository.Repository;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JiraConfiguration implements Validateable
{

  /** Field description */
  public static final String DEFAULT_COMMENT_PREFIX = "[SCM]";

  /** Field description */
  public static final String PROPERTY_AUTOCLOSE = "jira.auto-close";

  /** Field description */
  public static final String PROPERTY_AUTOCLOSEWORDS = "jira.auto-close-words";

  /** Field description */
  public static final String PROPERTY_COMMENT_PREFIX = "jira.comment-prefix";

  /** Field description */
  public static final String PROPERTY_JIRA_URL = "jira.url";

  /** Field description */
  public static final String PROPERTY_PASSWORD = "jira.password";

  /** Field description */
  public static final String PROPERTY_ROLELEVEL = "jira.role-level";

  /** Field description */
  public static final String PROPERTY_UPDATEISSUES = "jira.update-issues";

  /** Field description */
  public static final String PROPERTY_USERNAME = "jira.username";

  /** Field description */
  public static final String SEPARATOR = ",";
  
  /** Address used in case of error */
  public static final String PROPERTY_ERROR_MAIL = "jira.mail-error-address";
  
  /** Mail host */
  public static final String PROPERTY_MAIL_HOST ="jira.mail-host";
  
  /** Send mail address */
  public static final String PROPERTY_SEND_MAIL = "jira.sendmail";
  
  /** Save path */
  public static final String PROPERTY_SAVE_PATH = "jira.savePath";
  
  //~--- constructors ---------------------------------------------------------

  /**
   * This constructor should only be used by jaxb.
   *
   */
  public JiraConfiguration() {}

  /**
   * Constructs ...
   *
   *
   * @param repository
   */
  public JiraConfiguration(Repository repository)
  {
    url = repository.getProperty(PROPERTY_JIRA_URL);
    updateIssues = getBooleanProperty(repository, PROPERTY_UPDATEISSUES);
    autoClose = getBooleanProperty(repository, PROPERTY_AUTOCLOSE);
    autoCloseWords = getSetProperty(repository, PROPERTY_AUTOCLOSEWORDS);
    username = repository.getProperty(PROPERTY_USERNAME);
    password = getEncryptedProperty(repository, PROPERTY_PASSWORD);
    roleLevel = repository.getProperty(PROPERTY_ROLELEVEL);
    commentPrefix = repository.getProperty(PROPERTY_COMMENT_PREFIX);
    mailAddress = repository.getProperty(PROPERTY_ERROR_MAIL);
    mailHost = repository.getProperty(PROPERTY_MAIL_HOST);
    sendMail = repository.getProperty(PROPERTY_SEND_MAIL);
    savePath = repository.getProperty(PROPERTY_SAVE_PATH);

    if (Strings.isNullOrEmpty(commentPrefix))
    {
      commentPrefix = DEFAULT_COMMENT_PREFIX;
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public Set<String> getAutoCloseWords()
  {
    return autoCloseWords;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getCommentPrefix()
  {
    return commentPrefix;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getRoleLevel()
  {
    return roleLevel;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUsername()
  {
    return username;
  }
  
  public String getMailAddress() {
	return mailAddress;
  }

  public String getMailHost() {
	return mailHost;
  }
  
  public String getSendMail() {
	  return sendMail;
  }
  
  public String getSavePath() {
	  return savePath;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isAutoCloseEnabled()
  {
    return isUpdateIssuesEnabled() && autoClose
      && Util.isNotEmpty(autoCloseWords);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isUpdateIssuesEnabled()
  {
    return isValid() && updateIssues;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public boolean isValid()
  {
    return Util.isNotEmpty(url);
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param key
   *
   * @return
   */
  private boolean getBooleanProperty(Repository repository, String key)
  {
    boolean result = false;
    String value = repository.getProperty(key);

    if (Util.isNotEmpty(value))
    {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param key
   *
   * @return
   */
  private String getEncryptedProperty(Repository repository, String key)
  {
    String value = repository.getProperty(key);

    if (EncryptionUtil.isEncrypted(value))
    {
      value = EncryptionUtil.decrypt(value);
    }

    return value;
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param key
   *
   * @return
   */
  private Set<String> getSetProperty(Repository repository, String key)
  {
    Set<String> values;
    String value = repository.getProperty(key);

    if (!Strings.isNullOrEmpty(value))
    {
      //J-
      values = ImmutableSet.copyOf(
        Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(value)
      );
      //J+
    }
    else
    {
      values = Collections.EMPTY_SET;
    }

    return values;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @XmlElement(name = "auto-close")
  private boolean autoClose;

  /** Field description */
  @XmlElement(name = "auto-close-words")
  @XmlJavaTypeAdapter(XmlStringSetAdapter.class)
  private Set<String> autoCloseWords;

  /** Field description */
  @XmlElement(name = "comment-prefix")
  private String commentPrefix = DEFAULT_COMMENT_PREFIX;

  /** Field description */
  @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
  private String password;

  /** Field description */
  @XmlElement(name = "role-level")
  private String roleLevel;

  /** Field description */
  @XmlElement(name = "update-issues")
  private boolean updateIssues;

  /** Field description */
  private String url;

  /** Field description */
  private String username;

  /** Address to send Error-Message to */
  @XmlElement(name = "mail-error-address")
  private String mailAddress;
  
  /** Host to send mail to */
  @XmlElement(name = "mail-host")
  private String mailHost;
  
  /** Address of the sender */
  @XmlElement(name = "sendmail")
  private String sendMail;
  
  /** The save path */
  @XmlElement(name = "savePath")
  private String savePath;

@Override
public String toString() {
	return "JiraConfiguration [autoClose=" + autoClose + ", autoCloseWords="
			+ autoCloseWords + ", commentPrefix=" + commentPrefix
			+ ", roleLevel=" + roleLevel
			+ ", updateIssues=" + updateIssues + ", url=" + url + ", username="
			+ username + ", mailAddress=" + mailAddress + ", mailHost="
			+ mailHost + ", sendMail=" + sendMail + ", savePath=" + savePath
			+ "]";
}
  

}
