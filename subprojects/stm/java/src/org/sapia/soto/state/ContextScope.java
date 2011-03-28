package org.sapia.soto.state;

/**
 * Implements a <code>Scope</code> over a <code>Context</code> instance.
 * <p/>This class is provided as a convenience: the current object on the
 * <code>Context</code> stack can thus be read and written using the
 * <code>Scope</code> interface. <p/>An instance of this class takes a
 * <code>Context</code> at construction time. The instance recognizes the
 * <code>currentObject</code> key. The code snippet below illustrates the use
 * of this class:
 * 
 * <pre>
 * someContext.push(&quot;Hello World&quot;);
 * ContextScope scope = new ContextScope(someContext);
 * 
 * // will print &quot;Hello World&quot; 
 * System.out.println(scope.getVal(&quot;currentObject&quot;));
 * scope.putVal(&quot;currentObject&quot;, &quot;Foo Bar&quot;);
 * 
 * // will print &quot;Foo Bar&quot; 
 * System.out.println(scope.getVal(&quot;currentObject&quot;));
 * </pre>
 * 
 * 
 * @see org.sapia.soto.state.ContextImpl
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class ContextScope implements Scope {

  public static final String CURRENT_OBJECT_KEY = "currentObject";

  private Context            _context;

  public ContextScope(Context ctx) {
    _context = ctx;
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    if(key != null) {
      if(key.equals(CURRENT_OBJECT_KEY) && _context.hasCurrentObject()) {
        return _context.currentObject();
      }
    }
    return null;
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    if(key != null) {
      if(key.equals(CURRENT_OBJECT_KEY)) {
        _context.push(value);
      }
    }
  }

}
