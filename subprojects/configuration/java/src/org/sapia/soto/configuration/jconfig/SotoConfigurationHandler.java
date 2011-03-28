/*
 * SotoConfigurationHandler.java
 *
 * Created on August 10, 2005, 12:23 PM
 *
 */

package org.sapia.soto.configuration.jconfig;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.ConfigurationHandler;
import org.jconfig.parser.NestedConfigParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author yduchesne
 */
public class SotoConfigurationHandler implements ConfigurationHandler{
  
  private String _name;
  private InputStream _resource;
  private String _uri;
  
  /** Creates a new instance of SotoConfigurationHandler */
  public SotoConfigurationHandler(String name, InputStream resource, String uri) {
    _name = name;
    _resource = resource;
    _uri = uri;
  }

  public Configuration load(String str) throws ConfigurationManagerException {
    DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
    try{
      DocumentBuilder builder = fac.newDocumentBuilder();
      Document doc = builder.parse(_resource);
      NestedConfigParser parser = new NestedConfigParser();
      Configuration conf = parser.parse(doc, _name);
      conf.resetCreated(); // changes the created flag to false
      return conf;
    }catch(ParserConfigurationException e){
      throw new ConfigurationManagerException("Could not load DOM parser - " + e.getMessage());
    }catch(SAXException e){
      throw new ConfigurationManagerException("Could not parse configuration from: " + _uri + " - " + e.getMessage());
    }catch(IOException e){
      throw new ConfigurationManagerException("Could not retrieve resource : " + _uri + " - " + e.getMessage());
    }
  }

  public void store(org.jconfig.Configuration configuration) throws ConfigurationManagerException {
    throw new ConfigurationManagerException(getClass().getName() + " does not support storing configurations");
  }
  
}
