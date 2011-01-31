package org.sapia.corus.deployer.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Models a network, stream-based connection.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Connection {
	
	/**
	 * @return the <code>InputStream</code> of this client.
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;
	
	
	/**
	 * @return the <code>OutputStream</code> of this client.
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException;
	
	/**
	 * Closes this connection.
	 */
	public void close();
	

}