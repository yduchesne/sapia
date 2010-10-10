package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.util.Param;

public class BasePropAliasTag {

  List _params = new ArrayList();
  
  public Param createParam(){
    return createAlias();
  }
  
  public PropertyAlias createAlias(){
    PropertyAlias alias = new PropertyAlias();
    _params.add(alias);
    return alias;
  }
}
