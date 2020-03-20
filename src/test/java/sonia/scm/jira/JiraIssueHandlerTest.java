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
    private CommentTemplateHandlerFactory templateHandlerFactory;

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
      when(templateHandlerFactory.create(any())).thenReturn(templateHandler);
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
