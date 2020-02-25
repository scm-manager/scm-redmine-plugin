/***
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
  @Operation(summary = "Update global redmine configuration", description = "Modifies the global redmine configuration.", tags = "Redmine Plugin")
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
  @Operation(summary = "Get global redmine configuration", description = "Returns the global redmine configuration.", tags = "Redmine Plugin")
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
  @Operation(summary = "Update repository-specific redmine configuration", description = "Modifies the repository-specific redmine configuration.", tags = "Redmine Plugin")
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
  @Operation(summary = "Get repository-specific redmine configuration", description = "Returns the repository-specific redmine configuration.", tags = "Redmine Plugin")
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
