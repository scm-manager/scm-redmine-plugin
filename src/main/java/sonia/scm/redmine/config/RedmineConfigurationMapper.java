package sonia.scm.redmine.config;

import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.BaseMapper;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.redmine.Constants;

import javax.inject.Inject;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class RedmineConfigurationMapper extends BaseMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;


  public abstract RedmineConfigurationDto map(RedmineConfiguration configuration);

  public abstract RedmineConfiguration map(RedmineConfigurationDto configurationDto);

  public abstract RedmineGlobalConfigurationDto map(RedmineGlobalConfiguration configuration);

  public abstract RedmineGlobalConfiguration map(RedmineGlobalConfigurationDto configurationDto);

  @AfterMapping
  public void addLinks(RedmineConfiguration source, @MappingTarget RedmineConfigurationDto target) {
    Links.Builder linksBuilder = linkingTo().self(self());
    if (ConfigurationPermissions.write(Constants.NAME).isPermitted()) {
      linksBuilder.single(Link.link("update", update()));
    }
    target.add(linksBuilder.build());
  }

  private String self() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), RedmineConfigurationResource.class);
    return linkBuilder.method("getGlobalConfiguration").parameters().href();
  }

  private String update() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), RedmineConfigurationResource.class);
    return linkBuilder.method("updateGlobalConfiguration").parameters().href();
  }

}
