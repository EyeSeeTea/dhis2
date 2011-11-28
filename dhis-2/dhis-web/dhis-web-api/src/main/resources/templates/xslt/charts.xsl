<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="charts">

    <html>
      <body>
        <h1>CHARTS!</h1>

        <ul>
          <xsl:apply-templates />
        </ul>
      </body>
    </html>

  </xsl:template>

  <xsl:template match="chart">
    <li><xsl:value-of select="@name"/></li>
  </xsl:template>
</xsl:stylesheet>
