package org.hisp.dhis.importexport.dhis14.file.rowhandler;

import static org.hisp.dhis.importexport.dhis14.util.Dhis14ExpressionConverter.convertExpressionFromDhis14;

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.object.Dhis14CalculatedDataElement;
import org.hisp.dhis.importexport.importer.IndicatorImporter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.period.YearlyPeriodType;

import com.ibatis.sqlmap.client.event.RowHandler;

public class CaculatedDataElementRowHandler
    extends IndicatorImporter
    implements RowHandler
{
    private Map<Object, Integer> indicatorTypeMap;

    private Map<Object, Integer> dataElementMap;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private ImportParams params;

    private Indicator indicator;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CaculatedDataElementRowHandler( BatchHandler<Indicator> batchHandler,
        ImportObjectService importObjectService, IndicatorService indicatorService,
        Map<Object, Integer> indicatorTypeMap, Map<Object, Integer> dataElementMap,
        DataElementCategoryOptionCombo categoryOptionCombo, ImportParams params, ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.indicatorService = indicatorService;
        this.indicatorTypeMap = indicatorTypeMap;
        this.dataElementMap = dataElementMap;
        this.categoryOptionCombo = categoryOptionCombo;
        this.params = params;
        this.importAnalyser = importAnalyser;
        this.indicator = new Indicator();
    }

    // -------------------------------------------------------------------------
    // RowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {

        final Dhis14CalculatedDataElement dhis14CalcDE = (Dhis14CalculatedDataElement) object;

        if ( indicator.getCode() != null && indicator.getCode().trim().length() == 0 )
        {
            indicator.setCode( null );
        }

        indicator.setId( dhis14CalcDE.getDataElementId() );
        indicator.setName( dhis14CalcDE.getDataElementName() );
        indicator.setShortName( dhis14CalcDE.getDataElementShort() );
        indicator.setDescription( dhis14CalcDE.getDataElementDescription() );
        indicator.getIndicatorType().setId( 1151589 );
        indicator.setSortOrder( dhis14CalcDE.getSortOrder() );
        indicator.setLastUpdated( dhis14CalcDE.getLastUpdated() );
        indicator.setUid( dhis14CalcDE.getUid() );

        indicator.setNumerator( convertExpressionFromDhis14( indicator.getNumerator(), dataElementMap,
            categoryOptionCombo.getId(), dhis14CalcDE.getDataElementName() ) );
        indicator.setDenominator( convertExpressionFromDhis14( indicator.getDenominator(), dataElementMap,
            categoryOptionCombo.getId(), dhis14CalcDE.getDataElementName() ) );

        importObject( indicator, params );

    }
}
