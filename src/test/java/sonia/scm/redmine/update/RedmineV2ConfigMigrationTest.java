 package sonia.scm.redmine.update;

 import com.google.common.collect.ImmutableMap;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.ArgumentCaptor;
 import org.mockito.Captor;
 import org.mockito.Mock;
 import org.mockito.junit.jupiter.MockitoExtension;
 import sonia.scm.redmine.config.RedmineConfigStore;
 import sonia.scm.redmine.config.RedmineGlobalConfiguration;
 import sonia.scm.redmine.config.TextFormatting;
 import sonia.scm.update.RepositoryV1PropertyReader;
 import sonia.scm.update.V1Properties;
 import sonia.scm.update.V1PropertyDAO;
 import sonia.scm.update.V1PropertyReader;

 import java.util.Map;
 import java.util.function.BiConsumer;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.argThat;
 import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.Mockito.doNothing;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedmineV2ConfigMigrationTest {

  @Mock
  V1PropertyDAO v1PropertyDAO;
  @Mock
  V1PropertyReader.Instance v1PropertyReader;
  @Mock
  RedmineConfigStore configStore;

  @Captor
  ArgumentCaptor<BiConsumer<String, V1Properties>> consumerCaptor;
  @Captor
  ArgumentCaptor<RedmineGlobalConfiguration> globalConfigurationCaptor;
  @Captor
  ArgumentCaptor<String> repositoryIdCaptor;

  @Test
  void shouldMigrateGlobalConfig() {
    when(v1PropertyDAO.getProperties(argThat(argument -> argument instanceof RepositoryV1PropertyReader))).thenReturn(v1PropertyReader);
    doNothing().when(v1PropertyReader).forEachEntry(consumerCaptor.capture());
    doNothing().when(configStore).storeConfiguration(globalConfigurationCaptor.capture(), repositoryIdCaptor.capture());

    RedmineV2ConfigMigration redmineV2ConfigMigration = new RedmineV2ConfigMigration(v1PropertyDAO, configStore);

    redmineV2ConfigMigration.doUpdate();

    consumerCaptor.getValue().accept("repo", new V1Properties() {
      private final Map<String, String> values =
        ImmutableMap.of(
          "redmine.url", "http://redmine.example.com",
          "redmine.auto-close-username-transformer", "{0}",
          "redmine.update-issues", "true",
          "redmine.text-formatting", "TEXTILE",
          "redmine.auto-close", "true"
        );
      @Override
      public String get(String key) {
        return values.get(key);
      }
    });

    verify(configStore).storeConfiguration(any(), eq("repo"));

    assertThat(globalConfigurationCaptor.getValue())
      .hasFieldOrPropertyWithValue("url", "http://redmine.example.com")
      .hasFieldOrPropertyWithValue("textFormatting", TextFormatting.TEXTILE)
      .hasFieldOrPropertyWithValue("autoClose", true)
      .hasFieldOrPropertyWithValue("updateIssues", true);
  }
}
