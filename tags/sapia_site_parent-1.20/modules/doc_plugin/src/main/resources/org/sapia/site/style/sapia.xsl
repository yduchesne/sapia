<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sapia="http://www.sapia-oss.org/2003/XSL/Transform">


  <xsl:output method="html" indent="yes" encoding="US-ASCII" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

  <xsl:param name="project.name" required="yes" as="xs:string"/>
  <xsl:param name="project.description" required="yes" as="xs:string"/>
  <xsl:param name="project.version" required="yes" as="xs:string"/>
  <xsl:param name="project.groupId" required="yes" as="xs:string"/>
  <xsl:param name="project.artifactId" required="yes" as="xs:string"/>
  <xsl:param name="project.inceptionYear" required="yes" as="xs:string"/>
  <xsl:param name="project.packaging" required="yes" as="xs:string"/>
  <xsl:param name="project.url" required="yes" as="xs:string"/>
  <xsl:param name="project.organization.name" required="yes" as="xs:string"/>
  <xsl:param name="project.organization.url" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist.name" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist.subscribe" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist.unsubscribe" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist.post" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist.archive" required="yes" as="xs:string"/>
  <xsl:param name="project.scm.connection" required="yes" as="xs:string"/>
  <xsl:param name="project.scm.developerConnection" required="yes" as="xs:string"/>
  <xsl:param name="project.scm.url" required="yes" as="xs:string"/>
  <xsl:param name="project.scm.tag" required="yes" as="xs:string"/>
  <xsl:param name="project.build.directory" required="yes" as="xs:string"/>
  <xsl:param name="project.build.outputDirectory" required="yes" as="xs:string"/>
  <xsl:param name="project.build.finalName" required="yes" as="xs:string"/>
  <xsl:param name="project.mailinglist" required="yes" as="xs:string"/>
  <xsl:param name="build.currentYear" required="yes" as="xs:string"/>
  <xsl:param name="build.currentDate" required="yes" as="xs:string"/>
  <xsl:param name="build.currentTime" required="yes" as="xs:string"/>
  <xsl:param name="build.timestamp" required="yes" as="xs:string"/>
  <xsl:param name="build.username" required="yes" as="xs:string"/>


