package org.sapia.corus.interop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Corresponds to a process event.
 * 
 * @author yduchesne
 *
 */
public class ProcessEvent extends AbstractCommand {
  
  private static final long serialVersionUID = 1L;
  
  private List<Param> _params = new ArrayList<>();
  private String      _type;

  public ProcessEvent() {
  }
  
  /**
   * @param type an event type.
   */
  public void setType(String type) {
    _type = type;
  }
  
  /**
   * @return this instance's event type.
   */
  public String getType() {
    return _type;
  }
  
  /**
   * Adds a param to this instance.
   * 
   * @param param a {@link Param}
   */
  public void addParam(Param param) {
    _params.add(param);
  }
   
  /**
   * @return this instance's {@link Param} params.
   */
  public List<Param> getParams() {
    return _params;
  }
  
  /**
   * @return this instance's {@link Param} bindings, as a {@link Map}.
   */
  public Map<String, String> paramMap() {
    Map<String, String> toReturn = new HashMap<String, String>();
    for (Param p : _params) {
      if (p.getName() != null && p.getValue() != null) {
        toReturn.put(p.getName(), p.getValue());
      }
    }
    return toReturn;
  }

}
