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

import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.repository.Changeset;

/**
 * Unit tests for {@link JiraIssueHandler}.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class JiraIssueHandlerTest {

    @Mock
    private MessageProblemHandler problemHandler;

    @Mock
    private JiraIssueRequest request;

    @Mock
    private CommentTemplateHandler templateHandler;

    @Mock
    private JiraConfiguration configuration;

    @InjectMocks
    private JiraIssueHandler issueHandler;

    /**
     * Set up mocks for testing.
     */
    @Before
    public void setUp() {
        when(request.getConfiguration()).thenReturn(configuration);
    }

    /**
     * Tests {@link JiraIssueHandler#searchAutoCloseWord(Changeset)}.
     */
    @Test
    public void testSearchAutoCloseWords() {
        assertEquals("close", autoCloseWord("close the issue", "close"));
        assertEquals("close", autoCloseWord("the word to close the issue is in the middle", "close"));
        assertEquals("close", autoCloseWord("description with auto close", "close"));
        assertEquals("close", autoCloseWord("description with auto close", "other", "close"));
        assertEquals("close", autoCloseWord("description with auto Close", "close"));
        assertEquals("Close", autoCloseWord("description with auto close", "Close"));
        assertEquals("auto Close", autoCloseWord("description with auto close", "auto Close"));
    }

    private String autoCloseWord(String description, String... autoCloseWords) {
        Set<String> autoCloseWordSet = Sets.newHashSet(autoCloseWords);
        when(configuration.getAutoCloseWords()).thenReturn(autoCloseWordSet);
        return issueHandler.searchAutoCloseWord(changeset(description));
    }

    private Changeset changeset(String description) {
        Changeset changeset = new Changeset();
        changeset.setDescription(description);
        return changeset;
    }
}