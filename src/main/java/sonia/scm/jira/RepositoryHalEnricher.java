package sonia.scm.jira;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryHalEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final JiraGlobalContext jiraContext;
  private final JiraPermissions permissions;

  @Inject
  public RepositoryHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, JiraGlobalContext jiraContext, JiraPermissions permissions) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.jiraContext = jiraContext;
    this.permissions = permissions;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (!jiraContext.getGlobalConfiguration().isDisableRepositoryConfiguration() && permissions.isPermittedReadRepositoryConfig(repository)) {
      String globalJiraConfigUrl = new LinkBuilder(scmPathInfoStore.get().get(), JiraConfigurationResource.class)
        .method("getForRepository")
        .parameters(repository.getNamespace(), repository.getName())
        .href();
      appender.appendLink("jiraConfig", globalJiraConfigUrl);
    }
  }
}