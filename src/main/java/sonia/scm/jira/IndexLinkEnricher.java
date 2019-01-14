package sonia.scm.jira;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Index.class)
public class IndexLinkEnricher implements LinkEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final JiraPermissions permissions;

  @Inject
  public IndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, JiraPermissions permissions) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.permissions = permissions;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    if (permissions.isPermittedReadGlobalConfig()) {
      String globalJiraConfigUrl = new LinkBuilder(scmPathInfoStore.get().get(), JiraConfigurationResource.class)
        .method("get")
        .parameters()
        .href();
      appender.appendOne("jiraConfig", globalJiraConfigUrl);
    }
  }
}
