package sonia.scm.redmine.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import sonia.scm.repository.Repository;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

@Singleton
public class RedmineConfigStore {
  public static final String NAME = "redmine";
  private ConfigurationStoreFactory storeFactory;

  @Inject
  public RedmineConfigStore(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  public void storeConfiguration(RedmineGlobalConfiguration configuration) {
    createGlobalStore().set(configuration);
  }

  public void storeConfiguration(RedmineConfiguration configuration, Repository repository) {
    createStore(repository).set(configuration);
  }

  public RedmineGlobalConfiguration getConfiguration() {
    return createGlobalStore().getOptional().orElse(new RedmineGlobalConfiguration());
  }

  public RedmineConfiguration getConfiguration(Repository repository) {
    return createStore(repository).getOptional().orElse(new RedmineConfiguration());
  }

  private ConfigurationStore<RedmineConfiguration> createStore(Repository repository) {
    return storeFactory.withType(RedmineConfiguration.class).withName(NAME).forRepository(repository).build();
  }

  private ConfigurationStore<RedmineGlobalConfiguration> createGlobalStore() {
    return storeFactory.withType(RedmineGlobalConfiguration.class).withName(NAME).build();
  }

}
