# Introduction #

Typically, you'll "interact" with Sapia projects to:

  1. Depend on releases of specific libraries
  1. Build from source
  1. Publish new releases (this requires developer access)

1) and 2) are the typical use cases (they only require read access to the Maven repository where the Sapia libs are published).

3) Is a bit more involving: it requires an account at Bintray.

**Note**: You can find more details on the [Bintray](https://bintray.com/) web site.

# 1) and 2) Using Maven to acquire dependencies #

If you want to include Sapia libraries as dependencies in your projects, you must configure the Java.net Maven 2 repository - either in your projects' POM, or in your `settings.xml` file:

```
<profiles>
  <profile> 
    <id>sapia</id> 
    <activation> 
      <activeByDefault>true</activeByDefault>  
    </activation>
    <repositories>
      <repository>
        <id>bintray-sapia</id>
        <url>http://dl.bintray.com/sapiaoss/main</url>
        <releases>
          <enabled>true</enabled>
        </releases>
        <snapshots>
          <enabled>false</enabled>
        </snapshots>
      </repository>
    </repositories> 
  </profile>
</profiles>

<activeProfiles>
  <activeProfile>sapia</activeProfile>
</activeProfiles>
```

If you are building the Sapia projects from source, these already include the above information in their POM, so you may just check out and do a `mvn install`.

Also, note that you can add the sapia profile to the `activeProfiles` element, so that the Bintray repo is always searched.

# 3) Publishing releases #

To publish releases, you'll need an account at Bintray, in order to upload the releases to their Maven repository. You have to configure your Bintray username and API key as part of your Maven settings: in your `settings.xml` file, add the following within the `servers` element:

```
  <server>
    <id>bintray-sapia</id>
    <username>johnsmith</username>    
    <password>your-bintray-api-key</password>
  </server>
```