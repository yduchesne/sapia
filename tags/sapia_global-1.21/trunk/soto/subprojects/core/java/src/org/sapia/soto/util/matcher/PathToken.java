package org.sapia.soto.util.matcher;

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
public class PathToken extends AbstractPatternElement {
  /**
   * Constructor for PathPattern.
   */
  public PathToken() {
    super();
  }

  /**
   * @see org.sapia.soto.util.matcher.PatternElement#matches(Stack)
   */
  public boolean matches(Stack tokens) {
    while(tokens.size() > 0) {
      if(_next == null) {
        return true;
      }

      if(tokens.size() <= 1) {
        return _next.matches(tokens);
      }

      Stack newStack = new Stack();
      newStack.addAll(tokens);

      if(_next.matches(newStack)) {
        return true;
      } else {
        tokens.pop();
      }
    }

    return true;
  }
}
