package org.sapia.soto;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a <code>ServiceSelector</code> that selects 
 * Soto <code>Service</code>s based on their configured attributes.
 * <p>
 * Usage:
 * <pre>
 *  // container is a SotoContainer or Env instance
 *  AttributeServiceSelector selector = new AttributeServiceSelector();
 *  selector
 *    .addAttribute(
 *      new Attribute().setName("attribute1")
 *     )
 *    .addAttribute(
 *      new Attribute().setName("attribute2").setValue("value2")
 *     );
 *   
 *  List result = services.lookup(selector, false);
 *  ...
 * </pre>
 * <p>
 * The above example looks for all services that have an "attribute1"
 * attribute AND an "attribute2" attribute with value "value2".
 * <p>
 * The involved classes support chained invocation, for convenience.
 * 
 * @see org.sapia.soto.SotoContainer#lookup(ServiceSelector,boolean)
 * @see Env#lookup(ServiceSelector, boolean)
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeServiceSelector implements ServiceSelector{
  
  private List _criteria = new ArrayList();
  
  public AttributeServiceSelector addCriteria(Attribute attr){
    if(attr.getName() == null){
      throw new IllegalArgumentException("Attribute name not specified");
    }
    _criteria.add(attr);
    return this;
  }
  
  /**
   * @see org.sapia.soto.ServiceSelector#accepts(org.sapia.soto.ServiceMetaData)
   */
  public boolean accepts(ServiceMetaData meta) {
    for(int i = 0; i < _criteria.size(); i++){
      Attribute criterion = (Attribute)_criteria.get(i);
      Attribute attribute = meta.getAttribute(criterion.getName());
      if(attribute == null){
        return false;
      }      
      else if(criterion.getValue() == null){
        if(!criterion.getName().equals(attribute.getName())){
          return false;
        }
      }
      else if(!criterion.getValue().equals(attribute.getValue())){
        return false;
      }
    }
    return true;
  }

}
