package io.github.zodh.video.domain.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.zodh.video.domain.model.validation.Error;
import java.util.LinkedList;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  @DisplayName("Given Valid User "
      + "When Validate "
      + "Then Return No Errors ")
  void validateValidUser() {
    UUID userId = UUID.randomUUID();
    User user = new User("test@test.com", userId);
    LinkedList<Error> result = user.validate();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Given Invalid User "
      + "When Validate "
      + "Then Errors ")
  void validateInvalidUser() {
    User user = new User("", null);
    LinkedList<Error> result = user.validate();

    Error e1 = new Error("email", "Email not valid!");
    Error e2 = new Error("externalUserIdentifier", "Every user must have an external identifier informed!");
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrder(e1, e2);
  }

}
