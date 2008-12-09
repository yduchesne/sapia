package org.sapia.regis.codegen.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.sapia.regis.Node;
import org.sapia.regis.Path;

public class CodeGenContext {

  private Path packagePath;

  private String className;

  private boolean isInterface;

  private Node node;

  private Hints hints;

  public CodeGenContext(Node node) {
    this.node = node;
  }

  public Path getPackagePath() {
    return packagePath;
  }

  public void setPackagePath(Path packagePath) {
    this.packagePath = packagePath;
  }

  public String getClassName() {
    return className;
  }

  public String getFullyQualifiedClassName() {
    String packageName = packagePath.toString('.');
    if (packageName.length() == 0) {
      return className;
    } else {
      return packageName + "." + className;
    }
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public void setInterface(boolean isInterface) {
    this.isInterface = isInterface;
  }

  public Node getNode() {
    return node;
  }

  public void setNode(Node node) {
    this.node = node;
  }

  public PrintWriter createWriter(File destDir) throws IOException {
    String packagePath = this.getPackagePath().toString(File.separatorChar);
    File outdir = null;
    if (packagePath != null && packagePath.length() > 0) {
      outdir = new File(destDir + File.separator + packagePath);
      outdir.mkdirs();
    } else {
      outdir = destDir;
      outdir.mkdirs();
    }
    if (!outdir.exists()) {
      throw new IllegalStateException("Could not make directory: "
          + outdir.getAbsolutePath());
    }

    File outfile = new File(outdir, getClassName() + ".java");

    System.out.println("Generating into: " + outfile.getAbsolutePath());
    return new PrintWriter(new FileOutputStream(outfile), true);

  }

  public Hints getHints() {
    if (hints == null)
      hints = new Hints();
    return hints;
  }

  public void setHints(Hints hints) {
    this.hints = hints;
  }
}
