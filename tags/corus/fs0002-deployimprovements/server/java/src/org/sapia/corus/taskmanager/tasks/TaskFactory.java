package org.sapia.corus.taskmanager.tasks;

import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;

import org.sapia.corus.taskmanager.AntTaskHelper;
import org.sapia.corus.taskmanager.v2.TaskV2;

import java.io.File;


/**
 * @author Yanick Duchesne
 */
public class TaskFactory {

  public static TaskV2 newUnjarTask(File srcJar, File destDir) {
    Expand unzip = new Expand();
    unzip.setSrc(srcJar);
    unzip.setDest(destDir);

    return AntTaskHelper.init(unzip);
  }

  public static TaskV2 newDeleteFileTask(File toDelete) {
    Delete del = new Delete();
    del.setFile(toDelete);

    return AntTaskHelper.init(del);
  }

  public static TaskV2 newDeleteDirTask(File toDelete) {
    Delete del = new Delete();
    del.setDir(toDelete);
    del.setIncludeEmptyDirs(true);

    return AntTaskHelper.init(del);
  }

}
