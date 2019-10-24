import React from "react";
import { Title, Configuration } from "@scm-manager/ui-components";
import GlobalJiraConfigurationForm from "./GlobalJiraConfigurationForm";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  link: string;
};

class GlobalJiraConfiguration extends React.Component<Props> {
  render() {
    const { t, link } = this.props;
    return (
      <>
        <Title title={t("scm-jira-plugin.global.title")} />
        <Configuration link={link} t={t} render={props => <GlobalJiraConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(GlobalJiraConfiguration);
