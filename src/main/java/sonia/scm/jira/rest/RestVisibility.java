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
import lombok.Getter;

/**
 * Jira rest api visibility of a comment.
 *
 * @author Sebastian Sdorra
 */
@Getter
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestVisibility {
  RestVisibility() {
  }

  /**
   * The type has to be either 'role' or 'group' and MUST NOT be serialized in JSON to all-caps or something else.
   * Other values would lead to a 400 status code from Jira.
   */
  private final String type = "role";

  private String value;

  public RestVisibility(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("type", type)
      .add("value", value)
      .toString();
  }
}
