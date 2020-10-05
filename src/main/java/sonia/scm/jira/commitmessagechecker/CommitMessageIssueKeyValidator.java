package sonia.scm.jira.commitmessagechecker;

import com.cloudogu.scm.commitmessagechecker.Context;
import com.cloudogu.scm.commitmessagechecker.InvalidCommitMessageException;
import com.cloudogu.scm.commitmessagechecker.Validator;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.util.RegExPatternMatcher;
import sonia.scm.ContextEntry;
import sonia.scm.jira.IssueKeys;
import sonia.scm.jira.JiraConfigurationResolver;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.plugin.Extension;
import sonia.scm.util.GlobUtil;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

@Extension
public class CommitMessageIssueKeyValidator implements Validator {

  private static final String DEFAULT_ERROR_MESSAGE = "The commit message doesn't contain a valid Jira issue key.";
  private static final RegExPatternMatcher matcher = new RegExPatternMatcher();

  private final JiraGlobalContext jiraGlobalContext;

  @Inject
  public CommitMessageIssueKeyValidator(JiraGlobalContext jiraGlobalContext) {
    this.jiraGlobalContext = jiraGlobalContext;
  }

  @Override
  public boolean isApplicableMultipleTimes() {
    return false;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(CommitMessageIssueKeyValidatorConfig.class);
  }

  @Override
  public void validate(Context context, String commitMessage) {
    CommitMessageIssueKeyValidatorConfig configuration = context.getConfiguration(CommitMessageIssueKeyValidatorConfig.class);
    String commitBranch = context.getBranch();
    Pattern issueKeyPattern = IssueKeys.createPattern(JiraConfigurationResolver.resolve(jiraGlobalContext, context.getRepository()).getFilter());

    if (shouldValidateBranch(configuration, commitBranch) && isInvalidCommitMessage(issueKeyPattern.pattern(), commitMessage)) {
      throw new InvalidCommitMessageException(
        ContextEntry.ContextBuilder.entity(context.getRepository()),
        DEFAULT_ERROR_MESSAGE
      );
    }
  }

  private boolean shouldValidateBranch(CommitMessageIssueKeyValidatorConfig configuration, String commitBranch) {
    if (Strings.isNullOrEmpty(commitBranch) || Strings.isNullOrEmpty(configuration.getBranches())) {
      return true;
    }
    return Arrays
      .stream(configuration.getBranches().split(","))
      .anyMatch(branch -> GlobUtil.matches(branch.trim(), commitBranch));
  }

  private boolean isInvalidCommitMessage(String pattern, String commitMessage) {
    String keyPattern = MessageFormat.format(".*{0}.*", pattern);
    return !matcher.matches(keyPattern, commitMessage);
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @XmlRootElement
  static class CommitMessageIssueKeyValidatorConfig {
    private String branches;
  }
}
