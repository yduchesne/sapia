package org.sapia.ubik.ioc.guice;

import com.google.inject.AbstractModule;

public class DummyModule extends AbstractModule{
  
  @Override
  protected void configure() {
    bind(IDummy.class).to(Dummy.class);
    
  }

}
