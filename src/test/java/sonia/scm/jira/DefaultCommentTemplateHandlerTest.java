package sonia.scm.jira;

import org.junit.Before;
import org.junit.Test;
import sonia.scm.repository.Changeset;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultCommentTemplateHandlerTest {

    private DefaultCommentTemplateHandler defaultCommentTemplateHandler;
    private JiraIssueRequest request;
    private Changeset changeset;

    private String systemLineSeparator = "\n";

    @Test
    public void shouldNotSplitSingleLine() {
        when(changeset.getDescription()).thenReturn("description");
        Map<String, Object> env = defaultCommentTemplateHandler.createBaseEnvironment(request, changeset);
        assertEquals(asList("description"), env.get("descriptionLine"));
    }

    @Test
    public void shouldSplitWithUnixLineSeparator() {
        when(changeset.getDescription()).thenReturn("one\ntwo");
        Map<String, Object> env = defaultCommentTemplateHandler.createBaseEnvironment(request, changeset);
        assertEquals(asList("one", "two"), env.get("descriptionLine"));
    }

    @Test
    public void shouldSplitWithWindowsLineSeparator() {
        systemLineSeparator = "\r\n";
        when(changeset.getDescription()).thenReturn("one\r\ntwo");
        Map<String, Object> env = defaultCommentTemplateHandler.createBaseEnvironment(request, changeset);
        assertEquals(asList("one", "two"), env.get("descriptionLine"));
    }

    @Test
    public void shouldSplitWithUnixLineSeparatorEvenWhenOtherSeparatorIsConfigured() {
        systemLineSeparator = "\r\n";
        when(changeset.getDescription()).thenReturn("one\ntwo");
        Map<String, Object> env = defaultCommentTemplateHandler.createBaseEnvironment(request, changeset);
        assertEquals(asList("one", "two"), env.get("descriptionLine"));
    }

    @Before
    public void init() {
        changeset = mock(Changeset.class);
        request = mock(JiraIssueRequest.class);
        when(request.getConfiguration()).thenReturn(mock(JiraConfiguration.class));
        defaultCommentTemplateHandler = new DefaultCommentTemplateHandler(null, mock(LinkHandler.class)) {
            @Override
            String getSystemLineSeparator() {
                return systemLineSeparator;
            }
        };
    }
}
