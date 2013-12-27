package org.sapia.regis.codegen.output;

class CodeGenUtils {

  static String toCamelCase(String s) {
    StringBuilder sb = new StringBuilder();
    boolean setToCapital = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (i == 0) {
        c = Character.toUpperCase(c);
      } else if (!Character.isLetterOrDigit(c)) {
        setToCapital = true;
        continue;
      }
      if (setToCapital) {
        sb.append(Character.toUpperCase(c));
        setToCapital = false;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  static String removeInvalidChars(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!Character.isLetterOrDigit(c)) {
        continue;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
