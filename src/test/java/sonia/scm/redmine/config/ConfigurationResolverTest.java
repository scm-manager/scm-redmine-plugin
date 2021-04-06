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

package sonia.scm.redmine.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationResolverTest {

  private RedmineConfigurationStore store;

  private ConfigurationResolver resolver;

  private final Repository repository = RepositoryTestData.createHeartOfGold();

  @BeforeEach
  void setUpResolver() {
    store = new RedmineConfigurationStore(new InMemoryConfigurationStoreFactory());
    resolver = new ConfigurationResolver(store);
  }

  @Test
  void shouldReturnEmptyOptional() {
    Optional<RedmineConfiguration> configuration = resolver.resolve(repository);
    assertThat(configuration).isEmpty();
  }

  @Test
  void shouldResolveRepositoryConfiguration() {
    RedmineConfiguration configuration = new RedmineConfiguration();
    configuration.setUrl("https://issues.hitchhiker.com");
    store.storeConfiguration(configuration, repository);

    Optional<RedmineConfiguration> resolvedConfiguration = resolver.resolve(repository);
    assertThat(resolvedConfiguration).hasValueSatisfying(c -> {
      assertThat(c.getUrl()).isEqualTo("https://issues.hitchhiker.com");
    });
  }

  @Test
  void shouldResolveGlobalConfiguration() {
    RedmineGlobalConfiguration configuration = new RedmineGlobalConfiguration();
    configuration.setUrl("https://hitchhiker.com/issues");
    store.storeConfiguration(configuration);

    Optional<RedmineConfiguration> resolvedConfiguration = resolver.resolve(repository);
    assertThat(resolvedConfiguration).hasValueSatisfying(c -> {
      assertThat(c.getUrl()).isEqualTo("https://hitchhiker.com/issues");
    });
  }

  @Test
  void shouldPreferRepositoryConfiguration() {
    RedmineGlobalConfiguration globalConfiguration = new RedmineGlobalConfiguration();
    globalConfiguration.setUrl("https://hitchhiker.com/issues");
    store.storeConfiguration(globalConfiguration);

    RedmineConfiguration repositoryConfiguration = new RedmineConfiguration();
    repositoryConfiguration.setUrl("https://issues.hitchhiker.com");
    store.storeConfiguration(repositoryConfiguration, repository);

    Optional<RedmineConfiguration> resolvedConfiguration = resolver.resolve(repository);
    assertThat(resolvedConfiguration).hasValueSatisfying(c -> {
      assertThat(c.getUrl()).isEqualTo("https://issues.hitchhiker.com");
    });
  }

  @Test
  void shouldUseGlobalConfigurationIfRepositoryConfigurationIsDisabled() {
    RedmineGlobalConfiguration globalConfiguration = new RedmineGlobalConfiguration();
    globalConfiguration.setUrl("https://hitchhiker.com/issues");
    globalConfiguration.setDisableRepositoryConfiguration(true);
    store.storeConfiguration(globalConfiguration);

    RedmineConfiguration repositoryConfiguration = new RedmineConfiguration();
    repositoryConfiguration.setUrl("https://issues.hitchhiker.com");
    store.storeConfiguration(repositoryConfiguration, repository);

    Optional<RedmineConfiguration> resolvedConfiguration = resolver.resolve(repository);
    assertThat(resolvedConfiguration).hasValueSatisfying(c -> {
      assertThat(c.getUrl()).isEqualTo("https://hitchhiker.com/issues");
    });
  }

  @Test
  void shouldReturnEmptyIfRepositoryConfigurationIsDisabledAndGlobalIsNotValid() {
    RedmineGlobalConfiguration globalConfiguration = new RedmineGlobalConfiguration();
    globalConfiguration.setDisableRepositoryConfiguration(true);
    store.storeConfiguration(globalConfiguration);

    Optional<RedmineConfiguration> resolvedConfiguration = resolver.resolve(repository);
    assertThat(resolvedConfiguration).isEmpty();
  }


}
