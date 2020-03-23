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
package sonia.scm.redmine.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static sonia.scm.redmine.config.TextFormatting.TEXTILE;
import static sonia.scm.redmine.update.RedmineV2GlobalConfigMigrationUpdateStep.*;

@ExtendWith(MockitoExtension.class)
class RedmineV2GlobalConfigMigrationUpdateStepTest {

  RedmineV2GlobalConfigMigrationUpdateStep updateStep;

  private InMemoryConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();

  @BeforeEach
  void initUpdateStep() {
    updateStep = new RedmineV2GlobalConfigMigrationUpdateStep(storeFactory);
  }

  @Nested
  class WithExistingV1Config {

    @BeforeEach
    void createRedmineV1XMLInMemory() {
      V1RedmineGlobalConfiguration redmineGlobalConfiguration = new V1RedmineGlobalConfiguration();
      redmineGlobalConfiguration.setUrl("test.de");
      redmineGlobalConfiguration.setPassword("1234");
      redmineGlobalConfiguration.setAutoClose(false);
      redmineGlobalConfiguration.setUsername("tester");
      redmineGlobalConfiguration.setTextFormatting(TEXTILE);
      redmineGlobalConfiguration.setDisableRepositoryConfiguration(false);
      redmineGlobalConfiguration.setUpdateIssues(true);
      storeFactory.withType(V1RedmineGlobalConfiguration.class).withName("redmine").build().set(redmineGlobalConfiguration);
    }

    @Test
    void shouldMigrateGlobalConfiguration() {
      updateStep.doUpdate();
      ConfigurationStore<RedmineGlobalConfiguration> testStore = storeFactory.get("redmine",null);
      RedmineGlobalConfiguration redmineGlobalConfiguration = testStore.get();
      assertThat(redmineGlobalConfiguration.getUrl()).isEqualToIgnoringCase("test.de");
      assertThat(redmineGlobalConfiguration.isAutoClose()).isFalse();
      assertThat(redmineGlobalConfiguration.getTextFormatting()).isEqualTo(TEXTILE);
      assertThat(redmineGlobalConfiguration.isDisableRepositoryConfiguration()).isFalse();
      assertThat(redmineGlobalConfiguration.isUpdateIssues()).isTrue();

      //Username and password does not exist in V1 and should not be migrated
      assertThat(redmineGlobalConfiguration.getUsername()).isEmpty();
      assertThat(redmineGlobalConfiguration.getPassword()).isEmpty();
    }
  }

  @Nested
  class WithExistingV2Config {
    @BeforeEach
    void createRedmineV2XMLInMemory() {
      RedmineGlobalConfiguration globalConfiguration = new RedmineGlobalConfiguration();
      storeFactory.withType(RedmineGlobalConfiguration.class).withName("redmine").build().set(globalConfiguration);
    }

    @Test
    void shouldNotFailForExistingV2Config() {
      updateStep.doUpdate();
    }
  }

  @Nested
  class WithoutAnyConfig {
    @BeforeEach
    void createRedmineV2XMLInMemory() {
    }

    @Test
    void shouldNotFailForMissingConfig() {
      updateStep.doUpdate();
    }
  }
}
