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
import { Configuration } from "@scm-manager/ui-components";
import { Subtitle, useDocumentTitle, useDocumentTitleForRepository } from "@scm-manager/ui-core";
import LocalJiraConfigurationForm from "./LocalJiraConfigurationForm";
import { useTranslation } from "react-i18next";
import { useRepositoryContext } from "@scm-manager/ui-api";

type Props = {
  link: string;
};

type WithRepositoryProps = Props & {
  repository: NonNullable<ReturnType<typeof useRepositoryContext>>;
};

export default function LocalJiraConfiguration({ link }: Props) {
  const repository = useRepositoryContext();

  return repository ? (
    <LocalJiraConfigurationWithRepository link={link} repository={repository} />
  ) : (
    <LocalJiraConfigurationWithoutRepository link={link} />
  );
}

function LocalJiraConfigurationWithRepository({ link, repository }: WithRepositoryProps) {
  const [t] = useTranslation("plugins");

  useDocumentTitleForRepository(repository, t("scm-jira-plugin.local.title"));

  return (
    <>
      <Subtitle subtitle={t("scm-jira-plugin.local.title")} />
      <Configuration
        link={link}
        render={(props: any) => <LocalJiraConfigurationForm {...props} />}
      />
    </>
  );
}

function LocalJiraConfigurationWithoutRepository({ link }: Props) {
  const [t] = useTranslation("plugins");

  useDocumentTitle(t("scm-jira-plugin.local.title"));

  return (
    <>
      <Subtitle subtitle={t("scm-jira-plugin.local.title")} />
      <Configuration
        link={link}
        render={(props: any) => <LocalJiraConfigurationForm {...props} />}
      />
    </>
  );
}
