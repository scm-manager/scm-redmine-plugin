package sonia.scm.redmine;

import sonia.scm.issuetracker.spi.StateChanger;
import sonia.scm.redmine.dto.IssueStatus;
import sonia.scm.redmine.dto.RedmineIssue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RedmineStateChanger implements StateChanger {

  private final RedmineRestApiService apiService;
  private List<IssueStatus> statusList;

  RedmineStateChanger(RedmineRestApiService apiService) {
    this.apiService = apiService;
  }

  @Override
  public void changeState(String issueKey, String keyWord) throws IOException {
    RedmineIssue issue = apiService.getIssueById(issueKey);
    IssueStatus status = getIssueStatusByKeyword(keyWord);
    issue.setStatus(status);
    apiService.updateIssue(issue);
  }

  @Override
  public Iterable<String> getKeyWords(String issueKey) throws IOException {
    return getStatusList().stream().map(IssueStatus::getName).collect(Collectors.toList());
  }

  private IssueStatus getIssueStatusByKeyword(String keyword) throws IOException {
    return getStatusList().stream()
      .filter(status -> keyword.equalsIgnoreCase(status.getName()))
      .findAny()
      .orElseThrow(() -> new IOException("Could not find status for key word" + keyword));
  }

  private List<IssueStatus> getStatusList() throws IOException {
    if (statusList == null) {
      statusList = apiService.getStatuses();
    }
    return statusList;
  }
}
