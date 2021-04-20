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

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.jira.JiraPermissions;

import javax.inject.Inject;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class JiraGlobalConfigurationMapper extends BaseMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;
  @Inject
  private JiraPermissions permissions;

  @Mapping(target = "autoCloseWords", ignore = true)
  @Mapping(target = "attributes", ignore = true)
  public abstract JiraGlobalConfigurationDto map(JiraGlobalConfiguration config);

  @Mapping(target = "autoCloseWords", ignore = true)
  public abstract JiraGlobalConfiguration map(JiraGlobalConfigurationDto dto, @Context JiraConfiguration oldConfiguration);

  @AfterMapping
  void appendLinks(@MappingTarget JiraGlobalConfigurationDto target) {
    Links.Builder linksBuilder = linkingTo().self(self());
    if (permissions.isPermittedWriteGlobalConfig()) {
      linksBuilder.single(link("update", update()));
    }
    target.add(linksBuilder.build());
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

  void setPermissions(JiraPermissions permissions) {
    this.permissions = permissions;
  }
}
