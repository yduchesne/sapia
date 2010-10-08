package org.sapia.util.text;


// Import of Sun's JDK classes
// ---------------------------
import java.util.ArrayList;


/**
 * An instance of this class is used to parse a "template", and replace the variables
 * in the template by values that are found in a context. Values are bound in the context
 * under a given name. Variables names (in the template) correspond to the names
 * under which the values are bound in the context.
 * <p>
 * Variables are delimited by a 'start' delimiter and an 'end' delimiter.
 * <p>
 *  Usage:
 * <pre>
 * String toRender = "your current directory: ${user.dir}";
 * TemplateFactory fac = new TemplateFactory();
 * TemplateContextIF ctx = new SystemContext();
 * TemplateElementIF template = fac.parse(toRender);
 * System.out.println(template.render(ctx));
 * </pre>
 *
 * @see org.sapia.util.text.TemplateContextIF
 * @see org.sapia.util.text.SystemContext
 *
 * @author JC Descrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TemplateFactory {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the default stating variable delimiter. */
  public static final String DEFAULT_STARTING_DELIMITER = "${";

  /** Defines the default ending variable delimiter. */
  public static final String DEFAULT_ENDING_DELIMITER = "}";

  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The starting delimiter of this template factory. */
  private String _theStartingDelimiter = DEFAULT_STARTING_DELIMITER;

  /** The ending delimiter of this template factory. */
  private String _theEndingDelimiter = DEFAULT_ENDING_DELIMITER;
  
  private boolean _throwExcMissingVar = true;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new TemplateFactory with ${ and } as start and end delimiters, respectively.
   */
  public TemplateFactory() {
  }

  /**
   * Creates a new TemplateFactory instance with the arguments passed in.
   *
   * @param aStartingDelimiter The starting variable delimiter.
   * @param anEndingDelimiter The ending variable delimiter.
   */
  public TemplateFactory(String aStartingDelimiter, String anEndingDelimiter) {
    _theStartingDelimiter   = aStartingDelimiter;
    _theEndingDelimiter     = anEndingDelimiter;
  }
  
  /**
   * @param throwEx if <code>true</code>, indicates that an exception is thrown
   * if a variable could not be resolved - defaults to true.
   */
  public void setThrowExcOnMissingVar(boolean throwEx){
    _throwExcMissingVar = throwEx;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  HELPER METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Parses the string content passed in a creates a template element that
   * represents it.
   *
   * @param aContent The content to parse.
   * @exception IllegalArgument If the string content passed in is null.
   */
  public TemplateElementIF parse(String aContent) {
    // Validate the argument
    if (aContent == null) {
      throw new IllegalArgumentException("The content passed in is null.");
    }

    String    aBuffer;
    ArrayList someElements = new ArrayList();

    int       aLastPosition = 0;
    boolean   isFinished    = false;

    while (!isFinished && (aLastPosition < aContent.length())) {
      // Look for a variable starting delimiter
      int anIndex = aContent.indexOf(_theStartingDelimiter, aLastPosition);

      // If no delimiter is found the content is constant
      if (anIndex < 0) {
        aBuffer = aContent.substring(aLastPosition);
        someElements.add(new ConstantElement(aBuffer));
        isFinished = true;

        // A delimiter was found
      } else {
        // If there's a content prior to the variable definition
        if (anIndex > aLastPosition) {
          // Create a constant element with the content
          aBuffer = aContent.substring(aLastPosition, anIndex);
          someElements.add(new ConstantElement(aBuffer));
        }

        // Searching for the ending variable delimiter
        aLastPosition   = anIndex + _theStartingDelimiter.length();
        anIndex         = aContent.indexOf(_theEndingDelimiter, aLastPosition);

        // If no ending delimiter is found
        if (anIndex < 0) {
          // Creating a constant element starting with the starting delimiter
          aBuffer = aContent.substring(aLastPosition -
              _theStartingDelimiter.length());
          someElements.add(new ConstantElement(aBuffer));
          isFinished = true;
        }
        // The variable delimeters are found but without a name
        else if (anIndex == aLastPosition) {
          aBuffer = aContent.substring(aLastPosition -
              _theStartingDelimiter.length(),
              anIndex + _theEndingDelimiter.length());
          someElements.add(new ConstantElement(aBuffer));
          aLastPosition = anIndex + _theEndingDelimiter.length();
        }
        else {
          int aNextDelimiter = aContent.indexOf(_theStartingDelimiter,
              aLastPosition);

          // There's another starting delimiter between the two position
          if ((aNextDelimiter != -1) && (anIndex > aNextDelimiter)) {
            aBuffer = aContent.substring(aLastPosition -
                _theStartingDelimiter.length(), aNextDelimiter);
            someElements.add(new ConstantElement(aBuffer));
            aLastPosition = aNextDelimiter;
          }
          // Creating a variable element with the variable name
          else {
            aBuffer = aContent.substring(aLastPosition, anIndex);
            someElements.add(new VariableElement(aBuffer, _throwExcMissingVar));
            aLastPosition = anIndex + _theEndingDelimiter.length();
          }
        }
      }
    }

    return new CompositeElement(someElements);
  }
}
