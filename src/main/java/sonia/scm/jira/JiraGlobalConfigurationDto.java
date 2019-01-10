package sonia.scm.jira;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JiraGlobalConfigurationDto extends JiraConfigurationDto {
  private boolean disableRepositoryConfiguration;
}
