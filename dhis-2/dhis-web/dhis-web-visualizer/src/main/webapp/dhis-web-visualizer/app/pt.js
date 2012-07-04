function PeriodType()
{    
    var monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
		'July', 'August', 'September', 'October', 'November', 'December'];
    
	var format_yyyymmdd = function(date) {
		var y = date.getFullYear(),
			m = new String(date.getMonth() + 1),
			d = new String(date.getDate());
		m = m.length < 2 ? '0' + m : m;
		d = d.length < 2 ? '0' + d : d;
		return y + '-' + m + '-' + d;
	};
	
	var dateFormat = 'yyyy-MM-dd';

    this.reverse = function( array )
    {
        var reversed = [];
        var i = 0;

        for ( var j = array.length - 1; j >= 0; j-- )
        {
            reversed[i++] = array[j];
        }

        return reversed;
    };

    this.filterFuturePeriods = function( periods )
    {
        var array = [];
        var i = 0;

        var now = new Date().getTime();

        for ( var j = 0; j < periods.length; j++ )
        {
            if ( $.date( periods[j]['endDate'], dateFormat ).date().getTime() <= now )
            {
                array[i++] = periods[j];
            }
        }

        return array;
    };

    var periodTypes = [];
    periodTypes['Daily'] = new DailyPeriodType( format_yyyymmdd );
    periodTypes['Weekly'] = new WeeklyPeriodType( format_yyyymmdd );
    periodTypes['Monthly'] = new MonthlyPeriodType( format_yyyymmdd, monthNames, this.reverse );
    periodTypes['BiMonthly'] = new BiMonthlyPeriodType( dateFormat );
    periodTypes['Quarterly'] = new QuarterlyPeriodType( dateFormat );
    periodTypes['SixMonthly'] = new SixMonthlyPeriodType( dateFormat );
    periodTypes['Yearly'] = new YearlyPeriodType( dateFormat );
    periodTypes['FinancialOct'] = new FinancialOctoberPeriodType( dateFormat );
    periodTypes['FinancialJuly'] = new FinancialJulyPeriodType( dateFormat );
    periodTypes['FinancialApril'] = new FinancialAprilPeriodType( dateFormat );

    this.get = function( key )
    {
        return periodTypes[key];
    };
}

function DailyPeriodType( format_yyyymmdd )
{	
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var date = new Date( '01 Jan ' + year );

        while ( date.getFullYear() === year )
        {
            var period = [];
            period['startDate'] = format_yyyymmdd( date );
            period['endDate'] = period['startDate'];
            period['name'] = period['startDate'];
            period['id'] = 'Daily_' + period['startDate'];
            period['iso'] = period['startDate'].replace( /-/g, '' );
            periods.push( period );
            date.setDate( date.getDate() + 1 );
        }

        return periods;
    };
}

function WeeklyPeriodType( format_yyyymmdd )
{	
    this.generatePeriods = function( offset )
    {
		var periods = [];
		var year = new Date().getFullYear() + offset;
		var date = new Date( '01 Jan ' + year );
		var day = date.getDay();
		var week = 1;
		
		if ( day <= 4 )
		{
			date.setDate( date.getDate() - ( day - 1 ) );
		}
		else
		{
			date.setDate( date.getDate() + ( 8 - day ) );
		}		
		
		while ( date.getFullYear() === year )
		{
			var period = [];
			period['startDate'] = format_yyyymmdd( date );
			period['iso'] = year + 'W' + week;
			period['id'] = 'Weekly_' + period['startDate'];
			date.setDate( date.getDate() + 6 );
			period['endDate'] = format_yyyymmdd( date );
			period['name'] = 'W' + week + ' - ' + period['startDate'] + ' - ' + period['endDate'];
			periods.push( period );			
			date.setDate( date.getDate() + 1 );
			week++;
		}
		
        return periods;
    };
}

