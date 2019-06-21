package sonia.scm.redmine.update;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.update.MapBasedPropertyReaderInstance;
import sonia.scm.update.RepositoryV1PropertyReader;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1Property;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.update.V1PropertyReader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedmineV2ConfigMigrationUpdateStepTest {

  @Mock
  V1PropertyDAO v1PropertyDAO;
  @Mock
  RedmineConfigStore configStore;

  @Captor
  ArgumentCaptor<RedmineGlobalConfiguration> globalConfigurationCaptor;
  @Captor
  ArgumentCaptor<String> repositoryIdCaptor;
  private RedmineV2ConfigMigrationUpdateStep updateStep;

  @BeforeEach
  void captureStoreCalls() {
    lenient().doNothing().when(configStore).storeConfiguration(globalConfigurationCaptor.capture(), repositoryIdCaptor.capture());
  }

  @BeforeEach
  void initUpdateStep() {
    updateStep = new RedmineV2ConfigMigrationUpdateStep(v1PropertyDAO, configStore);
  }

  @Test
  void shouldMigrateRepositoryConfig() {
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

  @Test
  void shouldSkipRepositoriesWithoutRedmineConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "any", "value"
      );

    mockRepositoryProperties(new PropertiesForRepository("repo", mockedValues));

    updateStep.doUpdate();

    verify(configStore, never()).storeConfiguration(any(), eq("repo"));
  }

  protected void mockRepositoryProperties(PropertiesForRepository... mockedPropertiesForRepositories) {
    Map<String, V1Properties> map = new HashMap<>();
    stream(mockedPropertiesForRepositories).forEach(p -> map.put(p.repositoryId, p.asProperties()));
    V1PropertyReader.Instance v1PropertyReader = new MapBasedPropertyReaderInstance(map);
    when(v1PropertyDAO.getProperties(argThat(argument -> argument instanceof RepositoryV1PropertyReader))).thenReturn(v1PropertyReader);
  }

  protected static class PropertiesForRepository {
    private final String repositoryId;
    private final Map<String, String> properties;

    public PropertiesForRepository(String repositoryId, Map<String, String> properties) {
      this.repositoryId = repositoryId;
      this.properties = properties;
    }

    V1Properties asProperties() {
      return new V1Properties(properties.entrySet().stream().map(e -> new V1Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }
  }
}
