package org.sapia.regis.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.cfg.Configuration;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.RegistryFactory;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;
import org.w3c.dom.Document;

/**
 * An instance of this class creates an <code>HibernateRegistry</code> instance.
 *
 * @author yduchesne
 *
 */
public class HibernateRegistryFactory implements RegistryFactory{
  
  /**
   * This method expects properties that follow the Hibernate configuration spec. The
   * properties are simply delegated to an Hibernate <code>Configuration</code> object.
   * <p>
   * The returned <code>Registry</code> implementation will thus be based on an
   * Hibernate <code>SessionFactory</code>.
   * 
   * @param props some Hibernate properties.
   * @return an <code>HibernateRegistry</code> instance.
   */
  public Registry connect(Properties props) throws Exception {
    Configuration cfg = new Configuration();
    cfg.addClass(NodeImpl.class);
    
    TemplateFactory        fac        = new TemplateFactory();
    TemplateElementIF      templ      = fac.parse(loadHibernateConfig(props.getProperty("hbm2ddl.auto") != null));
    String                 configStr  = templ.render(new HibernateConfigContext(props));
    DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
    DocumentBuilder        builder    = builderFac.newDocumentBuilder();
    Document               doc        = builder.parse(new ByteArrayInputStream(configStr.getBytes()));
    String render = props.getProperty(RegistryContext.INTERPOLATION_ACTIVE, "true");    
    cfg.configure(doc);
    return new HibernateRegistry(cfg.buildSessionFactory(), new Boolean(render).booleanValue());
  }
  
  private String loadHibernateConfig(boolean ddl) throws IOException{
    
    InputStream is = null;
    if(ddl){
      is = getClass().getResourceAsStream("hibernate.ddl.xml");
    }
    else{
      is = getClass().getResourceAsStream("hibernate.xml");      
    }
    try{
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buf = new byte[100];
      int i;
      while((i = is.read(buf)) > 0){
        bos.write(buf, 0, i);
      }
      return new String(bos.toByteArray());
    }finally{
      is.close();
    }
  }
  
  public static class HibernateConfigContext implements TemplateContextIF{
    
    private static Properties DEFAULT = new Properties();
    private Properties _props;
    
    HibernateConfigContext(Properties props){
      _props = props;
    }
    
    static{
      InputStream is = HibernateConfigContext.class.getResourceAsStream("hibernate.default.properties");
      try{
        DEFAULT.load(is);
        is.close();
      }catch(IOException e){
        e.printStackTrace();
        throw new IllegalStateException("Could not load default Hibernate configuration properties");
      }
    }
    
    public Object getValue(String name) {
      String prop = _props.getProperty(name);
      if(prop == null) prop = DEFAULT.getProperty(name);
      return prop;
    }
    
    public void put(String arg0, Object arg1) {
    }
    
  }

}
