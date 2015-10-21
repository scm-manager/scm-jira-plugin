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

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public JiraHandler createJiraHandler(JiraIssueRequest request,
    String username, String password)
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
