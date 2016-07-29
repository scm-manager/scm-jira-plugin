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

import org.junit.Test;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sonia.scm.repository.Changeset;

/**
 * Unit tests for {@link JiraHookProcessor}.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class JiraHookProcessorTest extends JiraTestBase {

  @Mock
  private JiraGlobalContext context;
  
  @Mock
  private JiraIssueRequest request;
  
  @Mock
  private JiraIssueHandler issueHandler;

  @InjectMocks
  private JiraHookProcessor processor;
  
  @Before
  public void setUpMocks(){
    when(request.getRepository()).thenReturn(repository);
  }
  
  /**
   * Testing {@link JiraHookProcessor#process(JiraIssueHandler, JiraIssueRequest, Changeset)}.
   */
  @Test
  public void testProcess() {
    description("TST-1 are ready to review");
    processor.process(issueHandler, request, changeset);
    verify(issueHandler).handleIssue("TST-1", changeset);
    verify(context).markAsHandled(repository, changeset);
  }

  /**
   * Testing {@link JiraHookProcessor#process(JiraIssueHandler, JiraIssueRequest, Changeset)} with multiple issue ids.
   */
  @Test
  public void testProcessWithMultipleIssueIds() {
    description("TST-1 and TST-2 are ready to review and we have fixed TST-3");
    processor.process(issueHandler, request, changeset);
    verify(issueHandler).handleIssue("TST-1", changeset);
    verify(issueHandler).handleIssue("TST-2", changeset);
    verify(issueHandler).handleIssue("TST-3", changeset);
    verify(context).markAsHandled(repository, changeset);
  }

  /**
   * Testing {@link JiraHookProcessor#process(JiraIssueHandler, JiraIssueRequest, Changeset)} with already handled changeset.
   */
  @Test
  public void testProcessWithAlreadyHandledChangeset() {
    description("TST-1");
    when(context.isHandled(repository, changeset)).thenReturn(true);
    processor.process(issueHandler, request, changeset);
    verify(issueHandler, never()).handleIssue("TST-1", changeset);
    verify(context, never()).markAsHandled(repository, changeset);
  }

  /**
   * Testing {@link JiraHookProcessor#process(JiraIssueHandler, JiraIssueRequest, Changeset)} with multiple times the 
   * same issue id.
   */
  @Test
  public void testProcessCallsHandleIssueOnlyOnce() {
    description("TST-1, TST-2 and TST-1");
    processor.process(issueHandler, request, changeset);
    verify(issueHandler).handleIssue("TST-1", changeset);
    verify(issueHandler).handleIssue("TST-2", changeset);
  }

}
