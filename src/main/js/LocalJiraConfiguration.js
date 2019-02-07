// @flow
import React from "react";
import { Subtitle, Configuration } from "@scm-manager/ui-components";
import LocalJiraConfigurationForm from "./LocalJiraConfigurationForm";
import { translate } from "react-i18next";

type Props = {
  link: string,
  t: string => string
};

class LocalJiraConfiguration extends React.Component<Props> {
  render() {
    const { t, link } = this.props;
    return (
      <>
        <Subtitle subtitle={t("scm-jira-plugin.local.title")} />
        <Configuration
          link={link}
          t={t}
          render={props => <LocalJiraConfigurationForm {...props} />}
        />
      </>
    );
  }
}

export default translate("plugins")(LocalJiraConfiguration);
