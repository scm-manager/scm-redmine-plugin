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
import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { InputField } from "@scm-manager/ui-components";

type Configuration = {
  branches?: string;
};

type ConfigProps = {
  configurationChanged: (newRuleConfiguration: Configuration, valid: boolean) => void;
};

const RedmineCommitMessageIssueKeyValidatorConfig: FC<ConfigProps> = ({ configurationChanged }) => {
  const [t] = useTranslation("plugins");
  const [branches, setBranches] = useState<string | undefined>();

  useEffect(() => configurationChanged({ branches }, true), []);

  const onBranchesChange = (value: string) => {
    setBranches(value);
    configurationChanged({ branches: value }, true);
  };

  return (
    <InputField
      value={branches}
      label={t("validator.RedmineCommitMessageIssueKeyValidator.branches.label")}
      helpText={t("validator.RedmineCommitMessageIssueKeyValidator.branches.helpText")}
      onChange={onBranchesChange}
    />
  );
};

export default RedmineCommitMessageIssueKeyValidatorConfig;
