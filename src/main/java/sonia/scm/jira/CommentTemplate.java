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
 * Comment templates.
 *
 * @author Sebastian Sdorra
 */
public enum CommentTemplate
{

  //J-
  /** update template */
  UPADTE("update", "sonia/scm/jira/template/update.mustache"),
  
  /** autoclose template */
  AUTOCLOSE("autoclose", "sonia/scm/jira/template/autoclose.mustache"),
  
  /** resend template */
  RESEND("resend", "sonia/scm/jira/template/resend.mustache");
  //J+

  /**
   * Constructs new CommentTemplate.
   *
   *
   * @param name template name
   * @param resource template resource
   */
  private CommentTemplate(String name, String resource)
  {
    this.name = name;
    this.resource = resource;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the name of the template.
   *
   *
   * @return template name
   */
  public String getName()
  {
    return name;
  }

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

  //~--- fields ---------------------------------------------------------------

  /** template name */
  private final String name;

  /** template resource */
  private final String resource;
}
