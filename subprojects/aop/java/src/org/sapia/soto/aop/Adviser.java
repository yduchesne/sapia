package org.sapia.soto.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.util.Utils;
import org.sapia.soto.util.matcher.PathPattern;
import org.sapia.soto.util.matcher.Pattern;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements provides behavior to "advise" objects dynamically.
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
public class Adviser {
  private List      _methods    = new ArrayList();
  private List      _adviceDefs = new ArrayList();
  private List      _groups     = new ArrayList();
  private Pattern[] _interfaces;

  /**
   * Constructor for AopLayer.
   */
  public Adviser() {
    super();
  }

  /**
   * @see org.sapia.soto.Layer#init(ServiceMetaData)
   */
  public void init(ServiceMetaData meta) throws Exception {
    Object proxy = advise(meta.getService());
    meta.setService(proxy);
  }

  /**
   * Advises the given object and returns a proxy for it.
   * 
   * @param toAdvise
   * @return a proxy for the given object.
   * @throws Exception
   */
  public Object advise(Object toAdvise) throws Exception {
    if(!matchesImplements(toAdvise)) {
      return toAdvise;
    }

    MethodPointCut pc;
    List methods;
    List adviceRefs;
    List groupRefs;
    List advices;
    AdviceRef ref;
    GroupRef groupRef;

    Map defs = filterDefs();
    Map groups = filterGroups(defs);
    AdviceInterceptor interceptor;
    boolean advised;

    if(advised = isAdvised(toAdvise)) {
      interceptor = getInterceptor(toAdvise);
    } else {
      interceptor = new AdviceInterceptor(toAdvise.getClass());
    }

    Method meth;

    for(int i = 0; i < _methods.size(); i++) {
      pc = (MethodPointCut) _methods.get(i);
      adviceRefs = pc.getAdviceRefs();
      groupRefs = pc.getGroupRefs();
      advices = new ArrayList(adviceRefs.size());
      methods = pc.scanMethods(toAdvise.getClass());

      for(int j = 0; j < groupRefs.size(); j++) {
        groupRef = (GroupRef) groupRefs.get(j);

        List resolved = groupRef.resolve(groups);
        advices.addAll(resolved);
      }

      for(int j = 0; j < adviceRefs.size(); j++) {
        ref = (AdviceRef) adviceRefs.get(j);
        advices.add(ref.resolve(defs));
      }

      for(int j = 0; j < methods.size(); j++) {
        Invoker delegate = new Invoker(advices);
        meth = (Method) methods.get(j);

        if(Factory.class.isAssignableFrom(meth.getDeclaringClass())) {
          meth = interceptor.getAdvisedClass().getDeclaredMethod(
              meth.getName(), meth.getParameterTypes());
        }

        interceptor.addInvoker(meth, delegate);
      }

      advices.clear();
    }

    Object proxy;

    if(advised) {
      proxy = toAdvise;
    } else {
      proxy = Enhancer.create(toAdvise.getClass(), Utils.getClasses(toAdvise),
          interceptor);
      Utils.copyFields(toAdvise, proxy);
    }

    return proxy;
  }

  /**
   * Clears this instance's state - will have to be reinitialized in order to be
   * reused.
   */
  public void clear() {
    // Freeing memory - being zealous about it...
    _adviceDefs.clear();
    _adviceDefs = null;
    _groups.clear();
    _groups = null;
    _methods.clear();
    _methods = null;
  }

  /**
   * Allows to pass a comma-delimited list of interfaces names (or, rather name
   * patterns) that an object passed to this instance must implement in order to
   * be advised.
   * 
   * @param interfaces
   *          a comma-delimited list of interfaces.
   */
  public void setImplements(String interfaces) {
    String[] interfacePatterns = Utils.split(interfaces, ',', true);
    _interfaces = new Pattern[interfacePatterns.length];

    for(int i = 0; i < interfacePatterns.length; i++) {
      _interfaces[i] = PathPattern.parse(interfacePatterns[i], '.', false);
    }
  }

  /**
   * Return true if the given object implements the interfaces specified to this
   * instance. If no interfaces were specified, this method returns true all the
   * time.
   * 
   * @param o
   *          an <code>Object</code>.
   * @return <code>true</code> if the given object implements the interfaces
   *         specified to this instance, or if this instance was not specified
   *         any interface.
   * 
   * @see #setImplements(String)
   */
  public boolean matchesImplements(Object o) {
    if(_interfaces == null) {
      return true;
    }

    Class[] interfaces = Utils.getClasses(o);
    boolean[] matched = new boolean[_interfaces.length];
    Pattern pattern;

    for(int i = 0; i < interfaces.length; i++) {
      for(int j = 0; j < _interfaces.length; j++) {
        pattern = (Pattern) _interfaces[j];

        if(pattern.matches(interfaces[i].getName())) {
          matched[j] = true;
        }
      }
    }

    for(int i = 0; i < matched.length; i++) {
      if(!matched[i]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Creates a method pointcut.
   * 
   * @return a <code>MethodPointCut</code>.
   */
  public MethodPointCut createMethod() {
    MethodPointCut pc = new MethodPointCut();
    _methods.add(pc);

    return pc;
  }

  /**
   * Creates an advice definition.
   * 
   * @return an <code>AdviceDef</code> instance.
   */
  public AdviceDef createAdviceDef() {
    AdviceDef def = new AdviceDef();
    _adviceDefs.add(def);

    return def;
  }

  /**
   * Creates a group and returns it.
   * 
   * @return a <code>Group</code>.
   */
  public Group createGroup() {
    Group g = new Group();
    _groups.add(g);

    return g;
  }

  /**
   * Returns true if the given object is "advised": i.e., if it is a proxy that
   * encapsulates advices.
   * 
   * @param obj
   * @return true if the given object is "advised": i.e., if it is a proxy that
   *         encapsulates advices.
   */
  public static boolean isAdvised(Object obj) {
    try {
      AdviceInterceptor interceptor = (AdviceInterceptor) ((Factory) obj)
          .getCallback(0);

      return true;
    } catch(ClassCastException e) {
      return false;
    }
  }

  /**
   * Returns the advice interceptor that is encapsulated within the given
   * object, which is a proxy.
   * 
   * @param obj
   * @return an <code>AdviceInterceptor</code>, or <code>null</code> if the
   *         given object is not an "advised" object.
   */
  public static AdviceInterceptor getInterceptor(Object obj) {
    try {
      return (AdviceInterceptor) ((Factory) obj).getCallback(0);
    } catch(ClassCastException e) {
      return null;
    }
  }

  private Map filterDefs() throws ConfigurationException {
    Map defs = new HashMap();
    AdviceDef def;

    for(int i = 0; i < _adviceDefs.size(); i++) {
      def = (AdviceDef) _adviceDefs.get(i);

      if(def.getId() == null) {
        throw new ConfigurationException(
            "'id' attribute not specified on advice definition");
      }

      defs.put(def.getId(), def);
    }

    return defs;
  }

  private Map filterGroups(Map defs) throws ConfigurationException {
    Map groups = new HashMap();
    Group group;

    for(int i = 0; i < _groups.size(); i++) {
      group = (Group) _groups.get(i);

      if(group.getId() == null) {
        throw new ConfigurationException(
            "'id' attribute not specified on advice definition");
      }

      group.resolve(defs);

      groups.put(group.getId(), group);
    }

    return groups;
  }
}
