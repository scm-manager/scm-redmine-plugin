// @flow
import React from "react";
import { translate }from "react-i18next";
import { Title, Configuration } from "@scm-manager/ui-components";
import RedmineGlobalConfigurationForm from "./RedmineGlobalConfigurationForm";

type Props = {
  link: string,

  t: string => string
};

class RedmineGlobalConfiguration extends React.Component<Props> {
  constructor(props: Props) {
    super(props);
  }

  render() {
    const { link, t } = this.props;

    return (
      <div>
        <Title title={t("scm-redmine-plugin.config.title")} />
        <Configuration
          link={link}
          render={props => <RedmineGlobalConfigurationForm {...props} />}
        />
      </div>
    );
  }
}

export default translate("plugins")(RedmineGlobalConfiguration);
