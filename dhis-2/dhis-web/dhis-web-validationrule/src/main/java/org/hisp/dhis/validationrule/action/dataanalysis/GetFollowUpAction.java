package org.hisp.dhis.validationrule.action.dataanalysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataanalysis.FollowupAnalysisService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.util.SessionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Halvdan Hoem Grelland
 */
public class GetFollowUpAction
    extends ActionPagingSupport<DeflatedDataValue>
{
    public static final String KEY_ANALYSIS_DATA_VALUES = "analysisDataValues";

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        OrganisationUnit orgUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();

        this.paging = createPaging( followupAnalysisService.getFollowupDataValuesCount( orgUnit ) );

        if( paging.getPageSize() > 0 )
        {
            dataValues = followupAnalysisService.getFollowupDataValues( orgUnit, paging.getStartPos(), paging.getPageSize() );
        }
        else
        {
            dataValues = new ArrayList<>();
        }

        SessionUtils.setSessionVar( KEY_ANALYSIS_DATA_VALUES, dataValues );

        return SUCCESS;
    }
}
