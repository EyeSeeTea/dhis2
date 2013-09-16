
function showValidationRuleDetails( validationId )
{
    jQuery.post( 'getValidationRule.action', { id: validationId }, function ( json ) {
		setText( 'nameField', json.validationRule.name );
		
		var description = json.validationRule.description;
		setText( 'descriptionField', description ? description : '[' + i18n_none + ']' );
		
		var importance = json.validationRule.importance;
		setText( 'importanceField', i18nalizeImportance( importance ) );
		
		var leftSideDescription = json.validationRule.leftSideDescription;
		setText( 'leftSideDescriptionField', leftSideDescription ? leftSideDescription : '[' + i18n_none + ']' );
		
		var operator = json.validationRule.operator;
		setText( 'operatorField', i18nalizeOperator( operator ) );
		
		var rightSideDescription = json.validationRule.rightSideDescription;
		setText( 'rightSideDescriptionField', rightSideDescription ? rightSideDescription : '[' + i18n_none + ']' );

		showDetails();
	});
}

function i18nalizeImportance ( importance )
{
	if ( importance == "high" )
	{
		return i18n_high;
	}
	else if ( importance == "medium" )
	{
		return i18n_medium;
	}
	if ( importance == "low" )
	{
		return i18n_low;
	}
	
	return null;
}

function i18nalizeOperator( operator )
{
    if ( operator == "equal_to" )
    {
        return i18n_equal_to;
    }
    else if ( operator == "not_equal_to" )
    {
        return i18n_not_equal_to;
    }
    else if ( operator == "greater_than" )
    {
        return i18n_greater_than;       
    }
    else if ( operator == "greater_than_or_equal_to" )
    {
        return i18n_greater_than_or_equal_to;
    }
    else if ( operator == "less_than" )
    {
        return i18n_less_than;
    }
    else if ( operator == "less_than_or_equal_to" )
    {
        return i18n_less_than_or_equal_to;
    }
    
    return null;
}

function removeValidationRule( ruleId, ruleName )
{
	removeItem( ruleId, ruleName, i18n_confirm_delete, 'removeValidationRule.action' );
}
