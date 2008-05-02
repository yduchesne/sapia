package org.sapia.beeq.hibernate.conf;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sapia.beeq.hibernate.HibernateMessage;
import org.sapia.beeq.hibernate.queue.QueueElement;
import org.sapia.beeq.hibernate.queue.retry.RetryQueueElement;

public class ConfigLoader {

  private Configuration config = new Configuration();
  
  
  private void loadClasses(){
    config
      .addClass(HibernateMessage.class)
      .addClass(QueueElement.class)
      .addClass(RetryQueueElement.class);
  }
  
  private void setDialect(String dialect){
    config.setProperty("hibernate.dialect", dialect);
  }
  
  private void setDriver(String driver){
    config.setProperty("hibernate.connection.driver_class", driver);
  }
  
  private void setURL(String url){
    config.setProperty("hibernate.connection.url", url);
  }  
  
  private void setUsername(String username){
    config.setProperty("hibernate.connection.username", username);
  }
  
  private void setPassword(String password){
    config.setProperty("hibernate.connection.password", password);
  }  
  
  public static Configuration createEmbedded(){
    ConfigLoader loader = new ConfigLoader();
    loader.loadClasses();
    loader.setDriver("org.hsqldb.jdbcDriver");
    loader.setURL("jdbc:hsqldb:mem:beeq");
    loader.setUsername("sa");
    loader.setPassword("");
    loader.setDialect("org.hibernate.dialect.HSQLDialect");
    loader.config.setProperty("hibernate.hbm2ddl.auto", "create");
    //loader.config.setProperty("hibernate.show_sql", "true");
    return loader.config;
  }
  
  public static void main(String[] args) {
    SessionFactory fac = null;
    Session session = null;
    try{
      Configuration conf = createEmbedded();
      fac = conf.buildSessionFactory();
      session = fac.openSession();
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      if(fac != null)
        fac.close();
      if(session != null)
        session.close();
    }
  }
  
}
