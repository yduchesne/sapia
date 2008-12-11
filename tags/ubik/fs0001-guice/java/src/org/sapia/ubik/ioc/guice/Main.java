package org.sapia.ubik.ioc.guice;

import com.google.inject.Guice;

public class Main {

  public static void main(String[] args) {
    IDummy dummy = Guice.createInjector(new DummyModule()).getInstance(IDummy.class);
    System.out.println(dummy.getInjector());
  }
}
