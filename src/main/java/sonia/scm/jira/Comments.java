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

import com.google.common.base.Strings;

import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.regex.Matcher;

/**
 * Util methods fo handling comments.
 *
 * @author Sebastian Sdorra
 */
public final class Comments
{

  public static final String REPLACEMENT_LINK =
    "<a target=\"_blank\" href=\"{0}/browse/$0\">$0</a>";

  /**
   * Constructs ...
   *
   */
  private Comments() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Creates a new comment from the given request and body.
   *
   *
   * @param request jira issue request
   * @param body comment body
   *
   * @return new comment
   */
  public static Comment createComment(JiraIssueRequest request, String body)
  {
    String prefix = request.getConfiguration().getCommentPrefix();

    if (!Strings.isNullOrEmpty(prefix))
    {
      prefix = prefix.concat(" ");
    }

    //J-
    return new Comment(
      Strings.nullToEmpty(prefix).concat(Strings.nullToEmpty(body)),
      request.getCreation(),
      request.getConfiguration().getRoleLevel()
    );
    //J+
  }

  /**
   * Prepares the content of the comment.
   *
   *
   * @param request jira issue request
   * @param issueId issue id
   * @param comment comment
   *
   * @return prepared comment content
   */
  public static String prepareComment(JiraIssueRequest request, String issueId,
    Comment comment)
  {
    return prepareComment(request.getConfiguration().getUrl(), issueId,
      comment);
  }

  /**
   * Prepares the content of the comment.
   *
   *
   * @param jiraUrl jira server url
   * @param issueId issue id
   * @param comment comment
   *
   * @return prepared comment content
   */
  public static String prepareComment(String jiraUrl, String issueId,
    Comment comment)
  {
    String body = Strings.nullToEmpty(comment.getBody());

    return removeIssueLink(jiraUrl, issueId, body);
  }

  /**
   * Remove issue self reference link.
   * {@see https://bitbucket.org/sdorra/scm-manager/issue/337/jira-comment-contains-unneccessary-link}.
   *
   * TODO: The preprocessor order on hooks should be fixed in the core.
   *
   *
   * @param jiraUrl url of jira server
   * @param issueId issue id
   * @param body comment content
   *
   * @return
   */
  public static String removeIssueLink(String jiraUrl, String issueId,
    String body)
  {
    //J-
    String link = MessageFormat.format(
      REPLACEMENT_LINK,
      jiraUrl
    ).replaceAll(Matcher.quoteReplacement("$0"), issueId);
    //J+

    body = body.replaceAll(link, issueId);
//    body = body.replaceAll(EscapeUtil.escape(link), issueId);

    return body;
  }

  /**
   * Returns formatted date string or an empty string if the calendar object is
   * {@code null}.
   *
   *
   * @param calendar calendar object
   *
   * @return formatted date string
   */
  public static String format(Calendar calendar)
  {
    String formatted = Util.EMPTY_STRING;

    if (calendar != null)
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      formatted = sdf.format(calendar.getTime());
    }

    return formatted;
  }
}
