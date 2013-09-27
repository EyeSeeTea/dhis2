function changeRuleType( ruleType )
{
	if (ruleType == 'validation')
	{
		hideById( 'organisationUnitLevelTR');
		hideById( 'periodExtentTR');
		hideById( 'precedingSampleCountTR');
		hideById( 'precedingSampleTypeTR');
		hideById( 'highOutliersTR');
		hideById( 'lowOutliersTR');
	} else
	{
		showById( 'organisationUnitLevelTR');
		showById( 'periodExtentTR');
		showById( 'precedingSampleCountTR');
		showById( 'precedingSampleTypeTR');
		showById( 'highOutliersTR');
		showById( 'lowOutliersTR');
    }
}
