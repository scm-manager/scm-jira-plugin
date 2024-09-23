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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper object to handle transition changes.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestDoTransition
{
  
  /**
   * Constructs a new {@link RestDoTransition}.
   */
  RestDoTransition() {}

  /**
   * Constructs a new {@link RestDoTransition}.
   * 
   * @param id transition id
   */
  public RestDoTransition(String id)
  {
    this.transition = new RestTransition(id);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns jira rest transition.
   * 
   * @return jira rest transitions
   */
  public RestTransition getTransition()
  {
    return transition;
  }

  //~--- fields ---------------------------------------------------------------

  /** wrapped transition */
  private RestTransition transition;
}
