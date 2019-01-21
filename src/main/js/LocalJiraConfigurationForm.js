//@flow

import React from "react";
import { Configuration } from "@scm-manager/ui-components";
import JiraConfigurationForm from "./JiraConfigurationForm";

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void
};

class LocalJiraConfigurationForm extends React.Component<Props> {
  render() {
    const {
      readOnly,
      initialConfiguration,
      onConfigurationChange
    } = this.props;
    return (
      <JiraConfigurationForm
        initialConfiguration={initialConfiguration}
        readOnly={readOnly}
        includeGlobalConfigItem={false}
        onConfigurationChange={onConfigurationChange}
      />
    );
  }
}

export default LocalJiraConfigurationForm;
