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

package sonia.scm.jira.config;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;

public class BaseMapper {

  @SuppressWarnings("squid:S2068") // we have no password here
  static final String DUMMY_PASSWORD = "__DUMMY__";

  @AfterMapping
  void replaceDummyWithOldPassword(@MappingTarget JiraConfiguration target, @Context JiraConfiguration oldConfiguration) {
    if (DUMMY_PASSWORD.equals(target.getPassword())) {
      target.setPassword(oldConfiguration.getPassword());
    }
  }

  @AfterMapping
  void replacePasswordWithDummy(@MappingTarget JiraConfigurationDto target) {
    if (StringUtils.isNotEmpty(target.getPassword())) {
      target.setPassword(DUMMY_PASSWORD);
    }
  }
}
