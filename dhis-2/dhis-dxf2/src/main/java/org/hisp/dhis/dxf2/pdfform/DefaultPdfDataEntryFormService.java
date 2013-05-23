package org.hisp.dhis.dxf2.pdfform;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PushbuttonField;
import com.lowagie.text.pdf.RadioCheckField;
import com.lowagie.text.pdf.TextField;

public class DefaultPdfDataEntryFormService implements PdfDataEntryFormService
{

    // --------- Other Static Values ---------

    private static final Color COLOR_BACKGROUDTEXTBOX = Color.LIGHT_GRAY;

    private static final String TEXT_BLANK = " ";

    // --- All the Settings Related
    private static final int TEXTBOXWIDTH_NUMBERTYPE = 20;

    private static final int TEXTBOXWIDTH = 200;

    private static final int PERIODRANGE_PREVYEARS = 1;

    private static final int PERIODRANGE_FUTUREYEARS = 2;

    private static Integer MAX_OPTIONS_DISPLAYED = 30;

    private static Integer PROGRAM_FORM_ROW_NUMBER = 10;
    
    private static final float UNITSIZE_DEFAULT = 10;


    // Variables

    private PdfFormFontSettings pdfFormFontSettings;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private DataValueSetService dataValueSetService;

    // -------------------------------------------------------------------------
    // GETTER / SETTER
    // -------------------------------------------------------------------------

    // Get fonts Settings - for modifying/overriding from outside

//    public PdfFormFontSettings getPdfFormFontSettings()
//    {
//        return this.pdfFormFontSettings;
//    }

//    // Get document - for modifying/overriding from outside
//    private Document document;
//
//    public Document getDocument()
//    {
//        return document;
//    }

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // METHODS / CLASSES
    // -------------------------------------------------------------------------

    @Override
    public void generatePDFDataEntryForm( Document document, PdfWriter writer, String inputUid, int typeId
        , Rectangle pageSize, PdfFormFontSettings pdfFormFontSettings )
    {
        try
        {
            
            // STEP 1. Assign the parameters to the global variable
            this.pdfFormFontSettings = pdfFormFontSettings;

            
            // STEP 2. Set Page Size
            document.setPageSize( pageSize );
            
            
            // STEP 3. PDF Document Open
            document.open();

            
            // STEP 3. Generate the PDF Form
            if ( typeId == PdfDataEntryFormUtil.DATATYPE_DATASET )
            {
                setDataSet_DocumentContent( document, writer, inputUid );
            }
            else if ( typeId == PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE )
            {
                setProgramStage_DocumentContent( document, writer, inputUid );
            }

            
            // STEP 4. Close the PDF Document
            document.close();

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

    }

//    private void setDefaultFooter( Document document, String footerText, Font font )
//    {
//        boolean isNumbered = true;
//
//        HeaderFooter footer = new HeaderFooter( new Phrase( footerText, font ), isNumbered );
//        footer.setBorder( Rectangle.NO_BORDER );
//        footer.setAlignment( Element.ALIGN_RIGHT );
//        document.setFooter( footer );
//    }

    // -----------------------------------------------------------------------------
    // --- Retrieve Document Content - DataSet [START]

    private void setDataSet_DocumentContent( Document document, PdfWriter writer, String dataSetUid )
        throws IOException, DocumentException, ParseException, Exception
    {

        DataSet dataSet = dataSetService.getDataSet( dataSetUid );

        if(dataSet == null)
        {
            throw new Exception("Error - DataSet not found for UID " + dataSetUid);
        }
        else
        {
        // Step 1. Set DataSet Name/Description
        setDataSet_DocumentTopSection( document, dataSet );

        // Add a line space
        document.add( Chunk.NEWLINE );

        // Step 2. Generate period listings
        List<Period> periods = getPeriods_DataSet( dataSet.getPeriodType() );
        List<String> periodsTitle = PeriodHelper.getPeriodTitle( periods, dataSet.getPeriodType() );
        List<String> periodsValues = PeriodHelper.getPeriodValues( periods, dataSet.getPeriodType() );

        // Step 3. 'Main Table' Create and Set
        // The 'Main Table' is used to hold the layout of the page content.
        PdfPTable mainTable = new PdfPTable( 1 ); // Table with 1 cell.
        setMainTable( mainTable );

        // Step 4. Add Organisation & Period input textfield
        insertTable_OrgAndPeriod( mainTable, writer, periodsTitle.toArray( new String[0] ),
            periodsValues.toArray( new String[0] ) );

        // Step 5. Insert DataSet Info - [The MAIN Section]
        insertTable_DataSet( mainTable, writer, dataSet );

        // Step 6. Add the mainTable with content in it to the document.
        document.add( mainTable );

        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );

        // Step. 7 - Add 'Save As' Button
        insertSaveAsButton( document, writer, PdfDataEntryFormUtil.LABELCODE_BUTTON_SAVEAS );
        }
    }

