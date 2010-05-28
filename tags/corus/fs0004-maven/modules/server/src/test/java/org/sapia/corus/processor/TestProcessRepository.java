package org.sapia.corus.processor;

import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.db.HashDbMap;
import org.sapia.corus.db.persistence.ClassDescriptor;


/**
 * @author Yanick Duchesne
 */
public class TestProcessRepository extends ProcessRepositoryImpl {
  public TestProcessRepository() {
    super(new ProcessDatabaseImpl(new HashDbMap<String, Process>(new ClassDescriptor<Process>(Process.class))), 
          new ProcessDatabaseImpl(new HashDbMap<String, Process>(new ClassDescriptor<Process>(Process.class))),
          new ProcessDatabaseImpl(new HashDbMap<String, Process>(new ClassDescriptor<Process>(Process.class))));
  }
}
