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

/**
 * Comment templates.
 *
 * @author Sebastian Sdorra
 */
public enum CommentTemplate
{

  /** update template */
  UPADTE("sonia/scm/jira/template/update.mustache"),

  /** autoclose template */
  AUTOCLOSE("sonia/scm/jira/template/autoclose.mustache"),

  /** resend template */
  RESEND("sonia/scm/jira/template/resend.mustache"),

  /** pull request template */
  PR("sonia/scm/jira/template/pr.mustache");

  /**
   * Constructs new CommentTemplate.
   *
   *
   * @param resource template resource
   */
  CommentTemplate(String resource)
  {
    this.resource = resource;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the classpath path for the resource.
   *
   *
   * @return classpath path
   */
  public String getResource()
  {
    return resource;
  }

  /** template resource */
  private final String resource;
}
