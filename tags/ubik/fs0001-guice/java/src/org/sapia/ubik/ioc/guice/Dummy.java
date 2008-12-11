package org.sapia.ubik.ioc.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class Dummy implements IDummy{

  @Inject
  private Injector injector;
  
  public Injector getInjector() {
    return injector;
  }
}
