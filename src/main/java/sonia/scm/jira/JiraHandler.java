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

/**
 * The jira handler is the main component for communication between scm-manager 
 * and jira.
 *
 * @author Sebastian Sdorra
 */
public interface JiraHandler
{

  /**
   * Adds a comment to the issue.
   *
   *
   * @param issueId id of the issue
   * @param comment comment
   *
   * @throws JiraException
   */
  public void addComment(String issueId, Comment comment) throws JiraException;

  /**
   * Closes the issue with the given id.
   *
   *
   * @param issueId id of the issue
   * @param autoCloseWord word of the commit message which has caused the close 
   *    request
   *
   * @throws JiraException
   */
  public void close(String issueId, String autoCloseWord) throws JiraException;

  /**
   * Logs the user out of jira. 
   *
   *
   * @throws JiraException
   */
  public void logout() throws JiraException;

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns {@code true} if the comment already exists on the issue. The method
   * checks every comment which is associated with the issue id for the given 
   * strings.
   *
   * @param issueId id of the issue
   * @param contains strings which are able to uniquely identify the comment
   *
   * @return
   *
   * @throws JiraException
   */
  public boolean isCommentAlreadyExists(String issueId, String... contains)
    throws JiraException;
}
