// @flow

import type { Links } from "@scm-manager/ui-types";

export type RedmineConfiguration = {
  url: string,
  autoClose: boolean,
  updateIssues: boolean,
  usernameTransformPattern: string,
  textFormatting: string,
  _links: Links
};


export type RedmineGlobalConfiguration = RedmineConfiguration & {
  disableRepositoryConfiguration: boolean
}
