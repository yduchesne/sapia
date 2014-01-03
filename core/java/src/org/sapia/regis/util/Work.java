package org.sapia.regis.util;

import org.hibernate.Session;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.cache.CacheRegistry;
import org.sapia.regis.hibernate.HibernateRegistry;
import org.sapia.regis.local.LocalRegistry;

/**
 * A convenient class which maybe used for invoking a Registry instance in the context of a {@link RegisSession}.
 * An instance of this class corresponds to a unit of work for which a {@link RegisSession} is transparently
 * created and closed.
 * <p>
 * Usage:
 * <pre>
 *    Work.with(someRegistry).run(new Runnable(){
 *      public void run(){
 *        // access the registry here...
 *      }
 *    });
 * </pre>
 * At exiting the {@link #run(Runnable)} method of the {@link Work} instance, the session 
 * is automatically closed.
 * <p>
 * Using this class may not be required, depending on the underlying {@link Registry} implementation. The {@link HibernateRegistry},
 * for example, fully implements the Regis session API on top of the Hibernate {@link Session}.
 * <p>
 * The {@link LocalRegistry} does not require such precaution, and the {@link CacheRegistry} neither.
 * 
 * @author yduchesne
 *
 */
public class Work {
  
  private Registry registry;

  /**
   * Creates an instance of this class and returns it.
   * 
   * @param registry a {@link Registry}
   * @return a new instance of this class.
   */
  public static Work with(Registry registry){
    Work w = new Work();
    w.registry = registry;
    return w;
  }
  
  /**
   * Calls the {@link #run(Runnable)} method of the given {@link Runnable} instance, in
   * the context of a {@link RegisSession}. 
   * 
   * @param runnable a {@link Runnable}. 
   */
  public void run(Runnable runnable){
    RegisSession session = registry.open();
    try{
      runnable.run();
    }finally{
      session.close();
    }
  }
}
