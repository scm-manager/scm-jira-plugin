/**
 * Copyright (c) 2015, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * https://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;

import org.apache.axis.NoEndPointException;

import sonia.scm.jira.soap.RemoteAuthenticationException;
import sonia.scm.jira.soap.RemotePermissionException;
import sonia.scm.jira.soap.RemoteValidationException;

/**
 * Util methods to handle {@link JiraException}.
 *
 * @author Sebastian Sdorra
 */
public final class JiraExceptions
{

  /**
   * Constructs ...
   *
   */
  private JiraExceptions() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Creates a message from the given exception.
   *
   *
   * @param ex exception
   *
   * @return message
   */
  public static String createMessage(Exception ex)
  {
    return createMessage(ex, null);
  }

  /**
   * Creates a message from the given exception.
   *
   *
   * @param ex exception
   * @param messagePrefix prefix for the message
   *
   * @return message
   */
  public static String createMessage(Exception ex, String messagePrefix)
  {
    String message = "";

    if (!Strings.isNullOrEmpty(messagePrefix))
    {
      message = messagePrefix.concat(". ");
    }

    if (ex instanceof RemotePermissionException)
    {
      message = message.concat("The permission was denied.");
    }
    else if (ex instanceof RemoteAuthenticationException)
    {
      message = message.concat("The authentication was incorrect.");
    }
    else if (ex instanceof RemoteValidationException)
    {
      message = message.concat("The message could not be validated.");
    }
    else if (ex instanceof NoEndPointException)
    {
      message = message.concat("No endpoint could be found.");
    }
    else
    {
      message = message.concat("Unknown error occurred.");
    }

    return message;
  }

  /**
   * Propagates the given exception. If the exception is an instance of
   * {@link JiraException} the exception is casted to {@link JiraException}, the
   * exception is wrapped into a {@link JiraException} if not.
   *
   * @param ex exception
   * @param messagePrefix message prefix for the wrapped exception
   *
   * @return jira exception
   */
  public static JiraException propagate(Exception ex, String messagePrefix)
  {
    JiraException jiraException;

    if (ex instanceof JiraException)
    {
      jiraException = (JiraException) ex;
    }
    else
    {
      jiraException = new JiraException(createMessage(ex, messagePrefix), ex);
    }

    return jiraException;
  }

  /**
   * Throws a {@link JiraException}, if the given exception is an instance of
   * {@link JiraException}.
   *
   * @param ex exception
   *
   * @throws JiraException
   */
  public static void propagateIfPossible(Exception ex) throws JiraException
  {
    if (ex instanceof JiraException)
    {
      throw(JiraException) ex;
    }
  }
}
