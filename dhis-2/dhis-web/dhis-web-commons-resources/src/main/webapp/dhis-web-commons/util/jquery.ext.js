jQuery.extend({
	postJSON: function( url, data, callback ) {
		return jQuery.post(url, data, callback, "json");
	},
	loadDataElementGroups: function( target ){
		DATA_ELEMENT_GROUPS.load( jQuery( target ) );
	},
	loadDataElementByGroup: function( groupId, target ){
		var group = DATA_ELEMENT_GROUPS.getDataElementGroup( groupId );
		if( group == null ){
			group = new DataElementGroup( groupId );
		}
		group.members( jQuery( target ) );
	},
	loadCategoryOptionCombos: function( dataElementId, target ){
		var dataElement = new DataElement( dataElementId );
		dataElement.getCategoryOptionCombos( jQuery(target) ); 
	},
	loadIndicatorGroups: function( target ){
		INDICATOR_GROUPS.load( jQuery( target ) );
	},
	loadIndicatorByGroup: function( groupId, target ){
		var group = INDICATOR_GROUPS.getIndicatorGroup( groupId );
		if( group == null ){
			group = new IndicatorGroup( groupId );
		}
		group.members( jQuery( target ) );
	}
});