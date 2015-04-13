# Introduction #

This page will guide you through the necessary steps to install Corus on Fedora Core 13 (64 bits).


# 1. Preparing the environment #

## i) Java ##
  * Before installing Corus you need to have Java installed on your machine. Corus requires at least Java 6.
  * Corus requires the presence of the  `$JAVA_HOME` environment variable that points to the Java runtime you want to use (JRE or JDK).

## ii) Corus user ##
  * Since Corus is mainly a background process, we strongly suggest that the Corus server be installed and executed with the privileges of a restricted user.
  * Using the command `useradd`, created a new system user named "corus". Here's a quick example of it:

> |` [root@cygnus-fedora ~]# useradd --home-dir /home/corus --create-home --system --shell /bin/bash corus `<br />` [root@cygnus-fedora ~]#`|
|:----------------------------------------------------------------------------------------------------------------------------------------|

## iii) Installation directory ##
  * You need to create a directory on your system in which you will install Corus.
  * For the purpose of this demonstration, we create the `/opt/sapia-corus` directory.
> |` [root@cygnus-fedora ~]# mkdir /opt/sapia-corus`<br />` [root@cygnus-fedora ~]#`|
|:--------------------------------------------------------------------------------|


# 2. Extract the Corus distribution #
  * Extract the Corus distribution file into the Corus installation directory created above.
> |` [root@cygnus-fedora sapia-corus]# tar -xvf ../datastore/sapia_corus_server-2.0-linux64.tar.gz `<br />` [root@cygnus-fedora sapia-corus]#`|
|:------------------------------------------------------------------------------------------------------------------------------------------|

  * Rename the created root directory of the archive if you need.
  * For example, we extracted the tar and the files reside under the directory `/opt/sapia-corus/sapia_corus-2.0`
  * Make sure that the files of the distribution are own by the corus user. Here's an example using the `chown` command:
> |` [root@cygnus-fedora sapia-corus]# chown -R corus:corus sapia_corus-2.0/ `<br />` [root@cygnus-fedora sapia-corus]#`|
|:--------------------------------------------------------------------------------------------------------------------|


# 3. Configure the Environment #
  * To run properly, Corus requires the presence of another environment variable: CORUS\_HOME
  * The variable must point to the home directory of the Corus installation, which in our case is the `/opt/sapia-corus/sapia_corus-2.0` directory
  * However, we suggest the usage of a `current` symbolic link under `/opt/sapia-corus` to facilitate the future upgrades of Corus. That we if you ever extract another distribution of Corus you will simply have to change the sybolic link and upon restart the new distribution will start.
  * Here an example of how to create a symbolic link using the `ln` command (make sure you are in the install direcotry):

> | `[root@cygnus-fedora sapia-corus]# pwd`<br />`/opt/sapia-corus`<br /><br />`[root@cygnus-fedora sapia-corus]# ln -s sapia_corus-2.0/ current`<br /><br />`[root@cygnus-fedora sapia-corus]# ls -latr`<br />`total 12`<br />`drwxr-xr-x. 5 root   root      4096 Nov 24 22:29 ..`<br />`drwxr-xr-x. 6 corus corus 4096 Nov 26 06:15 sapia_corus-2.0`<br />`lrwxrwxrwx. 1 root   root        16 Nov 26 06:54 current -> sapia_corus-2.0/`<br />`drwxr-xr-x. 3 root   root      4096 Nov 26 06:54 .`<br />`[root@cygnus-fedora sapia-corus]#` |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

  * To define an environment variable on Fedora Core, we suggest to modify the file `/etc/profile` which contains the global environment variables setup for every user.
  * Using `vi` (or the file editor of your choice) add the following lines to the file:
> |` CORUS_HOME=/opt/sapia-corus/current `<br />` export CORUS_HOME `|
|:-----------------------------------------------------------------|

  * You can also modify the `$PATH` environment variable to have an easy access to the various commands of the Corus distribution
  * To do so, edit the `/etc/profile` file again and add the following lines to it:
> | ` PATH=$PATH:$CORUS_HOME/bin`<br />` export PATH ` |
|:---------------------------------------------------|

At this point you should be able to start Corus by manually invoking the `$CORUS_HOME\bin\corus.bat` file. However performing this task manually is cumbersome. The next section will guide you through the steps to register Corus as a service with the Linux run levels.


