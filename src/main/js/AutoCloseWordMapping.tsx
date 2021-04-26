/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { InputField, AddButton, Icon, Help, Notification } from "@scm-manager/ui-components";
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
      <td>
        <InputField
          className="is-grouped"
          onChange={onKeywordsChange}
          value={mapping.keywords}
          placeholder={t("scm-jira-plugin.form.autoCloseMapping.keywords")}
        />
      </td>
      <td>
        <InputField
          className="is-grouped"
          onChange={onTransitionChange}
          value={mapping.transition}
          placeholder={t("scm-jira-plugin.form.autoCloseMapping.transition")}
        />
      </td>
      <VCenteredTd>
        <a onClick={remove} className={"pointer"} title={t("scm-jira-plugin.form.autoCloseMapping.remove")}>
          <span className="icon is-small">
            <Icon name="trash" color="inherit" />
          </span>
        </a>
      </VCenteredTd>
    </tr>
  );
};

const AddMappingButton = styled(AddButton)`
  float: right;
`;

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
        <Notification type="info">
          {t("scm-jira-plugin.form.autoCloseMapping.no-mapping")}
        </Notification>
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
              <th/>
            </tr>
          </thead>
          <tbody>
            {mappings.map((mapping, idx) => (
              <MappingForm key={idx} mapping={mapping} remove={removeMapping(idx)} update={updateMapping(idx)} />
            ))}
          </tbody>
        </table>
      )}
      <AddMappingButton label={t("scm-jira-plugin.form.autoCloseMapping.add")} action={addMapping} />
    </>
  );
};

export default AutoCloseWordMapping;
