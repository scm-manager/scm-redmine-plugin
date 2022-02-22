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

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
import sonia.scm.issuetracker.spi.StateChanger;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.dto.IssueStatus;
import sonia.scm.redmine.dto.RedmineIssue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedmineStateChanger implements StateChanger {

  private final RedmineConfiguration configuration;
  private final RedmineRestApiService apiService;
  private List<IssueStatus> statusList;

  RedmineStateChanger(RedmineConfiguration configuration, RedmineRestApiService apiService) {
    this.configuration = configuration;
    this.apiService = apiService;
  }

  @Override
  public void changeState(String issueKey, String keyWord) throws IOException {
    RedmineIssue issue = apiService.getIssueById(issueKey);
    IssueStatus status = getIssueStatusByKeyword(resolveMapping(keyWord));
    issue.setStatus(status);
    apiService.updateIssue(issue);
  }

  @Override
  public Iterable<String> getKeyWords(String issueKey) throws IOException {
    return Streams.concat(getStatusKeyWords(), getMappingKeyWords())
      .collect(Collectors.toList());
  }

  @Override
  public boolean isStateChangeActivatedForCommits() {
    return !configuration.isDisableStateChangeByCommit();
  }

  private String resolveMapping(String keyWord) {
    for (Map.Entry<String, String> entry : configuration.getKeywordMapping().entrySet()) {
      List<String> values = split().splitToList(entry.getValue());
      if (values.contains(keyWord)) {
        return entry.getKey();
      }
    }
    return keyWord;
  }

  @SuppressWarnings("UnstableApiUsage")
  private Stream<String> getMappingKeyWords() {
    return configuration.getKeywordMapping().values()
      .stream()
      .flatMap(v -> split().splitToStream(v));
  }

  private Splitter split() {
    return Splitter.on(',').omitEmptyStrings().trimResults();
  }

  private Stream<String> getStatusKeyWords() throws IOException {
    return getStatusList()
      .stream()
      .map(IssueStatus::getName);
  }

  private IssueStatus getIssueStatusByKeyword(String keyword) throws IOException {
    return getStatusList()
      .stream()
      .filter(status -> keyword.equalsIgnoreCase(status.getName()))
      .findAny()
      .orElseThrow(() -> new IOException("Could not find status for key word " + keyword));
  }

  private List<IssueStatus> getStatusList() throws IOException {
    if (statusList == null) {
      statusList = apiService.getStatuses();
    }
    return statusList;
  }
}
