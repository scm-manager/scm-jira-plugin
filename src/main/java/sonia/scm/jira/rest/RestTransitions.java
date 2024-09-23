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
import com.google.common.collect.Lists;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper object jira rest transitions.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "transitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTransitions implements Iterable<RestTransition>
{
 
  /**
   * Constructs a new {@link RestTransitions}.
   */
  RestTransitions() {}

  /**
   * Returns an {@link Iterator} over a list of jira rest transitions.
   * 
   * @return {@link Iterator} for transitions
   */
  @Override
  public Iterator<RestTransition> iterator()
  {
    return getTransitions().iterator();
  }

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("transitions", transitions)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns a {@link List} jira rest transitions.
   * 
   * @return {@link List} jira rest transitions
   */
  public List<RestTransition> getTransitions()
  {
    if (transitions == null)
    {
      transitions = Lists.newArrayList();
    }

    return transitions;
  }

  //~--- fields ---------------------------------------------------------------

  /** list of jira rest transitions */
  private List<RestTransition> transitions;
}
