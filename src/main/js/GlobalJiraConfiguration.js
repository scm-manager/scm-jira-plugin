// @flow
import React from "react";
import {Title, Configuration} from "@scm-manager/ui-components"
import GlobalJiraConfigurationForm from "./GlobalJiraConfigurationForm";
import {translate} from "react-i18next";

type Props = {
  link: string,
  t: (string) => string
}

class GlobalJiraConfiguration extends React.Component<Props> {

  render(): React.ReactNode {
    const {t, link} = this.props;
    return (
      <>
        <Title title={t("scm-jira-plugin.global.header")}></Title>
        <Configuration link={link} t={t} render={props => <GlobalJiraConfigurationForm {...props}/>}/>
      </>
    );
  }
}

export default translate("plugins")(GlobalJiraConfiguration);
