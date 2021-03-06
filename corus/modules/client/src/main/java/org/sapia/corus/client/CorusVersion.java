package org.sapia.corus.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * An instance of this class is used to load the version number of the Corus
 * installation. The version number is kept in a properties file, in the
 * classpath, under: <code>org/sapia/corus/version.properties</code>.
 * 
 * @author yduchesne
 * 
 */
public class CorusVersion {

  private String ver;

  private CorusVersion(String ver) {
    if (ver == null)
      this.ver = "undefined";
    else
      this.ver = ver;
  }

  public String toString() {
    return ver;
  }

  public static void main(String[] args) {
    System.out.println("Corus version: " + CorusVersion.create());
  }

  public static CorusVersion create() {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/sapia/corus/version.properties");

    if (is == null) {
      return new CorusVersion(null);
    } else {
      try {
        Properties props = new Properties();
        try {
          props.load(is);

          String ver = props.getProperty("corus.version");
          return new CorusVersion(ver);
        } catch (IOException e) {
          return new CorusVersion(null);
        }
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // noop
        }
      }
    }

  }
}
