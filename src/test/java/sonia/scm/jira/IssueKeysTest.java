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

package sonia.scm.jira;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link IssueKeys}.
 *
 * @author Sebastian Sdorra <s.sdorra@gmail.com>
 */
public class IssueKeysTest {


  /**
   * Tests {@link IssueKeys#createPattern(java.lang.String)}.
   */
  @Test
  public void testCreatePattern() {
    Pattern pattern = IssueKeys.createPattern("ASD");
    assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
  }

  /**
   * Tests {@link IssueKeys#createPattern(java.lang.String)} with an empty string.
   */
  @Test
  public void testCreatePatternWithoutConfiguration() {
    Pattern pattern = IssueKeys.createPattern("");
    assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
  }

  /**
   * Tests {@link IssueKeys#createPattern(java.lang.String)} with multiple project keys.
   */
  @Test
  public void testCreatePatternWithMultipleProjectKeys() {
    Pattern pattern = IssueKeys.createPattern("SCM, TST,ASD");
    assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
  }

  @Test
  public void testShouldNotFailOnNull() {
    Pattern pattern = IssueKeys.createPattern((String) null);
    assertEquals("ASD-42", extract(pattern, "test matcher for ASD-42"));
  }

  private String extract(Pattern pattern, String message) {
    Matcher matcher = pattern.matcher(message);
    matcher.find();
    return matcher.group(1);
  }
}
