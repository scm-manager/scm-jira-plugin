package sonia.scm.jira;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryLinkEnricher implements LinkEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final JiraGlobalContext jiraContext;

  @Inject
  public RepositoryLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, JiraGlobalContext jiraContext) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.jiraContext = jiraContext;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (!jiraContext.getConfiguration().isDisableRepositoryConfiguration() && RepositoryPermissions.modify(repository).isPermitted()) {
      String globalJiraConfigUrl = new LinkBuilder(scmPathInfoStore.get().get(), JiraConfigurationResource.class)
        .method("getForRepository")
        .parameters(repository.getNamespace(), repository.getName())
        .href();
      appender.appendOne("jiraConfig", globalJiraConfigUrl);
    }
  }
}