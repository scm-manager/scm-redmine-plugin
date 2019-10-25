import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { Title, Configuration } from "@scm-manager/ui-components";
import RedmineGlobalConfigurationForm from "./RedmineGlobalConfigurationForm";

type Props = WithTranslation & {
  link: string;
};

class RedmineGlobalConfiguration extends React.Component<Props> {
  render() {
    const { link, t } = this.props;

    return (
      <>
        <Title title={t("scm-redmine-plugin.config.title")} />
        <Configuration link={link} render={props => <RedmineGlobalConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(RedmineGlobalConfiguration);
