/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React from "react";
import { Checkbox, InputField, validation } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";
import { JiraConfiguration } from "./types";
import AutoCloseWordMapping from "./AutoCloseWordMapping";

type Props = WithTranslation & {
  initialConfiguration: JiraConfiguration;
  readOnly: boolean;
  onConfigurationChange: (configuration: JiraConfiguration, valid: boolean) => void;
  includeGlobalConfigItem: boolean;
};

type State = JiraConfiguration & {
  urlValid: boolean;
};

class JiraConfigurationItems extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.initialConfiguration,
      urlValid: true
    };
  }

  urlChangeHandler = (value: string) => {
    const urlValid = value === "" || validation.isUrlValid(value);
    this.setState(
      {
        url: value,
        urlValid
      },
      this.configurationChangedCallback
    );
  };

  valueChangeHandler = (value: string | boolean | Record<string, string>, name?: string) => {
    if (!name) {
      return;
    }
    this.setState(
      // @ts-ignore hard to type
      {
        [name]: value
      },
      this.configurationChangedCallback
    );
  };

  autoCloseWordChanged = (mapping: Record<string, string>) => {
    this.valueChangeHandler(mapping, "autoCloseWords");
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
    return this.state.urlValid;
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
            errorMessage={t("scm-jira-plugin.form.urlValidationError")}
            validationError={!this.state.urlValid}
            type="url"
            onChange={this.urlChangeHandler}
          />
        </div>
        <div className="column is-full">
          <InputField
            name="filter"
            label={t("scm-jira-plugin.form.filter")}
            helpText={t("scm-jira-plugin.form.filterHelp")}
            disabled={readOnly}
            value={this.state.filter}
            onChange={this.valueChangeHandler}
          />
        </div>
        <div className="column is-full">
          <Checkbox
            name="updateIssues"
            label={t("scm-jira-plugin.form.updateJiraIssues")}
            helpText={t("scm-jira-plugin.form.updateJiraIssuesHelp")}
            checked={this.state.updateIssues}
            disabled={readOnly}
            onChange={this.valueChangeHandler}
          />
        </div>
        {this.state.updateIssues ? (
          <>
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
              <InputField
                name="roleLevel"
                label={t("scm-jira-plugin.form.roleLevel")}
                helpText={t("scm-jira-plugin.form.roleLevelHelp")}
                disabled={readOnly || !this.state.updateIssues}
                value={this.state.roleLevel}
                onChange={this.valueChangeHandler}
              />
            </div>
            <div className="column is-full">
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
              <Checkbox
                name="disableStateChangeByCommit"
                label={t("scm-jira-plugin.form.disableStateChangeByCommit")}
                helpText={t("scm-jira-plugin.form.disableStateChangeByCommitHelp")}
                checked={this.state.disableStateChangeByCommit}
                disabled={readOnly || !this.state.updateIssues}
                onChange={this.valueChangeHandler}
              />
            </div>
            {this.state.autoClose ? (
              <div className="column is-full is-flex is-flex-direction-column">
                <AutoCloseWordMapping mappings={this.state.autoCloseWords} onChange={this.autoCloseWordChanged} />
              </div>
            ) : null}
          </>
        ) : null}

        {this.renderGlobalConfigItem()}
      </div>
    );
  }

  renderGlobalConfigItem() {
    const { t, includeGlobalConfigItem, readOnly } = this.props;
    if (includeGlobalConfigItem) {
      return (
        <div className="column is-full">
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
