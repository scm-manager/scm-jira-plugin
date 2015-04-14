/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
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
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Objects;

//~--- JDK imports ------------------------------------------------------------

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
    return Objects.toStringHelper(this)
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
