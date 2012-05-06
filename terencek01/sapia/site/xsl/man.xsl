<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sapia="http://www.sapia-oss.org/2003/XSL/Transform">
                
  <!-- ========================================= MANUAL ========================================= -->

  <xsl:template match="/manual">
    <sapia:page cssPath="../css/sapia.css"><xsl:attribute name="title"><xsl:value-of select="@title"/></xsl:attribute>

      <sapia:vmenu>
        <sapia:vsection name="Project Home" href="../home.html"/>
        <sapia:vsection name="Javadoc" href="../maven/api/index.html"/>
        <sapia:vsection name="Download" href="../download.html"/>
        <sapia:vsection name="Mailing List" href="../list.html"/>
        <sapia:vsection name="Maven" href="../maven/index.html"/>
      </sapia:vmenu>

      <sapia:sect1>
        <xsl:attribute name="title"><xsl:value-of select="@title"/></xsl:attribute>
        <sapia:section>
          <sapia:path name="home" href="../../../home.html" />
          <sapia:path name="projects" href="../../../projects.html" />
          <sapia:path name="vlad" href="../home.html"/>
          <sapia:path name="rule reference" />
        </sapia:section>

        <!--sapia:sect-desc><xsl:apply-templates select="description"/></sapia:sect-desc-->
        <toc/>
        <xsl:apply-templates select="namespace"/>
      </sapia:sect1>      
    </sapia:page>
  </xsl:template>
  
  <!-- ========================================= NAMESPACE ========================================= -->

  <xsl:template match="namespace">
    <sapia:sect2><xsl:attribute name="title"><xsl:value-of select="@prefix"/></xsl:attribute>
      <sapia:sect-desc><xsl:apply-templates select="description"/></sapia:sect-desc>
      <xsl:apply-templates select="category"/>
    </sapia:sect2>
  </xsl:template>

  <!-- ========================================= CATEGORY ========================================= -->

  <xsl:template match="category">
    <sapia:sect3><xsl:attribute name="title"><xsl:value-of select="@name"/></xsl:attribute>
    <xsl:if test="rule">
      <ul>
        <xsl:apply-templates select="rule"/>
      </ul>
    </xsl:if>
    </sapia:sect3>
  </xsl:template>
  
  <!-- ========================================= RULE ========================================= -->

  <xsl:template match="rule">
    <li>
      <a><xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute><xsl:value-of select="@name"/></a>
    </li>
  </xsl:template>  

  <!-- ========================================= DESCRIPTION ========================================= -->  
  
  <xsl:template match="description">
    <sapia:sect-desc><p>
      <xsl:apply-templates /></p>    
    </sapia:sect-desc>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
       <xsl:apply-templates select="@*"/>
       <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>  
                
</xsl:stylesheet>  
