package org.sapia.soto.util.matcher;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Matches path patterns against strings. The path delimiter can be set; it
 * defaults to a dot (.). An instance of this class can also be set as
 * case-sensitive/insensitive.
 * 
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
public class PathPattern implements Pattern {
  private static final String ASTERISK = "**";
  private static final String DOT      = ".";
  private PatternElement      _first;
  private boolean             _ignoreCase;

  private PathPattern(PatternElement first, boolean ignoreCase) {
    _first = first;
    _ignoreCase = ignoreCase;
  }

  /**
   * @see org.sapia.soto.util.matcher.Pattern#matches(String)
   */
  public boolean matches(String str) {
    StringTokenizer st = new StringTokenizer(str, DOT);
    Stack tokens = new Stack();

    while(st.hasMoreTokens()) {
      if(_ignoreCase) {
        tokens.add(0, st.nextToken().toLowerCase());
      } else {
        tokens.add(0, st.nextToken());
      }
    }

    if(_first == null) {
      return false;
    }

    return _first.matches(tokens);
  }

  public static PathPattern parse(String toParse, boolean ignoreCase) {
    return parse(toParse, '.', ignoreCase);
  }

  public static PathPattern parse(String toParse, char pathDelim,
      boolean ignoreCase) {
    StringTokenizer st = new StringTokenizer(toParse, "" + pathDelim);
    String token;

    PatternElement first = null;
    PatternElement current = null;
    PatternElement previous = null;

    while(st.hasMoreTokens()) {
      token = st.nextToken();

      if(ignoreCase) {
        token.toLowerCase();
      }

      if(token.equals(ASTERISK)) {
        current = new PathToken();
      } else {
        current = new CharSeq(token, ignoreCase);
      }

      if((first == null) && (current != null)) {
        first = current;
      } else if(previous != null) {
        previous.setNext(current);
      }

      previous = current;
    }

    return new PathPattern(first, ignoreCase);
  }
}
