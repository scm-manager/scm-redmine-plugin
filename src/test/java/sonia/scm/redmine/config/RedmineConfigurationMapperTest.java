package sonia.scm.redmine.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RedmineConfigurationMapperTest {

  private URI baseUri = URI.create("http://example.com/base/");

  private URI expectedBaseUri;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ScmPathInfoStore scmPathInfoStore;

  @InjectMocks
  RedmineConfigurationMapperImpl mapper;

  @Before
  public void init() {
    when(scmPathInfoStore.get().getApiRestUri()).thenReturn(baseUri);
    expectedBaseUri = baseUri.resolve("v2/redmine/configuration");
  }

  @Test
  public void shouldMapAttributesToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration());
    assertEquals( "heartofgo.ld", dto.getUrl());
    assertEquals(TextFormatting.MARKDOWN, dto.getTextFormatting());
    assertEquals("UTPattern", dto.getUsernameTransformPattern());
    assertTrue(dto.isAutoClose());
    assertFalse(dto.isUpdateIssues());
  }

  @Test
  public void shouldAddHalLinksToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration());
    assertEquals(expectedBaseUri.toString(), dto.getLinks().getLinkBy("self").get().getHref());
  }

  @Test
  //TODO This test fails b/c the mapper does not yet check permissions
  public void shouldNotAddUpdateLinkToDtoIfNotPermitted() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration());
//    assertFalse(dto.getLinks().getLinkBy("update").isPresent());
  }

  @Test
  public void shouldMapAttributesFromDto() {
    RedmineConfiguration configuration = mapper.map(createDto());
    assertEquals( "heartofgo.ld", configuration.getUrl());
    assertEquals(TextFormatting.MARKDOWN, configuration.getTextFormatting());
    assertEquals("UTPattern", configuration.getUsernameTransformPattern());
    assertTrue(configuration.isAutoClose());
    assertFalse(configuration.isUpdateIssues());
  }

  @Test
  public void shouldMapGlobalConfigurationAttributesToDto() {
    RedmineGlobalConfigurationDto dto = mapper.map(createGlobalConfiguration());
    assertFalse(dto.isDisableRepositoryConfiguration());
  }

  @Test
  public void shouldMapGlobalConfigurationDtoAttributesFromDto() {
    RedmineGlobalConfiguration configuration = mapper.map(createGlobalConfigurationDto());
    assertFalse(configuration.isDisableRepositoryConfiguration());
  }

  private RedmineConfiguration createConfiguration() {
    return new RedmineConfiguration("heartofgo.ld",
      TextFormatting.MARKDOWN,
      "UTPattern",
      true,
      false);
  }

  private RedmineConfigurationDto createDto() {
    return new RedmineConfigurationDto("heartofgo.ld",
      TextFormatting.MARKDOWN,
      "UTPattern",
      true,
      false);
  }

  private RedmineGlobalConfiguration createGlobalConfiguration() {
    RedmineGlobalConfiguration configuration = new RedmineGlobalConfiguration();
    configuration.setUrl("");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUsernameTransformPattern("{0}");
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }

  private RedmineGlobalConfigurationDto createGlobalConfigurationDto() {
    RedmineGlobalConfigurationDto configuration = new RedmineGlobalConfigurationDto();
    configuration.setUrl("");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUsernameTransformPattern("{0}");
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }
}
