<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:include href="identifiable-row.xsl"/>
  
  <xsl:param name="elements"/>
  <xsl:param name="title"/>

  <xsl:template name="list">
    <h1>
      <xsl:value-of select="$title"/>
    </h1>

    <table border="1">
      <xsl:for-each select="child::*">
        <xsl:call-template name="identifiable-row">
          <xsl:with-param name="root">
            <xsl:value-of select="$elements"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
