package sonia.scm.redmine.config;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Index.class)
public class RedmineIndexLinkEnricher implements LinkEnricher {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;

  @Inject
  public RedmineIndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
  }

  private String createLink() {
    return new LinkBuilder(scmPathInfoStoreProvider.get().get(), RedmineGlobalConfigurationResource.class)
      .method("getConfiguration")
      .parameters()
      .href();
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    appender.appendOne("redmineConfig", createLink());
  }
}
