package org.sapia.soto.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.SotoContainer;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class implements the <code>FreemarkerService</code> interface.
 * 
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
public class FreemarkerServiceImpl implements FreemarkerService,
    TemplateLoader, Service, EnvAware {

  private Env           _env;
  private Configuration _config = Configuration.getDefaultConfiguration();
  private boolean _localized = false;
  private List _roots = new ArrayList();

  public FreemarkerServiceImpl() {
  }
  
  /**
   * @param root adds a template root to this instance.
   */
  public void addRoot(String root){
    _roots.add(root);
  }
  
  /**
   * @param localized if <code>true</code>, this instance will perform localized lookup
   * (default to <code>false</code>).
   */
  public void setLocalized(boolean localized){
    _localized = localized;
  }
  
  /**
   * @param seconds the interval (in seconds) at which the template cache will
   * check if templates have been modified. If seconds == 0, Freemarker will
   * chekc for modifications every time a template is accessed.
   */
  public void setUpdateDelay(int seconds) throws TemplateException{
    _config.setSetting(Configuration.TEMPLATE_UPDATE_DELAY_KEY, ""+seconds);
  }
  
  /**
   * @see org.sapia.soto.freemarker.FreemarkerService#resolveTemplate(java.lang.String)
   */
  public Template resolveTemplate(String path) throws IOException {
    return _config.getTemplate(path);
  }

  /**
   * @see org.sapia.soto.freemarker.FreemarkerService#resolveTemplate(java.lang.String,
   *      java.util.Locale)
   */
  public Template resolveTemplate(String path, Locale locale)
      throws IOException {
    return _config.getTemplate(path, locale);
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _config.setTemplateLoader(this);
    _config.setSharedVariable(SotoContainer.SOTO_ENV_KEY, _env);
    _config.setLocalizedLookup(_localized);
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    _config.clearTemplateCache();
    _config.clearSharedVariables();
  }

  /**
   * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
   */
  public Object findTemplateSource(String path) throws IOException {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < _roots.size(); i++){
      try{
        String root = (String)_roots.get(i);
        return _env.resolveResource(buf.append(root).append("/").append(path).toString());
      }catch(ResourceNotFoundException e2){
        buf.delete(0, buf.length());
      }
    }    
    return _env.resolveResource(path);
  }

  /**
   * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
   */
  public long getLastModified(Object res) {
    return ((Resource)res).lastModified();
  }

  /**
   * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object,
   *      java.lang.String)
   */
  public Reader getReader(Object resource, String encoding) throws IOException {
    return new InputStreamReader(((Resource) resource).getInputStream(),
        encoding);
  }

  /**
   * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
   */
  public void closeTemplateSource(Object arg0) throws IOException {
  }

  /**
   * @see Configuration#setSetting(java.lang.String, java.lang.String)
   */
  public void addSetting(String name, String value) throws TemplateException {
    _config.setSetting(name, value);
  }

  public Setting createSetting() {
    return new Setting(this);
  }

  public static final class Setting implements ObjectCreationCallback {

    private String                name, value;
    private FreemarkerServiceImpl _owner;

    Setting(FreemarkerServiceImpl owner) {
      _owner = owner;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    /**
     * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
     */
    public Object onCreate() throws ConfigurationException {
      if(_owner != null && name != null && value != null) {
        try {
          _owner.addSetting(name, value);
        } catch(TemplateException e) {
          throw new ConfigurationException("Could not add setting: " + name
              + "; value=" + value, e);
        }
      }
      return this;
    }

  }
}
