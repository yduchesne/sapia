package org.sapia.soto.state.markup;

import java.io.PrintWriter;
import java.util.List;

import org.sapia.soto.Settings;
import org.sapia.soto.state.Context;

/**
 * An instance of this class serializes markup data to the context's output. It
 * expects the passed in {@link Context} instance to implement the {@link org.sapia.soto.state.Output}
 * interface.
 * 
 * 
 * @author yduchesne
 *
 */
public class DefaultSerializer extends AbstractSerializer{
  
  boolean outputNamespace, xmlElements;
  
  public DefaultSerializer(Settings settings){
    super(settings);
    outputNamespace = MarkupSerializerFactory.isOutputNamespace(settings, false);
    xmlElements = MarkupSerializerFactory.isEncodingElements(settings, true);
  }

  public void start(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    pw.print("<");
    if(info.isNamespaceEnabled() && outputNamespace){
      pw.print(info.getPrefix());
      pw.print(":");
    }
    pw.print(info.getName());
    if(info.isNamespaceEnabled() && outputNamespace){
      pw.print(" xmlns:");
      pw.print(info.getPrefix());
      pw.print("=\"");
      pw.print(info.getUri());
      pw.print("\"");
    }
    List attributes = info.getAttributes();
    outputAttributes(pw, attributes);
 
    pw.print(">");
    
    if(xmlElements){
      outputElements(pw, attributes);      
    }
    pw.flush();
  }
  
  public void text(String txt, Context ctx, PrintWriter pw) throws Exception {
    if(txt != null){
      pw.print(txt);
      pw.flush();
    }
  }
  
  public void end(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    pw.print("</");
    if(info.isNamespaceEnabled() && outputNamespace){
      pw.print(info.getPrefix());
      pw.print(":");
    }
    pw.print(info.getName());
    pw.print(">");
    pw.flush();
  }
  
  private void outputElements(PrintWriter pw, List attributes){
    for(int i = 0; i < attributes.size(); i++){
      MarkupInfo.Attribute attr = (MarkupInfo.Attribute)attributes.get(i);
      if(attr.getValue() != null && !attr.encodeAsAttribute){
        pw.print("<");
        pw.print(attr.getName());
        pw.print(">");
        pw.print(attr.getValue());
        pw.print("</");
        pw.print(attr.getName());
        pw.print(">");
      }
    }
  }
  
  private void outputAttributes(PrintWriter pw, List attrs){
    for(int i = 0; i < attrs.size(); i++){
      MarkupInfo.Attribute attr = (MarkupInfo.Attribute)attrs.get(i);
      if(attr.encodeAsAttribute || !xmlElements){
        if(attr.getValue() != null){
          pw.print(" ");
          pw.print(attr.getName());
          pw.print("=\"");
          pw.print(attr.getValue());
          pw.print("\"");
        }
      }
    }
  }
}
