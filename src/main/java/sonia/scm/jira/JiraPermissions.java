package sonia.scm.jira;

import com.google.inject.Inject;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

class JiraPermissions {
  private final ScmConfiguration configuration;

  @Inject
  JiraPermissions(ScmConfiguration configuration) {
    this.configuration = configuration;
  }

  boolean isPermittedReadGlobalConfig() {
    return ConfigurationPermissions.list().isPermitted();
  }

  boolean isPermittedWriteGlobalConfig() {
    return ConfigurationPermissions.write(configuration).isPermitted();
  }

  void checkReadGlobalConfig() {
    ConfigurationPermissions.read(configuration).check();
  }

  void checkWriteGlobalConfig() {
    ConfigurationPermissions.read(configuration).check();
  }

  public boolean isPermittedReadRepositoryConfig(Repository repository) {
    return RepositoryPermissions.modify(repository).isPermitted();
  }

  public boolean isPermittedWriteRepositoryConfig(Repository repository) {
    return RepositoryPermissions.modify(repository).isPermitted();
  }

  public void checkReadRepositoryConfig(Repository repository) {
    RepositoryPermissions.modify(repository).check();
  }

  public void checkWriteRepositoryConfig(Repository repository) {
    RepositoryPermissions.modify(repository).check();
  }
}
