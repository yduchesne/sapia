<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
  <xsl:template match="/cat">
    <dog>
      <xsl:apply-templates />
    </dog>
  </xsl:template>
  
  <xsl:template match="race">
    <race>hound</race>
  </xsl:template>
  
  <xsl:template match="name">
    <name>goofy</name>
  </xsl:template>  
  
</xsl:stylesheet>