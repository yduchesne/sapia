package org.sapia.corus.interop.client;

import java.io.File;

/**
 * This class inherits from {@link BaseFileLogOutput}: it uses <code>stdout</code>
 * as the log file name:
 * 
 * <pre>
 * stdout.log
 * </pre>
 * 
 * The configuration of this instance can be specified through system properties
 * - see the javadoc further below for more information.
 * 
 * @author yduchesne
 * 
 */
class StdoutFileLogOutput extends BaseFileLogOutput {

  /**
   * Corresponds to the number of archived files that are kept (
   * <code>corus.iop.stdout.log.file.max-archive</code>). Defaults to 10.
   */
  public static final String PROP_MAX_ARCHIVE = "corus.iop.stdout.log.file.max-archive";

  
  /**
   * Corresponds to the maximum file size (in megabytes) before the current log
   * file is archived. (<code>corus.iop.stdout.log.file.size</code>). Defaults to 3
   * megabytes.
   */
  public static final String PROP_FILE_SIZE = "corus.iop.stdout.log.file.size";

  /**
   * @param logDir corresponds to the directory in which to create log files.
   */
  StdoutFileLogOutput(File logDir) {
    super(new Config().setMaxArchive(BaseFileLogOutput.getIntProperty(System.getProperties(), PROP_MAX_ARCHIVE, Config.DEFAULT_MAX_ARCHIVE))
        .setLogDirectory(logDir)
        .setLogFileName("stdout")
        .setMaxFileSize(BaseFileLogOutput.getIntProperty(System.getProperties(), PROP_FILE_SIZE, Config.DEFAULT_MAX_FILE_SIZE)));
  }

}
