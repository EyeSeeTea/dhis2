package org.hisp.dhis.reports.datasetlock.action;

//import java.util.ArrayList;
import java.util.Collection; //import java.util.Iterator;
//import org.hisp.dhis.organisationunit.OrganisationUnit;
//import org.hisp.dhis.organisationunit.OrganisationUnitService;
//import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType; //import org.hisp.dhis.reports.util.ReportService;
import com.opensymphony.xwork2.Action;

public class GenerateDataSetLockReportAnalyserFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    private final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        periodTypes = periodService.getAllPeriodTypes();

        return SUCCESS;
    }
}
