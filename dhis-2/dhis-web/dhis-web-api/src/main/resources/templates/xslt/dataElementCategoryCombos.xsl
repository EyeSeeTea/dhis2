<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="identifiable-row.xsl" />
  <xsl:include href="html-wrapper.xsl" />
  <xsl:include href="list.xsl" />

  <xsl:param name="title">DataElementCategoryCombos</xsl:param>
  <xsl:param name="elements">dataElementCategoryCombos</xsl:param>

  <xsl:template match="dataElementCategoryCombo">
    <xsl:apply-templates />
  </xsl:template>
</xsl:stylesheet>
