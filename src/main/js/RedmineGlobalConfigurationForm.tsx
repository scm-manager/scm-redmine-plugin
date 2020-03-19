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
import React from "react";
import { RedmineGlobalConfiguration } from "./types";
import { WithTranslation, withTranslation } from "react-i18next";
import { Checkbox } from "@scm-manager/ui-components";
import RedmineRepositoryConfigurationForm from "./RedmineRepositoryConfigurationForm";
import RedmineRepositoryConfiguration from "./RedmineRepositoryConfiguration";

type Props = WithTranslation & {
  initialConfiguration: RedmineGlobalConfiguration;
  readOnly: boolean;
  onConfigurationChange: (p1: RedmineGlobalConfiguration, p2: boolean) => void;
};

class RedmineGlobalConfigurationForm extends React.Component<Props, RedmineGlobalConfiguration> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.initialConfiguration
    };
  }

  configChangeHandler = (value: string, name: string) => {
    this.setState(
      {
        [name]: value
      },
      () =>
        this.props.onConfigurationChange(
          {
            ...this.state
          },
          this.isStateValid()
        )
    );
  };

  render() {
    const { readOnly } = this.props;
    return (
      <>
        <RedmineRepositoryConfigurationForm
          initialConfiguration={this.state}
          readOnly={readOnly}
          onConfigurationChange={this.configChange}
        />
        {this.renderCheckbox("disableRepositoryConfiguration")}
      </>
    );
  }

  configChange = (config: RedmineRepositoryConfiguration, valid: boolean) => {
    const { disableRepositoryConfiguration } = this.state;
    this.setState(
      {
        ...config,
        disableRepositoryConfiguration
      },
      () => {
        this.props.onConfigurationChange(
          {
            ...this.state
          },
          valid && this.isStateValid()
        );
      }
    );
  };

  renderCheckbox = (name: string) => {
    const { readOnly, t } = this.props;
    return (
      <Checkbox
        name={name}
        label={t("scm-redmine-plugin.config.form." + name)}
        helpText={t("scm-redmine-plugin.config.form." + name + "-helptext")}
        checked={this.state[name]}
        onChange={this.configChangeHandler}
        disabled={readOnly}
      />
    );
  };

  isStateValid = () => {
    return !!this.state.textFormatting;
  };
}

export default withTranslation("plugins")(RedmineGlobalConfigurationForm);
