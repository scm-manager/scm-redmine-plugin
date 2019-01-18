package sonia.scm.redmine;

import sonia.scm.issuetracker.IssueLinkFactory;
import sonia.scm.util.HttpUtil;

public class RedmineIssueLinkFactory implements IssueLinkFactory {

  private static final String PATH_ISSUES = "issues";

  private final String redmineUrl;

  RedmineIssueLinkFactory(String redmineUrl) {
    this.redmineUrl = redmineUrl;
  }

  @Override
  public String createLink(String key) {
    return HttpUtil.concatenate(redmineUrl, PATH_ISSUES, Ids.parse(key));
  }
}
