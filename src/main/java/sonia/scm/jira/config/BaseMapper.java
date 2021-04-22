/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
