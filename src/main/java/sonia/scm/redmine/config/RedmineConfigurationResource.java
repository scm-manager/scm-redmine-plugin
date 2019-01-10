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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.redmine.Constants;
import sonia.scm.redmine.RedmineIssueTracker;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

/**
 * @author Sebastian Sdorra
 */
@Path("v2/redmine/configuration")
public class RedmineConfigurationResource {

  private RedmineIssueTracker tracker;
  private RedmineConfigurationMapper mapper;
  private RepositoryManager repositoryManager;

  private static final Logger logger =
    LoggerFactory.getLogger(RedmineConfigurationResource.class);

  @Inject
  public RedmineConfigurationResource(RedmineIssueTracker tracker, RedmineConfigurationMapper mapper, RepositoryManager repositoryManager) {
    if (!ConfigurationPermissions.write(Constants.NAME).isPermitted()) {
      logger.warn("user has not enough privileges to change global redmine configuration");

      throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    this.tracker = tracker;
    this.mapper = mapper;
    this.repositoryManager = repositoryManager;
  }

  @PUT
  @Path("/")
  @Consumes({MediaType.APPLICATION_JSON})
  public Response updateGlobalConfiguration(RedmineGlobalConfigurationDto updatedConfig) {
    tracker.setGlobalConfiguration(mapper.map(updatedConfig));

    return Response.ok().build();
  }

  @GET
  @Path("/")
  @Produces({MediaType.APPLICATION_JSON})
  public Response getGlobalConfiguration() {
    return Response.ok(mapper.map(tracker.getGlobalConfiguration())).build();
  }

  @PUT
  @Path("/{namespace}/{name}")
  @Consumes({MediaType.APPLICATION_JSON})
  public Response updateConfiguration(RedmineConfigurationDto updatedConfig) {
    //TODO
//    tracker.setConfiguration(mapper.map(updatedConfig));

    return Response.ok().build();
  }

  @GET
  @Path("/{namespace}/{name}")
  @Produces({MediaType.APPLICATION_JSON})
  public Response getConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    Repository repository = loadRepository(namespace, name);
    return Response.ok(repository).build();
  }

  private Repository loadRepository(String namespace, String name) {
    Repository repository = repositoryManager.get(new NamespaceAndName(namespace, name));
    if (repository == null) {
      throw notFound(entity(new NamespaceAndName(namespace, name)));
    }
    return repository;
  }

}
