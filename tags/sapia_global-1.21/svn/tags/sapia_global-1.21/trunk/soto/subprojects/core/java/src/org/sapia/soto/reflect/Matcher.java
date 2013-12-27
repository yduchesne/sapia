package org.sapia.soto.reflect;

import org.sapia.soto.util.Utils;
import org.sapia.soto.util.matcher.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that is used to perform method-matching operations.
 * 
 * <pre>
 * Matcher m = new Matcher();
 * m.setName(&quot;set*&quot;);
 * m.setSig(&quot;java.lang.String&quot;);
 * List methods = m.scan(Class.forName(&quot;org.acme.SomeClass&quot;));
 * </pre>
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
public class Matcher {
  public static final String MODIFIER_PUBLIC    = "public";
  public static final String MODIFIER_PROTECTED = "protected";
  public static final String MODIFIER_PRIVATE   = "private";
  private Pattern            _namePattern;
  private List               _includes;
  private List               _excludes;
  private List               _sigPatterns;
  private int                _modifiers;
  private boolean            _ignoreCase;
  private boolean            _declared;

  /**
   * Constructor for BaseMatcher.
   */
  public Matcher() {
    super();
    _modifiers = _modifiers | Modifier.PUBLIC;
    _modifiers = _modifiers | Modifier.PROTECTED;
    _modifiers = _modifiers | Modifier.PRIVATE;
  }

  /**
   * Specifies if this instance must perform case insensitive method name
   * matching or not.
   * 
   * @param ignoreCase
   *          if <code>true</code>, this instance will ignore method name
   *          case when matching.
   */
  public void setIgnoreCase(boolean ignoreCase) {
    _ignoreCase = ignoreCase;
  }

  /**
   * Sets the name pattern of the method(s) to match.
   * 
   * @param name
   *          a name pattern.
   */
  public void setName(String name) {
    _namePattern = PathPattern.parse(name, _ignoreCase);
  }

  /**
   * Specifies if this instance will match against the declared methods of
   * passed in classes.
   * 
   * @param declared
   *          if <code>true</code>, specifies that this instance will match
   *          against the declared methods (not methods of the superclass) of
   *          the <code>Class</code> objects that it receives.
   */
  public void setDeclaredMethods(boolean declared) {
    _declared = declared;
  }

  /**
   * Determines the visibility of the matched method (must be "public",
   * "protected" and/or "private".
   * 
   * @param modifierList
   *          a comma-delimited list of access modifiers.
   */
  public void setVisibility(String modifierList) {
    _modifiers = 0;

    String[] modifiers = Utils.split(modifierList, ',', true);

    for(int i = 0; i < modifiers.length; i++) {
      if(modifiers[i].equalsIgnoreCase(MODIFIER_PUBLIC)) {
        _modifiers = _modifiers | Modifier.PUBLIC;
      } else if(modifiers[i].equalsIgnoreCase(MODIFIER_PROTECTED)) {
        _modifiers = _modifiers | Modifier.PROTECTED;
      } else if(modifiers[i].equalsIgnoreCase(MODIFIER_PRIVATE)) {
        _modifiers = _modifiers | Modifier.PRIVATE;
      } else {
        throw new IllegalArgumentException("Unknown modifier: " + modifiers[i]);
      }
    }
  }

  /**
   * @param classNames
   *          a comma-delimited list of class names whose methods are matched by
   *          this instance should be included.
   */
  public void setIncludes(String classNames) {
    _includes = new ArrayList();

    String[] classNamePatterns = Utils.split(classNames.trim(), ',', true);

    for(int i = 0; i < classNamePatterns.length; i++) {
      _includes.add(PathPattern.parse(classNamePatterns[i], _ignoreCase));
    }
  }

  /**
   * @param classNames
   *          a comma-delimited list of class names whose methods are matched by
   *          this instance should be excluded.
   */
  public void setExcludes(String classNames) {
    _excludes = new ArrayList();

    String[] classNamePatterns = Utils.split(classNames.trim(), ',', true);

    for(int i = 0; i < classNamePatterns.length; i++) {
      _excludes.add(PathPattern.parse(classNamePatterns[i], _ignoreCase));
    }
  }

  /**
   * Sets this instance's method signature pattern. If not specified, methods
   * with any pattern will match.
   * 
   * @param sig
   *          a method signature pattern.
   */
  public void setSig(String sig) {
    if(sig.trim().length() == 0) {
      _sigPatterns = new ArrayList();
    } else {
      String[] sigStr = Utils.split(sig, ',', true);
      _sigPatterns = new ArrayList(sigStr.length);

      for(int i = 0; i < sigStr.length; i++) {
        _sigPatterns.add(PathPattern.parse(sigStr[i], _ignoreCase));
      }
    }
  }

  /**
   * Peforms matching. Returns the list of matched method objects.
   * 
   * @param clazz
   *          a <code>Class</code> instance to instrospect.
   * @return a <code>List</code> of <code>Method</code>s.
   */
  public List scanMethods(Class clazz) {
    Method[] meths;

    if(_declared) {
      meths = clazz.getDeclaredMethods();
    } else {
      meths = clazz.getMethods();
    }

    Class[] params;
    List scanned = new ArrayList();
    Pattern sigPattern;

    for(int i = 0; i < meths.length; i++) {
      if(((meths[i].getModifiers() & Modifier.PUBLIC) != 0)
          && ((_modifiers & Modifier.PUBLIC) == 0)) {
        continue;
      } else if(((meths[i].getModifiers() & Modifier.PROTECTED) != 0)
          && ((_modifiers & Modifier.PROTECTED) == 0)) {
        continue;
      } else if(((meths[i].getModifiers() & Modifier.PRIVATE) != 0)
          && ((_modifiers & Modifier.PRIVATE) == 0)) {
        continue;
      }

      if(!isIncluded(meths[i])) {
        continue;
      }

      params = meths[i].getParameterTypes();

      if(_sigPatterns == null) {
        if(_namePattern == null) {
          scanned.add(meths[i]);
        } else if(_namePattern.matches(meths[i].getName())) {
          scanned.add(meths[i]);
        }

        continue;
      }

      if(_sigPatterns.size() != params.length) {
        continue;
      }

      int matchCount = 0;

      for(int j = 0; j < params.length; j++) {
        sigPattern = (Pattern) _sigPatterns.get(j);

        if(sigPattern.matches(params[j].getName())) {
          matchCount++;
        }
      }

      if(matchCount == params.length) {
        if(_namePattern == null) {
          scanned.add(meths[i]);
        } else if(_namePattern.matches(meths[i].getName())) {
          scanned.add(meths[i]);
        }
      }
    }

    return scanned;
  }

  private boolean isIncluded(Method m) {
    boolean included = false;

    if(_includes != null) {
      for(int i = 0; i < _includes.size(); i++) {
        if(((Pattern) _includes.get(i))
            .matches(m.getDeclaringClass().getName())) {
          included = true;

          break;
        }
      }
    } else {
      included = true;
    }

    if(_excludes != null) {
      for(int i = 0; i < _excludes.size(); i++) {
        if(((Pattern) _excludes.get(i))
            .matches(m.getDeclaringClass().getName())) {
          included = false;

          break;
        }
      }
    }

    return included;
  }
}
