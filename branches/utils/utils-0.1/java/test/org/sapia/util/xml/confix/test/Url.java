package org.sapia.util.xml.confix.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Url implements ObjectCreationCallback{
	
	private String _link;
	
	public void setLink(String link){
		_link = link;
	}
	
	public Object onCreate() throws ConfigurationException {
		if(_link == null){
			throw new ConfigurationException("'link' not specified for Url");
		}
		try{
    	return new URL(_link);
		}catch(MalformedURLException e){
			throw new ConfigurationException("Invalid link: " + _link, e);
		}
  }
	
}
