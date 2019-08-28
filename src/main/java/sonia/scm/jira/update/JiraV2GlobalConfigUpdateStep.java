package sonia.scm.jira.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.XmlEncryptionAdapter;
import sonia.scm.jira.JiraGlobalConfiguration;
import sonia.scm.jira.XmlStringMapAdapter;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.StoreException;
import sonia.scm.version.Version;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static sonia.scm.version.Version.parse;

@Extension
public class JiraV2GlobalConfigUpdateStep implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(JiraV2ConfigMigrationUpdateStep.class);

  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public JiraV2GlobalConfigUpdateStep(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    readV1JiraConfiguration()
      .ifPresent(
        v1JiraConfig -> {
          JiraGlobalConfiguration v2JiraConfig = new JiraGlobalConfiguration();
          v1JiraConfig.copyTo(v2JiraConfig);
          storeFactory.withType(JiraGlobalConfiguration.class).withName("jira").build().set(v2JiraConfig);
        }
      );
  }

  private Optional<V1JiraGlobalConfiguration> readV1JiraConfiguration() {
    try {
      return storeFactory.withType(V1JiraGlobalConfiguration.class).withName("jira").build().getOptional();
    } catch (StoreException e) {
      LOG.debug("could not read existing jira configuration store; assume that it already is a v2 store", e);
      return empty();
    }
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.jira.config.global.xml";
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "jira-configuration")
  public static class V1JiraGlobalConfiguration {
    @XmlElement(name = "auto-close")
    private boolean autoClose;
    @XmlElement(name = "auto-close-words")
    @XmlJavaTypeAdapter(XmlStringMapAdapter.class)
    private Map<String, String> autoCloseWords;
    @XmlElement(name = "mail-error-address")
    private String mailAddress;
    @XmlJavaTypeAdapter(XmlEncryptionAdapter.class)
    private String password;
    @XmlElement(name = "rest-api-enabled")
    private boolean restApiEnabled = false;
    @XmlElement(name = "comment-prefix")
    private String commentPrefix;
    @XmlElement(name = "filter")
    private String filter;
    @XmlElement(name = "resubmission")
    private boolean resubmission;
    @XmlElement(name = "role-level")
    private String roleLevel;
    @XmlElement(name = "update-issues")
    private boolean updateIssues;
    private String url;
    private String username;
    @XmlElement(name = "comment-wrap")
    private String commentWrap;
    @XmlElement(name = "comment-monospace")
    private boolean commentMonospace;
    @XmlElement(name = "disable-repository-configuration")
    private boolean disableRepositoryConfiguration = false;

    public void copyTo(JiraGlobalConfiguration v2JiraConfig) {
        v2JiraConfig.setUrl(this.url);
        v2JiraConfig.setAutoClose(this.autoClose);
        v2JiraConfig.setCommentMonospace(this.commentMonospace);
        v2JiraConfig.setUsername(this.username);
        v2JiraConfig.setPassword(this.password);
        v2JiraConfig.setCommentPrefix(this.commentPrefix);
        v2JiraConfig.setUpdateIssues(this.updateIssues);
        v2JiraConfig.setAutoCloseWordsForMapping(this.autoCloseWords);
        v2JiraConfig.setRestApiEnabled(this.restApiEnabled);
        v2JiraConfig.setMailAddress(this.mailAddress);
        v2JiraConfig.setCommentWrap(this.commentWrap);
        v2JiraConfig.setFilter(this.filter);
        v2JiraConfig.setRoleLevel(this.roleLevel);
        v2JiraConfig.setResubmission(this.resubmission);
        v2JiraConfig.setDisableRepositoryConfiguration(this.disableRepositoryConfiguration);
    }
  }
}