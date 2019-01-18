package sonia.scm.redmine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedmineHandlerTest {

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private RedmineHandler handler;

  @Test
  void shouldRemoveHashPrefix() {
    assertThat(handler.parseIssueId("#123")).isEqualTo(123);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionOnNonRedmineId() {
    assertThrows(IllegalArgumentException.class, () -> handler.parseIssueId("123"));
  }

}
