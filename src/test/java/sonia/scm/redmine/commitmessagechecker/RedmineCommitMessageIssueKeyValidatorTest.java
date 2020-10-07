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
