package sonia.scm.jira;

import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

class JiraPermissions {

  private static final String PERMISSION_NAME = "jira";

  boolean isPermittedReadGlobalConfig() {
    return ConfigurationPermissions.list().isPermitted();
  }

  boolean isPermittedWriteGlobalConfig() {
    return ConfigurationPermissions.write(PERMISSION_NAME).isPermitted();
  }

  void checkReadGlobalConfig() {
    ConfigurationPermissions.read(PERMISSION_NAME).check();
  }

  void checkWriteGlobalConfig() {
    ConfigurationPermissions.read(PERMISSION_NAME).check();
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
