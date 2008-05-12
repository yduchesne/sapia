package org.sapia.cocoon.serialization.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.serialization.AbstractSerializer;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This serializer generates JSON data out of an XML document. The serializer has
 * the following features/limitations:
 * 
 * <ul>
 *   <li>The serializer creates a JSON graph that maps the structure of the XML document: a JSON
 *   object is created for each XML element in the document.
 *   <li>XML namespaces are ignored.
 *   <li>A JSON array is created for child elements that have the same name, under a given parent element. If there is
 *   only one child element of a given name under another element, then no array is created (a single JSON object is created
 *   for that single child element).
 *   <li>A "cdata" field is created to hold the content of the CDATA (text nested in a given XML element) of the 
 *   corresponding XML element.   
 * </ul>
 * For example, given the following XML document:
 * 
 * <pre>
 * &lt;addressBook&gt;
 *   &lt;contact firstName="bill" lastName="gates"&gt;
 *     Dinner next week
 *   &lt;/contact&gt;
 * &lt;/addressBook&gt;
 * </pre>
 * 
 * Will yield the following JSON data:
 * 
 * <pre>
 * {
 *   "addressBook": {
 *     "contact": {
 *       "firstName": "bill", 
 *       "lastName": "gates", 
 *       "cdata": "Dinner next week"
 *     }
 *   }
 * }
 * </pre>
 * 
 * As the following XML:
 * 
 * <pre>
 * &lt;addressBook&gt;
 *   &lt;contact firstName="bill" lastName="gates" /&gt;
 *   &lt;contact firstName="linus" lastName="torvalds" /&gt;
 * &lt;/addressBook&gt;
 * </pre>
 * 
 * Will yield:
 *
 * <pre>
 * {
 *   "addressBook": {
 *     "contact": [
 *       { "firstName": "bill", "lastName": "gates" },
 *       { "firstName": "linus", "lastName": "torvalds" }       
 *     ]
 *   }
 * }
 * </pre>
 * 
 * <p>
 * Note that this serializer takes the following parameters (as part of the sitemap configuration):
 * 
 * <ul>
 *   <li><code>content-type</code>: by default, set to <code>application/json</code>.
 *   <li><code>global-var-name</code>: the name of the Javascript global variable, if the JSON data is to be returned
 *   as a Javascript resource (see below for explanations).
 * </ul>
 * 
 * This serializer allows for the JSON data to be sent as a Javascript resource (i.e.: the corresponding JSON may then 
 * included as a Javascript resource in a HTML page). For this to be possible, the JSON data is returned as a Javascript global
 * variable - in this case, the <code>global-var-name</code> sitemap variable must be set and correspond to the name of the Javascript
 * variable that will be sent to the browser. For example, given our address book data (see above), setting the <code>global-var-name</code>
 * parameter to <code>AddressBookData</code> would trigger the generation of the following data:
 * 
 * <pre>
 * AddressBookData = {
 *   "addressBook": {
 *     "contact": [
 *       { "firstName": "bill", "lastName": "gates" },
 *       { "firstName": "linus", "lastName": "torvalds" }       
 *     ]
 *   }
 * }
 * </pre>
 *
 * <p>
 * <b>Example Spring configuration:</b>
 * </p>
 * <pre>
 *    <bean name="org.apache.cocoon.serialization.Serializer/json"
 *       class="org.sapia.cocoon.serialization.json.JsonSerializer" 
 *       scope="prototype" />   
 * </pre>  
 * <b>Example Sitemap configuration:</b>
 * </p>
 * <pre>
 *      <map:match pattern="**.json">
 *        <map:generate src="resource/data/{1}.xml" />
 *        <map:serialize type="json/>
 *     </map:match>
 * </pre> 
 * @author yduchesne
 *
 */
public class JsonSerializer extends AbstractSerializer implements SitemapModelComponent{
  
  public static final String PARAM_GLOBAL_VAR_NAME     = "global-var-name";
  public static final String PARAM_CONTENT_TYPE        = "content-type";
  public static final String MIME_TYPE_JSON            = "application/json";
  private static final String INDENTATION              = " ";  
  
  private PrintWriter        _pw;
  private int                _level = 0;
  private Stack<JsonElement> _elements;
  private String             _globalVarName, _mimeType;
  
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws ProcessingException, SAXException, IOException {
    _globalVarName = parameters.getParameter(PARAM_GLOBAL_VAR_NAME, null);
    _mimeType      = parameters.getParameter(PARAM_CONTENT_TYPE, MIME_TYPE_JSON);
  }
  
  public void recycle() {
    _pw = null;
    _level = 0;    
    _elements = null;
    _globalVarName = null;
  }
  
  
  public String getMimeType() {
    return _mimeType;
  }
  
  public void setOutputStream(OutputStream ous) throws IOException {
    _pw = new PrintWriter(ous);
  }
  
