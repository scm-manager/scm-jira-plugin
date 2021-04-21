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
