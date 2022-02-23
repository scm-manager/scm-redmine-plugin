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
package sonia.scm.redmine.config;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("squid:S2068")
public class RedmineConfigurationMapperTest {

  private URI baseUri = URI.create("http://example.com/base/");

  private URI expectedBaseUri;

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ScmPathInfoStore scmPathInfoStore;

  @InjectMocks
  RedmineConfigurationMapperImpl mapper;

  @Before
  public void init() {
    when(scmPathInfoStore.get().getApiRestUri()).thenReturn(baseUri);
    expectedBaseUri = baseUri.resolve("v2/redmine/configuration/");
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldMapAttributesToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertEquals( "heartofgo.ld", dto.getUrl());
    assertEquals(TextFormatting.MARKDOWN, dto.getTextFormatting());
    assertTrue(dto.isAutoClose());
    assertFalse(dto.isUpdateIssues());
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldAddHalLinksToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertEquals(expectedBaseUri.toString() + "foo/bar", dto.getLinks().getLinkBy("self").get().getHref());
    assertEquals(expectedBaseUri.toString() + "foo/bar", dto.getLinks().getLinkBy("update").get().getHref());
  }

  @Test
  @SubjectAware(username = "unpriv",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldNotAddUpdateLinkToDtoIfNotPermitted() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertFalse(dto.getLinks().getLinkBy("update").isPresent());
  }

  @Test
  public void shouldMapAttributesFromDto() {
    RedmineConfiguration configuration = mapper.map(createDto(), createConfiguration());
    assertEquals( "heartofgo.ld", configuration.getUrl());
    assertEquals(TextFormatting.MARKDOWN, configuration.getTextFormatting());
    assertTrue(configuration.isAutoClose());
    assertFalse(configuration.isUpdateIssues());
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldMapGlobalConfigurationAttributesToDto() {
    RedmineGlobalConfigurationDto dto = mapper.map(createGlobalConfiguration());
    assertFalse(dto.isDisableRepositoryConfiguration());
  }

  @Test
  public void shouldMapGlobalConfigurationDtoAttributesFromDto() {
    RedmineGlobalConfiguration configuration = mapper.map(createGlobalConfigurationDto(), createGlobalConfiguration());
    assertFalse(configuration.isDisableRepositoryConfiguration());
  }

  @Test
  @SubjectAware(
    username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldReplacePasswordAfterMappingOnGlobalDto() {
    RedmineGlobalConfigurationDto configuration = mapper.map(createGlobalConfiguration());
    assertEquals(RedmineConfigurationMapper.DUMMY_PASSWORD, configuration.getPassword());
  }

  @Test
  @SubjectAware(
    username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldReplacePasswordAfterMappingDto() {
    RedmineConfigurationDto configuration = mapper.map(createConfiguration(), createRepository());
    assertEquals(RedmineConfigurationMapper.DUMMY_PASSWORD, configuration.getPassword());
  }

  @Test
  @SubjectAware(
    username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldRestorePasswordAfterMappingFromGlobalDto() {
    RedmineGlobalConfigurationDto dto = createGlobalConfigurationDto();
    dto.setPassword(RedmineConfigurationMapper.DUMMY_PASSWORD);

    RedmineGlobalConfiguration configuration = mapper.map(dto, createGlobalConfiguration());
    assertEquals("secret", configuration.getPassword());
  }

  @Test
  @SubjectAware(
    username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldRestorePasswordAfterMappingFromDto() {
    RedmineConfigurationDto dto = createDto();
    dto.setPassword(RedmineConfigurationMapper.DUMMY_PASSWORD);

    RedmineConfiguration configuration = mapper.map(dto, createConfiguration());
    assertEquals("secret", configuration.getPassword());
  }

  private RedmineConfiguration createConfiguration() {
    return new RedmineConfiguration("heartofgo.ld",
      TextFormatting.MARKDOWN,
      true,
      false,
      "trillian",
      "secret",
      Collections.emptyMap(),
      false
    );
  }

  private RedmineConfigurationDto createDto() {
    return new RedmineConfigurationDto("heartofgo.ld",
      TextFormatting.MARKDOWN,
      true,
      false,
      "trillian",
      "secret"
      ,
      Collections.emptyMap(),
      false
    );
  }

  private RedmineGlobalConfiguration createGlobalConfiguration() {
    RedmineGlobalConfiguration configuration = new RedmineGlobalConfiguration();
    configuration.setUrl("");
    configuration.setUsername("trillian");
    configuration.setPassword("secret");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }

  private RedmineGlobalConfigurationDto createGlobalConfigurationDto() {
    RedmineGlobalConfigurationDto configuration = new RedmineGlobalConfigurationDto();
    configuration.setUrl("");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }

  private Repository createRepository() {
    return new Repository("42", "GIT", "foo", "bar");
  }
}
