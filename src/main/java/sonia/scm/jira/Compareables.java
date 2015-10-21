/**
 * Copyright (c) 2014, Sebastian Sdorra All rights reserved.
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



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import sonia.scm.jira.soap.RemoteComment;

//~--- JDK imports ------------------------------------------------------------

import java.util.Locale;

/**
 * Util class for compare operations.
 *
 * @author Sebastian Sdorra
 */
public final class Compareables
{
  private Compareables() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Returns {@code true} if the value contains one of the given strings.
   *
   * @param value value
   * @param contains string for the contains check
   *
   * @return {@code true} if the comment contains one of the strings
   */
  @VisibleForTesting
  public static boolean contains(String value, String... contains)
  {
    boolean result = false;

    if (!Strings.isNullOrEmpty(value))
    {
      result = true;

      for (String c : contains)
      {
        if (!value.contains(c))
        {
          result = false;

          break;

        }

      }
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param text
   * @param value
   *
   * @return
   */
  public static boolean contains(String text, String value)
  {
    return toLowerCase(text).contains(toLowerCase(value));
  }

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   */
  public static String toLowerCase(String value)
  {
    return Strings.nullToEmpty(value).toLowerCase(Locale.ENGLISH);
  }
}
