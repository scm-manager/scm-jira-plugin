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

package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jira rest api comment.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestComment
{
  /**
   * Constructs a new {@link RestComment}.
   */
  RestComment() {}

  /**
   * Constructs a new {@link RestComment}.
   * 
   * @param body comment body
   */
  public RestComment(String body)
  {
    this(body, null);
  }

  /**
   * Constructs a new {@link RestComment}.
   * 
   * @param body comment body
   * @param role name of role for visibility
   */
  public RestComment(String body, String role)
  {
    this.body = body;
    if (!Strings.isNullOrEmpty(role)){
      this.visibility = new RestVisibility(role);
    }
  }
  
  //~--- methods --------------------------------------------------------------

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("id", id)
                  .add("body", body)
                  .add("visibility", visibility)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns body of the comment.
   *
   * @return body comment
   */
  public String getBody()
  {
    return body;
  }

  /**
   * Returns the id of the comment.
   *
   * @return comment id
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns visibility of comment.
   * 
   * @return visibility of comment
   */
  public RestVisibility getVisibility()
  {
    return visibility;
  }

  //~--- fields ---------------------------------------------------------------

  /** comment body */
  private String body;

  /** id of comment */
  private String id;
  
  /** visibility of comment */
  private RestVisibility visibility;
}
