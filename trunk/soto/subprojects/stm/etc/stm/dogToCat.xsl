<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
  <xsl:template match="/dog">
    <cat>
      <xsl:apply-templates />
    </cat>
  </xsl:template>
  
  <xsl:template match="race">
    <race>siamese</race>
  </xsl:template>
  
  <xsl:template match="name">
    <name>maurice</name>
  </xsl:template>  
  
</xsl:stylesheet>