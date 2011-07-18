
var dateFormat = 'yyyy-MM-dd';

var _periodType = new PeriodType();

function PeriodType()
{
	var periodTypes = [];
	periodTypes["Monthly"] = new MonthlyPeriodType();
	
	this.get = function( key )
	{
		return periodTypes[key];
	}
}

function MonthlyPeriodType()
{
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear();
		var startDate = $.date( year + '-01-01', dateFormat );
		
		var i = 0;
				
		while ( startDate.date().getFullYear() == year )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['name'] = monthNames[i] + ' ' + year;
			
			periods[i] = period;
			
			startDate.adjust( 'M', +1 );
			i++;			
		}
		
		return periods;
	}
}
