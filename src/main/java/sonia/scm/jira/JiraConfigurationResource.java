/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
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
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
 */


package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import sonia.scm.jira.resubmit.ResubmitCommentsHandler;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
@Path(JiraConfigurationResource.JIRA_CONFIG_PATH_V2)
public class JiraConfigurationResource {

  static final String JIRA_CONFIG_PATH_V2 = "v2/config/jira";

  private final JiraGlobalContext context;
  private final JiraPermissions permissions;
  private final JiraGlobalConfigurationMapper jiraGlobalConfigurationMapper;
  private final JiraConfigurationMapper jenkinsConfigurationMapper;
  private final RepositoryManager repositoryManager;
  private final ResubmitCommentsHandler resubmitCommentsHandler;

  @Inject
  public JiraConfigurationResource(
    JiraGlobalContext context,
    JiraPermissions permissions,
    JiraGlobalConfigurationMapper jiraGlobalConfigurationMapper,
    JiraConfigurationMapper jenkinsConfigurationMapper,
    RepositoryManager repositoryManager,
    ResubmitCommentsHandler resubmitCommentsHandler) {
    this.context = context;
    this.permissions = permissions;
    this.jiraGlobalConfigurationMapper = jiraGlobalConfigurationMapper;
    this.jenkinsConfigurationMapper = jenkinsConfigurationMapper;
    this.repositoryManager = repositoryManager;
    this.resubmitCommentsHandler = resubmitCommentsHandler;
  }

  @GET
  @Path("/")
  @Produces({MediaType.APPLICATION_JSON})
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user has no privileges to read the configuration"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response get() {
    permissions.checkReadGlobalConfig();

    return Response.ok(jiraGlobalConfigurationMapper.map(context.getGlobalConfiguration())).build();
  }

  @PUT
  @Path("/")
  @Consumes({MediaType.APPLICATION_JSON})
  @StatusCodes({
    @ResponseCode(code = 204, condition = "update success"),
    @ResponseCode(code = 400, condition = "Invalid body,"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege to change the configuration"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response update(@Valid JiraGlobalConfigurationDto updatedConfig) {
    context.setGlobalConfiguration(jiraGlobalConfigurationMapper.map(updatedConfig, context.getGlobalConfiguration()));

    return Response.noContent().build();
  }

  @POST
  @Path("/resubmit")
  @StatusCodes({
    @ResponseCode(code = 204, condition = "resubmit triggered successfully"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege to change the configuration"),
    @ResponseCode(code = 404, condition = "not found, no repository with the specified namespace and name available"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response resubmitAll() throws IOException {
    resubmitCommentsHandler.resubmitAll();
    return Response.noContent().build();
  }

  @GET
  @Path("/{namespace}/{name}")
  @Produces({MediaType.APPLICATION_JSON})
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user has no privileges to read the configuration"),
    @ResponseCode(code = 404, condition = "not found, no repository with the specified namespace and name available"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response getForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    Repository repository = loadRepository(namespace, name);
    permissions.checkReadRepositoryConfig(repository);

    return Response.ok(jenkinsConfigurationMapper.map(context.getConfiguration(repository), repository)).build();
  }

  @PUT
  @Path("/{namespace}/{name}")
  @Consumes({MediaType.APPLICATION_JSON})
  @StatusCodes({
    @ResponseCode(code = 204, condition = "update success"),
    @ResponseCode(code = 400, condition = "Invalid body,"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege to change the configuration"),
    @ResponseCode(code = 404, condition = "not found, no repository with the specified namespace and name available"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response updateForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name, @Valid JiraConfigurationDto updatedConfig) {
    Repository repository = loadRepository(namespace, name);
    context.setConfiguration(jenkinsConfigurationMapper.map(updatedConfig, context.getConfiguration(repository)), repository);

    return Response.noContent().build();
  }

  @POST
  @Path("/{namespace}/{name}/resubmit")
  @StatusCodes({
    @ResponseCode(code = 204, condition = "resubmit triggered successfully"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege to change the configuration"),
    @ResponseCode(code = 404, condition = "not found, no repository with the specified namespace and name available"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response resubmitForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name) throws IOException {
    Repository repository = loadRepository(namespace, name);
    resubmitCommentsHandler.resubmitAllFromRepository(repository.getId());
    return Response.noContent().build();
  }

  @POST
  @Path("/resubmit/comment/{id}/remove")
  @StatusCodes({
    @ResponseCode(code = 201, condition = "comment removed"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege to change the configuration"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response removeCommentFromResubmitQueue(@PathParam("id") String commentId) {
    resubmitCommentsHandler.removeComment(commentId);
    return Response.noContent().build();
  }

  private Repository loadRepository(String namespace, String name) {
    Repository repository = repositoryManager.get(new NamespaceAndName(namespace, name));
    if (repository == null) {
      throw notFound(entity(new NamespaceAndName(namespace, name)));
    }
    return repository;
  }
}
