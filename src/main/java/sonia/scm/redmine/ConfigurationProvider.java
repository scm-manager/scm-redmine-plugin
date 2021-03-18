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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

public class ConfigurationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProvider.class);

  private final RedmineConfigStore configStore;

  @Inject
  ConfigurationProvider(RedmineConfigStore configStore) {
    this.configStore = configStore;
  }

  public RedmineConfiguration resolveConfiguration(Repository repository) {
    final RedmineGlobalConfiguration globalConfiguration = getGlobalConfiguration();

    if (globalConfiguration.isDisableRepositoryConfiguration()) {
      if (!globalConfiguration.isValid()) {
        LOG.debug("global redmine config is not valid, but disables repository config; no config returned");
        return null;
      }
      return globalConfiguration;
    }

    RedmineConfiguration configuration = configStore.getConfiguration(repository);

    if (!configuration.isValid()) {
      LOG.debug("repository config for {}/{} is not valid, falling back to global config",
        repository.getNamespace(), repository.getName());
      configuration = globalConfiguration;
    }

    if (!configuration.isValid()) {
      LOG.debug("no valid configuration for repository {}/{} found",
        repository.getNamespace(), repository.getName());
      configuration = null;
    }
    return configuration;
  }

  public void setGlobalConfiguration(RedmineGlobalConfiguration updatedConfig) {
    configStore.storeConfiguration(updatedConfig);
  }

  public void setRepositoryConfiguration(RedmineConfiguration updatedConfig, Repository repository) {
    configStore.storeConfiguration(updatedConfig, repository);
  }

  public RedmineConfiguration getRepositoryConfiguration(Repository repository) {
    return configStore.getConfiguration(repository);
  }

  public RedmineConfiguration getRepositoryConfigurationOrEmpty(Repository repository) {
    RedmineConfiguration configuration = configStore.getConfiguration(repository);
    if (configuration == null) {
      return new RedmineConfiguration();
    }
    return configuration;
  }

  public RedmineGlobalConfiguration getGlobalConfiguration() {
    return configStore.getConfiguration();
  }
}
