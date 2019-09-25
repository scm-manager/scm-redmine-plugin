// @flow
import React from "react";
import type { RedmineConfiguration } from "./types";
import { translate } from "react-i18next";
import { InputField, DropDown, Checkbox } from "@scm-manager/ui-components";

type Props = {
  initialConfiguration: RedmineConfiguration,
  readOnly: boolean,
  onConfigurationChange: (RedmineConfiguration, boolean) => void,

  // context prop
  t: string => string
};

class RedmineRepositoryConfigurationForm extends React.Component<
  Props,
  RedmineConfiguration
> {
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
        this.props.onConfigurationChange({ ...this.state }, this.isStateValid())
    );
  };

  render() {
    return (
      <>
        <div className="columns is-multiline">
          <div className="column is-full">{this.renderInputField("url")}</div>
          <div className="column is-half">
            {this.renderInputField("username")}
          </div>
          <div className="column is-half">
            {this.renderInputField("password", "password")}
          </div>
          <div className="column is-half">
            {this.renderTextFormattingDropDown()}
          </div>
        </div>
        {this.renderCheckbox("autoClose")}
        {this.renderCheckbox("updateIssues")}
      </>
    );
  }

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
        <label className="label">
          {t("scm-redmine-plugin.config.form.textFormatting")}
        </label>
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
    // this.setState({ ...this.state, textFormatting: selection });

    this.configChangeHandler(selection, "textFormatting");
  };

  isStateValid = () => {
    return !!this.state.textFormatting;
  };
}

export default translate("plugins")(RedmineRepositoryConfigurationForm);
