# Introduction #

This wiki section provides detailed information on how to install Corus, on all the
major OSes (Linux, Solaris, Mac, Windows).

**IMPORTANT - READ THE FOLLOWING**

  * Since the Google Code upload service has been discontinued, the latest Corus package can be downloaded from [Mediafire](https://www.mediafire.com/folder/v55q0gaqvvbda/corus).
  * The Corus installation based on the Java Service Wrapper does not work in JDK7 due to an [obscure bug](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7181721) under Linux which remains to be fixed. Use the plain-vanilla init.d-based installation as a workaround (explained in the [generic installation procedure](CorusGenericInstallationProcedure.md)).

# Supported Operating Systems #

Corus should run on any platform that supports Java. That being said, a native hook is required to be able to run Corus as an OS process or service (so that it starts automatically at OS boot time), and native scripts are required to start the various tools (the command-line interface, etc.).

The preferred way is to run Corus as a service using the Java Service Wrapper. If you do not want to use the Java Service Wrapper, we have [a procedure using a generic init.d script](CorusGenericInstallationProcedure.md).

The links below will point you to the JSW-based installation for different OSes.


| **Windows** <br /><p><ul><li><a href='CorusInstallationProcedure_WindowsXP.md'>Installation instructions for Windows XP</a></li><li><a href='CorusInstallationProcedure_WindowsVista.md'>Installation instructions for Windows Vista</a></li><li> Installation instructions for Windows 7 (to come)</li></ul></p><br />| **Mac** <br /><p><ul><li><a href='CorusInstallationProcedure_Leopard.md'>Installation instructions for OS X 10.5</a> and below (<i>with <code>launchd</code> integration</i>)</li><li><a href='CorusInstallationProcedure_SnowLeopard.md'>Installation instructions for OS X 10.6</a> (<i>with <code>launchd</code> integration</i>)</li></ul></p><br /><br />|
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Linux**<br /><p><ul><li><a href='CorusInstallationProcedure_Ubuntu.md'>Installation instructions for Ubuntu</a> (<i>with <code>upstart</code> integration</i>)  </li><li><a href='CorusInstallationProcedure_Fedora.md'>Installation instructions for Fedora</a>  (<i>with <code>init.d</code> integration</i>) </li><li><a href='CorusInstallationProcedure_Mandriva.md'>Installation instructions for Mandriva</a> (<i>with <code>init.d</code> integration</i>) </li><li><a href='CorusInstallationProcedure_OpenSUSE.md'>Installation instructions for OpenSUSE</a>  (<i>with <code>init.d</code> integration</i>) </li><li>Installation instructions for CentOS  (<i>with <code>init.d</code> integration</i>) </li></ul></p><br />| **Solaris** <br /><p><ul><li><a href='CorusInstallationProcedure_openSolaris.md'>Installation instructions for OpenSolaris</a> (<i>with SMF integration</i>)</li></ul></p><br /><br /><br /><br /><br />|


Contact us if your OS is not supported, we'll do the best we can.