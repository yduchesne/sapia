<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sapia="http://www.sapia-oss.org/2003/XSL/Transform">
                
  <!-- ========================================= RULE ========================================= -->

  <xsl:template match="/descriptor">
    <sapia:page cssPath="../../css/sapia.css"><xsl:attribute name="title"><xsl:value-of select="@namespace"/><xsl:text>:</xsl:text><xsl:value-of select="@name"/></xsl:attribute>

      <sapia:vmenu>
        <sapia:vsection name="Project Home" href="../../home.html"/>
        <sapia:vsection name="Javadoc" href="../../maven/api/index.html"/>
        <sapia:vsection name="Download" href="../../download.html"/>
        <sapia:vsection name="Mailing List" href="../../list.html"/>
        <sapia:vsection name="Maven" href="../../maven/index.html"/>
      </sapia:vmenu>

      <sapia:sect1>
        <xsl:attribute name="title"><xsl:value-of select="@namespace"/><xsl:text>:</xsl:text><xsl:value-of select="@name"/></xsl:attribute>

        <sapia:section>
          <sapia:path name="home" href="../../../../home.html" />
          <sapia:path name="projects" href="../../../../projects.html" />
          <sapia:path name="vlad" href="../../index.html"/>
          <sapia:path name="rule reference" href="../index.html"/>
          <sapia:path>
            <xsl:attribute name="name"><xsl:value-of select="@namespace"/><xsl:text>:</xsl:text><xsl:value-of select="@name"/></xsl:attribute>
          </sapia:path>
        </sapia:section>

        <sapia:sect-desc><xsl:apply-templates select="description"/></sapia:sect-desc>

        <xsl:apply-templates select="attributes"/>
        <xsl:apply-templates select="elements"/>
        <xsl:apply-templates select="examples"/>        
      </sapia:sect1>      
    </sapia:page>
  </xsl:template>
  
  <!-- ========================================= ATTRIBUTES ========================================= -->

  <xsl:template match="attributes">
    <sapia:sect2 title="Attributes">
      <sapia:table width="100%">
        <sapia:th>Name</sapia:th><sapia:th>Description</sapia:th><sapia:th>Mandatory</sapia:th>
        
        <xsl:apply-templates select="attribute"/>
      </sapia:table>
    </sapia:sect2>
  </xsl:template>

  <xsl:template match="attribute">
    <tr>
    <td width="20%" align="center" valign="top"><xsl:value-of select="@name"/></td>
    <td width="75%" valign="top"><xsl:apply-templates select="description"/></td>
    <td width="5%" align="center" valign="top"><xsl:value-of select="@mandatory"/></td>
    </tr>
  </xsl:template>

  <!-- ========================================= ELEMENTS ========================================= -->

  <xsl:template match="elements">
    <sapia:sect2 title="Elements">
    <sapia:table width="100%">
      <sapia:th>Name</sapia:th>
      <sapia:th>Description</sapia:th>
      <sapia:th>Parent</sapia:th>
      <sapia:th>Children</sapia:th>
      <sapia:th>Mandatory</sapia:th>      
      <xsl:apply-templates select="element"/>
    </sapia:table>
    </sapia:sect2>    
  </xsl:template>

  <xsl:template match="element">
    <tr>
    <td width="20%" align="center" valign="top"><xsl:value-of select="@name"/></td>
    <td width="40%" valign="top"><xsl:apply-templates select="description"/></td>
    <td width="17%" align="center" valign="top"><xsl:value-of select="@parent"/></td>    
    <td width="18%" align="center" valign="top"><xsl:apply-templates select="child"/></td>
    <td width="5%" align="center" valign="top"><xsl:value-of select="@mandatory"/></td>    
    </tr>
  </xsl:template>  
  
  <xsl:template match="child">
    <xsl:value-of select="@name"/><br></br>
  </xsl:template>
  
  <!-- ========================================= EXAMPLES ========================================= -->

  <xsl:template match="examples">
   <sapia:sect2 title="Example(s)">
     <xsl:apply-templates select="example"/>
   </sapia:sect2>
  </xsl:template>
  
  <xsl:template match="example">
    <sapia:sect3><xsl:attribute name="title"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:apply-templates select="description"/>
      <sapia:code><xsl:text></xsl:text><xsl:apply-templates select="content"/><xsl:text></xsl:text></sapia:code>
    </sapia:sect3>
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
