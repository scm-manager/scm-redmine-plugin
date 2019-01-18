package sonia.scm.redmine;

import com.google.common.base.Preconditions;

final class Ids {

  private Ids() {
  }

  static String parse(String key) {
    Preconditions.checkArgument(key.startsWith("#"), "id does not look like a redmine issue id");
    return key.substring(1);
  }

  static int parseAsInt(String key) {
    return Integer.parseInt(parse(key));
  }
}
