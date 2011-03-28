package org.sapia.soto.configuration.jconfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jconfig.Category;
import org.jconfig.NestedCategory;
import org.sapia.soto.configuration.ConfigCategory;

/**
 * This class make an adapts a JConfig category object to a <@link org.sapia.soto.configuration.ConfigCategory>
 * object.
 *
 * @author Jean-Cedric Desrochers
 */
public class JConfigCategoryAdapter implements ConfigCategory {

  /** The jconfig category encapsulated by this adapter. */
  private Category _jconfigCategory;
  
  /**
   * Creates a new JConfigCategoryAdapter instance.
   * 
   * @param aCategory The JConfig category to adapt.
   */
  public JConfigCategoryAdapter(Category aCategory) {
    _jconfigCategory = aCategory;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigCategory#getName()
   */
  public String getName() {
    return _jconfigCategory.getCategoryName();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigCategory#getProperty(java.lang.String)
   */
  public String getProperty(String aName) {
    return _jconfigCategory.getProperty(aName);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigCategory#getProperties()
   */
  public Properties getProperties() {
    return _jconfigCategory.getProperties();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigCategory#getCategory(java.lang.String)
   */
  public ConfigCategory getCategory(String aName) {
    if (_jconfigCategory instanceof NestedCategory) {
      Category childCategory = ((NestedCategory) _jconfigCategory).getCategory(aName);
      
      if (childCategory != null) {
        return new JConfigCategoryAdapter(childCategory);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigCategory#getCategories()
   */
  public List getCategories() {
    ArrayList childCategories = new ArrayList();
    
    if (_jconfigCategory instanceof NestedCategory) {
      for (Iterator it = ((NestedCategory) _jconfigCategory).getChildCategories().iterator(); it.hasNext(); ) {
	      Category childCategory = (Category) it.next();
	      if (childCategory != null) {
	        childCategories.add(new JConfigCategoryAdapter(childCategory));
	      }
      }
    }
    
    return childCategories;
  }
}
