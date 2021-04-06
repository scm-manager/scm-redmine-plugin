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

import sonia.scm.issuetracker.api.IssueTracker;
import sonia.scm.issuetracker.spi.IssueTrackerBuilder;
import sonia.scm.issuetracker.spi.IssueTrackerProvider;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.ConfigurationResolver;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Locale;
import java.util.Optional;

@Extension
public class RedmineIssueTrackerProvider implements IssueTrackerProvider {

  private final ConfigurationResolver configurationResolver;
  private final Provider<AdvancedHttpClient> httpClientProvider;

  @Inject
  public RedmineIssueTrackerProvider(ConfigurationResolver configurationResolver, Provider<AdvancedHttpClient> httpClientProvider) {
    this.configurationResolver = configurationResolver;
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public Optional<IssueTracker> create(IssueTrackerBuilder builder, Repository repository) {
    return configurationResolver.resolve(repository)
      .map(configuration -> create(builder, configuration, repository));
  }

  private IssueTracker create(IssueTrackerBuilder builder, RedmineConfiguration configuration, Repository repository) {
    RedmineIssueMatcher matcher = new RedmineIssueMatcher();
    RedmineIssueLinkFactory linkFactory = new RedmineIssueLinkFactory(configuration.getUrl());
    RedmineRestApiService apiService = new RedmineRestApiService(httpClientProvider.get(), configuration);

    IssueTrackerBuilder.ReadStage readStage = builder.start(Constants.NAME, matcher, linkFactory);
    if (configuration.isUpdateIssuesEnabled()) {
      IssueTrackerBuilder.ChangeStateStage changeStateStage = readStage.commenting(repository, new RedmineCommentator(apiService))
        .template(referenceTemplate(configuration));

      if (configuration.isAutoCloseEnabled()) {
        return changeStateStage.stateChanging(new RedmineStateChanger(configuration, apiService))
          .template(stateChangeTemplate(configuration))
          .build();
      }

      return changeStateStage.build();
    }
    return readStage.build();
  }

  private String stateChangeTemplate(RedmineConfiguration configuration) {
    return template(configuration, "statechange");
  }

  private String referenceTemplate(RedmineConfiguration configuration) {
    return template(configuration, "reference");
  }

  private String template(RedmineConfiguration configuration, String type) {
    String textFormat = configuration.getTextFormatting().name().toLowerCase(Locale.ENGLISH);
    return String.format("/scm/template/%s/{0}_%s.mustache", textFormat, type);
  }

}
