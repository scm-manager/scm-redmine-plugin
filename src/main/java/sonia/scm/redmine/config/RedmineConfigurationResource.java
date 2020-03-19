/**
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
package sonia.scm.redmine.config;

import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.redmine.Constants;
import sonia.scm.redmine.RedmineIssueTracker;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.web.VndMediaType;

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

/**
 * @author Sebastian Sdorra
 */
@OpenAPIDefinition(tags = {
  @Tag(name = "Redmine Plugin", description = "Redmine plugin provided endpoints")
})
@Path("v2/redmine/configuration")
public class RedmineConfigurationResource {

  private RedmineIssueTracker tracker;
  private RedmineConfigurationMapper mapper;
  private RepositoryManager repositoryManager;

  @Inject
  public RedmineConfigurationResource(RedmineIssueTracker tracker, RedmineConfigurationMapper mapper, RepositoryManager repositoryManager) {
    this.tracker = tracker;
    this.mapper = mapper;
    this.repositoryManager = repositoryManager;
  }

  @PUT
  @Path("/")
  @Consumes({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Update global redmine configuration",
    description = "Modifies the global redmine configuration.",
    tags = "Redmine Plugin",
    operationId = "redmine_update_global_config"
  )
  @ApiResponse(responseCode = "200", description = "update success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response updateGlobalConfiguration(RedmineGlobalConfigurationDto updatedConfig) {
    ConfigurationPermissions.write(Constants.NAME).check();
    tracker.setGlobalConfiguration(mapper.map(updatedConfig, tracker.getGlobalConfiguration()));
    return Response.ok().build();
  }

  @GET
  @Path("/")
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Get global redmine configuration",
    description = "Returns the global redmine configuration.",
    tags = "Redmine Plugin",
    operationId = "redmine_get_global_config"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = RedmineGlobalConfigurationDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response getGlobalConfiguration() {
    ConfigurationPermissions.read(Constants.NAME).check();
    return Response.ok(mapper.map(tracker.getGlobalConfiguration())).build();
  }

  @PUT
  @Path("/{namespace}/{name}")
  @Consumes({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Update repository-specific redmine configuration",
    description = "Modifies the repository-specific redmine configuration.",
    tags = "Redmine Plugin",
    operationId = "redmine_update_repo_config"
  )
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response updateConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name, RedmineConfigurationDto updatedConfig) {
    Repository repository = loadRepository(namespace, name);
    RepositoryPermissions.custom(Constants.NAME, repository).check();
    tracker.setRepositoryConfiguration(mapper.map(updatedConfig, tracker.getRepositoryConfiguration(repository)), repository);
    return Response.ok().build();
  }

  @GET
  @Path("/{namespace}/{name}")
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(
    summary = "Get repository-specific redmine configuration",
    description = "Returns the repository-specific redmine configuration.",
    tags = "Redmine Plugin",
    operationId = "redmine_get_repo_config")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = RedmineConfigurationDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / no repository available for given parameters",
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
  public Response getConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    Repository repository = loadRepository(namespace, name);
    RepositoryPermissions.custom(Constants.NAME, repository).check();
    RedmineConfiguration configuration = tracker.getRepositoryConfigurationOrEmpty(repository);
    return Response.ok(mapper.map(configuration, repository)).build();
  }

  private Repository loadRepository(String namespace, String name) {
    Repository repository = repositoryManager.get(new NamespaceAndName(namespace, name));
    if (repository == null) {
      throw notFound(entity(new NamespaceAndName(namespace, name)));
    }
    return repository;
  }

}
