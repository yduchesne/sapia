package org.sapia.soto.aop;

import org.sapia.soto.ConfigurationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An instance of this class encapsulates the advices that are to be called in
 * the context of a given method.
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
public class Invoker {
  private List    _around  = new ArrayList();
  private List    _before  = new ArrayList();
  private List    _after   = new ArrayList();
  private List    _throws  = new ArrayList();
  private List    _returns = new ArrayList();
  private boolean _hasBefore;
  private boolean _hasAfter;
  private boolean _hasThrows;
  private boolean _hasReturns;

  /**
   * Constructor for MethodInterceptor.
   * 
   * @param advices
   *          a <code>List</code> of <code>Advice</code> instance.
   */
  public Invoker(List advices) throws ConfigurationException {
    Advice adv;

    for(int i = 0; i < advices.size(); i++) {
      adv = (Advice) advices.get(i);

      if(adv instanceof BeforeAdvice) {
        _before.add(adv);
      }

      if(adv instanceof AfterAdvice) {
        _after.add(adv);
      }

      if(adv instanceof ThrowsAdvice) {
        _throws.add(adv);
      }

      if(adv instanceof ReturnAdvice) {
        _returns.add(adv);
      }
    }

    _hasBefore = _before.size() > 0;
    _hasAfter = _after.size() > 0;
    _hasThrows = _throws.size() > 0;
    _hasReturns = _returns.size() > 0;
  }

  /**
   * Dispatches a method call to the approriate advices that are kept within
   * this instance.
   * 
   * @param invocation the <code>Invocation</code> to dispatch.
   * @param itr an <code>Iterator</code> of <code>Invoker</code>s.
   */
  public Object invoke(Invocation invocation, Iterator itr) throws Throwable {
    if(_hasBefore) {
      for(int i = 0; (i < _before.size()) && !invocation.wasInvoked(); i++) {
        ((BeforeAdvice) _before.get(i)).preInvoke(invocation);
      }
    }

    if(_hasAfter) {
      Object toReturn;

      if(itr.hasNext()) {
        toReturn = ((Invoker) itr.next()).invoke(invocation, itr);
      } else {
        if(_hasThrows) {
          try {
            if(invocation.wasInvoked()) {
              toReturn = invocation.getReturnValue();
            } else {
              toReturn = invocation.invoke();
            }
          } catch(Throwable t) {
            for(int i = 0; i < _throws.size(); i++) {
              ((ThrowsAdvice) _throws.get(i)).onThrows(invocation, t);
            }

            throw t;
          }
        } else {
          if(invocation.wasInvoked()) {
            toReturn = invocation.getReturnValue();
          } else {
            toReturn = invocation.invoke();
          }
        }
      }

      if(_hasAfter) {
        for(int i = 0; i < _after.size(); i++) {
          ((AfterAdvice) _after.get(i)).postInvoke(invocation);
        }
      }

      if(_hasReturns
          && !invocation.getMethod().getReturnType().equals(void.class)) {
        for(int i = 0; i < _returns.size(); i++) {
          invocation.setReturnValue(((ReturnAdvice) _returns.get(i))
              .onReturn(invocation));
        }
      }

      return toReturn;
    } else {
      if(itr.hasNext()) {
        return ((Invoker) itr.next()).invoke(invocation, itr);
      } else {
        Object toReturn;

        if(_hasThrows) {
          try {
            if(invocation.wasInvoked()) {
              toReturn = invocation.getReturnValue();
            } else {
              toReturn = invocation.invoke();
            }
          } catch(Throwable t) {
            for(int i = 0; i < _throws.size(); i++) {
              ((ThrowsAdvice) _throws.get(i)).onThrows(invocation, t);
            }

            throw t;
          }
        } else {
          if(invocation.wasInvoked()) {
            toReturn = invocation.getReturnValue();
          } else {
            toReturn = invocation.invoke();
          }
        }

        if(_hasReturns
            && !invocation.getMethod().getReturnType().equals(void.class)) {
          for(int i = 0; i < _returns.size(); i++) {
            toReturn = ((ReturnAdvice) _returns.get(i)).onReturn(invocation);
          }
        }

        return toReturn;
      }
    }
  }

  /**
   * Return true if this instance contains "before" advices.
   * 
   * @return <code>true</code> if this instance contains
   *         <code>BeforeAdvice</code>s.
   */
  public boolean hasBefore() {
    return _hasBefore;
  }

  /**
   * Return true if this instance contains "after" advices.
   * 
   * @return <code>true</code> if this instance contains
   *         <code>AfterAdvice</code>s.
   */
  public boolean hasAfter() {
    return _hasAfter;
  }
}
