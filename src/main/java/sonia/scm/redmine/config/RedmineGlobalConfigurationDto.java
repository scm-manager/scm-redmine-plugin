package sonia.scm.redmine.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RedmineGlobalConfigurationDto extends RedmineConfigurationDto {
  private boolean disableRepositoryConfiguration;
}
