<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="dataSet">
    <div class="dataSet">
      <h2>
        <xsl:value-of select="@name" />
      </h2>
      <table border="1">
        <tr>
          <td>ID</td>
          <td>
            <xsl:value-of select="@id" />
          </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td>
            <xsl:value-of select="@lastUpdated" />
          </td>
        </tr>
        <tr>
          <td>Short Name</td>
          <td>
            <xsl:value-of select="shortName" />
          </td>
        </tr>
        <tr>
          <td>Opening Date</td>
          <td>
            <xsl:value-of select="openingDate" />
          </td>
        </tr>
        <tr>
          <td>Level</td>
          <td>
            <xsl:value-of select="level" />
          </td>
        </tr>
        <tr>
          <td>Active</td>
          <td>
            <xsl:value-of select="active" />
          </td>
        </tr>
        <tr>
          <td>Current Parent</td>
          <td>
            <xsl:value-of select="currentParent" />
          </td>
        </tr>
        <tr>
          <td>Has Patients</td>
          <td>
            <xsl:value-of select="hasPatients" />
          </td>
        </tr>

      </table>

      <xsl:apply-templates select="parent|groups|dataSets" />

    </div>
  </xsl:template>

  <xsl:template match="parent">
    <h3>Parent OrganisationUnit</h3>
    <table border="1" class="organisationUnits">
      <xsl:call-template name="identifiable-row">
        <xsl:with-param name="root">../organisationUnits</xsl:with-param>
      </xsl:call-template>
    </table>
  </xsl:template>

  <xsl:template match="groups">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnit Groups</h3>
      <table border="1" class="organisationUnitGroups">
        <xsl:for-each select="group">
          <xsl:call-template name="identifiable-row">
            <xsl:with-param name="root">../organisationUnitGroups</xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="dataSets">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table border="1" class="dataSets">
        <xsl:for-each select="dataSet">
          <xsl:call-template name="identifiable-row">
            <xsl:with-param name="root">../dataSets</xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
