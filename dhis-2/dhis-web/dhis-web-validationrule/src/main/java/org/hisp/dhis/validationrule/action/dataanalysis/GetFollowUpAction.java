package org.hisp.dhis.validationrule.action.dataanalysis;

import com.opensymphony.xwork2.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataanalysis.FollowupAnalysisService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.util.SessionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Halvdan Hoem Grelland
 */
public class GetFollowUpAction
    implements Action
{
    private static final int MAX_RESULTS = 500;

    private static final String KEY_ANALYSIS_DATA_VALUES = "analysisDataValues";

    private static final Log log = LogFactory.getLog( GetFollowUpAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FollowupAnalysisService followupAnalysisService;

    public void setFollowupAnalysisService( FollowupAnalysisService followupAnalysisService )
    {
        this.followupAnalysisService = followupAnalysisService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DeflatedDataValue> dataValues = new ArrayList<>();

    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }

    private boolean maxExceeded;

    public boolean getMaxExceeded()
    {
        return maxExceeded;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        OrganisationUnit orgUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();
        int totalResults;

        if( orgUnit != null )
        {
            dataValues = followupAnalysisService.getFollowupDataValues( orgUnit, MAX_RESULTS );

            totalResults = dataValues.size();

            if( totalResults == MAX_RESULTS )
            {
                totalResults = followupAnalysisService.getFollowupDataValuesCount( orgUnit );
            }

            maxExceeded = totalResults > dataValues.size();
        }
        else
        {
            dataValues = new ArrayList<>();
            maxExceeded = false;
        }

        SessionUtils.setSessionVar( KEY_ANALYSIS_DATA_VALUES, dataValues );

        return SUCCESS;
    }
}
