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
package sonia.scm.jira.commitmessagechecker;

import com.cloudogu.scm.commitmessagechecker.Context;
import com.cloudogu.scm.commitmessagechecker.InvalidCommitMessageException;
import com.cloudogu.scm.commitmessagechecker.Validator;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.ContextEntry;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.config.JiraConfigurationResolver;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.util.GlobUtil;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static sonia.scm.jira.IssueKeys.PATTERN_EXPRESSION_LOADER;

@Extension
@Requires("scm-commit-message-checker-plugin")
public class JiraCommitMessageIssueKeyValidator implements Validator {

  private static final String DEFAULT_ERROR_MESSAGE = "The commit message doesn't contain a valid Jira issue key.";

  private static final CacheLoader<String, Pattern> PATTERN_LOADER = new CacheLoader<String, Pattern>() {
    @Override
    public Pattern load(String key) {
      String pattern = PATTERN_EXPRESSION_LOADER.apply(key);
      return Pattern.compile(MessageFormat.format(".*{0}.*", pattern));
    }
  };

  private static final LoadingCache<String, Pattern> PATTERN_CACHE = CacheBuilder
    .newBuilder()
    .expireAfterAccess(2, TimeUnit.HOURS)
    .build(PATTERN_LOADER);

  private final JiraConfigurationResolver jiraConfigurationResolver;

  @Inject
  public JiraCommitMessageIssueKeyValidator(JiraConfigurationResolver jiraConfigurationResolver) {
    this.jiraConfigurationResolver = jiraConfigurationResolver;
  }

  @Override
  public boolean isApplicableMultipleTimes() {
    return false;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(JiraCommitMessageIssueKeyValidatorConfig.class);
  }

  @Override
  public void validate(Context context, String commitMessage) {
    JiraCommitMessageIssueKeyValidatorConfig configuration = context.getConfiguration(JiraCommitMessageIssueKeyValidatorConfig.class);
    String commitBranch = context.getBranch();

    String filter = jiraConfigurationResolver.resolve(context.getRepository())
      .map(JiraConfiguration::getFilter)
      .orElse(null);
    if (shouldValidateBranch(configuration, commitBranch) && isInvalidCommitMessage(filter, commitMessage)) {
      throw new InvalidCommitMessageException(
        ContextEntry.ContextBuilder.entity(context.getRepository()),
        DEFAULT_ERROR_MESSAGE
      );
    }
  }

  private boolean shouldValidateBranch(JiraCommitMessageIssueKeyValidatorConfig configuration, String commitBranch) {
    if (Strings.isNullOrEmpty(commitBranch) || Strings.isNullOrEmpty(configuration.getBranches())) {
      return true;
    }
    return Arrays
      .stream(configuration.getBranches().split(","))
      .anyMatch(branch -> GlobUtil.matches(branch.trim(), commitBranch));
  }

  private boolean isInvalidCommitMessage(String filter, String commitMessage) {
    return !PATTERN_CACHE.getUnchecked(filter).matcher(commitMessage).matches();
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @XmlRootElement
  static class JiraCommitMessageIssueKeyValidatorConfig {
    private String branches;
  }
}
