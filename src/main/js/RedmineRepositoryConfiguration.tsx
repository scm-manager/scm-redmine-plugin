import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { Subtitle, Configuration } from "@scm-manager/ui-components";
import RedmineRepositoryConfigurationForm from "./RedmineRepositoryConfigurationForm";

type Props = WithTranslation & {
  link: string;
};

class RedmineRepositoryConfiguration extends React.Component<Props> {
  render() {
    const { link, t } = this.props;

    return (
      <>
        <Subtitle subtitle={t("scm-redmine-plugin.config.title")} />
        <Configuration link={link} render={props => <RedmineRepositoryConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(RedmineRepositoryConfiguration);
