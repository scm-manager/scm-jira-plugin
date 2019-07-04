package sonia.scm.jira.update;

import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraGlobalConfiguration;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.version.Version;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static sonia.scm.version.Version.parse;

@Extension
public class JiraV2GlobalConfigMigration implements UpdateStep {

  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public JiraV2GlobalConfigMigration(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    storeFactory.withType(V1JiraGlobalConfiguration.class).withName("jira").build().getOptional()
      .ifPresent(
        v1JiraConfig -> {
          JiraGlobalConfiguration v2JiraConfig = new JiraGlobalConfiguration();
          v2JiraConfig.copyFrom(v1JiraConfig);
          v2JiraConfig.setDisableRepositoryConfiguration(v1JiraConfig.isDisableRepositoryConfiguration());
          storeFactory.withType(JiraGlobalConfiguration.class).withName("jira").build().set(v2JiraConfig);
        }
      );
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
  public static class V1JiraGlobalConfiguration extends JiraConfiguration {

    public boolean isDisableRepositoryConfiguration() {
      return disableRepositoryConfiguration;
    }

    @XmlElement(name = "disable-repository-configuration")
    private boolean disableRepositoryConfiguration = false;
  }
}
