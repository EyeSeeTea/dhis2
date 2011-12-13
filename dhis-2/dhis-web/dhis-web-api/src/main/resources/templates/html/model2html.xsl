<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:d="http://dhis2.org/schema/dxf/2.0"
    exclude-result-prefixes="d"
    >
    
    <xsl:output method="html"/>
    
    <!-- html page level settings -->
    <xsl:include href="html-wrapper.xsl"/>

    <!-- for resource 'directory' -->
    <xsl:include href="resource.xsl"/>
  
    <!-- for list views -->
    <xsl:include href="list.xsl"/>

    <!-- for rendering elements -->
    <xsl:include href="map.xsl"/>
    <xsl:include href="chart.xsl"/>
    <xsl:include href="category.xsl" />
    <xsl:include href="categoryOption.xsl" />
    <xsl:include href="categoryCombo.xsl" />
    <xsl:include href="categoryOptionCombo.xsl" />
    <xsl:include href="dataElement.xsl"/>
    <xsl:include href="dataElementGroup.xsl"/>
    <xsl:include href="dataElementGroupSet.xsl"/>
    <xsl:include href="indicator.xsl"/>
    <xsl:include href="indicatorType.xsl"/>
    <xsl:include href="indicatorGroup.xsl"/>
    <xsl:include href="indicatorGroupSet.xsl"/>
    <xsl:include href="organisationUnit.xsl"/>
    <xsl:include href="organisationUnitGroup.xsl"/>
    <xsl:include href="organisationUnitGroupSet.xsl"/>
    <xsl:include href="dataSet.xsl"/>
    <xsl:include href="attributeType.xsl"/>
    <xsl:include href="report.xsl"/>
    <xsl:include href="validationRule.xsl"/>
    <xsl:include href="validationRuleGroup.xsl"/>
    <xsl:include href="sqlView.xsl"/>
    <!-- etc ... -->

</xsl:stylesheet>
