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

import sonia.scm.repository.EscapeUtil;
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
      JiraChangesetPreProcessorFactory.REPLACEMENT_LINK,
      jiraUrl
    ).replaceAll(Matcher.quoteReplacement("$0"), issueId);
    //J+

    body = body.replaceAll(link, issueId);
    body = body.replaceAll(EscapeUtil.escape(link), issueId);

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