<!-- ========================================= PAGE ========================================= -->

  <xsl:template match="/sapia:page">
    <html>
      <head>
        <link rel="stylesheet" type="text/css">
          <xsl:choose>
            <xsl:when test="@cssPath">
              <xsl:attribute name="href">
                <xsl:value-of select="@cssPath"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href">
                <xsl:text>css/sapia.css</xsl:text>
              </xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </link>

        <xsl:if test="head">
          <xsl:apply-templates select="head/*" />
        </xsl:if>

        <xsl:choose>
          <xsl:when test="@title">
            <title>
              <xsl:value-of select="@title"/>
            </title>
          </xsl:when>
          <xsl:when test="title">
            <xsl:apply-templates select="title" />
          </xsl:when>
          <xsl:otherwise>
            <title>Sapia Open Source</title>
          </xsl:otherwise>
        </xsl:choose>


        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>

        <script type="text/javascript">
        
          var _gaq = _gaq || [];
          _gaq.push(['_setAccount', 'UA-20490461-1']);
          _gaq.push(['_trackPageview']);
        
          (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
          })();
        
        </script>
        
      </head>
      <body>

        <div id="header">
          <div id="logo">
            <span id="logo-title">s a p i a</span>
          </div>
          <div id="menu">
            <ul>
              <li>
                <a href="http://www.sapia-oss.org/index.html" title="">Home</a>
              </li>
              <li>
                <a href="http://www.sapia-oss.org/projects.html" title="">Projects</a>
              </li>
              <li>
                <a href="http://www.sapia-oss.org/about.html" title="">About Us</a>
              </li>
              <li>
                <a href="http://www.sapia-oss.org/contact.html" title="">Contact</a>
              </li>
            </ul>
          </div>
        </div>
        <hr />

        <div id="wrapper">
          <div id="page">

            <xsl:choose>
              <xsl:when test="sapia:vmenu">
                <div id="content">
                  <xsl:apply-templates select="sapia:sect1"/>
                </div>
                <xsl:apply-templates select="sapia:vmenu"/>
              </xsl:when>
              <xsl:otherwise>
                <div id="content" style="width: 754px">
                  <xsl:apply-templates select="sapia:sect1"/>
                </div>
                <div style="clear: both;" />
              </xsl:otherwise>
            </xsl:choose>

          </div>
        </div>
        <div id="footer">
          <p class="legal">Copyright (c) 2002-<xsl:value-of select="$build.currentYear"/> Sapia Open Source. All rights reserved.</p>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="sapia:sect1">
    <xsl:apply-templates select="sapia:section" />
    <xsl:call-template name="displayAnchor"/>
    <h1 class="section-title">
      <xsl:value-of select="@title"/>
    </h1>
    <xsl:apply-templates select="sapia:sect-desc"/>
    <xsl:if test="toc">
      <ul id="toc">
        <xsl:call-template name="toc2"/>
      </ul>
    </xsl:if>
    <xsl:apply-templates select="sapia:sect2"/>
  </xsl:template>

  <xsl:template match="sapia:sect-desc">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="sapia:sect2">
    <xsl:call-template name="displayAnchor"/>
    <h2 class="section-title">
      <xsl:value-of select="@title"/>
    </h2>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="sapia:sect3">
    <xsl:call-template name="displayAnchor"/>
    <h3 class="section-title">
      <xsl:value-of select="@title"/>
    </h3>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="sapia:sect4">
    <xsl:call-template name="displayAnchor"/>
    <h4 class="section-title">
      <xsl:value-of select="@title"/>
    </h4>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="sapia:sect5">
    <xsl:call-template name="displayAnchor"/>
    <h5 class="section-title">
      <xsl:value-of select="@title"/>
    </h5>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="sapia:sect6">
    <xsl:call-template name="displayAnchor" />
    <h6 class="section-title">
      <xsl:value-of select="@title"/>
    </h6>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="displayAnchor">
    <xsl:choose>
      <xsl:when test="@alias">
        <a>
          <xsl:attribute name="name">
            <xsl:value-of select="@alias"/>
          </xsl:attribute>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <a>
          <xsl:attribute name="name">
            <xsl:value-of select="@title"/>
          </xsl:attribute>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
<!-- =====================================      TOC     ====================================== -->

  <xsl:template name="toc2">
    <xsl:for-each select="sapia:sect2">
      <li>
        <xsl:call-template name="displayLink"/>
        <ul>
          <xsl:for-each select="sapia:sect3">
            <xsl:call-template name="toc3"/>
          </xsl:for-each>
        </ul>
      </li>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="toc3">
    <li>
      <xsl:call-template name="displayLink"/>
      <ul>
        <xsl:for-each select="sapia:sect4">
          <xsl:call-template name="toc4"/>
        </xsl:for-each>
      </ul>
    </li>
  </xsl:template>

  <xsl:template name="toc4">
    <li>
      <xsl:call-template name="displayLink"/>
      <ul>
        <xsl:for-each select="sapia:sect5">
          <xsl:call-template name="toc5"/>
        </xsl:for-each>
      </ul>
    </li>
  </xsl:template>

  <xsl:template name="toc5">
    <li>
      <xsl:call-template name="displayLink"/>
    </li>
  </xsl:template>

  <xsl:template name="displayLink">
    <font size="1">
      <xsl:choose>
        <xsl:when test="@alias">
          <a>
            <xsl:attribute name="href">
              <xsl:text>#</xsl:text>
              <xsl:value-of select="@alias"/>
            </xsl:attribute>
            <xsl:value-of select="@title"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <a>
            <xsl:attribute name="href">
              <xsl:text>#</xsl:text>
              <xsl:value-of select="@title"/>
            </xsl:attribute>
            <xsl:value-of select="@title"/>
          </a>
        </xsl:otherwise>
      </xsl:choose>
    </font>
  </xsl:template>

  
