// @flow
import React from "react";
import type { RedmineGlobalConfiguration } from "./types";
import { translate } from "react-i18next";
import { Checkbox } from "@scm-manager/ui-components";
import RedmineRepositoryConfigurationForm from "./RedmineRepositoryConfigurationForm";
import RedmineRepositoryConfiguration from "./RedmineRepositoryConfiguration";

type Props = {
  initialConfiguration: RedmineGlobalConfiguration,
  readOnly: boolean,
  onConfigurationChange: (RedmineGlobalConfiguration, boolean) => void,

  // context prop
  t: string => string
};

class RedmineGlobalConfigurationForm extends React.Component<
  Props,
  RedmineGlobalConfiguration
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
    this.setState({ ...config, disableRepositoryConfiguration }, () => {
      this.props.onConfigurationChange(
        { ...this.state },
        valid && this.isStateValid()
      );
    });
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

export default translate("plugins")(RedmineGlobalConfigurationForm);
