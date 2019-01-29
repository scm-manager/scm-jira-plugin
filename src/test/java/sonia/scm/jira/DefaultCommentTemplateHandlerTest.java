package sonia.scm.jira;

import org.junit.Before;
import org.junit.Test;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Person;
import sonia.scm.repository.Repository;

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
    when(changeset.getAuthor()).thenReturn(new Person("Arthur Dent"));
    request = mock(JiraIssueRequest.class);
    when(request.getConfiguration()).thenReturn(mock(JiraConfiguration.class));
    when(request.getRepository()).thenReturn(new Repository("id", "git", "space", "X"));
    ScmConfiguration configuration = mock(ScmConfiguration.class);
    defaultCommentTemplateHandler = new DefaultCommentTemplateHandler(null, new LinkHandler(configuration)) {
      @Override
      String getSystemLineSeparator() {
        return systemLineSeparator;
      }
    };
  }
}
