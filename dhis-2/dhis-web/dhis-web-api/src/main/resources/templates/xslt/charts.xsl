<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Nov 28, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>DXF2 Charts renderer</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output method="html"/>

    <xsl:include href="identifiable-row.xsl"/>
    <xsl:template match="/">
        <html>
            <head></head>
            <body>
                <xsl:apply-templates />
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="charts">
        <h1>Charts</h1>
        <p>Some CSS required!</p>
        <table border="1">
            <xsl:for-each select="chart">
                <xsl:call-template name="identifiable-row">
                    <xsl:with-param name="root">charts</xsl:with-param>
                </xsl:call-template>
            </xsl:for-each>
        </table>
    </xsl:template>
    
</xsl:stylesheet>
