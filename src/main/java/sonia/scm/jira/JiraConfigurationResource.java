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
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.jira.resubmit.ResubmitCommentsHandler;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.web.VndMediaType;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
 * @author Sebastian Sdorra
 */
@OpenAPIDefinition(tags = {
  @Tag(name = "Jira Plugin", description = "Jira plugin provided endpoints")
})
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
  @Operation(
    summary = "Get global jira configuration",
    description = "Returns the global jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_get_global_config"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = JiraGlobalConfigurationDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user has no privileges to read the configuration")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response get() {
    permissions.checkReadGlobalConfig();
    return Response.ok(jiraGlobalConfigurationMapper.map(context.getGlobalConfiguration())).build();
  }

  @PUT
  @Path("/")
  @Consumes({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Update global jira configuration",
    description = "Modifies the global jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_put_global_config"
  )
  @ApiResponse(responseCode = "204", description = "update success")
  @ApiResponse(responseCode = "400", description = "invalid body")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the privilege to change the configuration")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response update(@Valid JiraGlobalConfigurationDto updatedConfig) {
    context.setGlobalConfiguration(jiraGlobalConfigurationMapper.map(updatedConfig, context.getGlobalConfiguration()));
    return Response.noContent().build();
  }

  @POST
  @Path("/resubmit")
  @Operation(
    summary = "Resubmit global jira configuration",
    description = "Resubmits the pending global jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_resubmit_global_config"
  )
  @ApiResponse(responseCode = "204", description = "update success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the privilege to change the configuration")
  @ApiResponse(
    responseCode = "404",
    description = "not found, no repository with the specified namespace and name available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response resubmitAll() throws IOException {
    resubmitCommentsHandler.resubmitAll();
    return Response.noContent().build();
  }

  @GET
  @Path("/{namespace}/{name}")
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Get repository jira configuration",
    description = "Returns the repository jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_get_repo_config"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = JiraConfigurationDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user has no privileges to read the configuration")
  @ApiResponse(
    responseCode = "404",
    description = "not found, no repository with the specified namespace and name available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response getForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    Repository repository = loadRepository(namespace, name);
    permissions.checkReadRepositoryConfig(repository);
    return Response.ok(jenkinsConfigurationMapper.map(context.getConfiguration(repository), repository)).build();
  }

  @PUT
  @Path("/{namespace}/{name}")
  @Consumes({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Modify repository jira configuration",
    description = "Modifies the repository jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_put_repo_config"
  )
  @ApiResponse(responseCode = "204", description = "update success")
  @ApiResponse(responseCode = "400", description = "invalid body")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the privilege to change the configuration")
  @ApiResponse(
    responseCode = "404",
    description = "not found, no repository with the specified namespace and name available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response updateForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name, @Valid JiraConfigurationDto updatedConfig) {
    Repository repository = loadRepository(namespace, name);
    context.setConfiguration(jenkinsConfigurationMapper.map(updatedConfig, context.getConfiguration(repository)), repository);
    return Response.noContent().build();
  }

  @POST
  @Path("/{namespace}/{name}/resubmit")
  @Operation(
    summary = "Resubmit repository jira configuration",
    description = "Resubmits the pending repository jira configuration.",
    tags = "Jira Plugin",
    operationId = "jira_resubmit_repo_config"
  )
  @ApiResponse(responseCode = "204", description = "update success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the privilege to change the configuration")
  @ApiResponse(
    responseCode = "404",
    description = "not found, no repository with the specified namespace and name available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response resubmitForRepository(@PathParam("namespace") String namespace, @PathParam("name") String name) throws IOException {
    Repository repository = loadRepository(namespace, name);
    resubmitCommentsHandler.resubmitAllFromRepository(repository.getId());
    return Response.noContent().build();
  }

  @DELETE
  @Path("/resubmit/comment/{id}")
  @Operation(
    summary = "Remove resubmit comment",
    description = "Removes a resubmit comment.",
    tags = "Jira Plugin",
    operationId = "jira_delete_comment"
  )
  @ApiResponse(responseCode = "204", description = "comment removed")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the privilege to change the configuration")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
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
