/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sonia.scm.redmine;

import com.google.inject.Provider;
import org.junit.Before;
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
import sonia.scm.store.DataStoreFactory;
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
      false,
      false,
      "user",
      "password");
  }

  private RedmineConfiguration createValidConfiguration() {
    return new RedmineConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      false,
      false,
      "user",
      "password");
  }
  private RedmineGlobalConfiguration createValidGlobalConfiguration() {
    return new RedmineGlobalConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      false,
      false,
      false,
      "user",
      "password");
  }
}
