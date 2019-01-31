/**
 * Copyright (c) 2014, Sebastian Sdorra 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided with the distribution. 3. Neither the
 * name of SCM-Manager; nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import sonia.scm.jira.JiraConnectException;
import sonia.scm.jira.JiraHandlerFactory;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.util.HttpUtil;

/**
 * The {@link RestJiraHandlerFactory} is able to create a new instance of a
 * {@link JiraHandler} which uses the Jira Rest v2 protocol to communicate with jira.
 *
 * @author Sebastian Sdorra
 */
public class RestJiraHandlerFactory implements JiraHandlerFactory
{
  private static final String PATH_REST_API = "/rest/api/2/issue/";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new {@link RestJiraHandlerFactory}.
   *
   * @param client http client
   * @param username connection username
   * @param password connection password
   */
  public RestJiraHandlerFactory(AdvancedHttpClient client, String username, String password)
  {
    this.client = client;
    this.username = username;
    this.password = password;
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public RestJiraHandler createJiraHandler(JiraIssueRequest request)
    throws JiraConnectException
  {
    return new RestJiraHandler(client, request, createUrl(request), username, password);
  }

  private String createUrl(JiraIssueRequest request)
  {
    return HttpUtil.getUriWithoutEndSeperator(request.getConfiguration().getUrl()).concat(PATH_REST_API);
  }

  //~--- fields ---------------------------------------------------------------

  /** http client */
  private final AdvancedHttpClient client;

  private final String username;
  private final String password;
}
