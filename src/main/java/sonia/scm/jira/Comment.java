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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Calendar;

/**
 * A jira comment.
 *
 * @author Sebastian Sdorra
 */
public final class Comment
{

  /**
   * Constructs a new comment
   *
   *
   * @param body comment body
   * @param created creation time
   * @param roleLevel role level
   */
  public Comment(String body, Calendar created, String roleLevel)
  {
    this.body = body;
    this.created = created;
    this.roleLevel = roleLevel;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Comment other = (Comment) obj;

    return Objects.equal(body, other.body)
      && Objects.equal(created, other.created)
      && Objects.equal(roleLevel, other.roleLevel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(body, created, roleLevel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("body", body)
                  .add("created", created)
                  .add("roleLevel", roleLevel)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the body of the comment.
   *
   *
   * @return comment body
   */
  public String getBody()
  {
    return body;
  }

  /**
   * Returns the creation time of the comment.
   *
   *
   * @return creation time
   */
  public Calendar getCreated()
  {
    return created;
  }

  /**
   * Returns the role level of the comment. The role level defines which jira
   * users can view the comment.
   *
   *
   * @return role level
   */
  public String getRoleLevel()
  {
    return roleLevel;
  }

  //~--- fields ---------------------------------------------------------------

  /** body of the comment */
  private final String body;

  /** creation time */
  private final Calendar created;

  /** role level */
  private final String roleLevel;
}
