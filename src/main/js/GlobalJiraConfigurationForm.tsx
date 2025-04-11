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
import JiraConfigurationForm from "./JiraConfigurationForm";
import { JiraConfiguration } from "./types";
import { useDocumentTitle } from "@scm-manager/ui-core"
import {useTranslation} from "react-i18next";

type Props = {
  initialConfiguration: JiraConfiguration;
  readOnly: boolean;
  onConfigurationChange: (configuration: JiraConfiguration, valid: boolean) => void;
};

export default function GlobalJiraConfigurationForm({initialConfiguration, readOnly, onConfigurationChange}: Props) {
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-jira-plugin.global.title"));
  return (
      <JiraConfigurationForm
        initialConfiguration={initialConfiguration}
        readOnly={readOnly}
        includeGlobalConfigItem={true}
        onConfigurationChange={onConfigurationChange}
      />
    );
}
