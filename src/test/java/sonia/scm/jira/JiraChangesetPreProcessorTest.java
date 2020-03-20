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
package sonia.scm.jira;

import java.util.regex.Matcher;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link JiraChangesetPreProcessor}.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class JiraChangesetPreProcessorTest extends JiraTestBase {

  private JiraChangesetPreProcessor processor;

  /**
   * Set up mocks for tests.
   */
  @Before
  public void setUpMocks() {
    processor = new JiraChangesetPreProcessor(IssueKeys.createPattern(""), "_$0_");
  }

  /**
   * Unit tests for the key pattern.
   */
  @Test
  public void testKeyPattern() {
    assertTrue(processor.matcher("TST-1").find());
    assertTrue(processor.matcher("TST-1 and some string").find());
    assertTrue(processor.matcher("some string TST-1").find());
    assertTrue(processor.matcher("some string TST-1 more string").find());
    Matcher m = processor.matcher("TST-1 and TST-2 are equal with TST-3");
    assertTrue(m.find());
    assertEquals("TST-1", m.group());
    assertTrue(m.find());
    assertEquals("TST-2", m.group());
    assertTrue(m.find());
    assertEquals("TST-3", m.group());
    assertFalse(m.find());
  }

  /*
   * Testing {@link JiraChangesetPreProcessor#process(Changeset)}.
   */
  @Test
  public void testProcess() {
    description("TST-1 are ready to review");
    processor.process(changeset);
    verify(changeset).setDescription("_TST-1_ are ready to review");
  }
  
  /*
   * Testing {@link JiraChangesetPreProcessor#process(Changeset)}.
   */
  @Test
  public void testProcessWithMultipleKeys() {
    description("TST-1, TST-2 and TST-3 are ready to review");
    processor.process(changeset);
    verify(changeset).setDescription("_TST-1_, _TST-2_ and _TST-3_ are ready to review");
  }
  
  /*
   * Testing {@link JiraChangesetPreProcessor#process(Changeset)} without issue key.
   */
  @Test
  public void testProcessWithoutKey() {
    description("description without key");
    processor.process(changeset);
    verify(changeset).setDescription("description without key");
  }
}
