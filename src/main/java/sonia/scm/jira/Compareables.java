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

package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

//~--- JDK imports ------------------------------------------------------------

import java.util.Locale;

/**
 * Util class for compare operations.
 *
 * @author Sebastian Sdorra
 */
public final class Compareables
{
  /**
   * Private util constructor.
   */
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
   * Returns {@code true} if the given text contains the value.
   *
   *
   * @param text text
   * @param value value
   *
   * @return {@code true} if the text contains the value
   */
  public static boolean contains(String text, String value)
  {
    return toLowerCase(text).contains(toLowerCase(value));
  }

  /**
   * Returns the given value as lower case.
   *
   *
   * @param value value
   *
   * @return value as lower case
   */
  public static String toLowerCase(String value)
  {
    return Strings.nullToEmpty(value).toLowerCase(Locale.ENGLISH);
  }
}
