// @flow
import React from "react";
import { translate }from "react-i18next";
import { Subtitle, Configuration } from "@scm-manager/ui-components";
import RedmineRepositoryConfigurationForm from "./RedmineRepositoryConfigurationForm";

type Props = {
  link: string,

  t: string => string
};

class RedmineRepositoryConfiguration extends React.Component<Props> {
  constructor(props: Props) {
    super(props);
  }

  render() {
    const { link, t } = this.props;

    return (
      <div>
        <Subtitle subtitle={t("scm-redmine-plugin.config.title")} />
        <Configuration
          link={link}
          render={props => <RedmineRepositoryConfigurationForm {...props} />}
        />
      </div>
    );
  }
}

export default translate("plugins")(RedmineRepositoryConfiguration);
