package org.sapia.regis.remote;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RegistryServerLockManager {

  private static ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  
  public static ReentrantReadWriteLock lock(){
    return _lock;
  }
  
}
