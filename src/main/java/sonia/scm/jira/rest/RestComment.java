/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

//~--- JDK imports ------------------------------------------------------------

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
