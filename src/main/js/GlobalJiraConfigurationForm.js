//@flow

import React from "react";
import {apiClient, Configuration} from "@scm-manager/ui-components";
import JiraConfigurationItems from "./JiraConfigurationItems";
import {translate} from "react-i18next";

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void
}

type State = {
  resubmitSend: boolean,
  resubmitError: boolean
}

class GlobalJiraConfigurationForm extends React.Component<Props, State> {

  constructor(props: Props) {
    super(props);
    this.state = {
      resubmitSend: false,
      resubmitError: false
    }
  }

  resubmitHandler = () => {
    const resubmitLink = this.props.initialConfiguration._links.resubmit.href;
    console.log(resubmitLink);
    apiClient.post(resubmitLink, {})
      .then(response => {
        console.log(response);
        this.setState({resubmitSend: true, resubmitError: false});
      }).catch(err => {
        console.log(err);
        this.setState({resubmitError: true, resubmitSend: false});
    });
  };

  render(): React.ReactNode {
    const {readOnly, initialConfiguration, onConfigurationChange} = this.props;
    return (
      <>
        {this.renderResubmitMessage()}
        <JiraConfigurationItems initialConfiguration={initialConfiguration}
                                readOnly={readOnly}
                                includeGlobalConfigItem={true}
                                onConfigurationChange={onConfigurationChange}
                                resubmitHandler={this.resubmitHandler}/>
      </>
    );
  }

  renderResubmitMessage = () => {
    const {resubmitSend, resubmitError} = this.state;
    if (resubmitSend) {
      return this.renderResubmitDiv("resubmitSuccess");
    } else if (resubmitError) {
      return this.renderResubmitDiv("resubmitFailed");
    } else {
      return null;
    }
  };

  renderResubmitDiv = (key: string) => {
    return (
      <div className="notification is-info">
        <button
          className="delete"
          onClick={() =>
            this.setState({resubmitError: false, resubmitSend: false})
          }
        />
        {this.props.t("scm-jira-plugin.form." + key)}
      </div>
    )
  };
}

export default translate("plugins")(GlobalJiraConfigurationForm);
