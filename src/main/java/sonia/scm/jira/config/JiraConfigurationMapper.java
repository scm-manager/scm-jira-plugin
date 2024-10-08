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

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.jira.JiraPermissions;
import sonia.scm.repository.Repository;

import jakarta.inject.Inject;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class JiraConfigurationMapper extends BaseMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @Mapping(target = "attributes", ignore = true)
  public abstract JiraConfigurationDto map(JiraConfiguration config, @Context Repository repository);

  public abstract JiraConfiguration map(JiraConfigurationDto dto, @Context JiraConfiguration oldConfiguration);

  @AfterMapping
  void appendLinks(@MappingTarget JiraConfigurationDto target, @Context Repository repository) {
    Links.Builder linksBuilder = linkingTo().self(self(repository));
    if (JiraPermissions.isPermittedWriteRepositoryConfig(repository)) {
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
