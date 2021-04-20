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
package sonia.scm.jira.config;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.jira.JiraPermissions;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.web.VndMediaType;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

  private final JiraConfigurationStore context;
  private final JiraPermissions permissions;
  private final JiraGlobalConfigurationMapper jiraGlobalConfigurationMapper;
  private final JiraConfigurationMapper jenkinsConfigurationMapper;
  private final RepositoryManager repositoryManager;

  @Inject
  public JiraConfigurationResource(
    JiraConfigurationStore context,
    JiraPermissions permissions,
    JiraGlobalConfigurationMapper jiraGlobalConfigurationMapper,
    JiraConfigurationMapper jenkinsConfigurationMapper,
    RepositoryManager repositoryManager) {
    this.context = context;
    this.permissions = permissions;
    this.jiraGlobalConfigurationMapper = jiraGlobalConfigurationMapper;
    this.jenkinsConfigurationMapper = jenkinsConfigurationMapper;
    this.repositoryManager = repositoryManager;
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


  private Repository loadRepository(String namespace, String name) {
    Repository repository = repositoryManager.get(new NamespaceAndName(namespace, name));
    if (repository == null) {
      throw notFound(entity(new NamespaceAndName(namespace, name)));
    }
    return repository;
  }
}
