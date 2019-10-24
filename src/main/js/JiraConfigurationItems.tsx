import React from "react";
import { Button, Checkbox, Configuration, InputField, validation } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";

type JiraConfiguration = {
  url: string;
  disableRepositoryConfiguration: boolean;
  updateIssues: boolean;
  autoClose: boolean;
  autoCloseWords: string;
  roleLevel: string;
  commentPrefix: string;
  filter: string;
  username: string;
  password: string;
  resubmission: boolean;
  restApiEnabled: boolean;
  mailAddress: string;
  commentWrap: string;
  commentMonospace: boolean;
};

type Props = WithTranslation & {
  initialConfiguration: Configuration;
  readOnly: boolean;
  onConfigurationChange: (p1: Configuration, p2: boolean) => void;
  includeGlobalConfigItem: boolean;
  resubmitHandler: () => void;
};

type State = JiraConfiguration & {
  mailValid: boolean;
};

class JiraConfigurationItems extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      mailValid: true,
      ...props.initialConfiguration
    };
  }

  emailChangeHandler = (value: string, name: string) => {
    const mailValid = value === "" || validation.isMailValid(value);
    this.setState(
      {
        mailAddress: value,
        mailValid: mailValid
      },
      this.configurationChangedCallback
    );
  };

  valueChangeHandler = (value: string, name: string) => {
    this.setState(
      {
        [name]: value
      },
      this.configurationChangedCallback
    );
  };

  configurationChangedCallback = () => {
    this.props.onConfigurationChange(
      {
        ...this.state
      },
      this.isValid()
    );
  };

  isValid = () => {
    return this.state.mailValid;
  };

  resubmit = () => {
    this.props.resubmitHandler();
  };

  render() {
    const { t, readOnly } = this.props;
    return (
      <div className="columns is-multiline">
        <div className="column is-full">
          <InputField
            name="url"
            label={t("scm-jira-plugin.form.url")}
            helpText={t("scm-jira-plugin.form.urlHelp")}
            disabled={readOnly}
            value={this.state.url}
            type="url"
            onChange={this.valueChangeHandler}
          />
        </div>
        {this.renderGlobalConfigItem()}
        <div className="column is-half">
          <Checkbox
            name="updateIssues"
            label={t("scm-jira-plugin.form.updateJiraIssues")}
            helpText={t("scm-jira-plugin.form.updateJiraIssuesHelp")}
            checked={this.state.updateIssues}
            disabled={readOnly}
            onChange={this.valueChangeHandler}
          />
          <Checkbox
            name="autoClose"
            label={t("scm-jira-plugin.form.autoClose")}
            helpText={t("scm-jira-plugin.form.autoCloseHelp")}
            checked={this.state.autoClose}
            disabled={readOnly || !this.state.updateIssues}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-full">
          <InputField
            name="autoCloseWords"
            label={t("scm-jira-plugin.form.autoCloseWords")}
            helpText={t("scm-jira-plugin.form.autoCloseWordsHelp")}
            disabled={readOnly || !this.state.updateIssues || !this.state.autoClose}
            value={this.state.autoCloseWords}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="roleLevel"
            label={t("scm-jira-plugin.form.roleLevel")}
            helpText={t("scm-jira-plugin.form.roleLevelHelp")}
            disabled={readOnly || !this.state.updateIssues}
            value={this.state.roleLevel}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="filter"
            label={t("scm-jira-plugin.form.filter")}
            helpText={t("scm-jira-plugin.form.filterHelp")}
            disabled={readOnly || !this.state.updateIssues}
            value={this.state.filter}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="username"
            label={t("scm-jira-plugin.form.username")}
            helpText={t("scm-jira-plugin.form.usernameHelp")}
            disabled={readOnly || !this.state.updateIssues}
            value={this.state.username}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="password"
            label={t("scm-jira-plugin.form.password")}
            helpText={t("scm-jira-plugin.form.passwordHelp")}
            disabled={readOnly || !this.state.updateIssues}
            value={this.state.password}
            type="password"
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-full">
          <Checkbox
            name="resubmission"
            label={t("scm-jira-plugin.form.resubmission")}
            helpText={t("scm-jira-plugin.form.resubmissionHelp")}
            checked={this.state.resubmission}
            disabled={readOnly || !this.state.updateIssues}
            onChange={this.valueChangeHandler}
          />
          <Checkbox
            name="restApiEnabled"
            label={t("scm-jira-plugin.form.restApiEnabled")}
            helpText={t("scm-jira-plugin.form.restApiEnabledHelp")}
            checked={this.state.restApiEnabled}
            disabled={readOnly}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="mailAddress"
            label={t("scm-jira-plugin.form.mailAddress")}
            helpText={t("scm-jira-plugin.form.mailAddressHelp")}
            errorMessage={t("scm-jira-plugin.form.mailAddressError")}
            validationError={!this.state.mailValid}
            disabled={readOnly || !this.state.resubmission}
            value={this.state.mailAddress}
            onChange={this.emailChangeHandler}
          />
        </div>
        <div className="column is-half">
          <InputField
            name="commentWrap"
            label={t("scm-jira-plugin.form.commentWrap")}
            helpText={t("scm-jira-plugin.form.commentWrapHelp")}
            disabled={readOnly}
            value={this.state.commentWrap}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-full">
          <Checkbox
            name="commentMonospace"
            label={t("scm-jira-plugin.form.commentMonospace")}
            helpText={t("scm-jira-plugin.form.commentMonospaceHelp")}
            checked={this.state.commentMonospace}
            disabled={readOnly}
            onChange={this.valueChangeHandler}
          />
          <Button label={t("scm-jira-plugin.form.resubmit")} action={this.resubmit} />
        </div>
      </div>
    );
  }

  renderGlobalConfigItem() {
    const { t, includeGlobalConfigItem, readOnly } = this.props;
    if (includeGlobalConfigItem) {
      return (
        <div className="column is-half">
          <Checkbox
            name="disableRepositoryConfiguration"
            label={t("scm-jira-plugin.form.disableRepositoryConfiguration")}
            helpText={t("scm-jira-plugin.form.disableRepositoryConfigurationHelp")}
            checked={this.state.disableRepositoryConfiguration}
            disabled={readOnly}
            onChange={this.valueChangeHandler}
          />
        </div>
      );
    } else {
      return null;
    }
  }
}

export default withTranslation("plugins")(JiraConfigurationItems);
