/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
