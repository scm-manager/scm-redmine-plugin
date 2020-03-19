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
package sonia.scm.redmine.update;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.update.V1PropertyDaoTestUtil;
import sonia.scm.update.V1PropertyDaoTestUtil.PropertiesForRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedmineV2ConfigMigrationUpdateStepTest {

  V1PropertyDaoTestUtil testUtil = new V1PropertyDaoTestUtil();

  @Mock
  RedmineConfigStore configStore;
  @Captor
  ArgumentCaptor<RedmineConfiguration> configurationCaptor;
  @Captor
  ArgumentCaptor<String> repositoryIdCaptor;
  private RedmineV2ConfigMigrationUpdateStep updateStep;

  @BeforeEach
  void captureStoreCalls() {
    lenient().doNothing().when(configStore).storeConfiguration(configurationCaptor.capture(), repositoryIdCaptor.capture());
  }

  @BeforeEach
  void initUpdateStep() {
    updateStep = new RedmineV2ConfigMigrationUpdateStep(testUtil.getPropertyDAO(), configStore);
  }

  @Test
  void shouldMigrateRepositoryConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "redmine.url", "http://redmine.example.com",
        "redmine.auto-close-username-transformer", "{0}",
        "redmine.update-issues", "true",
        "redmine.text-formatting", "TEXTILE",
        "redmine.auto-close", "true"
      );

    testUtil.mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    verify(configStore).storeConfiguration(any(), eq("repo"));

    assertThat(configurationCaptor.getValue())
      .hasFieldOrPropertyWithValue("url", "http://redmine.example.com")
      .hasFieldOrPropertyWithValue("textFormatting", TextFormatting.TEXTILE)
      .hasFieldOrPropertyWithValue("autoClose", true)
      .hasFieldOrPropertyWithValue("updateIssues", true);
  }

  @Test
  void shouldSkipRepositoriesWithoutRedmineConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "any", "value"
      );

    testUtil.mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    verify(configStore, never()).storeConfiguration(any(), eq("repo"));
  }
}