<!-- =====================================      SECTION PATH     ====================================== -->

  <xsl:template match="sapia:section">
    <span id="breadcrumb" style="text-transform:lowercase">
      <xsl:for-each select="sapia:path">
        <xsl:choose>
          <xsl:when test="@href">
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="@href"/>
              </xsl:attribute>
              <xsl:text>/</xsl:text>
              <xsl:value-of select="@name"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>/</xsl:text>
            <xsl:value-of select="@name"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </span>
  </xsl:template>          

<!-- ======================================        POP UP      ====================================== -->

  <xsl:template match="sapia:popup">
    <a href="#">
      <xsl:attribute name="onClick">
        <xsl:text>popup('</xsl:text>
        <xsl:value-of select="@href"/>
        <xsl:text>','</xsl:text>
        <xsl:value-of select="@title"/>
        <xsl:text>',</xsl:text>
        <xsl:value-of select="@width"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of select="@height"/>
        <xsl:text>); return false</xsl:text>
      </xsl:attribute>
      <b>
        <xsl:value-of select="."/>
      </b>
    </a>
  </xsl:template>  
  
<!-- =========================================      NOTE     ========================================= -->

  <xsl:template match="sapia:note">
    <div class="note">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
   
<!-- =========================================      COMMAND     ========================================= -->

  <xsl:template match="sapia:command">
    <span style="font-family: courier, courier new; color: black;">
      <xsl:apply-templates/>
    </span>
  </xsl:template>   

<!-- =========================================      DEPENDENCY     ========================================= -->

  <xsl:template match="sapia:dependency">
    <pre>
          &lt;dependency&gt;
            &lt;groupId&gt;<xsl:value-of select="$project.groupId"/>&lt;/groupId&gt;
            &lt;artifactId&gt;<xsl:value-of select="$project.artifactId"/>&lt;/artifactId&gt;
            &lt;version&gt;<xsl:value-of select="$project.version"/>&lt;/version&gt;
          &lt;/dependency&gt;
       </pre>
  </xsl:template>   

