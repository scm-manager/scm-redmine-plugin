/*
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
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryDataStoreFactory;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;
import sonia.scm.template.TemplateType;

import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedmineIssueTrackerTest {

  private final DataStoreFactory dataStoreFactory = new InMemoryDataStoreFactory();

  @Mock
  private ConfigurationProvider configurationProvider;

  @Mock
  private Provider<LinkHandler> linkHandlerProvider;

  @Mock
  private AdvancedHttpClient advancedHttpClient;

  private RedmineIssueTracker redmineIssueTracker;

  @Before
  public void setUp() {
    TemplateEngine templateEngine = mock(TemplateEngine.class);
    when(templateEngine.getType()).thenReturn(new TemplateType("foo", "bar", emptySet()));
    TemplateEngineFactory templateEngineFactory = new TemplateEngineFactory(emptySet(), templateEngine);
    redmineIssueTracker = new RedmineIssueTracker(dataStoreFactory, advancedHttpClient, templateEngineFactory, linkHandlerProvider, configurationProvider);
  }

  @Test
  public void shouldCreateCommentHandler() {
    Repository repository = createRepository();
    final Changeset changeset = new Changeset();
    IssueRequest issueRequest = new IssueRequest(repository, changeset, Lists.emptyList(), Optional.empty());
    when(configurationProvider.resolveConfiguration(repository)).thenReturn(createValidGlobalConfigurationWithDisabledRepoConfigurationAndAutoCloseAndUpdateIssue());
    final CommentHandler commentHandler = redmineIssueTracker.getCommentHandler(issueRequest);
    assertThat(commentHandler).isNotNull();
  }

  private Repository createRepository() {
    return new Repository("42", "GIT", "foo", "bar");
  }

  private RedmineGlobalConfiguration createValidGlobalConfigurationWithDisabledRepoConfigurationAndAutoCloseAndUpdateIssue() {
    return new RedmineGlobalConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      true,
      true,
      true,
      "user",
      "password");
  }
}
