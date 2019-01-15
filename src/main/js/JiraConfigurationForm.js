//@flow

import React from "react";
import {apiClient, Configuration} from "@scm-manager/ui-components";
import {translate} from "react-i18next";
import JiraConfigurationItems from "./JiraConfigurationItems";

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void,
  includeGlobalConfigItem: boolean
}

type State = {
  resubmitSend: boolean,
  resubmitError: boolean
}

class JiraConfigurationForm extends React.Component<Props, State> {

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

  render(): React.ReactNode {
    const {readOnly, initialConfiguration, onConfigurationChange, includeGlobalConfigItem} = this.props;
    return (
      <>
        {this.renderResubmitMessage()}
        <JiraConfigurationItems initialConfiguration={initialConfiguration}
                                readOnly={readOnly}
                                includeGlobalConfigItem={includeGlobalConfigItem}
                                onConfigurationChange={onConfigurationChange}
                                resubmitHandler={this.resubmitHandler}/>
      </>
    );
  }
}

export default translate("plugins")(JiraConfigurationForm);
