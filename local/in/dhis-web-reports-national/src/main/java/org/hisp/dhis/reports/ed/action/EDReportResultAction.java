package org.hisp.dhis.reports.ed.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class EDReportResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private Integer selectedStartPeriodId;

    public void setSelectedStartPeriodId( Integer selectedStartPeriodId )
    {
        this.selectedStartPeriodId = selectedStartPeriodId;
    }

    private Integer selectedEndPeriodId;

    public void setSelectedEndPeriodId( Integer selectedEndPeriodId )
    {
        this.selectedEndPeriodId = selectedEndPeriodId;
    }

    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        raFolderName = reportService.getRAFolderName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );

        List<Integer> headerInfo = getInfoFromXMLForEDReport();

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "EDReport", 0 );

        if ( headerInfo == null || headerInfo.size() == 0 )
        {
            System.out.println( "There is problem with report xml file, please check" );
            sheet0.addCell( new Label( 2, 2, "There is problem with report xml file, please check", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "EDReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();
            statementManager.destroy();
            return SUCCESS;
        }

        Period selectedStartPeriod = periodService.getPeriod( selectedStartPeriodId );
        Period selectedEndPeriod = periodService.getPeriod( selectedEndPeriodId );

        if ( selectedStartPeriod == null || selectedEndPeriod == null )
        {
            System.out.println( "There is no period with that id" );
            sheet0.addCell( new Label( 2, 2, "There is no period with that id", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "EDReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();
            statementManager.destroy();
            return SUCCESS;
        }

        // HardCoded with Bihar State OrgUnitId - 7
        OrganisationUnit selectedOrgUnit = organisationUnitService.getOrganisationUnit( headerInfo.get( 0 ) );

        if ( selectedOrgUnit == null )
        {
            System.out.println( "There is no orgunit with that id" );
            sheet0.addCell( new Label( 2, 2, "There is no orgunit with that id", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "EDReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();
            statementManager.destroy();

            return SUCCESS;
        }

        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
        Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
        orgUnitList.add( selectedOrgUnit );

        // HardCoded with ED IndicatorGroup - 12
        IndicatorGroup selectedIndicatorGroup = indicatorService.getIndicatorGroup( headerInfo.get( 1 ) );

        if ( selectedIndicatorGroup == null )
        {
            System.out.println( "There is no IndicatorGroup with that id" );
            sheet0.addCell( new Label( 2, 2, "There is no IndicatorGroup with that id", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "EDReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();

            statementManager.destroy();
            return SUCCESS;
        }

        List<Indicator> indicators = new ArrayList<Indicator>( selectedIndicatorGroup.getMembers() );

        int rowCount = 4;
        int colCount = 0;

        // Printing Header Info
        sheet0.mergeCells( colCount, rowCount, colCount, rowCount + 1 );
        sheet0.addCell( new Label( colCount++, rowCount, "Sl. No.", getCellFormat1() ) );
        sheet0.mergeCells( colCount, rowCount, colCount, rowCount + 1 );
        sheet0.addCell( new Label( colCount++, rowCount, "Facility", getCellFormat1() ) );

        for ( Indicator indicator : indicators )
        {
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getName(), getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Numerator", getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Denominator", getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Indicator", getCellFormat1() ) );

            // colCount += 3;
        }

        // Printing Main Header Info
        String mainHeaderInfo = "Key Indicator Analysis - " + selectedOrgUnit.getName() + " From : "
            + simpleDateFormat.format( selectedStartPeriod.getStartDate() ) + " To : "
            + simpleDateFormat.format( selectedEndPeriod.getStartDate() );
        sheet0.mergeCells( 0, 1, colCount - 1, 1 );
        sheet0.addCell( new Label( 0, 1, mainHeaderInfo, getCellFormat1() ) );

        rowCount += 2;
        int slno = 1;
        for ( OrganisationUnit ou : orgUnitList )
        {
            colCount = 0;

            if ( slno != orgUnitList.size() )
            {
                sheet0.addCell( new Number( colCount++, rowCount, slno, getCellFormat2() ) );
            }
            else
            {
                sheet0.addCell( new Label( colCount++, rowCount, "", getCellFormat2() ) );
            }
            sheet0.addCell( new Label( colCount++, rowCount, ou.getName(), getCellFormat2() ) );

            for ( Indicator indicator : indicators )
            {
                double numValue = aggregationService.getAggregatedNumeratorValue( indicator, selectedStartPeriod
                    .getStartDate(), selectedEndPeriod.getEndDate(), ou );
                double denValue = aggregationService.getAggregatedDenominatorValue( indicator, selectedStartPeriod
                    .getStartDate(), selectedEndPeriod.getEndDate(), ou );
                double indValue = aggregationService.getAggregatedIndicatorValue( indicator, selectedStartPeriod
                    .getStartDate(), selectedEndPeriod.getEndDate(), ou );

                if ( indValue == -1 )
                    indValue = 0;

                numValue = Math.round( numValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                denValue = Math.round( denValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                sheet0.addCell( new Number( colCount++, rowCount, numValue, getCellFormat2() ) );
                sheet0.addCell( new Number( colCount++, rowCount, denValue, getCellFormat2() ) );
                sheet0.addCell( new Number( colCount++, rowCount, indValue, getCellFormat1() ) );
            }

            slno++;
            rowCount++;
        }

        // Printing Indicator Formula Info
        rowCount++;
        colCount = 2;

        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Indicator Name", getCellFormat1() ) );
        colCount += 3;
        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Numerator Desciption", getCellFormat1() ) );
        colCount += 3;
        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Denominator Description", getCellFormat1() ) );

        rowCount++;

        for ( Indicator indicator : indicators )
        {
            colCount = 2;

            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getName(), getCellFormat2() ) );
            colCount += 3;
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getNumeratorDescription(), getCellFormat2() ) );
            colCount += 3;
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getDenominatorDescription(), getCellFormat2() ) );

            rowCount++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "EDReport_" + simpleDateFormat.format( selectedStartPeriod.getStartDate() ) + "_"
            + simpleDateFormat.format( selectedEndPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }

    public List<Integer> getInfoFromXMLForEDReport()
    {
        String fileName = "edReportInfo.xml";
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;

        List<Integer> headerInfo = new ArrayList<Integer>();
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DHIS2 HOME is not set" );
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "report" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    NodeList orgUnitList = reportElement.getElementsByTagName( "orgunit" );
                    Element orgUnitElement = (Element) orgUnitList.item( 0 );
                    NodeList textOrgUnitList = orgUnitElement.getChildNodes();
                    String orgUnitIdStr = ((Node) textOrgUnitList.item( 0 )).getNodeValue().trim();

                    headerInfo.add( Integer.parseInt( orgUnitIdStr ) );

                    NodeList indicatorGroupList = reportElement.getElementsByTagName( "indicatorgroup" );
                    Element indicatorGroupElement = (Element) indicatorGroupList.item( 0 );
                    NodeList textindicatorGroupList = indicatorGroupElement.getChildNodes();
                    String indicatorGroupIdStr = ((Node) textindicatorGroupList.item( 0 )).getNodeValue().trim();

                    headerInfo.add( Integer.parseInt( indicatorGroupIdStr ) );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return headerInfo;
    }

    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

}
