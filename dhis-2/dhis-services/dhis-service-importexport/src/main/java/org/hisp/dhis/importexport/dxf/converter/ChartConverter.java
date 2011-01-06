package org.hisp.dhis.importexport.dxf.converter;

import java.util.Collection;
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.ChartImporter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;

public class ChartConverter
    extends ChartImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "charts";
    public static final String ELEMENT_NAME = "chart";

    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_SIZE = "size";
    private static final String FIELD_DIMENSION = "dimension";
    private static final String FIELD_HIDE_LEGEND = "hideLegend";
    private static final String FIELD_VERTICAL_LABELS = "verticalLabels";
    private static final String FIELD_HORIZONTAL_PLOT_ORIENTATION = "horizontalPlotOrientation";
    private static final String FIELD_REGRESSION = "regression";

    private static final String FIELD_INDICATORS = "indicators";
    private static final String FIELD_PERIODS = "periods";
    private static final String FIELD_ORGANISATION_UNITS = "organisationUnits";

    private static final String FIELD_REPORTING_MONTH = "reportingMonth";
    private static final String FIELD_MONTHS_THIS_YEAR = "monthsThisYear";
    private static final String FIELD_QUARTERS_THIS_YEAR = "quartersThisYear";
    private static final String FIELD_THIS_YEAR = "thisYear";
    private static final String FIELD_MONTHS_LAST_YEAR = "monthsLastYear";
    private static final String FIELD_QUARTERS_LAST_YEAR = "quartersLastYear";
    private static final String FIELD_LAST_YEAR = "lastYear";
    
    private IndicatorService indicatorService;
    private PeriodService periodService;
    private OrganisationUnitService organisationUnitService;

    private Map<Object, Integer> indicatorMapping;
    private Map<Object, Integer> periodMapping;
    private Map<Object, Integer> organisationUnitMapping;

    /**
     * Constructor for write operations.
     * 
     * @param chartService
     */
    public ChartConverter( ChartService chartService )
    {
        this.chartService = chartService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param chartService
     */
    public ChartConverter( ChartService chartService, 
        ImportObjectService importObjectService, 
        IndicatorService indicatorService, 
        PeriodService periodService,
        OrganisationUnitService organisationUnitService,
        Map<Object, Integer> indicatorMapping,
        Map<Object, Integer> periodMapping,
        Map<Object, Integer> organisationUnitMapping )
    {
        this.chartService = chartService;
        this.importObjectService = importObjectService;
        this.indicatorService = indicatorService;
        this.periodService = periodService;
        this.organisationUnitService = organisationUnitService;
        this.indicatorMapping = indicatorMapping;
        this.periodMapping = periodMapping;
        this.organisationUnitMapping = organisationUnitMapping;
    }
    
    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Chart> charts = chartService.getCharts( params.getCharts() );
        
        if ( charts != null && charts.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Chart chart : charts )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( chart.getId() ) );
                writer.writeElement( FIELD_TITLE, String.valueOf( chart.getTitle() ) );
                writer.writeElement( FIELD_TYPE, String.valueOf( chart.getType() ) );
                writer.writeElement( FIELD_SIZE, String.valueOf( chart.getSize() ) );
                writer.writeElement( FIELD_DIMENSION, String.valueOf( chart.getDimension() ) );
                writer.writeElement( FIELD_HIDE_LEGEND, String.valueOf( chart.isHideLegend() ) );
                writer.writeElement( FIELD_VERTICAL_LABELS, String.valueOf( chart.isVerticalLabels() ) );
                writer.writeElement( FIELD_HORIZONTAL_PLOT_ORIENTATION, String.valueOf( chart.isHorizontalPlotOrientation() ) );
                writer.writeElement( FIELD_REGRESSION, String.valueOf( chart.isRegression() ) );                

                writer.openElement( FIELD_INDICATORS );
                for ( Indicator indicator : chart.getIndicators() )
                {
                    writer.writeElement( FIELD_ID, String.valueOf( indicator.getId() ) );
                }
                writer.closeElement();

                writer.openElement( FIELD_PERIODS );
                for ( Period period : chart.getPeriods() )
                {
                    writer.writeElement( FIELD_ID, String.valueOf( period.getId() ) );
                }
                writer.closeElement();
                
                writer.openElement( FIELD_ORGANISATION_UNITS );
                for ( OrganisationUnit unit : chart.getOrganisationUnits() )
                {
                    writer.writeElement( FIELD_ID, String.valueOf( unit.getId() ) );
                }
                writer.closeElement();                

                writer.writeElement( FIELD_REPORTING_MONTH, String.valueOf( chart.getRelatives().isReportingMonth() ) );
                writer.writeElement( FIELD_MONTHS_THIS_YEAR, String.valueOf( chart.getRelatives().isMonthsThisYear() ) );
                writer.writeElement( FIELD_QUARTERS_THIS_YEAR, String.valueOf( chart.getRelatives().isQuartersThisYear() ) );
                writer.writeElement( FIELD_THIS_YEAR, String.valueOf( chart.getRelatives().isThisYear() ) );
                writer.writeElement( FIELD_MONTHS_LAST_YEAR, String.valueOf( chart.getRelatives().isMonthsLastYear() ) );
                writer.writeElement( FIELD_QUARTERS_LAST_YEAR, String.valueOf( chart.getRelatives().isQuartersLastYear() ) );
                writer.writeElement( FIELD_LAST_YEAR, String.valueOf( chart.getRelatives().isLastYear() ) );

                writer.closeElement();
            }

            writer.closeElement();
        }        
    }
    
    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Chart chart = new Chart();
            
            final RelativePeriods relatives = new RelativePeriods();
            chart.setRelatives( relatives );

            reader.moveToStartElement( FIELD_ID );
            chart.setId( Integer.parseInt( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_TITLE );
            chart.setTitle( reader.getElementValue() );

            reader.moveToStartElement( FIELD_TYPE );
            chart.setType( reader.getElementValue() );

            reader.moveToStartElement( FIELD_SIZE );
            chart.setSize( reader.getElementValue() );

            reader.moveToStartElement( FIELD_DIMENSION );
            chart.setDimension( reader.getElementValue() );

            reader.moveToStartElement( FIELD_HIDE_LEGEND );
            chart.setHideLegend( Boolean.parseBoolean( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_VERTICAL_LABELS );
            chart.setVerticalLabels( Boolean.parseBoolean( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_HORIZONTAL_PLOT_ORIENTATION );
            chart.setHorizontalPlotOrientation( Boolean.parseBoolean( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_REGRESSION );
            chart.setRegression( Boolean.parseBoolean( reader.getElementValue() ) );
            
            while ( reader.moveToStartElement( FIELD_ID, FIELD_INDICATORS ) )
            {
                int id = Integer.parseInt( reader.getElementValue() );
                chart.getIndicators().add( indicatorService.getIndicator( indicatorMapping.get( id ) ) );
            }

            while ( reader.moveToStartElement( FIELD_ID, FIELD_PERIODS ) )
            {
                int id = Integer.parseInt( reader.getElementValue() );
                chart.getPeriods().add( periodService.getPeriod( periodMapping.get( id ) ) );
            }

            while ( reader.moveToStartElement( FIELD_ID, FIELD_ORGANISATION_UNITS ) )
            {
                int id = Integer.parseInt( reader.getElementValue() );
                chart.getOrganisationUnits().add( organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( id ) ) );
            }
            
            if ( params.minorVersionGreaterOrEqual( DXFConverter.MINOR_VERSION_12 ) )
            {
                reader.moveToStartElement( FIELD_REPORTING_MONTH );          
                chart.getRelatives().setReportingMonth( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_MONTHS_THIS_YEAR );
                chart.getRelatives().setMonthsThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_QUARTERS_THIS_YEAR );
                chart.getRelatives().setQuartersThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_THIS_YEAR );
                chart.getRelatives().setThisYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_MONTHS_LAST_YEAR );
                chart.getRelatives().setMonthsLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_QUARTERS_LAST_YEAR );
                chart.getRelatives().setQuartersLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
                
                reader.moveToStartElement( FIELD_LAST_YEAR );
                chart.getRelatives().setLastYear( Boolean.parseBoolean( reader.getElementValue() ) );
            }
            
            importObject( chart, params );
        }
    }
}
