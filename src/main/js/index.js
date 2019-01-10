// @flow

import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import RedmineGlobalConfiguration from "./RedmineGlobalConfiguration";
import RedmineRepositoryConfiguration from "./RedmineRepositoryConfiguration";


cfgBinder.bindGlobal(
  "/redmine",
  "scm-redmine-plugin.config.link",
  "redmineConfig",
  RedmineGlobalConfiguration
);

cfgBinder.bindRepository("/redmine", "scm-redmine-plugin.config.link", "redmineConfig", RedmineRepositoryConfiguration);
