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

package sonia.scm.jira;

import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public class JiraPermissions {

  private static final String PERMISSION_NAME = "jira";

  private JiraPermissions() {
  }

  public static boolean isPermittedReadGlobalConfig() {
    return ConfigurationPermissions.read(PERMISSION_NAME).isPermitted();
  }

  public static boolean isPermittedWriteGlobalConfig() {
    return ConfigurationPermissions.write(PERMISSION_NAME).isPermitted();
  }

  public static void checkReadGlobalConfig() {
    ConfigurationPermissions.read(PERMISSION_NAME).check();
  }

  public static void checkWriteGlobalConfig() {
    ConfigurationPermissions.write(PERMISSION_NAME).check();
  }

  public static boolean isPermittedReadRepositoryConfig(Repository repository) {
    return RepositoryPermissions.custom(PERMISSION_NAME, repository).isPermitted();
  }

  public static boolean isPermittedWriteRepositoryConfig(Repository repository) {
    return RepositoryPermissions.custom(PERMISSION_NAME, repository).isPermitted();
  }

  public static void checkReadRepositoryConfig(Repository repository) {
    RepositoryPermissions.custom(PERMISSION_NAME, repository).check();
  }

  public static void checkWriteRepositoryConfig(Repository repository) {
    RepositoryPermissions.custom(PERMISSION_NAME, repository).check();
  }

  public static void checkWriteRepositoryConfig(String repositoryId) {
    RepositoryPermissions.custom(PERMISSION_NAME, repositoryId).check();
  }
}
