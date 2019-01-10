package sonia.scm.redmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RedmineRepositoryConfigLinkEnricher implements LinkEnricher {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  private Logger log = LoggerFactory.getLogger(RedmineRepositoryConfigLinkEnricher.class);

  @Inject
  public RedmineRepositoryConfigLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    log.error("==================");
    log.error("==================");
    log.error("==================");
    log.error("==================");
    log.error("==================");
    appender.appendOne("foo", "pczora.de");
  }


}

