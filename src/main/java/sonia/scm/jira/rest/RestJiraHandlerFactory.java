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


package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

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
