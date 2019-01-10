//@flow

import React from "react";
import {Configuration} from "@scm-manager/ui-components";
import JiraConfigurationItems from "./JiraConfigurationItems";

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void
}

class LocalJiraConfigurationForm extends React.Component<Props> {

  render(): React.ReactNode {
    const {readOnly, initialConfiguration, onConfigurationChange} = this.props;
    return (
      <JiraConfigurationItems initialConfiguration={initialConfiguration}
                              readOnly={readOnly}
                              includeGlobalConfigItem={false}
                              onConfigurationChange={onConfigurationChange}/>
    );
  }
}

export default LocalJiraConfigurationForm;
