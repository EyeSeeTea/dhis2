//====================================================================================
// DATAELEMENT & DATA ELEMENT GROUP & CATEGORY OPTION COMBO
//====================================================================================

function DataDictionary()
{
	var dataElementsGroups = new Array();
	
	var dataElements = new Array();
	
	var indicatorGroups = new Array();
	
	var indicators = new Array();
	
	this.loadDataElementGroups = function( target )
	{
		target.children().remove();
		
		if( dataElementsGroups.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getDataElementGroups.action'
				, function( json ){
					target.append('<option value="100">ALL</option>');
					dataElementsGroups.push( new DataElementGroup(100, 'ALL') );
					jQuery.each( json.dataElementGroups, function(i, item){
						dataElementsGroups.push( new DataElementGroup(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( dataElementsGroups, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}
	}
	
	this.loadIndicatorGroups = function( target )
	{
		target.children().remove();
		
		if( indicatorGroups.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getIndicatorGroups.action'
				, function( json ){
					jQuery.each( json.indicatorGroups, function(i, item){
						indicatorGroups.push( new IndicatorGroup(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( indicatorGroups, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}	
	}
	
	this.loadDataElements = function( target )
	{
		target.children().remove();
		
		if( dataElements.length == 0 )
		{
			if(this.id == 100) this.id = '';
			jQuery.getJSON('../dhis-web-commons-ajax-json/getDataElements.action'								
				, function( json ){
					jQuery.each( json.dataElements, function(i, item){
						dataElements.push( new DataElement(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( dataElements, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}
	}
	
	this.loadIndicators = function( target )
	{
		target.children().remove();
		
		if( indicators.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getIndicators.action'
				, {id: this.id }					
				, function( json ){
					jQuery.each( json.indicators, function(i, item){
						indicators.push( new Indicator(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( indicators, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}	
	}
	
}

var DataDictionary = new DataDictionary();




function DataElementGroups()
{
	var groups = new Array();
	
	this.load = function( target )
	{
		target.children().remove();
		
		if( groups.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getDataElementGroups.action'
				, function( json ){
					target.append('<option value="100">ALL</option>');
					groups.push( new DataElementGroup(100, 'ALL') );
					jQuery.each( json.dataElementGroups, function(i, item){
						groups.push( new DataElementGroup(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( groups, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}			
	}		
	
	this.members = function()
	{
		return groups;
	}
	
	this.getDataElementGroup = function( id )
	{
		var result = null;
		jQuery.each( groups, function(i, item){
			if( id == item.id ) result = item;
		});		
		return result;
	}
	
}

function DataElementGroup( id_, name_ )
{
	this.id = id_;
	this.name = name_;	
	var elements = new Array();
	
	this.members = function( target )
	{
		target.children().remove();
		
		if( elements.length == 0 )
		{
			if(this.id == 100) this.id = '';
			jQuery.getJSON('../dhis-web-commons-ajax-json/getDataElements.action'
				, {id: this.id }					
				, function( json ){
					jQuery.each( json.dataElements, function(i, item){
						elements.push( new DataElement(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( elements, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}		
	}
	
	this.getDataElement = function( id )
	{
		var result = null;
		jQuery.each( elements, function(i, item){
			if( id == item.id ) result = item;
		});
		
		return result;
	}		
}	

function DataElement( id_, name_ )
{
	this.id = id_;
	this.name = name_;
	
	var optionCombos = new Array();
	
	this.getCategoryOptionCombos = function( target )
	{
		target.children().remove();
		
		if( optionCombos.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getCategoryOptionCombos.action'
				, {id: this.id }					
				, function( json ){
					jQuery.each( json.categoryOptionCombos, function(i, item){
						optionCombos.push( new OptionCombo(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( optionCombos, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}
	}
}

function OptionCombo( id_, name_ )
{
	this.id = id_;
	this.name = name_;
}

var DATA_ELEMENT_GROUPS = new DataElementGroups();

//====================================================================================
// DATAELEMENT & DATA ELEMENT GROUP & CATEGORY OPTION COMBO
//====================================================================================

function IndicatorGroups()
{
	var groups = new Array();
	
	this.load = function( target )
	{
		target.children().remove();
		
		if( groups.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getIndicatorGroups.action'
				, function( json ){
					jQuery.each( json.indicatorGroups, function(i, item){
						groups.push( new IndicatorGroup(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( groups, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}			
	}		
	
	this.members = function()
	{
		return groups;
	}
	
	this.getIndicatorGroup = function( id )
	{
		var result = null;
		jQuery.each( groups, function(i, item){
			if( id == item.id ) result = item;
		});
		
		return result;
	}
	
}

function IndicatorGroup( id_, name_ )
{
	this.id = id_;
	this.name = name_;	
	var elements = new Array();
	
	this.members = function( target )
	{
		target.children().remove();
		
		if( elements.length == 0 )
		{
			jQuery.getJSON('../dhis-web-commons-ajax-json/getIndicators.action'
				, {id: this.id }					
				, function( json ){
					jQuery.each( json.indicators, function(i, item){
						elements.push( new Indicator(item.id, item.name) );
						target.append('<option value="' + item.id + '">' + item.name + '</option>');
					});					
			});
		}else{
			jQuery.each( elements, function(i, item){
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});		
		}		
	}
	
	this.getIndicator = function( id )
	{
		var result = null;
		jQuery.each( elements, function(i, item){
			if( id == item.id ) result = item;
		});
		
		return result;
	}		
}	

function Indicator( id_, name_ )
{
	this.id = id_;
	this.name = name_;	
}

var INDICATOR_GROUPS = new IndicatorGroups();



