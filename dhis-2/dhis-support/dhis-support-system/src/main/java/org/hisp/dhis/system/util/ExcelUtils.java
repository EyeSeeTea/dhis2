package org.hisp.dhis.system.util;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExcelUtils
{
    public static final String EXTENSION_XLS = ".xls";

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    /**
     * @throws WriteException
     */
    public static void setUpFormat( WritableCellFormat cellFormat, Alignment alignment, Border border,
        BorderLineStyle borderLineStyle, Colour colour )
        throws WriteException
    {
        cellFormat.setAlignment( alignment );
        cellFormat.setBackground( colour );
        cellFormat.setBorder( border, borderLineStyle );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void printDataElementHeaders( WritableSheet sheet, WritableCellFormat format, I18n i18n, int row,
        int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "alternative_name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "description" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "active" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "type" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "aggregation_operator" ), format ) );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */

    public static void addDataElementCellToSheet( WritableSheet sheet, WritableCellFormat format, DataElement element,
        I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, element.getName(), format ) );
        sheet.addCell( new Label( column++, row, element.getAlternativeName(), format ) );
        sheet.addCell( new Label( column++, row, element.getShortName(), format ) );
        sheet.addCell( new Label( column++, row, element.getCode(), format ) );
        sheet.addCell( new Label( column++, row, element.getDescription(), format ) );
        sheet.addCell( new Label( column++, row, getBoolean().get( element.isActive() ), format ) );
        sheet.addCell( new Label( column++, row, getType().get( element.getType() ), format ) );
        sheet.addCell( new Label( column++, row, getAggregationOperator().get( element.getAggregationOperator() ),
            format ) );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void printIndicatorHeaders( WritableSheet sheet, WritableCellFormat format, I18n i18n, int row,
        int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "alternative_name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "description" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "annualized" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "indicator_type" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "numerator_description" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "numerator_aggregation_operator" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "numerator_formula" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "denominator_description" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "denominator_aggregation_operator" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "denominator_formula" ), format ) );

    }

    /**
     * @param expressionService
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void addIndicatorCellToSheet( WritableSheet sheet, WritableCellFormat format, Indicator indicator,
        I18n i18n, ExpressionService expressionService, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, indicator.getName(), format ) );
        sheet.addCell( new Label( column++, row, indicator.getAlternativeName(), format ) );
        sheet.addCell( new Label( column++, row, indicator.getShortName(), format ) );
        sheet.addCell( new Label( column++, row, indicator.getCode(), format ) );
        sheet.addCell( new Label( column++, row, indicator.getDescription(), format ) );
        sheet.addCell( new Label( column++, row, getBoolean().get( indicator.getAnnualized() ), format ) );
        sheet.addCell( new Label( column++, row, getType().get( indicator.getIndicatorType().getName() ), format ) );

        sheet.addCell( new Label( column++, row, indicator.getNumeratorDescription(), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( getAggregationOperator().get(
            indicator.getNumeratorAggregationOperator() ) ), format ) );
        sheet.addCell( new Label( column++, row,
            expressionService.getExpressionDescription( indicator.getNumerator() ), format ) );

        sheet.addCell( new Label( column++, row, indicator.getDenominatorDescription(), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( getAggregationOperator().get(
            indicator.getDenominatorAggregationOperator() ) ), format ) );
        sheet.addCell( new Label( column++, row, expressionService
            .getExpressionDescription( indicator.getDenominator() ), format ) );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void printExtendedDataElementHeaders( WritableSheet sheet, WritableCellFormat format1,
        WritableCellFormat format2, I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column, row, i18n.getString( "identifying_and_definitional_attributes" ), format1 ) );
        sheet.mergeCells( column, row, 16, row );

        column = 17;
        sheet
            .addCell( new Label( column, row, i18n.getString( "relational_and_representational_attributes" ), format1 ) );
        sheet.mergeCells( column, row, 26, row );

        column = 27;
        sheet.addCell( new Label( column, row, i18n.getString( "administrative_attributes" ), format1 ) );
        sheet.mergeCells( 27, row, 39, row );

        // row = 2 & column = 1->8
        row++;
        column = 1;
        printDataElementHeaders( sheet, format2, i18n, row, column );

        // row = 2 & column = 9->16
        column = 9;
        sheet.addCell( new Label( column++, row, i18n.getString( "mnemonic" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "version" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "context" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "synonyms" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "hononyms" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "keywords" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "status" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "status_date" ), format2 ) );

        // -------------------------------------------------------------------------
        // Relational and representational attributes
        // -------------------------------------------------------------------------

        // row = 2 & column = 17->26
        sheet.addCell( new Label( column++, row, i18n.getString( "data_type" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "representational_form" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "representational_layout" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "minimum_size" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "maximum_size" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "data_domain" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "validation_rules" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "related_data_references" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "guide_for_use" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "collection_methods" ), format2 ) );

        // -------------------------------------------------------------------------
        // Administrative attributes
        // -------------------------------------------------------------------------

        // row = 2 & column = 27->36
        sheet.addCell( new Label( column++, row, i18n.getString( "responsible_authority" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "update_rules" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "access_authority" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "update_frequency" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "location" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "reporting_methods" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "version_status" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "previous_version_references" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "source_document" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "source_organisation" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "comment" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "saved" ), format2 ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "last_updated" ), format2 ) );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */

    public static void addExtendedDataElementCellToSheet( WritableSheet sheet, WritableCellFormat format,
        DataElement element, I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        // row = 3 & column = 1->8
        addDataElementCellToSheet( sheet, format, element, i18n, row, column );

        if ( element.getExtended() != null )
        {
            // row = 3 & column = 9->16
            column = 9;
            sheet.addCell( new Label( column++, row, element.getExtended().getMnemonic(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getVersion(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getContext(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getSynonyms(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getHononyms(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getKeywords(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getStatus(), format ) );
            sheet.addCell( new Label( column++, row, DateUtils.getMediumDateString( element.getExtended()
                .getStatusDate() ) ) );

            // -------------------------------------------------------------------------
            // Relational and representational attributes
            // -------------------------------------------------------------------------

            // row = 3 & column = 17->26
            sheet.addCell( new Label( column++, row, element.getExtended().getDataType(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getRepresentationalForm(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getRepresentationalLayout(), format ) );
            sheet.addCell( new Label( column++, row, String.valueOf( element.getExtended().getMinimumSize() ) ) );
            sheet.addCell( new Label( column++, row, String.valueOf( element.getExtended().getMaximumSize() ) ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getDataDomain(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getValidationRules(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getRelatedDataReferences(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getGuideForUse(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getCollectionMethods(), format ) );

            // -------------------------------------------------------------------------
            // Administrative attributes
            // -------------------------------------------------------------------------

            // row = 3 & column = 27->36
            sheet.addCell( new Label( column++, row, element.getExtended().getResponsibleAuthority(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getUpdateRules(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getAccessAuthority(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getUpdateFrequency(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getLocation(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getReportingMethods(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getVersionStatus(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getPreviousVersionReferences(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getSourceDocument(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getSourceOrganisation(), format ) );
            sheet.addCell( new Label( column++, row, element.getExtended().getComment(), format ) );
            sheet
                .addCell( new Label( column++, row, DateUtils.getMediumDateString( element.getExtended().getSaved() ) ) );
            sheet.addCell( new Label( column++, row, DateUtils.getMediumDateString( element.getExtended()
                .getLastUpdated() ) ) );
        }
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */

    public static void printOrganisationUnitHeaders( WritableSheet sheet, WritableCellFormat format, I18n i18n,
        int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "opening_date" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "closed_date" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "active" ), format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "comment" ), format ) );

    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */

    public static void addOrganisationUnitCellToSheet( WritableSheet sheet, WritableCellFormat format,
        OrganisationUnit unit, I18n i18n, I18nFormat i18nFormat, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, unit.getShortName(), format ) );
        sheet.addCell( new Label( column++, row, unit.getCode(), format ) );
        sheet.addCell( new Label( column++, row, unit.getOpeningDate() != null ? i18nFormat.formatDate( unit
            .getOpeningDate() ) : "", format ) );
        sheet.addCell( new Label( column++, row, unit.getClosedDate() != null ? i18nFormat.formatDate( unit
            .getClosedDate() ) : "", format ) );
        sheet.addCell( new Label( column++, row, i18n.getString( getBoolean().get( unit.isActive() ) ), format ) );
        sheet.addCell( new Label( column++, row, unit.getComment(), format ) );
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */

    public static void printOrganisationUnitHierarchyHeaders( WritableSheet sheet, WritableCellFormat format1,
        WritableCellFormat format2, I18n i18n, int row, int column, int level )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column, row, i18n.getString( "organisation_unit_level" ), format1 ) );
        sheet.mergeCells( column, row, level-1, row );

        row++;

        for ( int i = 1; i <= level; i++ )
        {
            sheet.addCell( new Label( column++, row, (i + ""), format2 ) );
        }
    }

    public static void addOrganisationUnitHierarchyCellToSheet( WritableSheet sheet, WritableCellFormat format,
        OrganisationUnit unit, I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column, row, unit.getName(), format ) );
    }

    /**
     * Creates a writable workbook.
     * 
     * @param outputStream The output stream to write the document content.
     * @param pageSize the page size.
     * @return A Document.
     */
    public static WritableWorkbook openWorkbook( OutputStream outputStream )
    {
        try
        {
            return Workbook.createWorkbook( outputStream );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to open a writable workbook", e );
        }
    }

    /**
     * @throws IOException
     * @throws WriteException
     */
    public static void writeAndCloseWorkbook( WritableWorkbook workbook )
    {
        if ( workbook != null )
        {
            try
            {
                workbook.write();
                workbook.close();
            }
            catch ( IOException ioe )
            {
                throw new RuntimeException( "Failed to write data to workbook", ioe );
            }
            catch ( WriteException we )
            {
                throw new RuntimeException( "Failed to close the workbook", we );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "Yes" );
        map.put( false, "No" );
        return map;
    }

    private static Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.VALUE_TYPE_STRING, "Text" );
        map.put( DataElement.VALUE_TYPE_INT, "Number" );
        map.put( DataElement.VALUE_TYPE_BOOL, "Yes/No" );
        return map;
    }

    private static Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "Sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "Average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "Count" );
        return map;
    }
}