  public void startElement(String uri, String localName, String qname, Attributes attrs) throws SAXException {
    JsonElement elem = new JsonElement(_level++, _pw, localName, attrs);
    _elements.peek().addChild(elem);
    _elements.push(elem);
  }
  
  public void endElement(String uri, String localName, String qName) throws SAXException {
    _elements.pop();
    _level--;
  }
  
  public void startDocument() throws SAXException {
    JsonElement root = new JsonElement(_pw);
    _elements = new Stack<JsonElement>();
    _elements.push(root);
    _level++;
  }
  
  public void endDocument() throws SAXException {
    JsonElement root = _elements.pop();
    if(_globalVarName != null){
      _pw.print(_globalVarName);
      _pw.print(" = ");
    }
    root.output(false);
    _pw.flush();
    _pw.close();
    _pw.println(";");
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    String s = new String(ch, start, length).trim();
    if(s.length() > 0){
      this._elements.peek().addContent(s);
    }
  }

  public boolean shouldSetContentLength() {
    return true;
  }
  static class JsonElement{
    boolean root;
    String name;
    Map<String, String> attributes;
    Map<String, List<JsonElement>> children;
    PrintWriter writer;
    int level;
    StringBuffer content;
    
    JsonElement(PrintWriter writer){
      this.root = true;
      this.writer = writer;
    }    
    
    JsonElement(int level, PrintWriter writer, String name, Attributes attrs){
      this.level = level;
      this.name = name;
      if(attrs.getLength() == 0){
        attributes = Collections.EMPTY_MAP;
      }
      else{
        attributes = new TreeMap<String, String>();
        for(int i = 0; i < attrs.getLength(); i++){
          this.attributes.put(attrs.getLocalName(i), attrs.getValue(i));
        }
      }
      this.writer = writer;
    }
    
    void addContent(String content){
      if(this.content == null){
        this.content = new StringBuffer();
      }
      this.content.append(content);
    }
    
    void addChild(JsonElement elem){
      if(children == null){
        children = new TreeMap<String, List<JsonElement>>();
      }
      List<JsonElement> childList = children.get(elem.name);
      if(childList == null){
        childList = new ArrayList<JsonElement>(3);
        children.put(elem.name, childList);
      }
      childList.add(elem);
    }
    
    void output(boolean anonymous){
      if(root){
        println("{");
        outputChildren();
        println("}");        
      }
      else{
        if(anonymous){
          level++;
        }
        indent();
        if(!anonymous){
          printQuoted(name).print(": ");
        }
        println("{");
        if(content != null){
          indent().print(INDENTATION);          
          outputContent();
          if(attributes.size() > 0 || (children != null && children.size() > 0)){
            println(",");
          }
        }
        if(attributes.size() > 0){
          indent().print(INDENTATION);          
          int attrCount = 0;
          for(String name: attributes.keySet()){
            String value = attributes.get(name);
            
            // name
            printQuoted(name).print(": ");
            
            // value
            printQuoted(value);
            
            if(attrCount < attributes.size() - 1){
              print(", ");
            }
            attrCount++;
          }
        }
        if(attributes.size() > 0){
          if(children != null && children.size() > 0){
            println(", ");            
          }
          else{
            println();            
          }
        }
        outputChildren();
        indent().println("}");
      }
    }
    
    private void outputChildren(){
      if(children != null  && children.size() > 0){
        int nameCount = 0;
        for(String name:children.keySet()){
          List<JsonElement> childList = children.get(name);
          int elemCount = 0;
          if(childList.size() > 0){
            
            if(childList.size() > 1){
              indent().print(INDENTATION).printQuoted(name).println(": [");
            }
            for(JsonElement elem:childList){
              elem.output(childList.size() > 1);
              if(elemCount < childList.size() - 1){
                indent(1).print(INDENTATION).println(", ");
              }                  
              elemCount++;
            }
            if(childList.size() > 1){
              indent().print(INDENTATION).println("]");
            }            
          }
          if(nameCount < children.size() - 1){
            indent().print(INDENTATION).println(", ");
            nameCount++;
          }
        }
      }      
    }
    
    private void outputContent(){
      if(content != null){
        printQuoted("cdata").print(": ").printQuoted(content.toString());
      }
    }
    
    private JsonElement printQuoted(String data){
      writer.print("\"");
      writer.print(data);
      writer.print("\"");      
      return this;
    }    
    
    private JsonElement print(String data){
      writer.print(data);
      return this;
    }
    
    private JsonElement println(String data){
      writer.println(data);
      return this;
    }    
    
    private JsonElement println(){
      writer.println();
      return this;
    }    
    
    private JsonElement indent(){
      for(int i = 0; i < level; i++){
        writer.print(INDENTATION);
      }
      return this;
    }
    
    private JsonElement indent(int offset){
      for(int i = 0; i < offset+level; i++){
        writer.print(INDENTATION);
      }
      return this;
    }
    
    public String toString(){
      return "[name="+name+", attributes="+attributes+", children="+children+"]";
    }
  }
}
