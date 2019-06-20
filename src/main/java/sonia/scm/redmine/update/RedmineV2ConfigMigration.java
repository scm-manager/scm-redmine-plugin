package sonia.scm.redmine.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.update.RepositoryV1PropertyReader;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.version.Version;

import javax.inject.Inject;

import static sonia.scm.version.Version.parse;

@Extension
public class RedmineV2ConfigMigration implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(RedmineV2ConfigMigration.class);

  private final V1PropertyDAO v1PropertyDAO;
  private final RedmineConfigStore configStore;

  @Inject
  public RedmineV2ConfigMigration(V1PropertyDAO v1PropertyDAO, RedmineConfigStore configStore) {
    this.v1PropertyDAO = v1PropertyDAO;
    this.configStore = configStore;
  }

  @Override
  public void doUpdate() {
    v1PropertyDAO
      .getProperties(new RepositoryV1PropertyReader())
      .forEachEntry((key, properties) -> configStore.storeConfiguration(buildConfig(key, properties), key));
  }

  private RedmineConfiguration buildConfig(String key, V1Properties value) {
    LOG.debug("migrating repository specific redmine configuration for repository id {}", key);
    return new RedmineConfiguration(
      value.get("redmine.url"),
      TextFormatting.valueOf(value.get("redmine.text-formatting")),
      Boolean.valueOf(value.get("redmine.auto-close")),
      Boolean.valueOf(value.get("redmine.update-issues")),
      "",
      ""
    );
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.redmine.config.repository.xml";
  }
}
