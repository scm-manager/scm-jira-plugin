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

/**
 * Visibility type for {@link RestComment} object, not to be confused with the internal flag.
 * Currently, the only type supported are roles with their values as concrete role names.
 *
 * @see RestComment
 */
public enum RestVisibilityType {
  ROLE("role");

  RestVisibilityType(String name) {
    this.name = name;
  }

  private final String name;

  @Override
  public String toString() {
    return name;
  }
}