function MonthlyPeriodType( format_yyyymmdd, monthNames, rev )
{
	var format_iso = function(date) {
		var y = date.getFullYear(),
			m = new String(date.getMonth() + 1);
		m = m.length < 2 ? '0' + m : m;
		return y + m;
	};
	
    this.generatePeriods = function( offset )
    {
		var periods = [];
		var year = new Date().getFullYear() + offset;
		var date = new Date( '31 Dec ' + year );
		
		while ( date.getFullYear() === year )
		{
			var period = [];
			period['endDate'] = format_yyyymmdd( date );
			date.setDate( 1 );
			period['startDate'] = format_yyyymmdd( date );
			period['iso'] = format_iso( date );
			period['name'] = monthNames[date.getMonth()] + ' ' + date.getFullYear();
			period['id'] = 'Monthly_' + period['startDate'];
			periods.push( period );
			date.setDate( 0 );
		}
		
        return rev(periods);
    };
}

function BiMonthlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var endDate = startDate.clone().adjust( 'M', +2 ).adjust( 'D', -1 );
        var i = 0;
        var j = 0;

        while ( startDate.date().getFullYear() == year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] = monthNames[i] + ' - ' + monthNames[i + 1] + ' ' + year;
            period['id'] = 'BiMonthly_' + period['startDate'];
            period['iso'] = startDate.format( 'yyyyMM' ) + 'B';
            periods[j] = period;

            startDate.adjust( 'M', +2 );
            endDate = startDate.clone().adjust( 'M', +2 ).adjust( 'D', -1 );
            i += 2;
            j++;
        }

        return periods;
    };
}

function QuarterlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var endDate = startDate.clone().adjust( 'M', +3 ).adjust( 'D', -1 );
        var i = 0;
        var j = 0;

        while ( startDate.date().getFullYear() == year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] = monthNames[i] + ' - ' + monthNames[i + 2] + ' ' + year;
            period['id'] = 'Quarterly_' + period['startDate'];
            period['iso'] = year + 'Q' + ( j + 1 );
            periods[j] = period;

            startDate.adjust( 'M', +3 );
            endDate = startDate.clone().adjust( 'M', +3 ).adjust( 'D', -1 );
            i += 3;
            j++;
        }

        return periods;
    };
}

function SixMonthlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;

        var period = [];
        period['startDate'] = year + '-01-01';
        period['endDate'] = year + '-06-30';
        period['name'] = monthNames[0] + ' - ' + monthNames[5] + ' ' + year;
        period['id'] = 'SixMonthly_' + period['startDate'];
        period['iso'] = year + 'S1';
        periods[0] = period;

        period = [];
        period['startDate'] = year + '-07-01';
        period['endDate'] = year + '-12-31';
        period['name'] = monthNames[6] + ' - ' + monthNames[11] + ' ' + year;
        period['id'] = 'SixMonthly_' + period['startDate'];
        period['iso'] = year + 'S2';
        periods[1] = period;

        return periods;
    };
}

function YearlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat ).adjust( 'Y', -5 );
        var endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] = startDate.date().getFullYear();
            period['id'] = 'Yearly_' + period['startDate'];
            period['iso'] = year;
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
            endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );
        }

        return periods;
    };
}

function FinancialOctoberPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-10-01', dateFormat ).adjust( 'Y', -5 );
        var endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] =  monthNames[9] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[8] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialOct_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
            endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );
        }

        return periods;
    };
}

function FinancialJulyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-07-01', dateFormat ).adjust( 'Y', -5 );
        var endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] =  monthNames[6] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[5] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialJuly_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
            endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );
        }

        return periods;
    };
}

function FinancialAprilPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-04-01', dateFormat ).adjust( 'Y', -5 );
        var endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['endDate'] = endDate.format( dateFormat );
            period['name'] =  monthNames[3] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[2] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialApril_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
            endDate = startDate.clone().adjust( 'Y', +1 ).adjust( 'D', -1 );
        }

        return periods;
    };
}
