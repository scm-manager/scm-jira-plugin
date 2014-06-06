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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Repository;
import sonia.scm.security.CipherUtil;
import sonia.scm.util.AssertUtil;

/**
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class JiraIssueRequestFactory
{

  @Override
	public String toString() {
		return "JiraIssueRequestFactory [handlerFactory=" + handlerFactory
				+ "]";
	}

/** Field description */
  public static final String SCM_CREDENTIALS = "SCM_CREDENTIALS";

  /**
   * the logger for JiraIssueRequestFactory
   */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraIssueRequestFactory.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param handlerFactory
   * @param requestProvider
   * @param securityContextProvider
   */
  @Inject
  public JiraIssueRequestFactory(JiraHandlerFactory handlerFactory)
  {
    this.handlerFactory = handlerFactory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param configuration
   * @param repository
   *
   * @return
   */
  public JiraIssueRequest createRequest(JiraConfiguration configuration,
    Repository repository)
  {
    String username = configuration.getUsername();
    String password = configuration.getPassword();

    if (Strings.isNullOrEmpty(username))
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("no username configured, use current credentials");
      }

      String[] credentials = getUserCredentials();

      username = credentials[0];
      password = credentials[1];
    }

    if (logger.isDebugEnabled())
    {
      logger.debug("create jira issue request for user {}", username);
    }

    return new JiraIssueRequest(handlerFactory, username, password,
      configuration, repository);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private String getCredentialsString()
  {
    String crendentials = null;

    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();

    if (session != null)
    {
      crendentials = (String) session.getAttribute(SCM_CREDENTIALS);

    }

    return crendentials;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private String[] getUserCredentials()
  {
    String credentialsString = getCredentialsString();

    AssertUtil.assertIsNotEmpty(credentialsString);
    credentialsString = CipherUtil.getInstance().decode(credentialsString);

    String[] credentialsArray = credentialsString.split(":");

    if (credentialsArray.length < 2)
    {
      throw new RuntimeException("non valid credentials found");
    }

    return credentialsArray;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private JiraHandlerFactory handlerFactory;
}
