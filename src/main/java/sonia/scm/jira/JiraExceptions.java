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
