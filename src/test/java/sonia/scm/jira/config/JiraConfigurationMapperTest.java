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
package sonia.scm.jira.config;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.jira.JiraPermissions;
import sonia.scm.repository.Repository;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SubjectAware(configuration = "classpath:sonia/scm/jira/shiro-001.ini")
public class JiraConfigurationMapperTest {

  public static final Repository REPOSITORY = new Repository("X", "git", "space", "X");

  @Rule
  public ShiroRule shiroRule = new ShiroRule();

  private JiraConfigurationMapper mapper;

  @Before
  public void init() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("/"));
    mapper = Mappers.getMapper(JiraConfigurationMapper.class);
    mapper.setScmPathInfoStore(scmPathInfoStore);
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldNotExposePassword() {
    JiraConfiguration config = new JiraConfiguration();
    config.setPassword("DO_NOT_MAP");

    JiraConfigurationDto mappedConfig = mapper.map(config, REPOSITORY);

    assertNotEquals("DO_NOT_MAP", mappedConfig.getPassword());
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldNotHideUnsetPassword() {
    JiraConfiguration config = new JiraConfiguration();
    config.setPassword(null);

    JiraConfigurationDto mappedConfig = mapper.map(config, REPOSITORY);

    assertNull(mappedConfig.getPassword());
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldNotHideEmptyPassword() {
    JiraConfiguration config = new JiraConfiguration();
    config.setPassword("");

    JiraConfigurationDto mappedConfig = mapper.map(config, REPOSITORY);

    assertEquals("", mappedConfig.getPassword());
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldNotOverwriteWithDummyPassword() {
    JiraConfigurationDto dto = new JiraConfigurationDto();
    dto.setPassword(BaseMapper.DUMMY_PASSWORD);

    JiraConfiguration oldConfig = new JiraConfiguration();
    oldConfig.setPassword("should_not_be_overwritten");

    JiraConfiguration mappedConfig = mapper.map(dto, oldConfig);

    assertEquals("should_not_be_overwritten", mappedConfig.getPassword());
  }

  @Test
  @SubjectAware(username = "dent", password = "secret")
  public void shouldOverwriteWithChangedPassword() {
    JiraConfigurationDto dto = new JiraConfigurationDto();
    dto.setPassword("new_password");

    JiraConfiguration oldConfig = new JiraConfiguration();
    oldConfig.setPassword("should_be_overwritten");

    JiraConfiguration mappedConfig = mapper.map(dto, oldConfig);

    assertEquals("new_password", mappedConfig.getPassword());
  }
}
