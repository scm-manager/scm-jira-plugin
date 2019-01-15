package sonia.scm.jira;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.io.Resources;
import org.apache.shiro.authz.UnauthorizedException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.UnhandledException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sonia.scm.NotFoundException;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.store.InMemoryConfigurationStore;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.jboss.resteasy.mock.MockDispatcherFactory.createDispatcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SubjectAware(configuration = "classpath:sonia/scm/jira/shiro-001.ini")
public class JiraConfigurationResourceTest {

  public static final Repository REPOSITORY = new Repository("X", "git", "space", "X");
  @Rule
  public ShiroRule shiroRule = new ShiroRule();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private Dispatcher dispatcher;
  private RepositoryManager repositoryManager;

  @Before
  public void init() {
    InMemoryConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory(new InMemoryConfigurationStore());
    JiraPermissions permissions = new JiraPermissions();
    JiraGlobalContext context = new JiraGlobalContext(storeFactory, permissions);
    repositoryManager = mock(RepositoryManager.class);
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("/"));
    JiraGlobalConfigurationMapperImpl jiraGlobalConfigurationMapper = new JiraGlobalConfigurationMapperImpl();
    jiraGlobalConfigurationMapper.setScmPathInfoStore(scmPathInfoStore);
    jiraGlobalConfigurationMapper.setPermissions(permissions);
    JiraConfigurationMapperImpl jenkinsConfigurationMapper = new JiraConfigurationMapperImpl();
    jenkinsConfigurationMapper.setScmPathInfoStore(scmPathInfoStore);
    jenkinsConfigurationMapper.setPermissions(permissions);
  JiraConfigurationResource resource = new JiraConfigurationResource(context, permissions, jiraGlobalConfigurationMapper, jenkinsConfigurationMapper, repositoryManager, null);
    dispatcher = createDispatcher();
    dispatcher.getRegistry().addSingletonResource(resource);
    when(repositoryManager.get(REPOSITORY.getNamespaceAndName())).thenReturn(REPOSITORY);
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void adminShouldGetConfigWithUpdateLink() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(HttpServletResponse.SC_OK, response.getStatus());

    assertTrue(response.getContentAsString().contains("\"autoClose\":false"));
    assertTrue(response.getContentAsString().contains("\"self\""));
    assertTrue(response.getContentAsString().contains("\"update\""));
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void adminShouldSetConfig() throws URISyntaxException, IOException {
    URL url = Resources.getResource("sonia/scm/jira/jiraConfig.json");
    byte[] configJson = Resources.toByteArray(url);
    MockHttpRequest request = MockHttpRequest.put("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2).contentType(MediaType.APPLICATION_JSON_TYPE).content(configJson);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

    MockHttpRequest readRequest = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2);
    MockHttpResponse readResponse = new MockHttpResponse();
    dispatcher.invoke(readRequest, readResponse);
    assertEquals(HttpServletResponse.SC_OK, readResponse.getStatus());
    assertTrue(readResponse.getContentAsString().contains("\"autoClose\":true"));
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void normalUserShouldNotGetConfig() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2);
    MockHttpResponse response = new MockHttpResponse();

    expectedException.expect(new ExceptionMatcher<>(UnauthorizedException.class));

    dispatcher.invoke(request, response);
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void normalUserShouldNotSetConfig() throws URISyntaxException, IOException {
    URL url = Resources.getResource("sonia/scm/jira/jiraConfig.json");
    byte[] configJson = Resources.toByteArray(url);
    MockHttpRequest request = MockHttpRequest.put("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2).contentType(MediaType.APPLICATION_JSON_TYPE).content(configJson);
    MockHttpResponse response = new MockHttpResponse();

    expectedException.expect(new ExceptionMatcher<>(UnauthorizedException.class));

    dispatcher.invoke(request, response);
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldHandleNotExistingRepository() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/not/there");
    MockHttpResponse response = new MockHttpResponse();

    expectedException.expect(new ExceptionMatcher<>(NotFoundException.class));

    dispatcher.invoke(request, response);
  }

  @Test
  @SubjectAware(username = "marvin", password = "secret")
  public void repositoryOwnerShouldGetConfigWithUpdateLink() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(HttpServletResponse.SC_OK, response.getStatus());

    assertTrue(response.getContentAsString().contains("\"autoClose\":false"));
    assertTrue(response.getContentAsString().contains("\"self\""));
    assertTrue(response.getContentAsString().contains("\"update\""));
  }

  @Test
  @SubjectAware(username = "marvin", password = "secret")
  public void repositoryOwnerShouldSetConfig() throws URISyntaxException, IOException {
    URL url = Resources.getResource("sonia/scm/jira/jiraConfig.json");
    byte[] configJson = Resources.toByteArray(url);
    MockHttpRequest request = MockHttpRequest.put("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X").contentType(MediaType.APPLICATION_JSON_TYPE).content(configJson);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

    MockHttpRequest readRequest = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X");
    MockHttpResponse readResponse = new MockHttpResponse();
    dispatcher.invoke(readRequest, readResponse);
    assertEquals(HttpServletResponse.SC_OK, readResponse.getStatus());
    assertTrue(readResponse.getContentAsString().contains("\"autoClose\":true"));
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void repositoryReaderShouldNotSetConfig() throws URISyntaxException, IOException {
    URL url = Resources.getResource("sonia/scm/jira/jiraConfig.json");
    byte[] configJson = Resources.toByteArray(url);
    MockHttpRequest request = MockHttpRequest.put("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X").contentType(MediaType.APPLICATION_JSON_TYPE).content(configJson);
    MockHttpResponse response = new MockHttpResponse();

    expectedException.expect(new ExceptionMatcher<>(UnauthorizedException.class));

    dispatcher.invoke(request, response);
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void repositoryReaderShouldNotGetConfig() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X");
    MockHttpResponse response = new MockHttpResponse();

    expectedException.expect(new ExceptionMatcher<>(UnauthorizedException.class));

    dispatcher.invoke(request, response);
  }

  private static class ExceptionMatcher<E extends Exception> extends BaseMatcher<E> {

    private final Class<E> expected;

    private ExceptionMatcher(Class<E> expected) {
      this.expected = expected;
    }

    @Override
    public boolean matches(Object exception) {
      return exception instanceof UnhandledException && ((UnhandledException) exception).getCause().getClass().isAssignableFrom(expected);
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("expected " + expected);
    }
  }
}
