var selectedOrganisationUnitListABCDEF;

function addSelectedOrganisationUnitABCDEF( id )
{
	selectedOrganisationUnitListABCDEF.append('<option value="' + id + ' selected="selected">' + id + '</option>');
}

function selectOrganisationUnitABCDEF( ids )
{
	selectedOrganisationUnitListABCDEF.empty();

	jQuery.each(ids, function( i, item )
	{
		selectedOrganisationUnitListABCDEF.append('<option value="' + item + ' selected="selected">' + item
				+ '</option>');
	});
	byId('treeSelectedId').selectedIndex = 0;
}

function unSelectChildren()
{
	jQuery.get('../dhis-web-commons/oust/removeorgunit.action', {
		children : true
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function selectChildren()
{
	jQuery.get('../dhis-web-commons/oust/addorgunit.action', {
		children : true
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function selectOrganisationUnitAtLevel()
{
	jQuery.get('../dhis-web-commons/oust/addorgunit.action', {
		level : getFieldValue('levelList')
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function unSelectOrganisationUnitAtLevel()
{
	jQuery.get('../dhis-web-commons/oust/removeorgunit.action', {
		level : getFieldValue('levelList')
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function unSelectAllTree()
{
	jQuery.get('../dhis-web-commons/oust/clearSelectedOrganisationUnits.action', function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function selectAllTree()
{
	jQuery.get('../dhis-web-commons/oust/selectallorgunit.action', function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function selectOrganisationUnitByGroup()
{
	jQuery.get('../dhis-web-commons/oust/addorgunit.action', {
		organisationUnitGroupId : getFieldValue('groupList')
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function unSelectOrganisationUnitByGroup()
{
	jQuery.get('../dhis-web-commons/oust/removeorgunit.action', {
		organisationUnitGroupId : getFieldValue('groupList')
	}, function( xml )
	{
		selectedOrganisationUnitXMLABCDEF(xml);
	});
}

function loadOrganisationUnitLevel()
{
	jQuery.getJSON('../dhis-web-commons-ajax-json/getOrganisationUnitLevels.action', function( json )
	{
		var levels = jQuery("#levelList");
		levels.empty();
		jQuery.each(json.levels, function( i, item )
		{
			levels.append('<option value="' + item.level + '">' + item.name + '</option>');
		});
		jQuery("#selectionTreeContainer").fadeIn();
	});
}

function loadOrganisationUnitGroup()
{
	jQuery.getJSON('../dhis-web-commons-ajax-json/getOrganisationUnitGroups.action', function( json )
	{
		var groups = jQuery("#groupList");
		groups.empty();
		jQuery.each(json.organisationUnitGroups, function( i, item )
		{
			groups.append('<option value="' + item.id + '">' + item.name + '</option>');
		});

		loadOrganisationUnitLevel();
	});
}
