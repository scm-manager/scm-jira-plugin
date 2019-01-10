//@flow

import React from "react";
import {Checkbox, Configuration, InputField} from "@scm-manager/ui-components";
import {translate} from "react-i18next";

type JiraConfiguration = {
  url: string,
  disableRepositoryConfiguration: boolean,
  updateIssues: boolean,
  autoClose: boolean,
  autoCloseWords: string,
  roleLevel: string,
  commentPrefix: string,
  filter: string,
  username: string,
  password: string,
  resubmission: boolean,
  restApiEnabled: boolean,
  mailAddress: string,
  commentWrap: string,
  commentMonospace: boolean
}

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void,
  includeGlobalConfigItem: boolean,
  t: (string) => string
}

type State = JiraConfiguration & {
  configurationChanged: boolean
};

class JiraConfigurationItems extends React.Component<Props, State> {

  constructor(props: Props) {
    super(props);
    this.state = {
      configurationChanged: false,
      ...props.initialConfiguration
    };
  }

  valueChangeHandler = (value: string, name: string) => {
    this.setState({
      [name]: value
    }, () => this.props.onConfigurationChange({...this.state}, true));
  };

  render(): React.ReactNode {
    const {t, readOnly} = this.props;
    console.log("this.state.updateJiraIssues: ", this.state);
    return (
      <>
        {this.renderConfigChangedNotification()}
        <InputField name={"url"}
                    label={t("scm-jira-plugin.form.url")}
                    helpText={t("scm-jira-plugin.form.urlHelp")}
                    disabled={readOnly}
                    value={this.state.url}
                    onChange={this.valueChangeHandler}/>
        {this.renderGlobalConfigItem()}
        <Checkbox name={"updateIssues"}
                  label={t("scm-jira-plugin.form.updateJiraIssues")}
                  helpText={t("scm-jira-plugin.form.updateJiraIssuesHelp")}
                  checked={this.state.updateIssues}
                  disabled={readOnly}
                  onChange={this.valueChangeHandler}/>
        <Checkbox name={"autoClose"}
                  label={t("scm-jira-plugin.form.autoClose")}
                  helpText={t("scm-jira-plugin.form.autoCloseHelp")}
                  checked={this.state.autoClose}
                  disabled={readOnly || !this.state.updateIssues}
                  onChange={this.valueChangeHandler}/>
        <InputField name={"autoCloseWords"}
                    label={t("scm-jira-plugin.form.autoCloseWords")}
                    helpText={t("scm-jira-plugin.form.autoCloseWordsHelp")}
                    disabled={readOnly || !this.state.updateIssues || !this.state.autoClose}
                    value={this.state.autoCloseWords}
                    onChange={this.valueChangeHandler}/>
        <InputField name={"roleLevel"}
                    label={t("scm-jira-plugin.form.roleLevel")}
                    helpText={t("scm-jira-plugin.form.roleLevelHelp")}
                    disabled={readOnly || !this.state.updateIssues}
                    value={this.state.roleLevel}
                    onChange={this.valueChangeHandler}/>
        <InputField name={"filter"}
                    label={t("scm-jira-plugin.form.filter")}
                    helpText={t("scm-jira-plugin.form.filterHelp")}
                    disabled={readOnly || !this.state.updateIssues}
                    value={this.state.filter}
                    onChange={this.valueChangeHandler}/>
        <InputField name={"username"}
                    label={t("scm-jira-plugin.form.username")}
                    helpText={t("scm-jira-plugin.form.usernameHelp")}
                    disabled={readOnly || !this.state.updateIssues}
                    value={this.state.username}
                    onChange={this.valueChangeHandler}/>
        <InputField name={"password"}
                    label={t("scm-jira-plugin.form.password")}
                    helpText={t("scm-jira-plugin.form.passwordHelp")}
                    disabled={readOnly || !this.state.updateIssues}
                    value={this.state.password}
                    type={"password"}
                    onChange={this.valueChangeHandler}/>
        <Checkbox name={"resubmission"}
                  label={t("scm-jira-plugin.form.resubmission")}
                  helpText={t("scm-jira-plugin.form.resubmissionHelp")}
                  checked={this.state.resubmission}
                  disabled={readOnly || !this.state.updateIssues}
                  onChange={this.valueChangeHandler}/>
        <Checkbox name={"restApiEnabled"}
                  label={t("scm-jira-plugin.form.restApiEnabled")}
                  helpText={t("scm-jira-plugin.form.restApiEnabledHelp")}
                  checked={this.state.restApiEnabled}
                  disabled={readOnly}
                  onChange={this.valueChangeHandler}/>
        <InputField name={"mailAddress"}
                    label={t("scm-jira-plugin.form.mailAddress")}
                    helpText={t("scm-jira-plugin.form.mailAddressHelp")}
                    disabled={readOnly || !this.state.resubmission}
                    value={this.state.mailAddress}
                    onChange={this.valueChangeHandler}/>
        <InputField name={"commentWrap"}
                    label={t("scm-jira-plugin.form.commentWrap")}
                    helpText={t("scm-jira-plugin.form.commentWrapHelp")}
                    disabled={readOnly}
                    value={this.state.commentWrap}
                    onChange={this.valueChangeHandler}/>
        <Checkbox name={"commentMonospace"}
                  label={t("scm-jira-plugin.form.commentMonospace")}
                  helpText={t("scm-jira-plugin.form.commentMonospaceHelp")}
                  checked={this.state.commentMonospace}
                  disabled={readOnly}
                  onChange={this.valueChangeHandler}/>
      </>
    );
  }

  renderGlobalConfigItem(): React.ReactNode {
    const {t, includeGlobalConfigItem, readOnly} = this.props;
    if (includeGlobalConfigItem) {
     return (
       <Checkbox name={"disableRepositoryConfiguration"}
                label={t("scm-jira-plugin.form.disableRepositoryConfiguration")}
                helpText={t("scm-jira-plugin.form.disableRepositoryConfigurationHelp")}
                checked={this.state.disableRepositoryConfiguration}
                disabled={readOnly}
                onChange={this.valueChangeHandler}/>
     );
    } else {
       return null;
    }
  }

  renderConfigChangedNotification = () => {
    if (this.state.configurationChanged) {
      return (
        <div className="notification is-primary">
          <button
            className="delete"
            onClick={() =>
              this.setState({...this.state, configurationChanged: false})
            }
          />
          {this.props.t("scm-jira-plugin.configurationChangedSuccess")}
        </div>
      );
    }
    return null;
  };
}

export default translate("plugins")(JiraConfigurationItems);
