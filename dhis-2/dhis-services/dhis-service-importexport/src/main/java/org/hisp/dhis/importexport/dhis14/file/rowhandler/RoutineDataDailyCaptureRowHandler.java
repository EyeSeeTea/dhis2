package org.hisp.dhis.importexport.dhis14.file.rowhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ImportDataDailyPeriodService;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.dhis14.object.Dhis14RoutineDataDailyCapture;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.ibatis.sqlmap.client.event.RowHandler;

public class RoutineDataDailyCaptureRowHandler 
extends DataValueImporter implements RowHandler
{
private Map<Object, Integer> dataElementMapping;

//private Map<Object, Integer> periodMapping;

private Map<Object, Integer> organisationUnitMapping;

private DataElementCategoryOptionCombo categoryOptionCombo;

private DataElement element;


private OrganisationUnit source;

private DataValue value;

private List<Integer> list;

private ImportDataDailyPeriodService importDataDailyPeriodService;

private Map<String, Integer> periodTypeMapping;

private Period period;

private int countRecords;

// -------------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------------

public RoutineDataDailyCaptureRowHandler( BatchHandler<DataValue> batchHandler,
    BatchHandler<ImportDataValue> importDataValueBatchHandler,
    DataValueService dataValueService,ImportDataDailyPeriodService importDataDailyPeriodService,
    Map<Object, Integer> dataElementMapping,
    Map<Object, Integer> periodMapping, 
    Map<Object, Integer> organisationUnitMapping,
    Map<String, Integer> periodTypeMapping,
    DataElementCategoryOptionCombo categoryOptionCombo,
    ImportParams params )
{
    this.batchHandler = batchHandler;
    this.importDataValueBatchHandler = importDataValueBatchHandler;
    this.dataValueService = dataValueService;
    this.importDataDailyPeriodService = importDataDailyPeriodService;
    this.dataElementMapping = dataElementMapping;
    //this.periodMapping = periodMapping;
    this.periodTypeMapping = periodTypeMapping;
    this.organisationUnitMapping = organisationUnitMapping;
    this.categoryOptionCombo = categoryOptionCombo;
    this.params = params;
    
    this.element = new DataElement();
    this.period = new Period();
    this.source = new OrganisationUnit();
    this.value = new DataValue();
    this.list = new ArrayList<Integer>();
    this.countRecords = 0;
}

// -------------------------------------------------------------------------
// RowHandler implementation
// -------------------------------------------------------------------------

public void handleRow( Object object )
{
    final Dhis14RoutineDataDailyCapture dhis14Value = (Dhis14RoutineDataDailyCapture) object;
    
    getDailyData(dhis14Value);
    /*countRecords++;
    System.out.println(">>>");
    System.out.println("DataDailyCapture Record: " + countRecords);
    System.out.println("listSize: " + list.size());
    System.out.println("list: " + list.toString());
    System.out.println("...");*/
    
    final Integer dataElementId = dataElementMapping.get( dhis14Value.getDataElementId() );
    final Integer organisationUnitId = organisationUnitMapping.get( dhis14Value.getOrganisationUnitId() );

    if ( dataElementId == null )
    {
        log.warn( "Data element does not exist for identifier: " + dhis14Value.getDataElementId() );
        return;
    }
    if ( organisationUnitId == null )
    {
        log.warn( "Organisation unit does not exist for identifier: " + dhis14Value.getOrganisationUnitId() );
        return;
    }
    
    
    element.setId( dataElementId );
    source.setId( organisationUnitId );        
    
    
    value.setDataElement( element );
    value.setCategoryOptionCombo( categoryOptionCombo );
    value.setSource( source );
    value.setStoredBy( dhis14Value.getStoredBy() );
    value.setComment( dhis14Value.getComment() );


    if(!list.isEmpty()){
    	int len=list.size();
    	for(int i = 0 ; i < len ; i++){
    		if(list.get(i) != null){
    			
    			MutableDateTime dateTime = new MutableDateTime(dhis14Value.getStartDate());  
            	if(i > 0){
            		dateTime.addDays(i);
            	}
            	
            	value.setValue( String.valueOf( list.get(i) ) );
            	
            	
            	
                //final Period period = new Period();
                
                Integer periodTypeId = periodTypeMapping.get( DailyPeriodType.NAME );
                
                DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

            	int periodid = importDataDailyPeriodService.getPeriodId(periodTypeId, dateTime.toString(dateFormatter), dateTime.toString(dateFormatter));
                //System.out.println("periodid = "+ periodid);
            	period.setId( periodid );
            	/*period.setPeriodType(PeriodType.getPeriodTypeByName(DailyPeriodType.NAME));
                period.getPeriodType().setId( periodTypeId );
                period.setStartDate( dateTime.toDate() );
                period.setEndDate( dateTime.toDate() );*/
            	
                value.setPeriod( period );
                
                value.setValue(String.valueOf(list.get(i)));
                
                if ( value.getDataElement() != null && value.getPeriod() != null && value.getSource() != null && value.getValue() != null )
                {
                    importObject( value, params );
                }
                
    			
    		}
    	}
    }
    
    list.clear();

	}


	public void getDailyData(Dhis14RoutineDataDailyCapture dhis14Value){
		
		
		list.add(dhis14Value.getDay01());
		list.add(dhis14Value.getDay02());
		list.add(dhis14Value.getDay03());
		list.add(dhis14Value.getDay04());
		list.add(dhis14Value.getDay05());
		list.add(dhis14Value.getDay06());
		list.add(dhis14Value.getDay07());
		list.add(dhis14Value.getDay08());
		list.add(dhis14Value.getDay09());
		list.add(dhis14Value.getDay10());
		list.add(dhis14Value.getDay11());
		list.add(dhis14Value.getDay12());
		list.add(dhis14Value.getDay13());
		list.add(dhis14Value.getDay14());
		list.add(dhis14Value.getDay15());
		list.add(dhis14Value.getDay16());
		list.add(dhis14Value.getDay17());
		list.add(dhis14Value.getDay18());
		list.add(dhis14Value.getDay19());
		list.add(dhis14Value.getDay20());
		list.add(dhis14Value.getDay21());
		list.add(dhis14Value.getDay22());
		list.add(dhis14Value.getDay23());
		list.add(dhis14Value.getDay24());
		list.add(dhis14Value.getDay25());
		list.add(dhis14Value.getDay26());
		list.add(dhis14Value.getDay27());
		list.add(dhis14Value.getDay28());
		list.add(dhis14Value.getDay29());
		list.add(dhis14Value.getDay30());
		list.add(dhis14Value.getDay31());
		
	}
	
}
