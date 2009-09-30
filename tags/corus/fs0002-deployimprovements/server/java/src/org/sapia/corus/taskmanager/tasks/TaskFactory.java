package org.sapia.corus.taskmanager.tasks;

import java.io.File;

import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.sapia.corus.taskmanager.AntTaskHelper;
import org.sapia.corus.taskmanager.core.Task;


/**
 * @author Yanick Duchesne
 */
public class TaskFactory {

  public static Task newUnjarTask(File srcJar, File destDir) {
    Expand unzip = new Expand();
    unzip.setSrc(srcJar);
    unzip.setDest(destDir);

    return AntTaskHelper.init(unzip);
  }

  public static Task newDeleteFileTask(File toDelete) {
    Delete del = new Delete();
    del.setFile(toDelete);

    return AntTaskHelper.init(del);
  }

  public static Task newDeleteDirTask(File toDelete) {
    Delete del = new Delete();
    del.setDir(toDelete);
    del.setIncludeEmptyDirs(true);

    return AntTaskHelper.init(del);
  }

}
