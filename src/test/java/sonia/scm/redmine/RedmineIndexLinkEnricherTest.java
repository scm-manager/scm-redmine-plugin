package sonia.scm.redmine;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.inject.util.Providers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.redmine.config.RedmineIndexLinkEnricher;

import javax.inject.Provider;
import java.net.URI;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RedmineIndexLinkEnricherTest {

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock
  private LinkAppender appender;

  private RedmineIndexLinkEnricher enricher;

  @Before
  public void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new RedmineIndexLinkEnricher(scmPathInfoStoreProvider);
  }

  @SubjectAware(
    username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  @Test
  public void testEnrich() {
    enricher.enrich(LinkEnricherContext.of(), appender);
    verify(appender).appendOne("redmineConfig", "https://scm-manager.org/scm/api/v2/redmine/configuration/");
  }

}
