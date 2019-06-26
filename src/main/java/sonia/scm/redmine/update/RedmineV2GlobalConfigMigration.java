package sonia.scm.redmine.update;

import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.version.Version;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static sonia.scm.version.Version.parse;

@Extension
public class RedmineV2GlobalConfigMigration implements UpdateStep {

  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public RedmineV2GlobalConfigMigration(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    storeFactory.withType(V1RedmineGlobalConfiguration.class).withName("redmine").build().getOptional()
    .ifPresent(
      v1RedmineConfig -> {
        RedmineGlobalConfiguration v2RedmineConfig = new RedmineGlobalConfiguration(
          v1RedmineConfig.getUrl(),
          v1RedmineConfig.getTextFormatting(),
          v1RedmineConfig.isAutoClose(),
          v1RedmineConfig.isUpdateIssues(),
          v1RedmineConfig.isDisableRepositoryConfiguration(),
          "",
          ""
        );
        storeFactory.withType(RedmineGlobalConfiguration.class).withName("redmine").build().set(v2RedmineConfig);
      }
    );
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.redmine.config.global.xml";
  }

  @XmlRootElement(name = "redmine")
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class V1RedmineGlobalConfiguration extends RedmineGlobalConfiguration {
  }
}
