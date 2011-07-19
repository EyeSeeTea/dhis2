
//TODO next/previous
//TODO filter future periods

var dateFormat = 'yyyy-MM-dd';

var _periodType = new PeriodType();

function PeriodType()
{
	var periodTypes = [];
	periodTypes['Daily'] = new DailyPeriodType();
	periodTypes['Weekly'] = new WeeklyPeriodType();
	periodTypes['Monthly'] = new MonthlyPeriodType();
	periodTypes['Quarterly'] = new QuarterlyPeriodType();
	periodTypes['SixMonthly'] = new SixMonthlyPeriodType();
	periodTypes['Yearly'] = new YearlyPeriodType();
	
	this.get = function( key )
	{
		return periodTypes[key];
	}
}

function DailyPeriodType()
{
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear();
		var startDate = $.date( year + '-01-01', dateFormat );
		
		while ( startDate.date().getFullYear() <= year )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['name'] = startDate.format( dateFormat );
			periods[i] = period;
			
			startDate.adjust( 'D', +1 );
			i++;
		}
		
		return periods;	
	}
}

function WeeklyPeriodType()
{
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear();
		var startDate = $.date( year + '-01-01', dateFormat );		
		var day = startDate.date().getDay();
		
		if ( day == 0 ) // Sunday, forward to Monday
		{
			startDate.adjust( 'D', +1 );
		}
		else if ( day <= 4 ) // Monday - Thursday, rewind to Monday
		{
			startDate.adjust( 'D', ( ( day - 1 ) * -1 ) );
		}
		else // Friday - Saturday, forward to Monday
		{
			startDate.adjust( 'D', ( 8 - day ) );
		}
		
		var i = 0;
		
		while ( startDate.date().getFullYear() <= year )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['name'] = 'W' + ( i + 1 ) + ' - ' + startDate.format( dateFormat );			
			periods[i] = period;
			
			startDate.adjust( 'D', +7 );
			i++;
		}
		
		return periods;
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

function QuarterlyPeriodType()
{
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear();
		var startDate = $.date( year + '-01-01', dateFormat );
		var i = 0;
		var j = 0;
		
		while ( startDate.date().getFullYear() == year )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['name'] = monthNames[i] + ' - ' + monthNames[i+2] + ' ' + year;
			periods[j] = period;
			
			startDate.adjust( 'M', +3 );
			i += 3;
			j++;
		}
		
		return periods;
	}
}

function SixMonthlyPeriodType()
{
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear();
		
		var period = [];
		period['startDate'] = $.date( year + '-01-01', dateFormat );
		period['name'] = monthNames[0] + ' - ' + monthNames[5] + ' ' + year;
		periods[0] = period;
		
		var period = [];
		period['startDate'] = $.date( year + '-06-01', dateFormat );
		period['name'] = monthNames[6] + ' - ' + monthNames[11] + ' ' + year;
		periods[1] = period;
		
		return periods;
	}	
}

function YearlyPeriodType()
{	
	this.generatePeriods = function()
	{
		var periods = [];
		var year = new Date().getFullYear() - 5;
		var startDate = $.date( year + '-01-01', dateFormat ).adjust( 'Y', -5 );
		
		for ( var i = 0; i < 11; i++ )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['name'] = startDate.date().getFullYear();
			periods[i] = period;
			
			startDate.adjust( 'Y', +1 );
		}
		
		return periods;
	}		
}
