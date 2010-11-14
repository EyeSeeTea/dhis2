package org.hisp.dhis.ll.action.lldataentry;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class GetValidatePostVacantAction
    implements Action
{
    // --------------------------------------------------------------------------
    // Dependency
    // --------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService optionComboService;

    public void setOptionComboService( DataElementCategoryService optionComboService )
    {
        this.optionComboService = optionComboService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // --------------------------------------------------------------------------
    // Input/Output
    // --------------------------------------------------------------------------
    private String datavalue;

    public void setDatavalue( String datavalue )
    {
        this.datavalue = datavalue;
    }

    private String dataValueMapKey;

    public void setDataValueMapKey( String dataValueMapKey )
    {
        this.dataValueMapKey = dataValueMapKey;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    public String reportingDate;

    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    private String storedBy;
    
    private LineListGroup lineListGroup;

    // --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit organisationunit = selectedStateManager.getSelectedOrganisationUnit();
        
        Period period = selectedStateManager.getSelectedPeriod();
        
        lineListGroup = selectedStateManager.getSelectedLineListGroup();
        
        LineListOption lineListOption = selectedStateManager.getSelectedLineListOption();
        
        String postLineListElementName = lineListGroup.getLineListElements().iterator().next().getShortName();
        String lastWorkingDateLLElementName  = "lastworkingdate";
        String departmentLineListName = lineListGroup.getName();
        
        // preparing map to filter records from linelist table
        Map<String, String> llElementValueMap = new HashMap<String, String>();
        llElementValueMap.put( postLineListElementName, lineListOption.getName() );
        llElementValueMap.put( lastWorkingDateLLElementName, "null" );

        int recordNo = dataBaseManagerInterface.getLLValueCountByLLElements( departmentLineListName, llElementValueMap, organisationunit, period );
        
        int dataValue = Integer.parseInt( datavalue );

        if( dataValue > recordNo )
        {
            message = "Number of Sanctioned Position is " + dataValue + "And Number of Filled Position is " + recordNo + "\n Do you want to Add ?";
            saveDataValue();
            return SUCCESS;
        }
        else
        {
            message = "Number of Filled Position is equal to Number Sanctioned Post";
            
            return INPUT;
        }
    }

    private void saveDataValue()
    {
        OrganisationUnit organisationunit = selectedStateManager.getSelectedOrganisationUnit();

        Period historyPeriod = getHistoryPeriod();

        storedBy = currentUserService.getCurrentUsername();

        String[] partsOfDatavalueMap = dataValueMapKey.split( ":" );

        int dataElementId = Integer.parseInt( partsOfDatavalueMap[1] );

        int optionComboId = Integer.parseInt( partsOfDatavalueMap[2] );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        DataElementCategoryOptionCombo optionCombo = optionComboService
        .getDataElementCategoryOptionCombo( optionComboId );

        if ( datavalue != null && datavalue.trim().length() == 0 )
        {
            datavalue = null;
        }
        if ( datavalue != null )
        {
            datavalue = datavalue.trim();
        }

        DataValue dataValueObj = dataValueService.getDataValue( organisationunit, dataElement, historyPeriod, optionCombo );

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( dataValueObj == null )
        {
            if ( datavalue != null )
            {
                dataValueObj = new DataValue( dataElement, historyPeriod, organisationunit, datavalue, storedBy, new Date(), null,
                    optionCombo );
                dataValueService.addDataValue( dataValueObj );
            }
        }
        else
        {
            dataValueObj.setValue( datavalue );
            dataValueObj.setTimestamp( new Date() );
            dataValueObj.setStoredBy( storedBy );

            dataValueService.updateDataValue( dataValueObj );
        }
    }
    
    private Period getHistoryPeriod( )
    {
        //lineListGroup = selectedStateManager.getSelectedLineListGroup();
        Date historyDate = format.parseDate( reportingDate );
        System.out.println("Report Date is :::::::" + reportingDate );
        
        Period period;
        period = periodService.getPeriod( 0 );
        Period historyPeriod;
        

        if ( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            PeriodType dailyPeriodType = new DailyPeriodType();
            historyPeriod = dailyPeriodType.createPeriod( historyDate );
            
            System.out.println( reportingDate + " : " + historyPeriod );
            if ( historyPeriod == null )
            {
                System.out.println( "historyPeriod is null" );
            }
            historyPeriod = reloadPeriodForceAdd( historyPeriod );
        }
        else
        {
            period = selectedStateManager.getSelectedPeriod();

            period = reloadPeriodForceAdd( period );

            historyPeriod = period;
        }
        
        return historyPeriod;
    }
    
    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

}
