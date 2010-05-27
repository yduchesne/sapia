package org.sapia.corus.http.helpers;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface OutputHelper {

  public void print(Request req, Response res) throws Exception;
  
}
