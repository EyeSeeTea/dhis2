
// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( selectionTreeSelection )
{
    selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitIds );
}

function validateDataSetReport()
{
    if ( !getListValue( "selectedDataSetId" ) || getListValue( "selectedDataSetId" ) == "null" )
    {
        setMessage( i18n_select_data_set );
        return false;
    }
    if ( !getListValue( "selectedPeriodIndex" ) || getListValue( "selectedPeriodIndex" ) == "null" )
    {
        setMessage( i18n_select_period );
        return false;
    }
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    
    return true;
}

function printDateSetReportPreview()
{
	var o = $("div#printDateSetPreviewDiv");
	o.jqprint(); 
}