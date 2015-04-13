Welcome to Sapia, yet another open-source project. Sapia is not a single project per-say; it is a virtual open-source organization that hosts different projects, each solving a specific problem, as elegantly as possible, in order to provide easy-to use, well-documented software to the developer community.

Web Site: [Sapia OSS dot Org](http://www.sapia-oss.org)

# Download from Mediafire #

Since Google does not allow uploading artifacts anymore, we're making **Corus distributions available on [Mediafire](https://www.mediafire.com/folder/v55q0gaqvvbda/corus)**.



# News #
  * 2015-01-22: **Corus 4.5.1 Released**
    * Bug fixes related to empty value properties and retry attempts for killing processes

  * 2015-01-18: Corus 4.5 Released
    * Rest API
    * Authentication / Authorization (for write/execute/deploy operations on REST API)
      * Role management
      * Application key management
      * SSL support
    * Dynamic domain change (CLI: cluster domain new\_domain)
    * Dynamic repository role change (CLI: cluster repo new\_role)
    * New 'alias' command
    * Added support for property categories (conf add -p ... -c my\_category)
    * Improved variable interpolation
      * Added support for using env. variables in corus.xml
      * Process properties kept in Corus (conf add -p...) that contain variables will be interpolated prior to JVM launch.
      * Added 'conf load' command: loads properties kept on the Corus server into the CLI, for variable interpolation on the client side (this means properties kept in Corus can be used for variable interpolation within client-side scripts. See the 'script', 'match' and 'ripple' commands).
    * 'undeploy' command now supports up to a certain number of distributions that should be kept as backups - in case of rollback (CLI example: undeploy all -backup 1).
    * Added support for ".profile.corus" that can optionally be stored under HOME/.corus. This allows adding commands to be executed at each CLI startup.
    * Bug Fixes
      * Switched back process stdout and stderr redirection to non-rolling file implementation (to workaround Tomcat-related glitch that leaves rolling files empty)
  * 2015-01-08: Corus 4.4.3 Released
    * Fixed issue related to SVN to Git migration
  * 2015-01-07: Corus 4.4.2 Released
    * Fixed issue with Java 8 integration
  * 2014-11-16: Corus 4.3
    * Improved clustering protocol
    * Added documentation for ‘port release’ (command existed, but was undocumented)
    * Removed dependency on JDBM library (used for Corus DB) given observed glitches with persistence (JDBM is a +10 years-old library which had not been updated since). Switched to MapDB.
    * Added automatic pull from repo clients when reconnection to Avis is detected (scenario: if Avis is down when a repo client is started, the client will sync with repo servers when Avis comes back up).
  * 2014-10-21: Corus 4.3 Released
    * Added 'match' command to Corus CLI
  * 2014-09-09: Corus 4.2.4 Released
    * Fixed bug: sym links would not be correctly displayed under Windows in Corus console's file system view
  * 2014-09-05: Corus 4.2.3 Released
    * Added support for resolving environment variables in corus.xml
    * Now passing corus.server.host.name system property to launched processes
  * 2014-08-14: Corus 4.2.2 Released
    * Fixed bug on exec -e -cluster where -cluster option would not be recognized
    * Fixed [Issue #17](https://code.google.com/p/sapia/issues/detail?id=#17) (single host info display through 'host' command)
  * 2014-08-05: Corus 4.2.1 Released
    * Fixed option validation for exec command ([Issue #18](https://code.google.com/p/sapia/issues/detail?id=#18))
    * Fixed bug where process would remain in 'restarting' queue forever upon an error occurring at process restart.
    * Added ps -clean command.
  * 2014-08-03: Corus 4.2 Released
    * Added support for setting display width (example: coruscli -w 150). Auto-detection is attempted and display takes whole width of terminal by default ([Issue #14](https://code.google.com/p/sapia/issues/detail?id=#14)).
    * Added output sorting (introduced 'sort' command, which allows specifying the order in which the other commands present their info - ls, ps, hosts, etc.)
    * Added command-line option validation ([Issue #16](https://code.google.com/p/sapia/issues/detail?id=#16))
    * Improved usage under Windows: full Win 64-bit support added. Now uses SIGAR to interact with processes natively.
    * Misc bug fixes:
      * [Issue #9](https://code.google.com/p/sapia/issues/detail?id=#9), [Issue #13](https://code.google.com/p/sapia/issues/detail?id=#13): added timestamps in stdout logs.
      * [Issue #11](https://code.google.com/p/sapia/issues/detail?id=#11): Connection to Corus can now be made by specifying host name (would only support IP address).
      * [Issue #12](https://code.google.com/p/sapia/issues/detail?id=#12): fixed config pull (additional Ubik-related properties would automatically be added, which proved more confusing than helpful).
  * 2014-02-01: Corus 4.1.2 Released.
    * Added preExec element in Corus descriptor: allows executing Corus CLI commands at Corus instance prior to process being executed.
    * Added "conf merge" command in CLI.
    * Added support for connection retry when Avis router is down (either at Corus startup or after).
    * Fixed concurrency bug in Purgatory (Ubik framework).
  * 2013-11-20: Corus 4.1.1 Released.
    * Fixed bug in "ripple" command.
    * -c option in Corus CLI now properly handles embedded options.
  * 2013-10-27: Corus 4.1 Released.
    * Added support for "ripple" command: allows targeted clustered command execution over N cluster nodes at a time. This means controlled degradation when redeploying over cluster nodes in production.
    * Cloud integration: "user-data" support. Allows passing in startup configuration (properties, tags) to instances in the Cloud.
    * Misc impprovements:
      * 'http check' command (for checking http endpoint status as part of deployment).
      * 'conf ls' command now supports wildcards.
      * 'resync' command for forcing cluster resync.
  * 2013-05-26: Corus 4.0 Released.
    * Repository functionality: Some Corus nodes act as repository servers, while others act as repository clients: upon startup, repo clients automatically synchronize their state with repo servers in their domain.
    * Added support for deployment and execution shell scripts on Corus nodes.
    * Added support for deployment of arbitrary files (making Corus nodes act as file repository).
    * Fixed bug in resume command of Corus command-line interface.
  * 2012-09-27: Corus 3.0 Released.
  * 2012-05-15: Corus 3.0 RC1 Released. Brings in support for email alerts, property includes, built upon Sapia's Ubik 3.0.
  * 2011-10-18: New Corus release 2.1.3, with Tomcat integration and fix for using Corus with multiple NIC
  * 2011-03-22: New Magnet release 2.1.1 to fix packaging issues
  * 2011-01-30: New Sapia OSS web site, featuring new [Corus](http://www.sapia-oss.org/projects/corus/index.html) 2.0 release
  * 2009-10-21: New Corus release (1.3.6)
  * 2008-12-11: Added [Guice](http://code.google.com/p/google-guice/) support in [Ubik](Ubik.md)

# Links #

## General ##
  * [Wiki Home](Home.md): find out more about the different projects, configuration and setup issues (such as building with Maven), etc.

## Projects ##

  * [Ubik](Ubik.md)
  * [Corus](CorusHome.md)
  * [Magnet](Magnet.md)