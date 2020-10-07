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
import com.cloudogu.scm.commitmessagechecker.Validator;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.util.RegExPatternMatcher;
import sonia.scm.ContextEntry;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.redmine.RedmineIssueMatcher;
import sonia.scm.util.GlobUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

@Extension
@Requires("scm-commit-message-checker-plugin")
public class RedmineCommitMessageIssueKeyValidator implements Validator {

  private static final String DEFAULT_ERROR_MESSAGE = "The commit message doesn't contain a valid Redmine issue key.";
  private static final String ISSUE_KEY_PATTERN = new RedmineIssueMatcher().getKeyPattern().pattern();
  private static final RegExPatternMatcher matcher = new RegExPatternMatcher();

  @Override
  public boolean isApplicableMultipleTimes() {
    return false;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(RedmineCommitMessageIssueKeyValidatorConfig.class);
  }

  @Override
  public void validate(Context context, String commitMessage) {
    RedmineCommitMessageIssueKeyValidatorConfig configuration = context.getConfiguration(RedmineCommitMessageIssueKeyValidatorConfig.class);
    String commitBranch = context.getBranch();

    if (shouldValidateBranch(configuration, commitBranch) && isInvalidCommitMessage(commitMessage)) {
      throw new InvalidCommitMessageException(
        ContextEntry.ContextBuilder.entity(context.getRepository()),
        DEFAULT_ERROR_MESSAGE
      );
    }
  }

  private boolean shouldValidateBranch(RedmineCommitMessageIssueKeyValidatorConfig configuration, String commitBranch) {
    if (Strings.isNullOrEmpty(commitBranch) || Strings.isNullOrEmpty(configuration.getBranches())) {
      return true;
    }
    return Arrays
      .stream(configuration.getBranches().split(","))
      .anyMatch(branch -> GlobUtil.matches(branch.trim(), commitBranch));
  }

  private boolean isInvalidCommitMessage(String commitMessage) {
    String keyPattern = MessageFormat.format(".*{0}.*", ISSUE_KEY_PATTERN);
    return !matcher.matches(keyPattern, commitMessage);
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @XmlRootElement
  static class RedmineCommitMessageIssueKeyValidatorConfig {
    private String branches;
  }
}
