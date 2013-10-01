function changeRuleType( ruleType )
{
	if (ruleType == 'validation')
	{
		hideById( 'organisationUnitLevelTR');
		hideById( 'periodExtentTR');
		hideById( 'sequentialSampleCountTR');
		hideById( 'annualSampleCountTR');
		hideById( 'highOutliersTR');
		hideById( 'lowOutliersTR');
	} else
	{
		showById( 'organisationUnitLevelTR');
		showById( 'periodExtentTR');
		showById( 'sequentialSampleCountTR');
		showById( 'annualSampleCountTR');
		showById( 'highOutliersTR');
		showById( 'lowOutliersTR');
    }
}
