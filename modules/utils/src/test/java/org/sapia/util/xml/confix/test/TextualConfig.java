package org.sapia.util.xml.confix.test;

// Import of Sun's JDK classes
// ---------------------------
import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TextualConfig extends Text {

  private String _theName;
  private ArrayList _theNamedValues;
  private ArrayList _theCustomConfigs;

  public TextualConfig() {
    _theNamedValues = new ArrayList();
    _theCustomConfigs = new ArrayList();
  }

  public String getName() {
    return _theName;
  }

  public List getNamedValues() {
    return _theNamedValues;
  }

  public List getCustomConfigs() {
    return _theCustomConfigs;
  }

  public void setName(String aName) {
    _theName = aName;
  }

  public void addWrappedNamedValue(WrappedNamedValue aWrapper) {
    _theNamedValues.add(aWrapper.getNamedValue());
  }

  public void addTextualNamedValue(TextualNamedValue aNamedValue) {
    _theNamedValues.add(aNamedValue);
  }

  public void addWrappedTextualNamedValue(WrappedTextualNamedValue aWrapper) {
    _theNamedValues.add(aWrapper.getTextualNamedValue());
  }

  public void addNamedValue(NamedValue aNamedValue) {
    _theNamedValues.add(aNamedValue);
  }

  public void addCustomConfig(CustomConfig aCustomConfig) {
    _theCustomConfigs.add(aCustomConfig);
  }

	public void addXMLConsumerConfig(XMLConsumerConfig aConsumerConfig) {
		_theCustomConfigs.add(aConsumerConfig);
	}  
}
