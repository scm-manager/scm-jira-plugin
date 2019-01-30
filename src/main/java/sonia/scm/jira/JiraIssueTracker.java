package sonia.scm.jira;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.issuetracker.CommentHandler;
import sonia.scm.issuetracker.DataStoreBasedIssueTracker;
import sonia.scm.issuetracker.IssueLinkFactory;
import sonia.scm.issuetracker.IssueMatcher;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Extension
@EagerSingleton
public class JiraIssueTracker extends DataStoreBasedIssueTracker {

  public static final String JIRA_ISSUE_TRACKER_NAME = "jira";
  public static final String JIRA_URL_TEMPLATE = "{0}/browse/{1}";

  private static final Logger logger = LoggerFactory.getLogger(JiraIssueTracker.class);

  private final JiraGlobalContext context;
  private final MessageProblemHandler messageProblemHandler;
  private final Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider;
  private final JiraIssueRequestFactory requestFactory;

  @Inject
  public JiraIssueTracker(JiraGlobalContext context, DataStoreFactory storeFactory, MessageProblemHandler messageProblemHandler, Provider<CommentTemplateHandlerFactory> templateHandlerFactoryProvider, JiraIssueRequestFactory requestFactory) {
    super(JIRA_ISSUE_TRACKER_NAME, storeFactory);
    this.context = context;
    this.messageProblemHandler = messageProblemHandler;
    this.templateHandlerFactoryProvider = templateHandlerFactoryProvider;
    this.requestFactory = requestFactory;
  }

  @Override
  public Optional<IssueMatcher> createMatcher(Repository repository) {
    Pattern pattern = IssueKeys.createPattern(JiraConfigurationResolver.resolve(context, repository).getFilter());
    return of(new IssueMatcher() {
      @Override
      public String getKey(Matcher matcher) {
        return matcher.group();
      }

      @Override
      public Pattern getKeyPattern() {
        return pattern;
      }
    });
  }

  @Override
  public Optional<IssueLinkFactory> createLinkFactory(Repository repository) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, repository);
    String jiraUrl = configuration.getUrl();
    if (jiraUrl == null) {
      return empty();
    } else {
      return of(key -> MessageFormat.format(JIRA_URL_TEMPLATE, jiraUrl, key));
    }
  }

  @Override
  protected CommentHandler getCommentHandler(IssueRequest request) {
    JiraConfiguration configuration = JiraConfigurationResolver.resolve(context, request.getRepository());
    JiraIssueRequest jiraIssueRequest = requestFactory.createRequest(configuration, request.getRepository(), request.getChangeset());
    return new JiraIssueHandler(messageProblemHandler, templateHandlerFactoryProvider.get(), jiraIssueRequest);
  }
}
