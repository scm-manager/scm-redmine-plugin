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
import { RedmineConfiguration } from "./types";
import { WithTranslation, withTranslation } from "react-i18next";
import { InputField, DropDown, Checkbox } from "@scm-manager/ui-components";
import KeyWordMapping from "./KeyWordMapping";

type Props = WithTranslation & {
  initialConfiguration: RedmineConfiguration;
  readOnly: boolean;
  onConfigurationChange: (p1: RedmineConfiguration, p2: boolean) => void;
};


class RedmineRepositoryConfigurationForm extends React.Component<Props, RedmineConfiguration> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.initialConfiguration
    };
  }

  configChangeHandler = (value: any, name: string) => {
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

  keywordMappingChanged = (keywordMapping: Record<string, string>) => {
    this.configChangeHandler(keywordMapping, "keywordMapping");
  };

  render() {
    return (
      <div className="columns is-multiline">
        <div className="column is-full">{this.renderInputField("url")}</div>
        <div className="column is-full">{this.renderCheckbox("updateIssues")}</div>
        {this.state.updateIssues ? (
          <>
            <div className="column is-half">{this.renderInputField("username")}</div>
            <div className="column is-half">{this.renderInputField("password", "password")}</div>
            <div className="column is-full">{this.renderTextFormattingDropDown()}</div>
            <div className="column is-full">{this.renderCheckbox("autoClose")}</div>
            <div className="column is-full">{this.renderCheckbox("disableStateChangeByCommit")}</div>
            {this.state.autoClose ? (
              <div className="column is-full">
                <KeyWordMapping mappings={this.state.keywordMapping} onChange={this.keywordMappingChanged} />
              </div>
            ) : null}
          </>
        ) : null}
      </div>
    );
  }

  /**

   <div className="column is-half">{this.renderTextFormattingDropDown()}</div>
   */

  renderInputField = (name: string, type?: string) => {
    const { readOnly, t } = this.props;
    return (
      <InputField
        type={type}
        label={t("scm-redmine-plugin.config.form." + name)}
        helpText={t("scm-redmine-plugin.config.form." + name + "-helptext")}
        onChange={this.configChangeHandler}
        value={this.state[name]}
        name={name}
        disabled={readOnly}
      />
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

  renderTextFormattingDropDown = () => {
    const { t } = this.props;
    return (
      <div className="field">
        <label className="label">{t("scm-redmine-plugin.config.form.textFormatting")}</label>
        <div className="control">
          <DropDown
            options={["TEXTILE", "MARKDOWN"]}
            optionSelected={this.handleDropDownChange}
            preselectedOption={this.state.textFormatting}
          />
        </div>
      </div>
    );
  };

  handleDropDownChange = (selection: string) => {
    this.configChangeHandler(selection, "textFormatting");
  };

  isStateValid = () => {
    return !!this.state.textFormatting;
  };
}

export default withTranslation("plugins")(RedmineRepositoryConfigurationForm);
