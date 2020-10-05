package sonia.scm.redmine.commitmessagechecker;

import com.cloudogu.scm.commitmessagechecker.Context;
import com.cloudogu.scm.commitmessagechecker.InvalidCommitMessageException;
import org.junit.jupiter.api.Test;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static sonia.scm.redmine.commitmessagechecker.RedmineCommitMessageIssueKeyValidator.RedmineCommitMessageIssueKeyValidatorConfig;

class RedmineCommitMessageIssueKeyValidatorTest {

  private static final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();
  private final RedmineCommitMessageIssueKeyValidator validator = new RedmineCommitMessageIssueKeyValidator();

  @Test
  void shouldValidateSuccessfully() {
    RedmineCommitMessageIssueKeyValidatorConfig config = new RedmineCommitMessageIssueKeyValidatorConfig();
    validator.validate(new Context(REPOSITORY, "", config), "valid commit by trillian #42");
  }

  @Test
  void shouldFailOnMissingIssueKey() {
    RedmineCommitMessageIssueKeyValidatorConfig config = new RedmineCommitMessageIssueKeyValidatorConfig();
    assertThrows(InvalidCommitMessageException.class,
      () -> validator.validate(new Context(REPOSITORY, "master", config), "invalid commit by trillian"));
  }

  @Test
  void shouldNotValidateIfBranchNotMatch() {
    RedmineCommitMessageIssueKeyValidatorConfig config = new RedmineCommitMessageIssueKeyValidatorConfig();
    config.setBranches("master");
    validator.validate(new Context(REPOSITORY, "develop", config), "invalid commit by trillian");
  }

}
