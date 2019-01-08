// @flow

import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import RedmineGlobalConfiguration from "./RedmineGlobalConfiguration";


cfgBinder.bindGlobal(
  "/redmine",
  "scm-redmine-plugin.config.link",
  "redmineConfig",
  RedmineGlobalConfiguration
);

