package sonia.scm.jira;

import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public class JiraPermissions {

  private static final String PERMISSION_NAME = "jira";

  public boolean isPermittedReadGlobalConfig() {
    return ConfigurationPermissions.read(PERMISSION_NAME).isPermitted();
  }

  public boolean isPermittedWriteGlobalConfig() {
    return ConfigurationPermissions.write(PERMISSION_NAME).isPermitted();
  }

  public void checkReadGlobalConfig() {
    ConfigurationPermissions.read(PERMISSION_NAME).check();
  }

  public void checkWriteGlobalConfig() {
    ConfigurationPermissions.write(PERMISSION_NAME).check();
  }

  public boolean isPermittedReadRepositoryConfig(Repository repository) {
    return RepositoryPermissions.custom(PERMISSION_NAME, repository).isPermitted();
  }

  public boolean isPermittedWriteRepositoryConfig(Repository repository) {
    return RepositoryPermissions.custom(PERMISSION_NAME, repository).isPermitted();
  }

  public void checkReadRepositoryConfig(Repository repository) {
    RepositoryPermissions.custom(PERMISSION_NAME, repository).check();
  }

  public void checkWriteRepositoryConfig(Repository repository) {
    RepositoryPermissions.custom(PERMISSION_NAME, repository).check();
  }

  public void checkWriteRepositoryConfig(String repositoryId) {
    RepositoryPermissions.custom(PERMISSION_NAME, repositoryId).check();
  }
}
