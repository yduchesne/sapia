package org.sapia.soto.me.util;

import javolution.text.TextBuilder;
import javolution.util.FastList;


/**
 * An instance of this class is used to parse a "template", and replace the variables
 * in the template by values that are found in a context. Values are bound in the context
 * under a given name. Variables names (in the template) correspond to the names
 * under which the values are bound in the context.
 *
 * @author JC Descrochers
 */
public class Template {

  /** Defines the default stating variable delimiter. */
  public static final String STARTING_DELIMITER = "${";

  /** Defines the default ending variable delimiter. */
  public static final String ENDING_DELIMITER = "}";

  /** The elements of this tempalte. */
  private FastList _elements;

  /**
   * Creates a new Template instance.
   */
  public Template() {
    _elements = new FastList();
  }
  
  /**
   * Adds the element to this template.
   * 
   * @param anElement The element to add.
   */
  public void addElement(TemplateElement anElement) {
    _elements.add(anElement);
  }
  
  /**
   * Renders this template using the property resolver passed in.
   * 
   * @param aResolver The property resolver.
   * @return The rendering result as a string.
   */
  public String render(PropertyResolver aResolver) {
    TextBuilder builder = new TextBuilder();
    for (FastList.Node n = (FastList.Node) _elements.head(); (n = (FastList.Node) n.getNext()) != _elements.tail(); ) {
      TemplateElement element = (TemplateElement) n.getValue();
      builder.append(element.render(aResolver));
    }
    
    return builder.toString();
  }
  
  /**
   * Parses the string content passed in a creates a template element that
   * represents it.
   *
   * @param aContent The content to parse.
   * @exception IllegalArgument If the string content passed in is null.
   */
  public static Template parse(String aContent) {
    // Validate the argument
    if (aContent == null) {
      throw new IllegalArgumentException("The content passed in is null.");
    }

    Template template = new Template();
    String buffer;
    int lastPosition = 0;
    boolean isFinished = false;

    while (!isFinished && (lastPosition < aContent.length())) {
      // Look for a variable starting delimiter
      int index = aContent.indexOf(STARTING_DELIMITER, lastPosition);

      // If no delimiter is found the content is constant
      if (index < 0) {
        buffer = aContent.substring(lastPosition);
        template.addElement(new ConstantElement(buffer));
        isFinished = true;

        // A delimiter was found
      } else {
        // If there's a content prior to the variable definition
        if (index > lastPosition) {
          // Create a constant element with the content
          buffer = aContent.substring(lastPosition, index);
          template.addElement(new ConstantElement(buffer));
        }

        // Searching for the ending variable delimiter
        lastPosition = index + STARTING_DELIMITER.length();
        index = aContent.indexOf(ENDING_DELIMITER, lastPosition);

        // If no ending delimiter is found
        if (index < 0) {
          // Creating a constant element starting with the starting delimiter
          buffer = aContent.substring(lastPosition - STARTING_DELIMITER.length());
          template.addElement(new ConstantElement(buffer));
          isFinished = true;
        
        // The variable delimeters are found but without a name
        } else if (index == lastPosition) {
          buffer = aContent.substring(lastPosition - STARTING_DELIMITER.length(), index + ENDING_DELIMITER.length());
          template.addElement(new ConstantElement(buffer));
          lastPosition = index + ENDING_DELIMITER.length();
          
        } else {
          int nextDelimiter = aContent.indexOf(STARTING_DELIMITER, lastPosition);

          // There's another starting delimiter between the two position
          if ((nextDelimiter != -1) && (index > nextDelimiter)) {
            buffer = aContent.substring(lastPosition - STARTING_DELIMITER.length(), nextDelimiter);
            template.addElement(new ConstantElement(buffer));
            lastPosition = nextDelimiter;

          // Creating a variable element with the variable name
          } else {
            buffer = aContent.substring(lastPosition, index);
            template.addElement(new VariableElement(buffer));
            lastPosition = index + ENDING_DELIMITER.length();
          }
        }
      }
    }

    return template;
  }
  
  
  /**
   * Interface that defines an element of a template 
   */
  public static interface TemplateElement {
    public String render(PropertyResolver aResolver);
  }
  
  /**
   * Implements a template element as a constant content.
   */
  public static class ConstantElement implements TemplateElement {
    private String _content;
    public ConstantElement(String aContent) {
      _content = aContent;
    }
    public String render(PropertyResolver aResolver) {
      return _content;
    }
  }

  /**
   * Implements a template element as a variable to be rendered.
   */
  public static class VariableElement implements TemplateElement {
    private String _variable;
    public VariableElement(String aVariable) {
      _variable = aVariable;
    }
    public String render(PropertyResolver aResolver) {
      String value = aResolver.getProperty(_variable);
      if (value == null) {
        return STARTING_DELIMITER+_variable+ENDING_DELIMITER;
      } else {
        return value;
      }
    }
  }
}
