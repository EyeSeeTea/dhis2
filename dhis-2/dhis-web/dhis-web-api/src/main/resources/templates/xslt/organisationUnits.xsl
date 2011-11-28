<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Nov 28, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>DXF2 OrgUnitss renderer</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:include href="identifiable-row.xsl"/>
    <xsl:include href="html-wrapper.xsl" />
    
    <xsl:template match="organisationUnits">
        <h1>Organisation Units</h1>
        <table border="1">
            <xsl:for-each select="organisationUnit">
                <xsl:call-template name="identifiable-row">
                    <xsl:with-param name="root">organisationUnits</xsl:with-param>
                </xsl:call-template>
            </xsl:for-each>
        </table>
    </xsl:template>
    
</xsl:stylesheet>
