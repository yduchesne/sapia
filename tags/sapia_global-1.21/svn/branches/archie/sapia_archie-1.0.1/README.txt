THIS PROJECT IS MADE PUBLIC UNDER  THE SAPIA-OSS
LICENSE. SEE LICENSE.txt THAT COMES  PACKAGED IN 
THE ROOT OF THE DISTRIBUTION OR GO TO 

http://www.sapia-oss.org/license.html 


SAPIA ARCHIE
============

Hierchical object storage framework.

SUPPORT
=======

Mailing List: sapia-utils_users@lists.sourceforge.net

Email: info@sapia-oss.org


INSTALLATION
============

1. Download

Download the distribution from SourceForge 
(http://www.sourceforge.net/projects/magnet). The download
link appears in the "Files" section; the latest "archie"
release should be downloaded.


2. Extract

Extract the distribution under a directory of your choice
on the target computer.

BUILD
=====

You must have Ant installed (see http://ant.apache.org).

In this directory, type the "ant" command, followed by the
name of the target you want to execute (example: to compile
the sources, type "ant compile".

The following targets are available:

- init

 Creates the directories (under this directory) necessary
 to build this project.

 THIS TARGET MUST BE CALLED THE FIRST TIME THIS PROJECT 
 IS BUILT.

- compile

 Compiles the sources.

- test

 Runs the unit tests.

- dist
 
 Creates the library for this project (in the ./dist
 directory).

