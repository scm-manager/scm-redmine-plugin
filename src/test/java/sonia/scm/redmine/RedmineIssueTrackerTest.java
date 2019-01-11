package sonia.scm.redmine;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.inject.Provider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.repository.Repository;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.store.InMemoryDataStoreFactory;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;
import sonia.scm.template.TemplateType;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedmineIssueTrackerTest {

  private DataStoreFactory dataStoreFactory = new InMemoryDataStoreFactory();

  @Mock
  private RedmineConfigStore configStore;

  @Mock
  private Provider<LinkHandler> linkHandlerProvider;


  private RedmineIssueTracker redmineIssueTracker;

  @Before
  public void setUp() {
    TemplateEngine templateEngine = mock(TemplateEngine.class);
    when(templateEngine.getType()).thenReturn(new TemplateType("foo", "bar", Collections.EMPTY_LIST));
    TemplateEngineFactory templateEngineFactory = new TemplateEngineFactory(Collections.emptySet(), templateEngine);
    redmineIssueTracker = new RedmineIssueTracker(configStore, dataStoreFactory, templateEngineFactory, linkHandlerProvider);
  }

  public void shouldWriteGlobalConfigToRedmineConfigStore() {
    RedmineGlobalConfiguration config = new RedmineGlobalConfiguration();
    redmineIssueTracker.setGlobalConfiguration(config);
    verify(configStore).storeConfiguration(config);
  }

  @Test
  public void shouldGetGlobalConfigFromConfigStore() {
    redmineIssueTracker.getGlobalConfiguration();
    verify(configStore).getConfiguration();
  }

  @Test
  public void shouldGetRepoConfigurationFromStore() {
    Repository repository = createRepository();
    when(configStore.getConfiguration(repository)).thenReturn(createValidConfiguration());
    redmineIssueTracker.resolveConfiguration(repository);
    verify(configStore).getConfiguration(repository);
  }

  @Test
  public void shouldFallBackToGlobalConfigIfRepoConfigInvalid() {
    Repository repository = createRepository();
    when(configStore.getConfiguration(repository))
      .thenReturn(createInvalidConfiguration());
    when(configStore.getConfiguration()).thenReturn(createValidGlobalConfiguration());
    redmineIssueTracker.resolveConfiguration(repository);
    verify(configStore).getConfiguration(repository);
    verify(configStore).getConfiguration();
  }

  private Repository createRepository() {
    return new Repository("42", "GIT", "foo", "bar");
  }

  private RedmineConfiguration createInvalidConfiguration() {
    return new RedmineConfiguration("",
      TextFormatting.MARKDOWN,
      "{0}",
      false,
      false,
      "user",
      "password");
  }

  private RedmineConfiguration createValidConfiguration() {
    return new RedmineConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      "{0}",
      false,
      false,
      "user",
      "password");
  }
  private RedmineGlobalConfiguration createValidGlobalConfiguration() {
    return new RedmineGlobalConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      "{0}",
      false,
      false,
      false,
      "user",
      "password");
  }
}
