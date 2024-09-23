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

//~--- JDK imports ------------------------------------------------------------

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Jira Rest Transition.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "transition")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTransition
{

  /**
   * Constructs a new {@link RestTransition}.
   */
  RestTransition() {}

  /**
   * Constructs a new {@link RestTransition}.
   *
   * @param id transition id
   */
  public RestTransition(String id) {
    this(id, null);
  }

  /**
   * Constructs a new {@link RestTransition}.
   *
   * @param id transition id
   * @param name name of transition
   */
  public RestTransition(String id, @Nullable String name) {
    this.id = id;
    this.name = name;
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("id", id)
                  .add("name", name)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns id of rest transition.
   *
   * @return id of transition
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns name of rest transition.
   *
   * @return name of transition
   */
  public String getName()
  {
    return name;
  }

  //~--- fields ---------------------------------------------------------------

  /** transition id */
  private String id;

  /** transition name */
  private String name;
}
