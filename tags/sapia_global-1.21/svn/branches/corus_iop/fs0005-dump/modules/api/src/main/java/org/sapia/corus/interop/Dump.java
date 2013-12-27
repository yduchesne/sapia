package org.sapia.corus.interop;

/**
 * Defines a dump command.
 * 
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">
 *     Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *     <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a>
 *     at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Dump extends AbstractCommand {
  
  private static final long serialVersionUID = 1L;
  
  public enum DumpType {
    THREAD,
    HEAP
  }
  
  /** Defines the type of dump requested. */
  private DumpType type;
  
  /** The output file where to write the dump. */ 
  private String outputFile;

  /**
   * Creates a new {@link Dump} instance.
   */
  public Dump() {
  }

  /**
   * Returns the dump type.
   * 
   * @return The dump type
   */
  public DumpType getType() {
    return type;
  }

  /**
   * Sets the dump type.
   * 
   * @param aType The new {@link DumpType} value.
   */
  public void setType(DumpType aType) {
    type = aType;
  }
  
  /**
   * Returns the output file name.
   * 
   * @return The output file name.
   */
  public String getOutputFile() {
    return outputFile;
  }

  /**
   * Sets the dump output file name.
   * 
   * @param anOutputFile The new output file value.
   */
  public void setOutputFile(String anOutputFile) {
    outputFile = anOutputFile;
  }

  /**
   * Returns a string representation of this shutdown command.
   *
   * @return A string representation of this shutdown command.
   */
  public String toString() {
    StringBuffer aBuffer = new StringBuffer(super.toString());
    aBuffer.append("[type=").append(type).
            append(" outputFile=").append(outputFile).
            append("]");

    return aBuffer.toString();
  }
  
}
