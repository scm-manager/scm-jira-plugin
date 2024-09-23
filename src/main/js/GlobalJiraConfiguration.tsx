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
        <Configuration link={link} render={(props: any) => <GlobalJiraConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(GlobalJiraConfiguration);