# 4. Linux System Service Integration #
With the installation of Corus as a Linux system service (in the run levels), you will be able to automatically start Corus on startup of your machine. Fedora provides the `chkconfig` command that help configuring Linux's run levels. Once properly registered you can easily stop, start and restart Corus service using the `service` command.

To register Corus as a system service, you need to:
## i) Define a Corus configuration file ##
  * you need to define a Corus configuration file that will be used when the service starts.
  * We provide a default configuration file to help you: it is located under `$CORUS_HOME/config/corus_service_33000.wrapper.properties`. You can have a look at the configuration section to know how to customize Corus.

## ii) Prepare the init.d script ##
  * A script is available to register Corus as a system service: it located under `$CORUS_HOME/bin/corus.init.d`
  * Copy it under the directory `/etc/init.d` and name it `corus` (make sure you are using root user)
> |` [root@cygnus-fedora sapia-corus]# cp $CORUS_HOME/current/bin/corus.init.d /etc/init.d/corus `<br />` [root@cygnus-fedora sapia-corus]#`|
|:----------------------------------------------------------------------------------------------------------------------------------------|

  * Once copied, you need to edit the file and set the proper value for the $CORUS\_HOME variable and set the proper corus configuration file.
  * By default, the file is configured to use the `$CORUS_HOME/config/corus_service_33000.wrapper.properties` file (so you can have a try right now).

## iii) Register with the run levels ##
  * Once you are ready, you simply have to execute the `chkconfig` command (as root):

> | ` [root@cygnus-fedora init.d]# chkconfig --add /etc/init.d/corus `<br />` [root@cygnus-fedora init.d]#` |
|:--------------------------------------------------------------------------------------------------------|

  * You can validate the run level registration like that:

> |` [root@cygnus-fedora init.d]# chkconfig --list | grep corus `<br />` corus          	0:off	1:off	2:on	3:on	4:on	5:on	6:off  `<br />` [root@cygnus-fedora init.d]#  ` |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------|

  * As you can see upon installation, the Corus service is not running. You can start it using the `service` command like that:

> | `[root@cygnus-fedora init.d]# service corus start ` <br /> `Starting Sapia Corus Server [33000]...` <br /> `[root@cygnus-fedora init.d]# ` |
|:-------------------------------------------------------------------------------------------------------------------------------------------|


Once you start the Corus service, you can validate the proper execution of the Corus service by:

1. looking at the Corus log files that resides under the `$CORUS_HOME/logs` directory

2. starting the Corus CLI (Command Line Interface) to connect to the server. The script is called `coruscli` and resides under the `$CORUS_HOME/bin` directory. If you previously added the `$CORUS_HOME/bin` directory in the `$PATH` environment variable, you can simply invoke the script. Otherwise you will have to get to the bin directory and invoke the command.

<table width='50%'>
<tr><td>
<a href='http://sapia.googlecode.com/svn/wiki/resources/ScreenShot_LinuxFedora_CorusCLI.jpg'><img width='100%' src='http://sapia.googlecode.com/svn/wiki/resources/ScreenShot_LinuxFedora_CorusCLI.jpg' /></a>
</td></tr>
<tr align='center'><td><i>Click on the image for full size resolution</i></td></tr>
</table>

And that is pretty much it for the installation. You can now envoy Corus.

## iv) Manage the Corus System Service ##
  * Using the `service` command you can start, stop and restart the Corus service

| `[root@cygnus-fedora init.d]# service corus restart` <br /> `Stopping Sapia Corus Server [33000]...` <br />  `Stopped Sapia Corus Server [33000].` <br /> `Starting Sapia Corus Server [33000]...` <br /> `[root@cygnus-fedora init.d]# ` <br /><br /> `[root@cygnus-fedora init.d]# service corus stop` <br /> `Stopping Sapia Corus Server [33000]...` <br /> `Stopped Sapia Corus Server [33000].` <br /> `[root@cygnus-fedora init.d]# ` |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

  * If you ever need to remove the Corus system service, you can use the `chkconfig` config command

| ` [root@cygnus-fedora init.d]# chkconfig --del corus `<br />` [root@cygnus-fedora init.d]#` |
|:--------------------------------------------------------------------------------------------|