    private void setDataSet_DocumentTopSection( Document document, DataSet dataSet )
        throws DocumentException
    {
        document.add( new Paragraph( dataSet.getDisplayName(), pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_TITLE ) ) );

        document
            .add( new Paragraph( dataSet.getDescription(), pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_DESCRIPTION ) ) );
    }

    private List<Period> getPeriods_DataSet( PeriodType periodType )
        throws ParseException
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PeriodHelper.PERIOD_DATEFORMAT_DEFAULT );

        Calendar currentDate = Calendar.getInstance();

        int currYear = currentDate.get( Calendar.YEAR );
        int startYear = currYear - PERIODRANGE_PREVYEARS;
        int endYear = currYear + PERIODRANGE_FUTUREYEARS;

        Date startDate = simpleDateFormat.parse( String.valueOf( startYear ) + "-01-01" );
        Date endDate = simpleDateFormat.parse( String.valueOf( endYear ) + "-01-01" );

        return PeriodHelper.getPeriodsFromDateRange( periodType, startDate, endDate );

    }

    private void setMainTable( PdfPTable mainTable )
    {
        mainTable.setWidthPercentage( 90.0f ); // Use 90% of space for Main
                                               // Table Width.
        mainTable.setHorizontalAlignment( Element.ALIGN_LEFT );
    }

    
    private void insertTable_DataSet(PdfPTable mainTable, PdfWriter writer, DataSet dataSet)
        throws IOException, DocumentException
    {
        Rectangle rectangle = new Rectangle( TEXTBOXWIDTH, PdfPCellHelper.CONTENT_HEIGHT_DEFAULT );

        if ( dataSet.getSections().size() > 0 )
        {
            // Sectioned Ones
            for ( Section section : dataSet.getSections() )
            {
                insertTable_DataSetSections( mainTable, writer, rectangle, section.getDataElements(), section.getDisplayName());
            }

        }
        else
        {
            // Default one
            insertTable_DataSetSections( mainTable, writer, rectangle, dataSet.getDataElements(), "");
        }
        
    }
    
    private void insertTable_DataSetSections( PdfPTable mainTable, PdfWriter writer, Rectangle rectangle
        , Collection<DataElement> dataElements, String sectionName )
        throws IOException, DocumentException
    {

            // Add Section Name and Section Spacing
            insertTable_TextRow( writer, mainTable, rectangle, TEXT_BLANK );

            if ( sectionName != "" )
            {
                insertTable_TextRow( writer, mainTable, rectangle, sectionName,
                    pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_SECTIONHEADER ) );
            }

            // Create A Table To Add For Each Section
            PdfPTable table = new PdfPTable( 2 ); // Code 1

            
            for ( DataElement dataElement : dataElements )
            {

                addCell_Text( table, dataElement.getDisplayName(), Element.ALIGN_RIGHT );

                String strFieldLabel = PdfDataEntryFormUtil.LABELCODE_DATAENTRYTEXTFIELD + dataElement.getUid() + "_"
                    + dataElement.getCategoryCombo().getSortedOptionCombos().get( 0 ).getUid();

                String dataElementTextType = dataElement.getType();

                // Yes Only case - render as Checkbox
                if ( dataElementTextType.equals( DataElement.VALUE_TYPE_TRUE_ONLY ) )
                {
                    addCell_WithCheckBox( table, writer, strFieldLabel );
                }
                else if ( dataElementTextType.equals( DataElement.VALUE_TYPE_BOOL ) )
                {
                    // Create Yes - true, No - false, Select..
                    String[] optionList = new String[] { "[No Value]", "Yes", "No" };
                    String[] valueList = new String[] { "", "true", "false" };

                    // addCell_WithRadioButton(table, writer, strFieldLabel);
                    addCell_WithDropDownListField( table, strFieldLabel, optionList, valueList, rectangle, writer );
                }
                else if ( dataElementTextType.equals( DataElement.VALUE_TYPE_NUMBER ) )
                {
                    rectangle = new Rectangle( TEXTBOXWIDTH_NUMBERTYPE, PdfPCellHelper.CONTENT_HEIGHT_DEFAULT );

                    addCell_WithTextField( table, rectangle, writer, strFieldLabel, FieldCell.TYPE_TEXT_NUMBER );
                }
                // if(dataElementTextType.equals(DataElement.VALUE_TYPE_DATE))
                else
                {
                    // NOTE: When Rendering for DataSet, DataElement's OptionSet
                    // does not get rendered.
                    // Only for events, it gets rendered as dropdown list.
                    addCell_WithTextField( table, rectangle, writer, strFieldLabel );

                }

            }

            PdfPCell cell_withInnerTable = new PdfPCell( table );
            cell_withInnerTable.setBorder( Rectangle.NO_BORDER );

            mainTable.addCell( cell_withInnerTable );

        

    }

    // --- Retrieve Document Content - DataSet [END]
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // --- Retrieve Document Content - ProgramStage [START]

    private void setProgramStage_DocumentContent( Document document, PdfWriter writer, String programStageUid )
        throws IOException, DocumentException, ParseException, Exception
    {

        // Get ProgramStage
        ProgramStage programStage = programStageService.getProgramStage( programStageUid );

        if ( programStage == null )
        {
            throw new Exception("Error - ProgramStage not found for UID " + programStageUid);
        }
        else
        {
            
            // STEP 1. Get Rectangle with TextBox Width to be used
            Rectangle rectangle = new Rectangle( 0, 0, TEXTBOXWIDTH, PdfPCellHelper.CONTENT_HEIGHT_DEFAULT );
            
            
            // STEP 2. Create Main Layout table and set the properties
            PdfPTable mainTable = getProgramStageMainTable();
            
            
            // STEP 3. Generate Period List for ProgramStage
            ArrayList<String> arrPeriods = getProgramStagePeriodList();
            
            
            // STEP 4. Add Org Unit, Period, Hidden ProgramStageID Field
            // Add Organisation & Period input textfield
            insertTable_OrgAndPeriod( mainTable, writer, arrPeriods.toArray( new String[0] ),
                arrPeriods.toArray( new String[0] ) );

            insertTable_TextRow( writer, mainTable, rectangle, TEXT_BLANK );

            // Add ProgramStage Field - programStage.getId();
            insertTable_HiddenValue( mainTable, rectangle, writer, PdfDataEntryFormUtil.LABELCODE_PROGRAMSTAGEIDTEXTBOX,
                String.valueOf( programStage.getId() ) );

            
            // STEP 5. Add ProgramStage Content to PDF - [The Main Section]
            insertTable_ProgramStage(mainTable, writer, programStage);
            
            
            // STEP 6. Add the mainTable to document
            document.add( mainTable );

        }

    }

    
    

    private void insertTable_ProgramStage(PdfPTable mainTable, PdfWriter writer, ProgramStage programStage)
        throws IOException, DocumentException
    {
        Rectangle rectangle = new Rectangle( TEXTBOXWIDTH, PdfPCellHelper.CONTENT_HEIGHT_DEFAULT );

        // Add Program Stage Sections
        if ( programStage.getProgramStageSections().size() > 0 )
        {
            // Sectioned Ones
            for ( ProgramStageSection section : programStage.getProgramStageSections() )
            {
                insertTable_ProgramStageSections( mainTable, rectangle, writer, section.getProgramStageDataElements() );
            }

        }
        else
        {
            // Default one
            insertTable_ProgramStageSections( mainTable, rectangle, writer, programStage.getProgramStageDataElements() );
        }
        
    }
    
    
    private void insertTable_ProgramStageSections( PdfPTable mainTable, Rectangle rectangle, PdfWriter writer,
        Collection<ProgramStageDataElement> programStageDataElements )
        throws IOException, DocumentException
    {

        // Add one to column count due to date entry + one hidden height set
        // field.
        int colCount = programStageDataElements.size() + 1 + 1;

        PdfPTable table = new PdfPTable( colCount ); // Code 1

        float totalWidth = 800f;
        float firstCellWidth_dateEntry = UNITSIZE_DEFAULT * 3;
        float lastCellWidth_hidden = UNITSIZE_DEFAULT;
        // float dataElementCell_offset = 4f;
        float dataElementCellWidth = (totalWidth - firstCellWidth_dateEntry - lastCellWidth_hidden)
            / programStageDataElements.size();

        // Create 2 types of Rectangles, one for Date field, one for data
        // elements
        // - to be used when rendering them.
        Rectangle rectangleDate = new Rectangle( 0, 0, UNITSIZE_DEFAULT * 2, UNITSIZE_DEFAULT );
        Rectangle rectangleDataElement = new Rectangle( 0, 0, dataElementCellWidth, UNITSIZE_DEFAULT );

        // Cell Width Set
        float[] cellWidths = new float[colCount];

        // Date Field Settings
        cellWidths[0] = firstCellWidth_dateEntry;

        for ( int i = 1; i < colCount - 1; i++ )
        {
            cellWidths[i] = dataElementCellWidth;
        }

        cellWidths[colCount - 1] = lastCellWidth_hidden;

        table.setWidths( cellWidths );

        // Create Header
        table.addCell( new PdfPCell( new Phrase( "Date" ) ) );

        // Add Program Data Elements Columns
        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            DataElement dataElement = programStageDataElement.getDataElement();

            table.addCell( new PdfPCell( new Phrase( dataElement.getDisplayFormName() ) ) );
        }

        table.addCell( new PdfPCell( new Phrase( TEXT_BLANK ) ) );

        // ADD A HIDDEN INFO FOR ProgramStageID
        // Print rows, having the data elements repeating on each column.

        for ( int rowNo = 1; rowNo <= PROGRAM_FORM_ROW_NUMBER; rowNo++ )
        {

            // Add Date Column
            String strFieldDateLabel = PdfDataEntryFormUtil.LABELCODE_DATADATETEXTFIELD + Integer.toString( rowNo );

            addCell_WithTextField( table, rectangleDate, writer, strFieldDateLabel );

            // Add Program Data Elements Columns
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                DataElement dataElement = programStageDataElement.getDataElement();

                OptionSet optionSet = dataElement.getOptionSet();
                String optionSetName = "";

                // addCell_Text(table, dataElement.getFormName());

                String strFieldLabel = PdfDataEntryFormUtil.LABELCODE_DATAENTRYTEXTFIELD + Integer.toString( dataElement.getId() )
                // + "_" + Integer.toString(programStageId) + "_" +
                // Integer.toString(rowNo);
                    + "_" + Integer.toString( rowNo );

                if ( optionSet != null )
                {
                    optionSetName = optionSet.getName();

                    String query = ""; // Get All Option

                    // TODO: This gets repeated <-- Create an array of the
                    // options. and apply only once.
                    List<String> options = optionService.getOptions( optionSet.getId(), query, MAX_OPTIONS_DISPLAYED );

                    addCell_WithDropDownListField( table, strFieldLabel, options.toArray( new String[0] ),
                        options.toArray( new String[0] ), rectangleDataElement, writer );
                    // (int)(rectangleDataElement.getWidth())
                }
                else
                {
                    // NOTE: When Rendering for DataSet, DataElement's OptionSet
                    // does not get rendered.
                    // Only for events, it gets rendered as dropdown list.
                    addCell_WithTextField( table, rectangleDataElement, writer, strFieldLabel );
                }
            }

            addCell_Text( table, TEXT_BLANK, Element.ALIGN_LEFT );

        }

        PdfPCell cell_withInnerTable = new PdfPCell( table );
        cell_withInnerTable.setBorder( Rectangle.NO_BORDER );

        mainTable.addCell( cell_withInnerTable );

    }

    private ArrayList<String> getProgramStagePeriodList()
    {

        ArrayList<String> arrPeriods = new ArrayList<String>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PeriodHelper.PERIOD_DATEFORMAT_DEFAULT );

        int currYear = Calendar.getInstance().get( Calendar.YEAR );

        for ( int y = currYear - PERIODRANGE_PREVYEARS; y < currYear + PERIODRANGE_FUTUREYEARS; y++ )
        {
            for ( int m = 1; m <= 12; m++ )
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set( y, m, 1 );

                arrPeriods.add( simpleDateFormat.format( calendar.getTime() ) );
            }
        }
                
        return arrPeriods;
    }
    
    private PdfPTable getProgramStageMainTable()
    {
        PdfPTable mainTable = new PdfPTable( 1 ); // Code 1

        mainTable.setTotalWidth( 800f );
        mainTable.setLockedWidth( true );
        mainTable.setHorizontalAlignment( Element.ALIGN_LEFT );  
        
        return mainTable;
    }
    
    // --- Retrieve Document Content - ProgramStage [END]
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // ------------ Insert Section/Item Related Methods [START] ------------

    private void insertTable_OrgAndPeriod( PdfPTable mainTable, PdfWriter writer, String[] periodsTitle,
        String[] periodsValue )
        throws IOException, DocumentException
    {
        // Input TextBox size
        Rectangle rectangle = new Rectangle( TEXTBOXWIDTH, PdfPCellHelper.CONTENT_HEIGHT_DEFAULT );

        // Add Organization ID/Period textfield
        // Create A table to add for each group AT HERE
        PdfPTable table = new PdfPTable( 2 ); // Code 1

        addCell_Text( table, "OrganizationID", Element.ALIGN_LEFT );
        addCell_WithTextField( table, rectangle, writer, PdfDataEntryFormUtil.LABELCODE_ORGID, FieldCell.TYPE_TEXT_ORGUNIT );

        addCell_Text( table, "PeriodID", Element.ALIGN_LEFT );
        addCell_WithDropDownListField( table, PdfDataEntryFormUtil.LABELCODE_PERIODID, periodsTitle, periodsValue, rectangle, writer );

        // Add to the main table
        PdfPCell cell_withInnerTable = new PdfPCell( table );
        // cell_withInnerTable.setPadding(0);
        cell_withInnerTable.setBorder( Rectangle.NO_BORDER );

        cell_withInnerTable.setHorizontalAlignment( Element.ALIGN_LEFT );

        mainTable.addCell( cell_withInnerTable );
    }

    private void insertTable_HiddenValue( PdfPTable mainTable, Rectangle rectangle, PdfWriter writer, String fieldName,
        String value )
        throws IOException, DocumentException
    {

        // Add Organization ID/Period textfield
        // Create A table to add for each group AT HERE
        PdfPTable table = new PdfPTable( 1 ); // Code 1

        addCell_WithTextField( table, rectangle, writer, fieldName, value );

        // Add to the main table
        PdfPCell cell_withInnerTable = new PdfPCell( table );
        // cell_withInnerTable.setPadding(0);
        cell_withInnerTable.setBorder( Rectangle.NO_BORDER );
        mainTable.addCell( cell_withInnerTable );
    }

    private void insertTable_TextRow( PdfWriter writer, PdfPTable mainTable, Rectangle rectangle, String text )
        throws IOException, DocumentException
    {
        insertTable_TextRow( writer, mainTable, rectangle, text, pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_BODY ) );
    }

    private void insertTable_TextRow( PdfWriter writer, PdfPTable mainTable, Rectangle rectangle, String text, Font font )
        throws IOException, DocumentException
    {

        // Add Organization ID/Period textfield
        // Create A table to add for each group AT HERE
        PdfPTable table = new PdfPTable( 1 );
        table.setHorizontalAlignment( Element.ALIGN_LEFT );

        addCell_Text( table, text, Element.ALIGN_LEFT, font );

        // Add to the main table
        PdfPCell cell_withInnerTable = new PdfPCell( table );
        cell_withInnerTable.setBorder( Rectangle.NO_BORDER );
        mainTable.addCell( cell_withInnerTable );
    }

    // Insert 'Save As' button to document.
    private void insertSaveAsButton( Document document, PdfWriter writer, String name )
        throws DocumentException
    {
        // Button Table
        PdfPTable tableButton = new PdfPTable( 1 );

        tableButton.setWidthPercentage( 20.0f );
        float buttonHeight = UNITSIZE_DEFAULT + 5;

        tableButton.setHorizontalAlignment( PdfPTable.ALIGN_CENTER );

        String jsAction = "app.execMenuItem('SaveAs');";

        addCell_WithPushButtonField( tableButton, name, buttonHeight, jsAction, writer );

        document.add( tableButton );
    }

    // ------------ Insert Section/Item Related Methods [END] ------------
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // ------------ Add Control To Cell Related Methods [START] ------------

    private void addCell_Text( PdfPTable table, String text, int horizontalAlignment )
    {
        addCell_Text( table, text, horizontalAlignment, pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_BODY ) );
    }

    private void addCell_Text( PdfPTable table, String text, int horizontalAlignment, Font font )
    {
        PdfPCell cell = PdfPCellHelper.getPdfPCell( PdfPCellHelper.CELL_MIN_HEIGHT_DEFAULT,
            PdfPCellHelper.CELL_COLUMN_TYPE_LABEL );

        cell.setHorizontalAlignment( horizontalAlignment );

        cell.setPhrase( new Phrase( text, font ) );

        table.addCell( cell );
    }

    private void addCell_WithTextField( PdfPTable table, Rectangle rect, PdfWriter writer, String strfldName )
        throws IOException, DocumentException
    {
        addCell_WithTextField( table, rect, writer, strfldName, FieldCell.TYPE_DEFAULT, "" );
    }

    private void addCell_WithTextField( PdfPTable table, Rectangle rect, PdfWriter writer, String strfldName,
        int fieldCellType )
        throws IOException, DocumentException
    {
        addCell_WithTextField( table, rect, writer, strfldName, fieldCellType, "" );
    }

    private void addCell_WithTextField( PdfPTable table, Rectangle rect, PdfWriter writer, String strfldName,
        String value )
        throws IOException, DocumentException
    {
        addCell_WithTextField( table, rect, writer, strfldName, FieldCell.TYPE_DEFAULT, value );
    }

    private void addCell_WithTextField( PdfPTable table, Rectangle rect, PdfWriter writer, String strfldName,
        int fieldCellType, String value )
        throws IOException, DocumentException
    {
        TextField nameField = new TextField( writer, rect, strfldName );
        // table.getc

        nameField.setBorderWidth( 1 );
        nameField.setBorderColor( Color.BLACK );
        nameField.setBorderStyle( PdfBorderDictionary.STYLE_SOLID );
        nameField.setBackgroundColor( COLOR_BACKGROUDTEXTBOX );

        nameField.setText( value );

        nameField.setAlignment( Element.ALIGN_RIGHT );
        nameField.setFontSize( UNITSIZE_DEFAULT );

        PdfPCell cell = PdfPCellHelper.getPdfPCell( PdfPCellHelper.CELL_MIN_HEIGHT_DEFAULT,
            PdfPCellHelper.CELL_COLUMN_TYPE_ENTRYFIELD );
        cell.setCellEvent( new FieldCell( nameField.getTextField(), (int) (rect.getWidth()), fieldCellType, writer ) );

        table.addCell( cell );
    }

    private void addCell_WithDropDownListField( PdfPTable table, String strfldName, String[] optionList,
        String[] valueList, Rectangle rect, PdfWriter writer )
        throws IOException, DocumentException
    {

        // If there is option, then create name-value set in 2 dimension array
        // and set it as dropdown option name-value list.
        String[][] optionValueList = new String[optionList.length][2];

        for ( int i = 0; i < optionList.length; i++ )
        {
            optionValueList[i][1] = optionList[i];
            optionValueList[i][0] = valueList[i];
        }

        // Code 2 create DROP-DOWN LIST
        PdfFormField dropDown = PdfFormField.createCombo( writer, true, optionValueList, 0 );

        dropDown.setWidget( rect, PdfAnnotation.HIGHLIGHT_INVERT );
        dropDown.setFieldName( strfldName );

        dropDown.setMKBorderColor( CMYKColor.BLACK );

        // Combine

        PdfPCell cell = PdfPCellHelper.getPdfPCell( PdfPCellHelper.CELL_MIN_HEIGHT_DEFAULT,
            PdfPCellHelper.CELL_COLUMN_TYPE_ENTRYFIELD );
        cell.setCellEvent( new FieldCell( dropDown, (int) (rect.getWidth()), writer ) );

        table.addCell( cell );
    }

    private void addCell_WithCheckBox( PdfPTable table, PdfWriter writer, String strfldName )
        throws IOException, DocumentException
    {
        float sizeDefault = UNITSIZE_DEFAULT;

        PdfContentByte canvas = writer.getDirectContent();

        PdfAppearance[] onOff = new PdfAppearance[2];
        onOff[0] = canvas.createAppearance( sizeDefault + 2, sizeDefault + 2 );
        onOff[0].rectangle( 1, 1, sizeDefault, sizeDefault );
        onOff[0].stroke();
        onOff[1] = canvas.createAppearance( sizeDefault + 2, sizeDefault + 2 );
        onOff[1].setRGBColorFill( 255, 128, 128 );
        onOff[1].rectangle( 1, 1, sizeDefault, sizeDefault );
        onOff[1].fillStroke();
        onOff[1].moveTo( 1, 1 );
        onOff[1].lineTo( sizeDefault + 1, sizeDefault + 1 );
        onOff[1].moveTo( 1, sizeDefault + 1 );
        onOff[1].lineTo( sizeDefault + 1, 1 );
        onOff[1].stroke();

        Rectangle rect = new Rectangle( sizeDefault, sizeDefault );

        RadioCheckField checkbox = new RadioCheckField( writer, rect, "Yes", "on" );
        checkbox.setBorderWidth( 1 );
        checkbox.setBorderColor( Color.BLACK );

        PdfFormField checkboxfield = checkbox.getCheckField();
        checkboxfield.setFieldName( strfldName );

        checkboxfield.setAppearance( PdfAnnotation.APPEARANCE_NORMAL, "", onOff[0] );
        checkboxfield.setAppearance( PdfAnnotation.APPEARANCE_NORMAL, "true", onOff[1] );

        PdfPCell cell = PdfPCellHelper.getPdfPCell( PdfPCellHelper.CELL_MIN_HEIGHT_DEFAULT,
            PdfPCellHelper.CELL_COLUMN_TYPE_ENTRYFIELD );

        cell.setCellEvent( new FieldCell( checkboxfield, (int) (rect.getWidth()), writer ) );

        table.addCell( cell );
    }

    private void addCell_WithRadioButton( PdfPTable table, PdfWriter writer, String strfldName )
        throws IOException, DocumentException
    {

        // Add to the main table
        PdfPCell cell = PdfPCellHelper.getPdfPCell( PdfPCellHelper.CELL_MIN_HEIGHT_DEFAULT,
            PdfPCellHelper.CELL_COLUMN_TYPE_ENTRYFIELD );

        // RADIO BUTTON FIELD
        PdfFormField radiogroupField = PdfFormField.createRadioButton( writer, true );
        radiogroupField.setFieldName( strfldName );

        cell.setCellEvent( new FieldCell( radiogroupField, new String[] { "Yes", "No", "null" }, new String[] { "true",
            "false", "" }, "", 30.0f, FieldCell.TYPE_RADIOBUTTON, writer ) );

        table.addCell( cell );

        // Last - Add Annotation
        writer.addAnnotation( radiogroupField );

    }

    private void addCell_WithPushButtonField( PdfPTable table, String strfldName, float buttonHeight, String jsAction,
        PdfWriter writer )
    {
        PdfPCell cell = PdfPCellHelper.getPdfPCell( buttonHeight, PdfPCellHelper.CELL_COLUMN_TYPE_ENTRYFIELD );
        cell.setCellEvent( new FieldCell( null, jsAction, "BTN_SAVEPDF", "Save PDF", FieldCell.TYPE_BUTTON, writer ) );

        table.addCell( cell );
    }

    // ------------ Add Control To Cell Related Methods [END] ------------
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // ------------ Helper Classes [START] ------------

    static class PdfPCellHelper
    {
        public final static float CELL_MIN_HEIGHT_DEFAULT = 13;

        public final static float CONTENT_HEIGHT_DEFAULT = 11;

        public final static int CELL_COLUMN_TYPE_LABEL = 0;

        public final static int CELL_COLUMN_TYPE_ENTRYFIELD = 1;

        public static PdfPCell getPdfPCell( float minHeight )
        {
            return getPdfPCell( minHeight, CELL_COLUMN_TYPE_LABEL );
        }

        public static PdfPCell getPdfPCell( float minHeight, int cellContentType )
        {
            PdfPCell cell = new PdfPCell();
            cell.setMinimumHeight( minHeight );
            cell.setBorder( Rectangle.NO_BORDER );

            if ( cellContentType == CELL_COLUMN_TYPE_LABEL )
            {
                cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
                cell.setVerticalAlignment( Element.ALIGN_TOP );
            }
            else if ( cellContentType == CELL_COLUMN_TYPE_ENTRYFIELD )
            {
                cell.setHorizontalAlignment( Element.ALIGN_CENTER );
                cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
            }

            return cell;
        }

    }

    private static class PeriodHelper
    {

        public static final String PERIOD_DATEFORMAT_DEFAULT = "yyyy-MM-dd";

        private static final String PERIOD_DATEFORMAT_MONTHLY = "MMMM_yyyy";

        private static final String PERIOD_DATEFORMAT_MONTH = "MMMM";

        private static final String PERIOD_DATEFORMAT_YEAR = "yyyy";

        public static List<String> getPeriodValues( List<Period> periods, PeriodType periodType )
            throws ParseException
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_DEFAULT );

            // Display Period info for proper listings
            ArrayList<String> arrPeriods = new ArrayList<String>();

            String periodTypeName = periodType.getName();

            for ( Period period : periods )
            {
                arrPeriods.add( periodTypeName + "_" + simpleDateFormat.format( period.getStartDate() ) );
            }

            return arrPeriods;
        }

        public static List<String> getPeriodTitle( List<Period> periods, PeriodType periodType )
        {
            List<String> periodTitles = new ArrayList<String>();

            String periodTypeName = periodType.getName();

            if ( periodTypeName == "Daily" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_DEFAULT );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) );
                }

            }
            else if ( periodTypeName == "Weekly" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_DEFAULT );
                int count = 1;

                for ( Period period : periods )
                {
                    periodTitles.add( "W" + String.valueOf( count ) + " - "
                        + simpleDateFormat.format( period.getStartDate() ) + " - "
                        + simpleDateFormat.format( period.getEndDate() ) );

                    count++;
                }

            }
            else if ( periodTypeName == "Monthly" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_MONTHLY );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) );
                }
            }
            else if ( periodTypeName == "BiMonthly" || periodTypeName == "Quarterly" || periodTypeName == "SixMonthly" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_MONTH );
                SimpleDateFormat simpleDateFormat_Year = new SimpleDateFormat( PERIOD_DATEFORMAT_YEAR );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) + " - "
                        + simpleDateFormat.format( period.getEndDate() ) + " "
                        + simpleDateFormat_Year.format( period.getStartDate() ) );
                }
            }
            else if ( periodTypeName == "Yearly" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_YEAR );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) );
                }
            }
            else if ( periodTypeName == "FinancialOct" || periodTypeName == "FinancialJuly"
                || periodTypeName == "FinancialApril" )
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_MONTH );
                SimpleDateFormat simpleDateFormat_Year = new SimpleDateFormat( PERIOD_DATEFORMAT_YEAR );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) + " "
                        + simpleDateFormat_Year.format( period.getStartDate() ) + " - "
                        + simpleDateFormat.format( period.getEndDate() ) + " "
                        + simpleDateFormat_Year.format( period.getStartDate() ) );
                }
            }
            else
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( PERIOD_DATEFORMAT_DEFAULT );

                for ( Period period : periods )
                {
                    periodTitles.add( simpleDateFormat.format( period.getStartDate() ) );
                }
            }

            return periodTitles;
        }

        public static List<Period> getPeriodsFromDateRange( PeriodType periodType, Date startDate, Date endDate )
        {
            String periodTypeName = periodType.getName();

            CalendarPeriodType periodTypeCal = (CalendarPeriodType) PeriodType.getPeriodTypeByName( periodTypeName );

            return periodTypeCal.generatePeriods( startDate, endDate );
        }

    }

    
    class FieldCell
        implements PdfPCellEvent
    {

        public final static int TYPE_DEFAULT = 0;

        public final static int TYPE_BUTTON = 1;

        public final static int TYPE_TEXT_ORGUNIT = 2;

        public final static int TYPE_TEXT_NUMBER = 3;

        public final static int TYPE_CHECKBOX = 4;

        public final static int TYPE_RADIOBUTTON = 5;

        private final static float RADIOBUTTON_WIDTH = 10.0f;

        private final static float RADIOBUTTON_TEXTOFFSET = 3.0f;

        private PdfFormField parent;

        private PdfFormField formField;

        private PdfWriter writer;

        private float width;

        private int type;

        private String jsAction;

        private String[] values;

        private String[] texts;

        private String checkValue;

        private String text;

        private String name;

        public FieldCell( PdfFormField formField, int width, PdfWriter writer )
        {
            this.formField = formField;
            this.width = width;
            this.writer = writer;
            this.type = TYPE_DEFAULT;
        }

        public FieldCell( PdfFormField formField, int width, int type, PdfWriter writer )
        {
            this.formField = formField;
            this.width = width;
            this.writer = writer;
            this.type = type;
        }

        public FieldCell( PdfFormField formField, String jsAction, String name, String text, int type, PdfWriter writer )
        {
            this.formField = formField;
            this.writer = writer;
            this.type = type;
            this.name = name;
            this.text = text;
            this.jsAction = jsAction;
        }

        public FieldCell( PdfFormField parent, String[] texts, String[] values, String checkValue, float width,
            int type, PdfWriter writer )
        {
            this.writer = writer;
            this.type = type;
            this.parent = parent;
            this.texts = texts;
            this.values = values;
            this.checkValue = checkValue;
            this.width = width;
        }

        public void cellLayout( PdfPCell cell, Rectangle rect, PdfContentByte[] canvas )
        {
            try
            {

                PdfContentByte cb = null;

                if ( type == TYPE_RADIOBUTTON )
                {
                    cb = canvas[PdfPTable.TEXTCANVAS];
                }
                else
                {
                    cb = canvas[PdfPTable.LINECANVAS];
                }

                switch ( type )
                {

                case TYPE_RADIOBUTTON:

                    if ( parent != null )
                    {

                        float leftLoc = rect.getLeft();
                        float rightLoc = rect.getLeft() + RADIOBUTTON_WIDTH;

                        try
                        {

                            String text;
                            String value;

                            for ( int i = 0; i < texts.length; i++ )
                            {

                                text = texts[i];
                                value = values[i];

                                Rectangle radioRec = new Rectangle( leftLoc, rect.getBottom(), rightLoc, rect.getTop() );

                                RadioCheckField rf = new RadioCheckField( writer, radioRec, "RDBtn_" + text, value );

                                if ( value == checkValue )
                                    rf.setChecked( true );

                                rf.setBorderColor( GrayColor.GRAYBLACK );
                                rf.setBackgroundColor( GrayColor.GRAYWHITE );
                                rf.setCheckType( RadioCheckField.TYPE_CIRCLE );

                                parent.addKid( rf.getRadioField() );

                                leftLoc = rightLoc;
                                rightLoc += width;

                                ColumnText.showTextAligned( cb, Element.ALIGN_LEFT, new Phrase( text ), leftLoc
                                    + RADIOBUTTON_TEXTOFFSET, (radioRec.getBottom() + radioRec.getTop()) / 2, 0 );

                                leftLoc = rightLoc;
                                rightLoc += RADIOBUTTON_WIDTH;

                            }

                        }
                        catch ( Exception ex )
                        {
                            throw new RuntimeException( ex.getMessage() );
                        }

                        writer.addAnnotation( parent );

                    }

                    break;

                case TYPE_BUTTON:
                    // Add the push button
                    PushbuttonField button = new PushbuttonField( writer, rect, name );
                    button.setBackgroundColor( new GrayColor( 0.75f ) );
                    button.setBorderColor( GrayColor.GRAYBLACK );
                    button.setBorderWidth( 1 );
                    button.setBorderStyle( PdfBorderDictionary.STYLE_BEVELED );
                    button.setTextColor( GrayColor.GRAYBLACK );
                    button.setFontSize( UNITSIZE_DEFAULT );
                    button.setText( text );
                    button.setLayout( PushbuttonField.LAYOUT_ICON_LEFT_LABEL_RIGHT );
                    button.setScaleIcon( PushbuttonField.SCALE_ICON_ALWAYS );
                    button.setProportionalIcon( true );
                    button.setIconHorizontalAdjustment( 0 );

                    formField = button.getField();
                    formField.setAction( PdfAction.javaScript( jsAction, writer ) );

                    break;

                case TYPE_CHECKBOX:

                    // Start from the middle of the cell width.
                    float startingPoint = rect.getLeft() + ((rect.getWidth() + width) / 2.0f);

                    formField.setWidget(
                        new Rectangle( startingPoint, rect.getBottom(), startingPoint + width, rect.getTop() ),
                        PdfAnnotation.HIGHLIGHT_NONE );

                    break;

                case TYPE_TEXT_ORGUNIT:
                    formField.setAdditionalActions( PdfName.BL, PdfAction.javaScript(
                        "if(event.value == '') app.alert('Warning! Please Enter The Org ID.');", writer ) );

                case TYPE_TEXT_NUMBER:

                default:

                    formField.setWidget(
                        new Rectangle( rect.getLeft(), rect.getBottom(), rect.getLeft() + width, rect.getTop() ),
                        PdfAnnotation.HIGHLIGHT_NONE );

                    break;

                }

                writer.addAnnotation( formField );

            }
            catch ( Exception ex )
            {
                throw new RuntimeException( ex.getMessage() );
            }
        }
    }

    // ------------ Helper Classes [END] ------------
    // -----------------------------------------------------------------------------

}
