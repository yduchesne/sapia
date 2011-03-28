package org.sapia.soto;


public class SotoMainTesting {

  public void testNormal() throws Exception {
    startMain("resource:/org/sapia/soto/singleService.xml", 3000);
    Thread.sleep(1000);
    System.exit(0);
  }
  
  private void startMain(final String aName, final long aTimeout) {
    Thread t = new Thread() {
      public void run() {
        if (aTimeout > 0) {
          SotoMain.main(new String[] {aName, String.valueOf(aTimeout)});
        } else {
          SotoMain.main(new String[] {aName});
        }
      }
    };
    t.start();
  }
}
