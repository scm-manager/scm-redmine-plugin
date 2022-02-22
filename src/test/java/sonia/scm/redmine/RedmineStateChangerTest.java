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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.dto.IssueStatus;
import sonia.scm.redmine.dto.RedmineIssue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedmineStateChangerTest {

  private RedmineConfiguration configuration;

  @Mock
  private RedmineRestApiService apiService;

  private RedmineStateChanger stateChanger;

  @Mock
  private RedmineIssue issue;

  @BeforeEach
  void setUpStateChanger() {
    configuration = new RedmineConfiguration();
    stateChanger = new RedmineStateChanger(configuration, apiService);
  }

  @Test
  void shouldReturnKeyWords() throws IOException {
    List<IssueStatus> statusList = ImmutableList.of(
      new IssueStatus(0, "Open"),
      new IssueStatus(1, "Closed")
    );
    when(apiService.getStatuses()).thenReturn(statusList);

    Iterable<String> keyWords = stateChanger.getKeyWords("#42");
    assertThat(keyWords).containsOnly("Open", "Closed");
  }

  @Test
  void shouldReturnKeyWordsFromMapping() throws IOException {
    List<IssueStatus> statusList = ImmutableList.of(
      new IssueStatus(0, "Open"),
      new IssueStatus(1, "Closed")
    );
    configuration.setKeywordMapping(ImmutableMap.of("Closed", "closes, closing"));
    when(apiService.getStatuses()).thenReturn(statusList);

    Iterable<String> keyWords = stateChanger.getKeyWords("#42");
    assertThat(keyWords).containsOnly("Open", "Closed", "closes", "closing");
  }

  @Test
  void shouldTriggerStateChange() throws IOException {
    IssueStatus status = new IssueStatus(1, "Closed");
    when(apiService.getStatuses()).thenReturn(Collections.singletonList(status));
    when(apiService.getIssueById("#21")).thenReturn(issue);

    stateChanger.changeState("#21", "Closed");

    verify(issue).setStatus(status);
    verify(apiService).updateIssue(issue);
  }

  @Test
  void shouldTriggerStateChangeWithMappedKeyWord() throws IOException {
    IssueStatus status = new IssueStatus(1, "Closed");
    configuration.setKeywordMapping(ImmutableMap.of("Closed", "fixes, closes"));

    when(apiService.getStatuses()).thenReturn(Collections.singletonList(status));
    when(apiService.getIssueById("#21")).thenReturn(issue);

    stateChanger.changeState("#21", "closes");

    verify(issue).setStatus(status);
    verify(apiService).updateIssue(issue);
  }

  @Test
  void shouldDisableUpdatesByCommits() {
    configuration.setDisableUpdateByCommit(true);

    assertThat(stateChanger.isStateChangeActivatedForCommits()).isFalse();
  }

  @Test
  void shouldEnableUpdatesByCommitsByDefault() {
    assertThat(stateChanger.isStateChangeActivatedForCommits()).isTrue();
  }
}
