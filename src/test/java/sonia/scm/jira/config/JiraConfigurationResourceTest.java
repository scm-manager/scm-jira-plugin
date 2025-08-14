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

package sonia.scm.jira.config;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.io.Resources;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.UnhandledException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.web.RestDispatcher;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SubjectAware(configuration = "classpath:sonia/scm/jira/shiro-001.ini")
public class JiraConfigurationResourceTest {

  public static final Repository REPOSITORY = new Repository("X", "git", "space", "X");
  @Rule
  public ShiroRule shiroRule = new ShiroRule();

  private RestDispatcher dispatcher;

  @Before
  public void init() {
    InMemoryConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();
    JiraConfigurationStore context = new JiraConfigurationStore(storeFactory);
    RepositoryManager repositoryManager = mock(RepositoryManager.class);
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("/"));
    JiraGlobalConfigurationMapperImpl jiraGlobalConfigurationMapper = new JiraGlobalConfigurationMapperImpl();
    jiraGlobalConfigurationMapper.setScmPathInfoStore(scmPathInfoStore);
    JiraConfigurationMapperImpl jenkinsConfigurationMapper = new JiraConfigurationMapperImpl();
    jenkinsConfigurationMapper.setScmPathInfoStore(scmPathInfoStore);
    JiraConfigurationResource resource = new JiraConfigurationResource(context, jiraGlobalConfigurationMapper, jenkinsConfigurationMapper, repositoryManager);
    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
    when(repositoryManager.get(REPOSITORY.getNamespaceAndName())).thenReturn(REPOSITORY);
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void adminShouldGetConfigWithUpdateLink() throws URISyntaxException, UnsupportedEncodingException {
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
    assertTrue(readResponse.getContentAsString().contains("\"start\":\"start issue\""));
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void normalUserShouldNotGetConfig() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void normalUserShouldNotSetConfig() throws URISyntaxException, IOException {
    URL url = Resources.getResource("sonia/scm/jira/jiraConfig.json");
    byte[] configJson = Resources.toByteArray(url);
    MockHttpRequest request = MockHttpRequest.put("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2).contentType(MediaType.APPLICATION_JSON_TYPE).content(configJson);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldHandleNotExistingRepository() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/not/there");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(404, response.getStatus());
  }

  @Test
  @SubjectAware(username = "marvin", password = "secret")
  public void repositoryOwnerShouldGetConfigWithUpdateLink() throws URISyntaxException, UnsupportedEncodingException {
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

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void repositoryReaderShouldNotGetConfig() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/" + JiraConfigurationResource.JIRA_CONFIG_PATH_V2 + "/space/X");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
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
