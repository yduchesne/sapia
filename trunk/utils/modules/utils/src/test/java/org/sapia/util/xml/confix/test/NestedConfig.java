package org.sapia.util.xml.confix.test;

import org.jdom.Element;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class NestedConfig {

	protected String _theType;
	protected Element _theConfigElement;

	public NestedConfig() {
	}

	public String getType() {
		return _theType;
	}

	public Element getConfigElement() {
		return _theConfigElement;
	}

	public void setType(String aType) {
		_theType = aType;
	}

}
