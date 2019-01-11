package sonia.scm.jira;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

@Getter @Setter
public class JiraConfigurationDto extends HalRepresentation {
  private boolean autoClose;
  private String autoCloseWords;
  @Email
  private String mailAddress;
  private String password;
  private boolean restApiEnabled = false;
  private String commentPrefix;
  private String filter;
  private boolean resubmission;
  private String roleLevel;
  private boolean updateIssues;
  private String url;
  private String username;
  private String commentWrap;
  private boolean commentMonospace;

  @Override
  @SuppressWarnings("squid:S1185") // We want to have this method available in this package
  protected HalRepresentation add(Links links) {
    return super.add(links);
  }
}
