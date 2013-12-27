package org.sapia.validator.rules.groovy;

import groovy.lang.GroovyClassLoader;

import java.io.ByteArrayInputStream;
import java.util.Random;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.validator.Rule;
import org.sapia.validator.ValidationContext;

/**
 * This class implements validation through a Groovy script. The script's source is provided
 * as CDATA in the XML element to which an instance of this class corresponds.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class GroovyRule extends Rule{
  
  private static final Random RAND = new Random();
  
  private String       _name;
  private String       _src;
  private Rule         _inner;
  private StringBuffer _imports = new StringBuffer();
  
  public void addImport(String imp) {
    _imports.append("import ").append(imp).append(';');
  }

  public void setImport(String imp) {
    addImport(imp);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    super.onCreate();
    try {
      Class clazz = generate();
      _inner = (Rule) clazz.newInstance();
      _inner.setId(getId());
      _inner.setErrorMessages(getErrorMessages());
      return this;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ConfigurationException("Could not generate Groovy code for: " +
        _src, e);
    }
  }

  public void setText(String src) throws Exception {
    _src = src;
  }

  
  /**
   * @see org.sapia.validator.Rule#validate(org.sapia.validator.ValidationContext)
   */
  public void validate(ValidationContext context) {
    if (_inner == null) {
      throw new IllegalStateException("Source code not defined");
    }

    _inner.validate(context);
  }

  private synchronized Class generate() throws Exception {
    if (_src == null) {
      throw new IllegalStateException("Groovy source not specified");
    }
    
    String src = _src;

    long id = RAND.nextLong();
    if(id < 0) id = -id;
    src = _imports.toString() + " class Vlad_Groovy_Rule_Java_" +
      id + " extends " + Rule.class.getName()  + " {" +
      "  public void validate(" + ValidationContext.class.getName() + " context){" + _src + "  }"  +
      /*"  public String getId(){ return " + (getId() != null ? getId() : "\"groovyRule\"") + ";  }"  +*/
      "}";
    
    ByteArrayInputStream bis    = new ByteArrayInputStream(src.getBytes());
    GroovyClassLoader    loader = new GroovyClassLoader(Thread.currentThread()
                                                              .getContextClassLoader());

    return loader.parseClass(bis, getClass().getName());
  }


}


