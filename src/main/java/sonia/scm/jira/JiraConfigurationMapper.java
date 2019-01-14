package sonia.scm.jira;

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;

import java.util.Map;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class JiraConfigurationMapper extends AutoCloseMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @Mapping(target = "autoCloseWords", ignore = true)
  @Mapping(target = "attributes", ignore = true)
  public abstract JiraConfigurationDto map(JiraConfiguration config, @Context Repository repository);

  @Mapping(target = "autoCloseWords", ignore = true)
  public abstract JiraConfiguration map(JiraConfigurationDto dto);

  @AfterMapping
  void appendLinks(@MappingTarget JiraConfigurationDto target, @Context Repository repository) {
    Links.Builder linksBuilder = linkingTo().self(self(repository));
    if (RepositoryPermissions.modify(repository).isPermitted()) {
      linksBuilder.single(link("update", update(repository)));
    }
    target.add(linksBuilder.build());
  }

  private String self(Repository repository) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), JiraConfigurationResource.class);
    return linkBuilder.method("getForRepository").parameters(repository.getNamespace(), repository.getName()).href();
  }

  private String update(Repository repository) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), JiraConfigurationResource.class);
    return linkBuilder.method("updateForRepository").parameters(repository.getNamespace(), repository.getName()).href();
  }

  void setScmPathInfoStore(ScmPathInfoStore scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }
}
