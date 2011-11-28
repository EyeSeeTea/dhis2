<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Nov 28, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>html wrapper</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output method="html"/>
    
    <xsl:template match="/">
        <html>
            <head>
                <title>DHIS Web-API</title>
                <!-- stylesheets, javascript etc -->
            </head>
            <body>
                <p>Some CSS required!</p>
                <xsl:apply-templates />
            </body>
        </html>
    </xsl:template>
    
    
</xsl:stylesheet>
