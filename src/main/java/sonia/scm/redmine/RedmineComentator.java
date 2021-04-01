package sonia.scm.redmine;

import sonia.scm.issuetracker.spi.Commentator;
import sonia.scm.redmine.dto.RedmineIssue;

import java.io.IOException;

public class RedmineComentator implements Commentator {

  private final RedmineRestApiService apiService;

  RedmineComentator(RedmineRestApiService apiService) {
    this.apiService = apiService;
  }

  @Override
  public void comment(String issueKey, String comment) throws IOException {
    RedmineIssue issue = apiService.getIssueById(issueKey);
    issue.setNote(comment);
    apiService.updateIssue(issue);
  }
}
