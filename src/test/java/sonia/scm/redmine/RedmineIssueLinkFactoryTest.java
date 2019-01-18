package sonia.scm.redmine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedmineIssueLinkFactoryTest {

  @Test
  void shouldCreateRedmineLink() {
    RedmineIssueLinkFactory issueLinkFactory = new RedmineIssueLinkFactory("http://h2g2/");
    String link = issueLinkFactory.createLink("#42");
    assertThat(link).isEqualTo("http://h2g2/issues/42");
  }

  @Test
  void shouldCreateRedmineLinkEventWithoutMissingSlash() {
    RedmineIssueLinkFactory issueLinkFactory = new RedmineIssueLinkFactory("http://h2g2");
    String link = issueLinkFactory.createLink("#42");
    assertThat(link).isEqualTo("http://h2g2/issues/42");
  }

}
