/**
 * *
 * Copyright (c) 2015, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * https://bitbucket.org/sdorra/scm-manager
 *
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
