package sonia.scm.redmine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IdsTest {

  @Test
  void shouldRemoveHashPrefix() {
    assertThat(Ids.parse("#123")).isEqualTo("123");
  }

  @Test
  void shouldParseAsInt() {
    assertThat(Ids.parseAsInt("#123")).isEqualTo(123);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionOnNonRedmineId() {
    assertThrows(IllegalArgumentException.class, () -> Ids.parse("123"));
  }

}
