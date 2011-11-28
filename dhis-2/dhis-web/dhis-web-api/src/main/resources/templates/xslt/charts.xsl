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
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="chart">
        <div class="chart">
            <h2>
                <xsl:value-of select="@name"/>
            </h2>
            <table border="1">
                <tr>
                    <td>ID</td>
                    <td>
                        <xsl:value-of select="@id"/>
                    </td>
                </tr>
                <tr>
                    <td>Last Updated</td>
                    <td>
                        <xsl:value-of select="@lastUpdated"/>
                    </td>
                </tr>
                <tr>
                    <td>Dimension</td>
                    <td>
                        <xsl:value-of select="dimension"/>
                    </td>
                </tr>
                <tr>
                    <td>Hide legend</td>
                    <td>
                        <xsl:value-of select="hideLegend"/>
                    </td>
                </tr>
                <tr>
                    <td>Hide subtitle</td>
                    <td>
                        <xsl:value-of select="hideSubtitle"/>
                    </td>
                </tr>
                <tr>
                    <td>Horizontal Pilot  Orientation</td>
                    <td>
                        <xsl:value-of select="horizontalPlotOrientation"/>
                    </td>
                </tr>
                <tr>
                    <td>Regression</td>
                    <td>
                        <xsl:value-of select="regression"/>
                    </td>
                </tr>
                <tr>
                    <td>Size</td>
                    <td>
                        <xsl:value-of select="size"/>
                    </td>
                </tr>
                <tr>
                    <td>Target line</td>
                    <td>
                        <xsl:value-of select="targetLine"/>
                    </td>
                </tr>
                <tr>
                    <td>Target line label</td>
                    <td>
                        <xsl:value-of select="targetLineLabel"/>
                    </td>
                </tr>
                <tr>
                    <td>Type</td>
                    <td>
                        <xsl:value-of select="type"/>
                    </td>
                </tr>
                <tr>
                    <td>User organisation unit</td>
                    <td>
                        <xsl:value-of select="userOrganisationUnit"/>
                    </td>
                </tr>
                <tr>
                    <td>Vertical labels</td>
                    <td>
                        <xsl:value-of select="verticalLabels"/>
                    </td>
                </tr>
            </table>
        </div>        
    </xsl:template>
</xsl:stylesheet>
