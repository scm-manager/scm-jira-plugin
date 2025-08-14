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
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@Getter
@XmlRootElement(name = "comments")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestComments implements Iterable<RestComment> {

  RestComments() {
  }

  private List<RestComment> comments = Lists.newArrayList();


  @Override
  public Iterator<RestComment> iterator() {
    return comments.iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("comments", comments)
      .toString();
  }
}
