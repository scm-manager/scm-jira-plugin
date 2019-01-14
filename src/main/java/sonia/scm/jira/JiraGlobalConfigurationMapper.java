package sonia.scm.jira;

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.config.ScmConfiguration;

import javax.inject.Inject;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class JiraGlobalConfigurationMapper extends AutoCloseMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @Mapping(target = "autoCloseWords", ignore = true)
  @Mapping(target = "attributes", ignore = true)
  public abstract JiraGlobalConfigurationDto map(JiraGlobalConfiguration config, @Context ScmConfiguration scmConfiguration);

  @Mapping(target = "autoCloseWords", ignore = true)
  public abstract JiraGlobalConfiguration map(JiraGlobalConfigurationDto dto);

  @AfterMapping
  void appendLinks(@MappingTarget JiraGlobalConfigurationDto target, @Context ScmConfiguration scmConfiguration) {
    Links.Builder linksBuilder = linkingTo().self(self());
    if (ConfigurationPermissions.write(scmConfiguration).isPermitted()) {
      linksBuilder.single(link("update", update()));
    }
    if (ConfigurationPermissions.read(scmConfiguration).isPermitted()) {
      target.add(linksBuilder.build());
    }
  }

  private String self() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), JiraConfigurationResource.class);
    return linkBuilder.method("get").parameters().href();
  }

  private String update() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), JiraConfigurationResource.class);
    return linkBuilder.method("update").parameters().href();
  }

  void setScmPathInfoStore(ScmPathInfoStore scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }
}
