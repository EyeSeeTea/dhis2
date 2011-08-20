// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function saveComment()
{
    var commentValue = $( '#commentTextArea' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

    var commentSaver = new CommentSaver( currentDataElementId, currentOptionComboId, currentOrganisationUnitId,
            periodId, commentValue );

    commentSaver.save();
}

function CommentSaver( dataElementId_, optionComboId_, organisationUnitId_, periodId_, value_ )
{
    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_;
    var organisationUnitId = organisationUnitId_;
    var periodId = periodId_;
    var value = value_;

    this.save = function()
    {
        markComment( COLOR_YELLOW );

        $.ajax( {
            url: 'saveComment.action',
            data:
            {
            	organisationUnitId: organisationUnitId,
            	dataElementId: dataElementId,
            	optionComboId: optionComboId,
            	periodId: periodId,
            	value: value
            },
            dataType: 'json',
            cache: false,
            success: handleResponse,
            error: handleError
        } );
    };

    function handleResponse( json )
    {
        var code = json.c;

        if ( code == 0 )
        {
            markComment( COLOR_GREEN );
        }
        else
        {
            markComment( COLOR_RED );
            window.alert( i18n_saving_comment_failed_status_code + '\n\n' + code );
        }
    }

    function handleError( jqXHR, textStatus, errorThrown )
    {
        markComment( COLOR_RED );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + textStatus );
    }

    function markComment( color )
    {
        $( '#commentTextArea' ).css( 'background-color', color );
    }
}

function removeMinMaxLimit()
{
    $( '#minLimit' ).val( '' );
    $( '#maxLimit' ).val( '' );

    $.get( 'removeMinMaxLimits.action', 
    	{
    		dataElementId: currentDataElementId,
    		categoryOptionComboId: currentOptionComboId,
    		organisationUnitId: currentOrganisationUnitId
    	},
    	refreshChart );
}

function saveMinMaxLimit()
{
    var minValue = $( '#minLimit' ).val();
    var maxValue = $( '#maxLimit' ).val();

    if ( !minValue || minValue == '' )
    {
        return;
    }
    else if ( !isInt( minValue ) )
    {
        $( '#minSpan' ).html( i18n_enter_digits );
        return;
    }
    else
    {
        $( '#minSpan' ).html( '' );
    }

    if ( !maxValue || maxValue == '' )
    {
        return;
    }
    else if ( !isInt( maxValue ) )
    {
        $( '#maxSpan' ).html( i18n_enter_digits );
        return;
    }
    else
    {
        $( '#maxSpan' ).html( '' );
    }

    if ( eval( minValue ) > eval( maxValue ) )
    {
        $( '#maxSpan' ).html( i18n_max_must_be_greater_than_min );
        return;
    }
    else
    {
        $( '#maxSpan' ).html( '' );
    }

    var minId = currentDataElementId + '-' + currentOptionComboId + '-min';
    var maxId = currentDataElementId + '-' + currentOptionComboId + '-max';

    currentMinMaxValueMap[minId] = minValue;
    currentMinMaxValueMap[maxId] = maxValue;

    $.get( 'saveMinMaxLimits.action', 
    	{
    		organisationUnitId: currentOrganisationUnitId,
    		dataElementId: currentDataElementId,
    		categoryOptionComboId: currentOptionComboId,
    		minLimit: minValue,
    		maxValue: maxValue
    	},
    	refreshChart );
}

function refreshChart()
{
    var source = 'getHistoryChart.action?dataElementId=' + currentDataElementId + '&categoryOptionComboId='
            + currentOptionComboId + '&periodId=' + periodId + '&organisationUnitId=' + currentOrganisationUnitId + '&r=' + Math.random();

    $( '#historyChart' ).attr( 'src', source );
}

function markValueForFollowup( dataElementId, periodId, sourceId, categoryOptionComboId )
{
    $.ajax( { url: 'markValueForFollowup.action',
    	data:
    	{
    		dataElementId: dataElementId,
    		periodId: periodId,
    		sourceId: sourceId,
    		categoryOptionComboId: categoryOptionComboId
    	},
    	cache: false,
    	dataType: 'json',
    	success: function( json )
	    {
	        if ( json.message == 'marked' )
	        {
	            $( '#followup' ).attr( 'src', '../images/marked.png' );
	            $( '#followup' ).attr( 'alt', i18n_unmark_value_for_followup );
	        }
	        else if ( json.message == 'unmarked' )
	        {
	            $( '#followup' ).attr( 'src', '../images/unmarked.png' );
	            $( '#followup' ).attr( 'alt', i18n_mark_value_for_followup );
	        }
	    }
	} );
}