<!-- =========================================      PARAM     ========================================= -->

  <xsl:template match="sapia:param">
    <xsl:choose>
      <xsl:when test="@value = 'project.name' or @name = 'project.name'">
        <xsl:value-of select="$project.name"/>
      </xsl:when>
      <xsl:when test="@value = 'project.description' or @name = 'project.description'">
        <xsl:value-of select="$project.description"/>
      </xsl:when>
      <xsl:when test="@value = 'project.version' or @name = 'project.version'">
        <xsl:value-of select="$project.version"/>
      </xsl:when>
      <xsl:when test="@value = 'project.groupId' or @name = 'project.groupId'">
        <xsl:value-of select="$project.groupId"/>
      </xsl:when>
      <xsl:when test="@value = 'project.artifactId' or @name = 'project.artifactId'">
        <xsl:value-of select="$project.artifactId"/>
      </xsl:when>
      <xsl:when test="@value = 'project.inceptionYear' or @name = 'project.inceptionYear'">
        <xsl:value-of select="$project.inceptionYear"/>
      </xsl:when>
      <xsl:when test="@value = 'project.packaging' or @name = 'project.packaging'">
        <xsl:value-of select="$project.packaging"/>
      </xsl:when>
      <xsl:when test="@value = 'project.url' or @name = 'project.url'">
        <xsl:value-of select="$project.url"/>
      </xsl:when>
      <xsl:when test="@value = 'project.mailinglist.name' or @name = 'project.mailinglist.name'">
        <xsl:value-of select="$project.mailinglist.name"/>
      </xsl:when>
      <xsl:when test="@value = 'project.mailinglist.subscribe' or @name = 'project.mailinglist.subscribe'">
        <xsl:value-of select="$project.mailinglist.subscribe"/>
      </xsl:when>
      <xsl:when test="@value = 'project.mailinglist.unsubscribe' or @name = 'project.mailinglist.unsubscribe'">
        <xsl:value-of select="$project.mailinglist.unsubscribe"/>
      </xsl:when>
      <xsl:when test="@value = 'project.mailinglist.archive' or @name = 'project.mailinglist.archive'">
        <xsl:value-of select="$project.mailinglist.archive"/>
      </xsl:when>
      <xsl:when test="@value = 'project.mailinglist.post' or @name = 'project.mailinglist.post'">
        <xsl:value-of select="$project.mailinglist.post"/>
      </xsl:when>
      <xsl:when test="@value = 'project.organization.name' or @name = 'project.organization.name'">
        <xsl:value-of select="$project.organization.name"/>
      </xsl:when>
      <xsl:when test="@value = 'project.organization.url' or @name = 'project.organization.url'">
        <xsl:value-of select="$project.organization.url"/>
      </xsl:when>
      <xsl:when test="@value = 'project.scm.connection' or @name = 'project.scm.connection'">
        <xsl:value-of select="$project.scm.connection"/>
      </xsl:when>
      <xsl:when test="@value = 'project.scm.developerConnection' or @name = 'project.scm.developerConnection'">
        <xsl:value-of select="$project.scm.developerConnection"/>
      </xsl:when>
      <xsl:when test="@value = 'project.scm.url' or @name = 'project.scm.url'">
        <xsl:value-of select="$project.scm.url"/>
      </xsl:when>
      <xsl:when test="@value = 'project.scm.tag' or @name = 'project.scm.tag'">
        <xsl:value-of select="$project.scm.tag"/>
      </xsl:when>
      <xsl:when test="@value = 'project.build.directory' or @name = 'project.build.directory'">
        <xsl:value-of select="$project.build.directory"/>
      </xsl:when>
      <xsl:when test="@value = 'project.build.outputDirectory ' or @name = 'project.build.outputDirectory'">
        <xsl:value-of select="$project.build.outputDirectory"/>
      </xsl:when>
      <xsl:when test="@value = 'project.build.finalName' or @name = 'project.build.finalName'">
        <xsl:value-of select="$project.build.finalName"/>
      </xsl:when>
      <xsl:when test="@value = 'build.currentYear' or @name = 'build.currentYear'">
        <xsl:value-of select="$build.currentYear"/>
      </xsl:when>
      <xsl:when test="@value = 'build.currentDate' or @name = 'build.currentDate'">
        <xsl:value-of select="$build.currentDate"/>
      </xsl:when>
      <xsl:when test="@value = 'build.currentTime' or @name = 'build.currentTime'">
        <xsl:value-of select="$build.currentTime"/>
      </xsl:when>
      <xsl:when test="@value = 'build.timestamp' or @name = 'build.timestamp'">
        <xsl:value-of select="$build.timestamp"/>
      </xsl:when>
      <xsl:when test="@value = 'build.username' or @name = 'build.timestamp'">
        <xsl:value-of select="$build.username"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NOT_FOUND</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>   

   <!-- =========================================     CLASS     ========================================= -->

  <xsl:template match="sapia:class">
    <span style="font-family: courier, courier new; color: black;">
      <xsl:choose>
        <xsl:when test="@link">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="@link"/>
            </xsl:attribute>
            <xsl:choose>
              <xsl:when test="@target">
                <xsl:attribute name="target">
                  <xsl:value-of select="@target"/>
                </xsl:attribute>
              </xsl:when>
            </xsl:choose>
            <xsl:apply-templates/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>

    </span>
  </xsl:template>   

<!-- =========================================      CODE     ========================================= -->

  <xsl:template match="sapia:code">
    <xsl:variable name="codeContent">
      <xsl:apply-templates />
    </xsl:variable>

    <p>
    <div class="snippet">
      <pre>
        <xsl:call-template name="trim">
          <xsl:with-param name="s" select="$codeContent" />
        </xsl:call-template>
      </pre>
    </div>
    </p>
  </xsl:template>
  
