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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { InputField, AddButton, Icon, Help, Notification, Button } from "@scm-manager/ui-components";
import styled from "styled-components";

type Props = {
  mappings: Record<string, string>;
  onChange: (mappings: Record<string, string>) => void;
};

type MappingProps = {
  mapping: Mapping;
  remove: () => void;
  update: (mapping: Mapping) => void;
};

const VCenteredTd = styled.td`
  display: table-cell;
  vertical-align: middle !important;
`;

const MappingForm: FC<MappingProps> = ({ mapping, remove, update }) => {
  const [t] = useTranslation("plugins");

  const onTransitionChange = (value: string) => {
    update({
      transition: value,
      keywords: mapping.keywords
    });
  };

  const onKeywordsChange = (value: string) => {
    update({
      transition: mapping.transition,
      keywords: value
    });
  };

  return (
    <tr>
      <VCenteredTd>
        <InputField
          className="m-0"
          onChange={onKeywordsChange}
          value={mapping.keywords}
          placeholder={t("scm-jira-plugin.form.autoCloseMapping.keywords")}
        />
      </VCenteredTd>
      <VCenteredTd>
        <InputField
          className="m-0"
          onChange={onTransitionChange}
          value={mapping.transition}
          placeholder={t("scm-jira-plugin.form.autoCloseMapping.transition")}
        />
      </VCenteredTd>
      <VCenteredTd>
        <Button
          color="text"
          icon="trash"
          action={remove}
          title={t("scm-jira-plugin.form.autoCloseMapping.remove")}
          className="px-2"
        />
      </VCenteredTd>
    </tr>
  );
};

type Mapping = {
  transition: string;
  keywords: string;
};

const convert = (mappings: Record<string, string>): Mapping[] => {
  return Object.keys(mappings).map(name => ({
    transition: mappings[name],
    keywords: name
  }));
};

const AutoCloseWordMapping: FC<Props> = props => {
  const [t] = useTranslation("plugins");
  const [mappings, setMappings] = useState<Mapping[]>(convert(props.mappings));

  const onChange = (newMappings: Mapping[]) => {
    setMappings(newMappings);
    const record: Record<string, string> = {};
    newMappings.forEach(mapping => {
      record[mapping.keywords] = mapping.transition;
    });
    props.onChange(record);
  };

  const addMapping = () => {
    onChange([...mappings, { keywords: "", transition: "" }]);
  };

  const updateMapping = (index: number) => {
    return (mapping: Mapping) => {
      mappings[index] = mapping;
      onChange([...mappings]);
    };
  };

  const removeMapping = (index: number) => {
    return () => {
      mappings.splice(index, 1);
      onChange([...mappings]);
    };
  };

  return (
    <>
      <h3>
        {t("scm-jira-plugin.form.autoCloseMapping.title")}
        <Help message={t("scm-jira-plugin.form.autoCloseMapping.help")} />
      </h3>
      {!mappings || mappings.length === 0 ? (
        <Notification type="info">{t("scm-jira-plugin.form.autoCloseMapping.no-mapping")}</Notification>
      ) : (
        <table className="card-table table is-hoverable is-fullwidth">
          <thead>
            <tr>
              <th>
                {t("scm-jira-plugin.form.autoCloseMapping.keywords")}
                <Help message={t("scm-jira-plugin.form.autoCloseMapping.keywordsHelp")} />
              </th>
              <th>
                {t("scm-jira-plugin.form.autoCloseMapping.transition")}
                <Help message={t("scm-jira-plugin.form.autoCloseMapping.transitionHelp")} />
              </th>
              <th />
            </tr>
          </thead>
          <tbody>
            {mappings.map((mapping, idx) => (
              <MappingForm key={idx} mapping={mapping} remove={removeMapping(idx)} update={updateMapping(idx)} />
            ))}
          </tbody>
        </table>
      )}
      <AddButton className="is-align-self-flex-end" label={t("scm-jira-plugin.form.autoCloseMapping.add")} action={addMapping} />
    </>
  );
};

export default AutoCloseWordMapping;
