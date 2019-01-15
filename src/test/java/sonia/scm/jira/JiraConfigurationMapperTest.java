package sonia.scm.jira;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
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
    JiraPermissions permissions = new JiraPermissions();
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("/"));
    mapper = Mappers.getMapper(JiraConfigurationMapper.class);
    mapper.setPermissions(permissions);
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
