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
import sonia.scm.jira.rest.property.RestInternalProperty;

//~--- JDK imports ------------------------------------------------------------

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * {@link RestComment} represents a comment sent to a Jira instance.<br/>
 * Note that the properties is realized with a list of {@link RestInternalProperty} instances
 * so that an array is sent as part of the JSON.
 * A more abstract and appropriate RestProperty object is not implemented.
 */
@Getter
@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestComment {

  private String body;

  private List<RestInternalProperty> properties = new ArrayList<>();

  private String id;

  private RestVisibility visibility;

  RestComment() {
  }

  public RestComment(String body) {
    this(body, null);
  }

  public RestComment(String body, String role) {
    this.body = body;
    this.properties.add(new RestInternalProperty());
    if (!Strings.isNullOrEmpty(role)) {
      this.visibility = new RestVisibility(role);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .add("body", body)
      .add("properties", properties)
      .add("visibility", visibility)
      .toString();
  }
}
