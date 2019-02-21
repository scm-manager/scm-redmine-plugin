package sonia.scm.redmine.config;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.Constants;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RedmineRepositoryConfigHalEnricher implements HalEnricher {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  private final RedmineConfigStore redmineConfigStore;

  @Inject
  public RedmineRepositoryConfigHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider,
                                             RedmineConfigStore redmineConfigStore) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
    this.redmineConfigStore = redmineConfigStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (!redmineConfigStore.getConfiguration().isDisableRepositoryConfiguration() && RepositoryPermissions.custom(Constants.NAME, repository).isPermitted()) {
      String linkBuilder = new LinkBuilder(scmPathInfoStoreProvider.get().get(), RedmineConfigurationResource.class)
        .method("getConfiguration")
        .parameters(repository.getNamespace(), repository.getName())
        .href();

      appender.appendLink("redmineConfig", linkBuilder);
    }
  }



}

