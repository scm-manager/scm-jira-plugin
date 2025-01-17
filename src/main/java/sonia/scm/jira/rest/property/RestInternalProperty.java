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

package sonia.scm.jira.rest.property;

import com.google.common.base.MoreObjects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Currently, this only supports {@link RestInternalPropertyValue} as a value.
 */
@Getter
@EqualsAndHashCode
@XmlRootElement(name = "sd.public.comment")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestInternalProperty {

  public RestInternalProperty() {
    this.key = "sd.public.comment";
    this.value = new RestInternalPropertyValue();
  }

  private String key;
  private RestInternalPropertyValue value;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("key", key)
      .add("value", value)
      .toString();
  }


}
