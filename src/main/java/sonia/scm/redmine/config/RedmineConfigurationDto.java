package sonia.scm.redmine.config;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedmineConfigurationDto extends HalRepresentation {

  private String url;
  private TextFormatting textFormatting = TextFormatting.TEXTILE;
  private String usernameTransformPattern = "{0}";
  private boolean autoClose;
  private boolean updateIssues;
  private String username;
  private String password;


  @Override
  @SuppressWarnings("squid:S1185") // We want to have this method available in this package
  protected HalRepresentation add(Links links) {
    return super.add(links);
  }
}
