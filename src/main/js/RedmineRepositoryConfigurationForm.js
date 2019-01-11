// @flow
import React from "react";
import type { RedmineConfiguration } from "./types";
import DropDown from "@scm-manager/ui-components/src/forms/DropDown";
import { translate } from "react-i18next";
import { InputField } from "@scm-manager/ui-components";
import Checkbox from "@scm-manager/ui-components/src/forms/Checkbox";

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
        {this.renderInputField("url")}
        {this.renderInputField("usernameTransformPattern")}
        {this.renderTextFormattingDropDown()}
        {this.renderCheckbox("autoClose")}
        {this.renderCheckbox("updateIssues")}
      </>
    );
  }

  renderInputField = (name: string) => {
    const { readOnly, t } = this.props;
    return (
      <InputField
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
    this.setState({ ...this.state, textFormatting: selection });

    this.configChangeHandler(selection, "textFormatting");
  };

  isStateValid = () => {
    return !!this.state.textFormatting;
  };
}

export default translate("plugins")(RedmineRepositoryConfigurationForm);
