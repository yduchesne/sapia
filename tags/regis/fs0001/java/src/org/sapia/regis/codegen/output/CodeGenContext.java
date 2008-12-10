package org.sapia.regis.codegen.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.sapia.regis.Node;
import org.sapia.regis.Path;

class CodeGenContext {

  private CodeGenConfig config;
  
  private Node node;
  
  private Path packagePath;

  private String className;

  private boolean isInterface;

  private Hints hints;

  CodeGenContext(CodeGenConfig config, Node node) {
    this.config = config;
    this.node = node;
  }

  Path getPackagePath() {
    return packagePath;
  }

  void setPackagePath(Path packagePath) {
    this.packagePath = packagePath;
  }

  String getClassName() {
    return className;
  }

  String getFullyQualifiedClassName() {
    String packageName = packagePath.toString('.');
    if (packageName.length() == 0) {
      return className;
    } else {
      return packageName + "." + className;
    }
  }

  void setClassName(String className) {
    this.className = className;
  }

  boolean isInterface() {
    return isInterface;
  }

  void setInterface(boolean isInterface) {
    this.isInterface = isInterface;
  }

  Node getNode() {
    return node;
  }

  void setNode(Node node) {
    this.node = node;
  }
  
  CodeGenConfig getConfig(){
    return config;
  }

  PrintWriter createWriter(File destDir) throws IOException {
    return createWriter(destDir, getClassName());
  }
  
  PrintWriter createWriter(File destDir, String className) throws IOException{
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

    File outfile = new File(outdir, className + ".java");
    return new PrintWriter(new FileOutputStream(outfile), true);
  }

  Hints getHints() {
    if (hints == null)
      hints = new Hints();
    return hints;
  }

  void setHints(Hints hints) {
    this.hints = hints;
  }
}
