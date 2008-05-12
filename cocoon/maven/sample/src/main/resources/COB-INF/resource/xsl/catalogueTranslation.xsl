<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:i18n="http://apache.org/cocoon/i18n/2.1">
                
  <xsl:template match="/catalogues">
    <catalogues>
      <xsl:apply-templates select="catalogue"/>
    </catalogues>
  </xsl:template>        

  <xsl:template match="catalogue">
    <catalogue>
      <xsl:if test="@id">
        <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
      </xsl:if>
      <xsl:for-each select="message">
        <message>
          <xsl:attribute name="key"><xsl:value-of select="@key" /></xsl:attribute>
          <i18n:text><xsl:attribute name="key"><xsl:value-of select="@key" /></xsl:attribute></i18n:text>
        </message>
      </xsl:for-each>
    </catalogue>
  </xsl:template>
</xsl:stylesheet>  
