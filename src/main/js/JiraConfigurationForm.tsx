import React from "react";
import { apiClient, Configuration } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";
import JiraConfigurationItems from "./JiraConfigurationItems";

type Props = WithTranslation & {
  initialConfiguration: Configuration;
  readOnly: boolean;
  onConfigurationChange: (p1: Configuration, p2: boolean) => void;
  includeGlobalConfigItem: boolean;
};

type State = {
  resubmitSend: boolean;
  resubmitError: boolean;
};

class JiraConfigurationForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      resubmitSend: false,
      resubmitError: false
    };
  }

  resubmitHandler = () => {
    const resubmitLink = this.props.initialConfiguration._links.resubmit.href;
    apiClient
      .post(resubmitLink, {})
      .then(() => {
        this.setState({
          resubmitSend: true,
          resubmitError: false
        });
      })
      .catch(() => {
        this.setState({
          resubmitError: true,
          resubmitSend: false
        });
      });
  };

  renderResubmitMessage = () => {
    const { resubmitSend, resubmitError } = this.state;
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
            this.setState({
              resubmitError: false,
              resubmitSend: false
            })
          }
        />
        {this.props.t("scm-jira-plugin.form." + key)}
      </div>
    );
  };

  render() {
    const { readOnly, initialConfiguration, onConfigurationChange, includeGlobalConfigItem } = this.props;
    return (
      <>
        {this.renderResubmitMessage()}
        <JiraConfigurationItems
          initialConfiguration={initialConfiguration}
          readOnly={readOnly}
          includeGlobalConfigItem={includeGlobalConfigItem}
          onConfigurationChange={onConfigurationChange}
          resubmitHandler={this.resubmitHandler}
        />
      </>
    );
  }
}

export default withTranslation("plugins")(JiraConfigurationForm);
