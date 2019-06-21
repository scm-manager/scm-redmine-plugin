package sonia.scm.redmine.update;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.update.RepositoryV1PropertyReader;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.update.V1PropertyReader;

import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedmineV2ConfigMigrationUpdateStepTest {

  @Mock
  V1PropertyDAO v1PropertyDAO;
  @Mock
  V1PropertyReader.Instance v1PropertyReader;
  @Mock
  RedmineConfigStore configStore;

  @Captor
  ArgumentCaptor<RedmineGlobalConfiguration> globalConfigurationCaptor;
  @Captor
  ArgumentCaptor<String> repositoryIdCaptor;
  private RedmineV2ConfigMigrationUpdateStep updateStep;

  @BeforeEach
  void captureStoreCalls() {
    doNothing().when(configStore).storeConfiguration(globalConfigurationCaptor.capture(), repositoryIdCaptor.capture());
  }

  @BeforeEach
  void initUpdateStep() {
    updateStep = new RedmineV2ConfigMigrationUpdateStep(v1PropertyDAO, configStore);
  }

  @Test
  void shouldMigrateGlobalConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "redmine.url", "http://redmine.example.com",
        "redmine.auto-close-username-transformer", "{0}",
        "redmine.update-issues", "true",
        "redmine.text-formatting", "TEXTILE",
        "redmine.auto-close", "true"
      );

    mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    verify(configStore).storeConfiguration(any(), eq("repo"));

    assertThat(globalConfigurationCaptor.getValue())
      .hasFieldOrPropertyWithValue("url", "http://redmine.example.com")
      .hasFieldOrPropertyWithValue("textFormatting", TextFormatting.TEXTILE)
      .hasFieldOrPropertyWithValue("autoClose", true)
      .hasFieldOrPropertyWithValue("updateIssues", true);
  }

  protected void mockRepositoryProperties(PropertiesForRepository... mockedPropertiesForRepositories) {
    when(v1PropertyDAO.getProperties(argThat(argument -> argument instanceof RepositoryV1PropertyReader))).thenReturn(v1PropertyReader);
    Answer callbackWithProperties = invocation -> {
      BiConsumer<String, V1Properties> callback = invocation.getArgument(0);
      stream(mockedPropertiesForRepositories).forEach(
        mockedProps ->
          callback.accept(mockedProps.repositoryId, new V1Properties() {
            @Override
            public String get(String key) {
              return mockedProps.properties.get(key);
            }
          })
      );
      return null;
    };
    doAnswer(callbackWithProperties).when(v1PropertyReader).forEachEntry(any());
  }

  protected static class PropertiesForRepository {
    private final String repositoryId;
    private final Map<String, String> properties;

    public PropertiesForRepository(String repositoryId, Map<String, String> properties) {
      this.repositoryId = repositoryId;
      this.properties = properties;
    }
  }
}