<!-- =========================================     TABLE     =========================================  s-->

  <xsl:template match="sapia:table">
    <table class="sapiaTable" cellspacing="5">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="sapia:th">
    <th>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </th>
  </xsl:template>   

<!-- =========================================     TAG     ========================================= -->

  <xsl:template match="sapia:tag">
    <xsl:call-template name="do_tag">
      <xsl:with-param name="name" select="@name" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="do_tag">
    <xsl:param name="name" />
    <xsl:element name="{$name}"><xsl:apply-templates select="sapia:attr"/><xsl:apply-templates/></xsl:element>
  </xsl:template>

  <xsl:template match="sapia:attr">
    <xsl:variable name="attribute_value"><xsl:apply-templates /></xsl:variable>
    <xsl:call-template name="do_attribute">
      <xsl:with-param name="name" select="@name" />
      <xsl:with-param name="value" select="$attribute_value" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="do_attribute">
    <xsl:param name="name" />
    <xsl:param name="value" />
    <xsl:attribute name="{$name}"><xsl:call-template name="trim"><xsl:with-param name="s" select="$value" /></xsl:call-template></xsl:attribute>
  </xsl:template>

<!-- =========================================     TEXT     ========================================= -->

  <xsl:template match="sapia:text">
    <xsl:apply-templates />
  </xsl:template>

<!-- ========================================= VERTICAL MENU ========================================= -->

  <xsl:template match="sapia:vmenu">
    <div id="sidebar">
      <ul>
        <xsl:apply-templates select="sapia:vsection"/>
      </ul>
    </div>
    <div style="clear: both;"></div>
  </xsl:template>

  <xsl:template match="sapia:vsection">
    <li>
      <h2>
        <xsl:choose>
          <xsl:when test="@href">
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="@href"/>
              </xsl:attribute>
              <xsl:value-of select="@name"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@name"/>
          </xsl:otherwise>
        </xsl:choose>
      </h2>
      <xsl:for-each select="sapia:vitem">
        <ul>
          <li>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="@href"/>
              </xsl:attribute>
              <xsl:value-of select="@name"/>

              <xsl:for-each select="sapia:vsubitem">
                <ul>
                  <li>
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="@href"/>
                      </xsl:attribute>
                      <xsl:value-of select="@name"/>
                    </a>
                  </li>
                </ul>
              </xsl:for-each>
            </a>
          </li>
        </ul>
      </xsl:for-each>
    </li>
  </xsl:template>
    
 <!-- ========================================= LICENSE ========================================= -->

  <xsl:template match="sapia:license">
    <p align="center">
Apache License<br />
Version 2.0, January 2004<br />
      <a href="http://www.apache.org/licenses/">http://www.apache.org/licenses/</a>
    </p>
    <p>
TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
</p>
    <p>
      <b>
        <a name="definitions">1. Definitions</a>
      </b>.</p>
    <p>
      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.
</p>
    <p>
      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.
</p>
    <p>
      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.
</p>
    <p>
      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.
</p>
    <p>
      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.
</p>
    <p>
      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.
</p>
    <p>
      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).
</p>
    <p>
      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.
</p>
    <p>
      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."
</p>
    <p>
      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.
</p>
    <p>
      <b>
        <a name="copyright">2. Grant of Copyright License</a>
      </b>.
Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.
</p>
    <p>
      <b>
        <a name="patent">3. Grant of Patent License</a>
      </b>.
Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.
</p>
    <p>
      <b>
        <a name="redistribution">4. Redistribution</a>
      </b>.
You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:
<ol type="a">
<li>You must give any other recipients of the Work or
          Derivative Works a copy of this License; and
<br />
          <br />
        </li>

        <li>You must cause any modified files to carry prominent notices
          stating that You changed the files; and
<br />
          <br />
        </li>

        <li>You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and
