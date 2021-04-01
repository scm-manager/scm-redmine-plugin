package sonia.scm.redmine;

import sonia.scm.issuetracker.api.IssueTracker;
import sonia.scm.issuetracker.spi.IssueTrackerBuilder;
import sonia.scm.issuetracker.spi.IssueTrackerProvider;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.plugin.Extension;
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

    IssueTrackerBuilder.ReadStage readStage = builder.start("redmine", matcher, linkFactory);
    if (configuration.isUpdateIssuesEnabled()) {
      IssueTrackerBuilder.ChangeStateStage changeStateStage = readStage.commenting(repository, new RedmineComentator(apiService))
        .template(referenceTemplate(configuration));

      if (configuration.isAutoCloseEnabled()) {
        return changeStateStage.stateChanging(new RedmineStateChanger(apiService))
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
