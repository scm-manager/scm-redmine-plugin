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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.repository.Repository;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationProviderTest {

  @Mock
  private RedmineConfigStore configStore;

  @InjectMocks
  private ConfigurationProvider configurationProvider;

  @Test
  void shouldWriteGlobalConfigToRedmineConfigStore() {
    RedmineGlobalConfiguration config = new RedmineGlobalConfiguration();
    configurationProvider.setGlobalConfiguration(config);
    verify(configStore).storeConfiguration(config);
  }

  @Test
  void shouldGetGlobalConfigFromConfigStore() {
    configurationProvider.getGlobalConfiguration();
    verify(configStore).getConfiguration();
  }

  @Test
  void shouldGetRepoConfigurationFromStore() {
    Repository repository = createRepository();
    when(configStore.getConfiguration()).thenReturn(createValidGlobalConfiguration());
    when(configStore.getConfiguration(repository)).thenReturn(createValidConfiguration());
    configurationProvider.resolveConfiguration(repository);
    verify(configStore).getConfiguration(repository);
  }

  @Test
  void shouldFallBackToGlobalConfigIfRepoConfigInvalid() {
    Repository repository = createRepository();
    when(configStore.getConfiguration(repository))
      .thenReturn(createInvalidConfiguration());
    when(configStore.getConfiguration()).thenReturn(createValidGlobalConfiguration());
    configurationProvider.resolveConfiguration(repository);
    verify(configStore).getConfiguration(repository);
    verify(configStore).getConfiguration();
  }

  @Test
  public void shouldReturnGlobalConfigIfRepoConfigIsDisabled() {
    Repository repository = createRepository();
    when(configStore.getConfiguration()).thenReturn(createValidGlobalConfigurationWithDisabledRepoConfiguration());
    configurationProvider.resolveConfiguration(repository);
    verify(configStore, never()).getConfiguration(repository);
    verify(configStore).getConfiguration();
  }

  private Repository createRepository() {
    return new Repository("42", "GIT", "foo", "bar");
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

  private RedmineGlobalConfiguration createValidGlobalConfigurationWithDisabledRepoConfiguration() {
    return new RedmineGlobalConfiguration("http://h2g2.com",
      TextFormatting.MARKDOWN,
      false,
      false,
      true,
      "user",
      "password");
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
}
