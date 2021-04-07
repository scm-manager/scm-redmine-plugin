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

  const onStatusChange = (value: string) => {
    update({
      status: value,
      keywords: mapping.keywords
    });
  };

  const onKeywordsChange = (value: string) => {
    update({
      status: mapping.status,
      keywords: value
    });
  };

  return (
    <tr>
      <td>
        <InputField
          className="is-grouped"
          onChange={onStatusChange}
          value={mapping.status}
          placeholder={t("scm-redmine-plugin.config.mapping.status")}
        />
      </td>
      <td>
        <InputField
          className="is-grouped"
          onChange={onKeywordsChange}
          value={mapping.keywords}
          placeholder={t("scm-redmine-plugin.config.mapping.keywords")}
        />
      </td>
      <VCenteredTd>
        <a onClick={remove} className={"pointer"} title={t("scm-redmine-plugin.config.mapping.remove")}>
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
  status: string;
  keywords: string;
};

const convert = (mappings: Record<string, string>): Mapping[] => {
  return Object.keys(mappings).map(name => ({
    status: name,
    keywords: mappings[name]
  }));
};

const KeyWordMapping: FC<Props> = props => {
  const [t] = useTranslation("plugins");
  const [mappings, setMappings] = useState<Mapping[]>(convert(props.mappings));

  const onChange = (newMappings: Mapping[]) => {
    setMappings(newMappings);
    const record: Record<string, string> = {};
    newMappings.forEach(mapping => {
      record[mapping.status] = mapping.keywords;
    });
    props.onChange(record);
  };

  const addMapping = () => {
    onChange([...mappings, { status: "", keywords: "" }]);
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
        {t("scm-redmine-plugin.config.mapping.title")}
        <Help message={t("scm-redmine-plugin.config.mapping.help")} />
      </h3>
      {!mappings || mappings.length === 0 ? (
        <Notification type="info">
          {t("scm-redmine-plugin.config.mapping.no-mapping")}
        </Notification>
        ) : (
        <table className="card-table table is-hoverable is-fullwidth">
          <thead>
            <tr>
              <th>
                {t("scm-redmine-plugin.config.mapping.status")}
                <Help message={t("scm-redmine-plugin.config.mapping.statusHelp")} />
              </th>
              <th>
                {t("scm-redmine-plugin.config.mapping.keywords")}
                <Help message={t("scm-redmine-plugin.config.mapping.keywordsHelp")} />
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
      <AddMappingButton label={t("scm-redmine-plugin.config.mapping.add")} action={addMapping} />
    </>
  );
};

export default KeyWordMapping;
