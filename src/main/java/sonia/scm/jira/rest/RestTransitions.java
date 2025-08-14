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
import lombok.Getter;

/**
 * Wrapper object jira rest transitions.
 *
 * @author Sebastian Sdorra
 */
@Getter
@XmlRootElement(name = "transitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTransitions implements Iterable<RestTransition> {
  RestTransitions() {
  }

  private List<RestTransition> transitions = Lists.newArrayList();

  @Override
  public Iterator<RestTransition> iterator() {
    return transitions.iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("transitions", transitions)
      .toString();
  }
}
