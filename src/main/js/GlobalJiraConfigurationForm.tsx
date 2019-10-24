import React from "react";
import { Configuration } from "@scm-manager/ui-components";
import JiraConfigurationForm from "./JiraConfigurationForm";

type Props = {
  initialConfiguration: Configuration;
  readOnly: boolean;
  onConfigurationChange: (p1: Configuration, p2: boolean) => void;
};

class GlobalJiraConfigurationForm extends React.Component<Props> {
  render() {
    const { readOnly, initialConfiguration, onConfigurationChange } = this.props;
    return (
      <JiraConfigurationForm
        initialConfiguration={initialConfiguration}
        readOnly={readOnly}
        includeGlobalConfigItem={true}
        onConfigurationChange={onConfigurationChange}
      />
    );
  }
}

export default GlobalJiraConfigurationForm;
