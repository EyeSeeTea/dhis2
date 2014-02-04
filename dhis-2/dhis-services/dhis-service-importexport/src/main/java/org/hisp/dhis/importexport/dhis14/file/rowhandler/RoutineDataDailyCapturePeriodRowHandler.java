package org.hisp.dhis.importexport.dhis14.file.rowhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.dhis14.object.Dhis14RoutineDataDailyCapture;
import org.hisp.dhis.importexport.dhis14.object.Dhis14RoutineDataValue;
import org.hisp.dhis.importexport.dhis14.util.Dhis14PeriodUtil;
import org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.importexport.importer.PeriodImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.ibatis.sqlmap.client.event.RowHandler;

public class RoutineDataDailyCapturePeriodRowHandler 
extends PeriodImporter implements RowHandler
{
	private ImportParams params;
    
    private Map<String, Integer> periodTypeMapping;
    
    private Set<Integer> identifiers;

    private List<Integer> list;
    
    private List<String> listUniqueIdentifiers;
    
    private int countRecords;
// -------------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------------

public RoutineDataDailyCapturePeriodRowHandler( BatchHandler<Period> batchHandler,
        ImportObjectService importObjectService,
        PeriodService periodService,
        Map<String, Integer> periodTypeMapping,
        ImportParams params,
        Set<Integer> identifiers )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.periodService = periodService;
        this.periodTypeMapping = periodTypeMapping;
        this.params = params;
        this.identifiers = identifiers;
        this.list = new ArrayList<Integer>();
        listUniqueIdentifiers = new ArrayList<String>();
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
    if(!list.isEmpty()){
    	int len=list.size();
    	for(int i = 0 ; i < len ; i++){
    		if(list.get(i) != null){
    			
    			MutableDateTime dateTime = new MutableDateTime(dhis14Value.getStartDate());  
            	if(i > 0){
            		dateTime.addDays(i);
            	}
                
                //int periodID = dhis14Value.getPeriodId()+ 1200000 + day;
                Integer periodTypeId = periodTypeMapping.get( DailyPeriodType.NAME );
                
                DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                
                String key =periodTypeId+", "+dateTime.toString(dateFormatter)+", "+dateTime.toString(dateFormatter);
                if ( !listUniqueIdentifiers.contains( key ) ) // Data is registered for period which is distinct
                {
                	final Period period = new Period();
                    
                	
                	//dhis14Value.getPeriodType().setId( periodTypeId );
                	//period.setId( periodID );
                	period.setPeriodType(PeriodType.getPeriodTypeByName(DailyPeriodType.NAME));
                    period.getPeriodType().setId( periodTypeId );
                    period.setStartDate( dateTime.toDate() );
                    period.setEndDate( dateTime.toDate() );
                    
                    listUniqueIdentifiers.add(key);
                    Dhis14PeriodUtil.addPeriod( period ); // For uniqueness
                    
                    importObject( period, params );
                }
               //day++; 	
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
