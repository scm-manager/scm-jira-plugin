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

package sonia.scm.jira.soap;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.JiraConnectException;
import sonia.scm.jira.JiraExceptions;
import sonia.scm.jira.JiraHandler;
import sonia.scm.jira.JiraHandlerFactory;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.util.HttpUtil;

//~--- JDK imports ------------------------------------------------------------

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The SoapJiraHandlerFactory is able to create a new instance of a
 * {@link JiraHandler} which uses the SOAP protocol to communicate with jira.
 *
 * @author Sebastian Sdorra
 */
public class SoapJiraHandlerFactory implements JiraHandlerFactory
{

  /** default soap path */
  public static final String PATH_SOAPSERVICE = "/rpc/soap/jirasoapservice-v2";

  /** the logger for SoapJiraHandlerFactory */
  private static final Logger logger =
    LoggerFactory.getLogger(SoapJiraHandlerFactory.class);

  private final String username;
  private final String password;

  public SoapJiraHandlerFactory(String username, String password) {
    this.username = username;
    this.password = password;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public JiraHandler createJiraHandler(JiraIssueRequest request)
    throws JiraConnectException
  {
    JiraHandler handler = null;

    String urlString = request.getConfiguration().getUrl();

    try
    {
      URL url = createSoapUrl(request.getConfiguration().getUrl());

      logger.debug("connect to jira {} as user {}", url, username);

      JiraSoapService service =
        new JiraSoapServiceServiceLocator().getJirasoapserviceV2(url);
      String token = service.login(username, password);

      handler = new SoapJiraHandler(service, request, token, username);
    }
    catch (Exception ex)
    {
      //J-
      throw new JiraConnectException(
        JiraExceptions.createMessage(ex, "Could not connect to jira instance at ".concat(urlString)),
        ex
      );
      //J+
    }

    return handler;
  }

  private URL createSoapUrl(String url) throws MalformedURLException
  {
    url = HttpUtil.getUriWithoutEndSeperator(url);
    url = url.concat(PATH_SOAPSERVICE);

    return new URL(url);
  }
}
