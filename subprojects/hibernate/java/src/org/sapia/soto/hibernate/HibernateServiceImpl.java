package org.sapia.soto.hibernate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.hibernate.util.DTDEntityResolver;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.FeedbackMonitorable;
import org.sapia.soto.util.Param;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * This class implements the <code>HibernateService</code> interface. It loads
 * a <code>SessionFactory</code> from an Hibernate XML configuration. The 
 * configuration can also be set using the appropriate methods of this class 
 * (the methods allows setting hibernate properties and defined class mappings).
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
public class HibernateServiceImpl implements Service, HibernateService, EnvAware, FeedbackMonitorable {
  private Configuration  _config = new Configuration();
  private SessionFactory _sessions;
  private List           _props  = new ArrayList();
  private String         _configResource;
  private Env            _env;

  public HibernateServiceImpl() {
    super();
  }
  
  /**
   * Sets the name of the resource corresponding to an Hibernate configuration.
   */
  public void setConfig(String resource){
    _configResource = resource;
  }

  /**
   * Configuration method that adds a class to the Hibernate configuration that
   * is internally used.
   * <p>
   * Corresponds to the following Hibernate snippet:
   * 
   * <pre>
   * Configuration cfg = new Configuration().addClass(eg.Vertex.class).addClass(
   *                       eg.Edge.class);
   * </pre>
   * 
   * @param className
   *          the name of the class to add.
   * @throws Exception
   */
  public void addClass(String className) throws Exception {
    _config.addClass(Class.forName(className));
  }

  /**
   * Configuration method that adds a property to the Hibernate configuration
   * that is internally used.
   * 
   * <pre>
   * Configuration cfg = new Configuration();
   * cfg.setProperty(&quot;name&quot;, &quot;value&quot;);
   * </pre>
   */
  public Param createProperty() {
    Param param = new Param();
    _props.add(param);

    return param;
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if(_configResource != null){
      InputStream confStream = _env.resolveStream(_configResource);
      try{
        SAXReader reader = new SAXReader();
        // Use hibernates entity resolver to prevent access to the net on each load
        reader.setEntityResolver(new DTDEntityResolver());
        Document  doc = reader.read(confStream);
        DOMWriter writer = new DOMWriter();
        _config.configure(writer.write(doc));
      }finally{
        confStream.close();
      }
    }
    _sessions = _config.buildSessionFactory();    
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
    try {
      _sessions.close();
    } catch(HibernateException e) {
      // noop;
    }
  }

  /**
   * @see org.sapia.soto.hibernate.HibernateService#getSessionFactory()
   */
  public SessionFactory getSessionFactory() throws HibernateException {
    return _sessions;
  }
  
  /**
   * @deprecated
   * @see #monitor()
   */
  public void ping() throws Exception {
    monitor();
  }
  
  public Properties monitor() throws Exception {
    try {
      Properties props = new Properties();
      Statistics stats = _sessions.getStatistics();
      props.setProperty("collectionFetchCount", Long.toString(stats.getCollectionFetchCount()));
      props.setProperty("collectionLoadCount", Long.toString(stats.getCollectionLoadCount()));
      props.setProperty("collectionRecreateCount", Long.toString(stats.getCollectionRecreateCount()));
      props.setProperty("collectionRemoveCount", Long.toString(stats.getCollectionRemoveCount()));
      props.setProperty("collectionLoadCount", Long.toString(stats.getCollectionLoadCount()));
      props.setProperty("collectionUpdateCount", Long.toString(stats.getCollectionUpdateCount()));
      props.setProperty("connectCount", Long.toString(stats.getConnectCount()));
      props.setProperty("entityDeleteCount", Long.toString(stats.getEntityDeleteCount()));
      props.setProperty("entityFetchCount", Long.toString(stats.getEntityFetchCount()));
      props.setProperty("entityInsertCount", Long.toString(stats.getEntityInsertCount()));
      props.setProperty("entityLoadCount", Long.toString(stats.getEntityLoadCount()));
      props.setProperty("entityUpdateCount", Long.toString(stats.getEntityUpdateCount()));
      
      Session session = null;
      try{
        session = _sessions.openSession();
      }finally{
        if(session != null){
          session.close();  
        }
      }
      
      // Adding c3p0 connection pool info
      try {
        int i = 0;
        for (Object o: C3P0Registry.getPooledDataSources()) {
          if (o instanceof PooledDataSource) {
            PooledDataSource ds = (PooledDataSource) o;
            String namePrefix = "c3p0.pool" + (++i) + ".";
            props.setProperty(namePrefix+"dataSourceName", ds.getDataSourceName());
            props.setProperty(namePrefix+"startTimeMillis", Long.toString(ds.getStartTimeMillisDefaultUser()));          
            props.setProperty(namePrefix+"upTimeMillis", Long.toString(ds.getUpTimeMillisDefaultUser()));          
            props.setProperty(namePrefix+"numberTotalConnections", Integer.toString(ds.getNumConnectionsDefaultUser()));
            props.setProperty(namePrefix+"numberBusyConnections", Integer.toString(ds.getNumBusyConnectionsDefaultUser()));
            props.setProperty(namePrefix+"numberIdleConnections", Integer.toString(ds.getNumIdleConnectionsDefaultUser()));
            props.setProperty(namePrefix+"numberHelperThreads", Integer.toString(ds.getNumHelperThreads()));          
            props.setProperty(namePrefix+"numberThreadsAwaitingCheckout", Integer.toString(ds.getNumThreadsAwaitingCheckoutDefaultUser()));          
            props.setProperty(namePrefix+"numberUnclosedOrphanedConnections", Integer.toString(ds.getNumUnclosedOrphanedConnectionsDefaultUser()));          
          }
        }
      } catch (Exception e) {
        System.out.println(new Date() + " Error getting c3p0 registry: " + e.getMessage());
        e.printStackTrace();
      }
      
      return props;
      
    } catch (Exception e) {
      System.out.println(new Date() + " Error monitoring this hibernate service: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
    
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env){
    _env = env;
  }
  /*
  private Configuration getConfiguration() {
    Param p;

    for(int i = 0; i < _props.size(); i++) {
      p = (Param) _props.get(i);

      if((p.getName() != null) && (p.getValue() != null)) {
        _config.setProperty(p.getName(), p.getValue().toString());
      }
    }

    return _config;
  }*/
}
