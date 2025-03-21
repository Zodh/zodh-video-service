package io.github.zodh.video.domain.model.user;

import io.github.zodh.video.domain.model.validation.Validable;
import io.github.zodh.video.domain.model.validation.Error;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class User implements Validable {

  private Long identifier;
  private final String email;
  private final UUID externalUserIdentifier;

  public User(String email, UUID externalUserIdentifier) {
    this.email = email;
    this.externalUserIdentifier = externalUserIdentifier;
  }

  public void setIdentifier(Long identifier) {
    this.identifier = identifier;
  }

  public Long getIdentifier() {
    return identifier;
  }

  @Override
  public LinkedList<Error> validate() {
    LinkedList<Error> errors = new LinkedList<>();
    if (StringUtils.isEmpty(email)) {
      errors.add(new Error("email", "Email not valid!"));
    }
    if (Objects.isNull(externalUserIdentifier)) {
      errors.add(new Error("externalUserIdentifier", "Every user must have an external identifier informed!"));
    }
    return errors;
  }


}
