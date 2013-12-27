<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:i18n="http://apache.org/cocoon/i18n/2.1">
                
  <xsl:template match="/rss">
    <html>
      <head>
	    <link rel="stylesheet" href="style.css" type="text/css" />
      </head>
      
      <body>
        <xsl:apply-templates select="channel"/>
      </body>
    </html>
  </xsl:template>        

  <xsl:template match="channel">
    <xsl:apply-templates select="item"/>
  </xsl:template>
  
  <xsl:template match="item">
    <h2><xsl:value-of select="title" /></h2>
    <p>
     <xsl:value-of select="description" disable-output-escaping="yes" /> 
     (<a><xsl:attribute name="href"><xsl:value-of select="link" /></xsl:attribute>full story</a>)
    </p>
    <i><xsl:value-of select="pubDate" /></i>
  </xsl:template>  
</xsl:stylesheet>  
