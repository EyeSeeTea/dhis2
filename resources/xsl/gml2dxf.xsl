<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://www.opengis.net/gml">

<xsl:template match="/">
<dxf xmlns="http://dhis2.org/schema/dxf/1.0" minorVersion="1.1">
<organisationUnits>
  <xsl:for-each select="//gml:featureMember">
    <organisationUnit>
      <id>0</id>
      <uuid/>
      <name><xsl:value-of select=".//*[local-name()='NAME']"/></name>
      <shortName><xsl:value-of select=".//*[local-name()='NAME']"/></shortName>
      <code/>
      <openingDate/>
      <closedDate/>
      <active/>
      <comment/>
      <geoCode/>
      <feature type="MultiPolygon">
        <xsl:for-each select=".//gml:Polygon">
          <coordinates>
            <xsl:value-of select=".//gml:coordinates"/>
          </coordinates>
        </xsl:for-each>
      </feature>
      <lastUpdated/>
    </organisationUnit>
  </xsl:for-each>
</organisationUnits>
</dxf>
</xsl:template>

</xsl:stylesheet>
