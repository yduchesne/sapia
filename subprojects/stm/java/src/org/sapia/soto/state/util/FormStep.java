package org.sapia.soto.state.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ClassUtils;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.helpers.ScopeParser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

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
public class FormStep implements Step {
  private List   _params = new ArrayList();
  private String _className;
  private String _encoding;

  public FormStep() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public void setClass(String className) {
    _className = className;
  }

  public void setEncoding(String encoding) {
    _encoding = encoding;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    Object bean;

    if(_className == null) {
      bean = res.getContext().currentObject();
      assign(bean, res);
    } else {
      try {
        bean = Class.forName(_className).newInstance();
      } catch(InstantiationException e) {
        res.error(e);

        return;
      } catch(ClassNotFoundException e) {
        res.error(e);

        return;
      } catch(IllegalAccessException e) {
        res.error(e);

        return;
      }

      assign(bean, res);
      res.getContext().push(bean);
    }
  }

  public Param createParam() {
    Param p = new Param();
    _params.add(p);

    return p;
  }

  private void assign(Object bean, Result res) {
    Param p;

    for(int i = 0; i < _params.size(); i++) {
      p = (Param) _params.get(i);

      try {
        if(p._encoding == null && _encoding != null) {
          p._encoding = _encoding;
        }
        p.process(bean, res.getContext());
      } catch(InvocationTargetException e) {
        res.error(e.getTargetException());

        return;
      } catch(IllegalAccessException e) {
        res.error(e);

        return;
      } catch(NoSuchMethodException e){
        res.error(e);
        
        return;
      }
    }
  }

  public static class Param {
    static final String  ON         = "on";
    static final String  TRUE       = "true";
    static final String  YES        = "yes";
    static final Boolean BOOL_TRUE  = new Boolean(true);
    static final Boolean BOOL_FALSE = new Boolean(false);
    String               _from, _value, _to, _encoding;
    String[]             _scopes;
    boolean              _isBoolean;

    public Param setBoolean(boolean bool) {
      _isBoolean = bool;

      return this;
    }

    public Param setValue(String value) {
      _value = value;

      return this;
    }

    public Param setFrom(String from) {
      _from = from;
      return this;
    }

    public void setEncoding(String encoding) {
      _encoding = encoding;
    }

    public Param setScopes(String scopes) {
      _scopes = ScopeParser.parse(scopes);

      return this;
    }

    public Param setTo(String to) {
      _to = to;

      return this;
    }

    void process(Object bean, Context ctx) throws InvocationTargetException,
        IllegalAccessException, IllegalStateException, NoSuchMethodException {
      Object value;

      if(_to == null) {
        throw new IllegalStateException("'to' attribute not specified");
      }

      if(_value != null) {
        value = _value;
        doAssign(bean, _to, value, _isBoolean);
      } else {
        if(_from == null) {
          throw new IllegalStateException("'from' attribute not specified");
        }
        if(_scopes == null) {
          value = ctx.get(_from);
        } else {
          value = ctx.get(_from, _scopes);
          if(value != null && _encoding != null) {
            try {
              value = new String(value.toString().getBytes(_encoding), "UTF-8");
            } catch(UnsupportedEncodingException ue) {
              try {
                value = new String(value.toString().getBytes(), "UTF-8");
              } catch(UnsupportedEncodingException ue2) {
                value = value.toString();
              }
            }
          }
        }
        doAssign(bean, _to, value, _isBoolean);
      }
    }

    private static void doAssign(Object bean, String to, Object val, boolean b)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      //try{
        if(b) {
          if(val != null) {
            val = val.toString().toLowerCase();
            if(val.toString().equals(ON) || val.toString().equals(TRUE)
                || val.toString().equals(YES)) {
              BeanUtils.setProperty(bean, to, BOOL_TRUE);
            } else {
              BeanUtils.setProperty(bean, to, BOOL_FALSE);
            }
          } else {
            BeanUtils.setProperty(bean, to, BOOL_FALSE);
          }
        } else {
          if(val != null) {
            if(val instanceof String){
              if(((String)val).length() == 0) return; 
            } 
            BeanUtils.setProperty(bean, to, val);
          }
        }
      /*}catch(NoSuchMethodException e){
        throw new NoSuchMethodException(e.getMessage() + " for object: " + bean + 
          "; attribute: " +  to + "; value to assign: " + val);
      }*/
      
    }
  }
}
