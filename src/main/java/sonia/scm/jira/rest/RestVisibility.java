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

import com.google.common.base.MoreObjects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Jira rest api visibility of a comment.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestVisibility
{

  /**
   * Constructs a new {@link RestVisibility}.
   */
  RestVisibility()
  {
  }

  /**
   * Constructs a new {@link RestVisibility}.
   * 
   * @param value name of role
   */
  public RestVisibility(String value)
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("type", type)
                  .add("value", value)
                  .toString();
    //J+
  }

  /**
   * Returns the type of visibility value. The only supported value is currently role.
   * 
   * @return type of visibility value
   */
  public String getType()
  {
    return type;
  }

  /**
   * Returns value of visibility. The only supported values are currently role names.
   * 
   * @return value of visibility
   */
  public String getValue()
  {
    return value;
  }
  
  /** type of visibility */
  private final String type = "role";
  
  /** value of visibility */
  private String value;
}
