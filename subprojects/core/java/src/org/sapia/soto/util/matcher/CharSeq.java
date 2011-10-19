package org.sapia.soto.util.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class CharSeq implements PatternElement {
  private static final char ASTERISK = '*';
  private String[]          _tokens;
  private boolean           _exactStart;
  private boolean           _exactEnd;
  private PatternElement    _next;

  /**
   * Constructor for CharSeq.
   */
  public CharSeq(String chars, boolean ignoreCase) {
    _tokens = split(chars);

    if(ignoreCase) {
      for(int i = 0; i < _tokens.length; i++) {
        _tokens[i] = _tokens[i].toLowerCase();
      }
    }
  }

  /**
   * @see org.sapia.soto.util.matcher.PatternElement#setNext(PatternElement)
   */
  public void setNext(PatternElement elem) {
    _next = elem;
  }

  /**
   * @see org.sapia.soto.util.matcher.PatternElement#getNext()
   */
  public PatternElement getNext() {
    return _next;
  }

  public boolean matches(Stack tokens) {
    if(tokens.size() == 0) {
      if(_tokens.length == 0) {
        return true;
      }

      return false;
    }

    String token = (String) tokens.pop();
    int idx = 0;

    for(int i = 0; i < _tokens.length; i++) {
      idx = token.indexOf(_tokens[i], idx);

      if(idx < 0) {
        return false;
      }

      if((i == 0) && _exactStart && (idx != 0)) {
        return false;
      }

      if((i == (_tokens.length - 1)) && _exactEnd
          && ((idx + _tokens[i].length()) != token.length())) {
        return false;
      }

      idx = idx + _tokens[i].length();

      if((idx >= token.length()) && (i < (_tokens.length - 1))) {
        return false;
      }
    }

    if(_next != null) {
      return _next.matches(tokens);
    }

    return true;
  }

  private String[] split(String chars) {
    _exactStart = ((chars.length() > 0) && (chars.charAt(0) != ASTERISK));
    _exactEnd = ((chars.length() > 0) && (chars.charAt(chars.length() - 1) != ASTERISK));

    List tokens = new ArrayList();

    StringBuffer token = new StringBuffer();

    for(int i = 0; i < chars.length(); i++) {
      if(chars.charAt(i) == ASTERISK) {
        tokens.add(token.toString());
        token.delete(0, token.length());
      } else {
        token.append(chars.charAt(i));
      }
    }

    if(token.length() > 0) {
      tokens.add(token.toString());
    }

    return (String[]) tokens.toArray(new String[tokens.size()]);
  }

  //  public static void main(String[] args) {
  //    CharSeq pattern = new CharSeq("*Str*Buf*er*");
  //// System.out.println(pattern.matches("SomeStingBufffferSome"));
  //  }
}