<br />
          <br />
        </li>

        <li>If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.</li>
      </ol>
      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.
</p>
    <p>
      <b>
        <a name="contributions">5. Submission of Contributions</a>
      </b>.
Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.
</p>
    <p>
      <b>
        <a name="trademarks">6. Trademarks</a>
      </b>.
This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.
</p>
    <p>
      <b>
        <a name="no-warranty">7. Disclaimer of Warranty</a>
      </b>.
Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.
</p>
    <p>
      <b>
        <a name="no-liability">8. Limitation of Liability</a>
      </b>.
In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.
</p>
    <p>
      <b>
        <a name="additional">9. Accepting Warranty or Additional Liability</a>
      </b>.
While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.
</p>
    <p>
END OF TERMS AND CONDITIONS
</p>
  </xsl:template>

  <xsl:template match="sapia:license-apache2">
    <p align="center">
Apache License<br />
Version 2.0, January 2004<br />
      <a href="http://www.apache.org/licenses/">http://www.apache.org/licenses/</a>
    </p>
    <p>
TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
</p>
    <p>
      <b>
        <a name="definitions">1. Definitions</a>
      </b>.</p>
    <p>
      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.
</p>
    <p>
      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.
</p>
    <p>
      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.
</p>
    <p>
      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.
</p>
    <p>
      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.
</p>
    <p>
      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.
</p>
    <p>
      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).
</p>
    <p>
      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.
</p>
    <p>
      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."
</p>
    <p>
      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.
</p>
    <p>
      <b>
        <a name="copyright">2. Grant of Copyright License</a>
      </b>.
Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.
</p>
    <p>
      <b>
        <a name="patent">3. Grant of Patent License</a>
      </b>.
Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.
</p>
    <p>
      <b>
        <a name="redistribution">4. Redistribution</a>
      </b>.
You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:
<ol type="a">
<li>You must give any other recipients of the Work or
          Derivative Works a copy of this License; and
<br />
          <br />
        </li>

        <li>You must cause any modified files to carry prominent notices
          stating that You changed the files; and
<br />
          <br />
        </li>

        <li>You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and
<br />
          <br />
        </li>

        <li>If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.</li>
      </ol>
      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.
</p>
    <p>
      <b>
        <a name="contributions">5. Submission of Contributions</a>
      </b>.
Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.
</p>
    <p>
      <b>
        <a name="trademarks">6. Trademarks</a>
      </b>.
This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.
</p>
    <p>
      <b>
        <a name="no-warranty">7. Disclaimer of Warranty</a>
      </b>.
Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.
</p>
    <p>
      <b>
        <a name="no-liability">8. Limitation of Liability</a>
      </b>.
In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.
</p>
    <p>
      <b>
        <a name="additional">9. Accepting Warranty or Additional Liability</a>
      </b>.
While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.
</p>
    <p>
END OF TERMS AND CONDITIONS
</p>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  
<!-- ============================ UTILITIES ============================ -->

  <xsl:template name="left-trim">
    <xsl:param name="s" />
    <xsl:choose>
      <xsl:when test="substring($s, 1, 1) = ''">
        <xsl:value-of select="$s"/>
      </xsl:when>
      <xsl:when test="normalize-space(substring($s, 1, 1)) = ''">
        <xsl:call-template name="left-trim">
          <xsl:with-param name="s" select="substring($s, 2)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$s" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="right-trim">
    <xsl:param name="s" />
    <xsl:choose>
      <xsl:when test="substring($s, 1, 1) = ''">
        <xsl:value-of select="$s"/>
      </xsl:when>
      <xsl:when test="normalize-space(substring($s, string-length($s))) = ''">
        <xsl:call-template name="right-trim">
          <xsl:with-param name="s" select="substring($s, 1, string-length($s) - 1)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$s" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="trim">
    <xsl:param name="s" />
    <xsl:call-template name="right-trim">
      <xsl:with-param name="s">
        <xsl:call-template name="left-trim">
          <xsl:with-param name="s" select="$s" />
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>  
