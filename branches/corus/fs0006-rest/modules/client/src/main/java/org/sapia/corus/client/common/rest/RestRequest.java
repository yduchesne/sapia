package org.sapia.corus.client.common.rest;

import java.util.Set;


/**
 * Abstracts a request. 
 * 
 * @author yduchesne
 *
 */
public interface RestRequest {

  /**
   * @return a request path.
   */
  public String getPath();

  /**
   * Returns a {@link Value}, whether a value exists for the given name or not. If no value exists,
   * the {@link Value#isNull()} method of the returned {@link Value} will return <code>true</code>, and
   * its {@link Value#isSet()} method will return <code>false</code>.
   * 
   * @param name a parameter name.
   * @return the {@link Value} corresponding to the given name.
   */
  public Value getValue(String name);

  /**
   * @param name a parameter name.
   * @param defaultVal the default value to use if no value is found for the given name.
   * @return the {@link Value} corresponding to the given name.
   */
  public Value getValue(String name, String defaultVal);
  
  /**
   * @return the name of the HTTP method that is invoked.
   */
  public String getMethod();
  
  /**
   * @return the {@link Set} of mime-types accepted by the sender.
   */
  public Set<String> getAccepts();
  
  /**
   * @return this instance's content type.
   */
  public String getContentType();

}
 
