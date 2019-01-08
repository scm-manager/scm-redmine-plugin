package sonia.scm.redmine.config;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;

@Extension
public class MapperModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RedmineConfigurationMapper.class).to(Mappers.getMapper(RedmineConfigurationMapper.class).getClass());
  }
}
