package io.github.zodh.video.domain.model.validation;

import java.util.LinkedList;

public interface Validable {

  LinkedList<Error> validate();

}
