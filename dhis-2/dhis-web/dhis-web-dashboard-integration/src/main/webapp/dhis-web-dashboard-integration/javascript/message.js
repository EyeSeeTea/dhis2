
function submitMessage()
{
    $( "#messageForm" ).submit();
}

function removeMessage( id )
{
    removeItem( id, "", i18n_confirm_delete_message, "removeMessage.action" );
}

function removeMessages( messages )
{
    var confirmed = window.confirm( i18n_confirm_delete_all_selected_messages );

    if ( confirmed )
    {
        setHeaderWaitMessage( i18n_deleting );

        $.ajax(
        {
            url: "../../api/messageConversations?" + $.param( messages, true ),
            contentType: "application/json",
            dataType: "json",
            type: "DELETE",
            success: function( response )
            {
                for( var i = 0 ; i < response.removed.length ; i++ )
                {
                    $( "#messages" ).find( "[name='" + response.removed[i] + "']" ).remove();
                }
                setHeaderDelayMessage( response );
                $( "#multiSelectCheckbox" ).removeAttr( "checked" );
            },
            error: function( response )
            {
                showErrorMessage( response.message );
            }
        });
    }
}

function markMessagesRead( messages )
{
    $.ajax(
    {
        url: "../../api/messageConversations/read",
        type: "PUT",
        data: JSON.stringify( messages.uid ),
        contentType: "application/json",
        dataType: "json",
        success: function( response )
        {
            toggleMessagesRead( response.markedRead );
            $( "#multiSelectCheckbox" ).removeAttr( "checked" );
        },
        error: function( response )
        {
            showErrorMessage( response.message );
        }
    });
}

function markMessagesUnread( messages )
{
    $.ajax(
    {
        url: "../../api/messageConversations/unread",
        type: "PUT",
        data: JSON.stringify( messages.uid ),
        contentType: "application/json",
        dataType: "json",
        success: function( response )
        {
            toggleMessagesRead( response.markedUnread );
            $( "#multiSelectCheckbox" ).removeAttr( "checked" );
        },
        error: function( response )
        {
            showErrorMessage( response.message );
        }
    });
}

function toggleMessagesRead( messageUids )
{
    var messages = $( "#messages" );

    for( var i = 0 ; i < messageUids.length ; i++ )
    {
        messages.find( "[name='" + messageUids[i] + "']" ).toggleClass( "unread bold" );
        messages.find( "input:checkbox" ).removeAttr( "checked" );
    }
}

function read( id )
{
    window.location.href = "readMessage.action?id=" + id;
}

function validateMessage()
{
	var subject = $( "#subject" ).val();
	var text = $( "#text" ).val();

	if ( isDefined( selectionTreeSelection ) && $( "#recipients" ).length )
	{
	    if ( !( selectionTreeSelection.isSelected() || $( "#recipients" ).val().length ) )
	    {
	        setHeaderMessage( i18n_select_one_or_more_recipients );
	        return false;
	    }
	}

	if ( subject == null || subject.trim().length == 0 )
	{
		setHeaderMessage( i18n_enter_subject );
		return false;
	}
	
	if ( text == null || text.trim().length == 0 )
	{
		setHeaderMessage( i18n_enter_text );
		return false;
	}
	
	return true;
}

function toggleMetaData( id )
{
	$( "#metaData" + id ).toggle();
}

function sendReply()
{
	var id = $( "#conversationId" ).val();
	var text = $( "#text" ).val();
	
	if ( text == null || text.trim() == '' )
	{
		setHeaderMessage( i18n_enter_text );
		return false;
	}
	
	$( "#replyButton" ).attr( "disabled", "disabled" );
	
	setHeaderWaitMessage( i18n_sending_message );
	
	$.postUTF8( "sendReply.action", { id:id, text:text }, function() 
	{
		window.location.href = "readMessage.action?id=" + id;
	} );
}

function toggleFollowUp( id, followUp )
{
	var imageId = "followUp" + id;
	
	var url = "toggleFollowUp.action?id=" + id;
	
	$.getJSON( url, function( json )
	{
		var imageSrc = json.message == "true" ? "../images/marked.png" : "../images/unmarked.png";
			
		$( "#" + imageId ).attr( "src", imageSrc );
	} );
}

function formatItem( item )
{
    if ( item.id && item.id.indexOf("u:") != -1 )
    {
        return '<img src="../icons/glyphicons_003_user.png" style="width: 12px; height: 12px; padding-right: 5px;"/>' + item.text;
    }
    else if ( item.id && item.id.indexOf('ug:') != -1 )
    {
        return '<img src="../icons/glyphicons_043_group.png" style="width: 12px; height: 12px; padding-right: 5px;"/>' + item.text;
    }
    else
    {
        return item.text;
    }
}

/* Multi select checkbox button */
function multiSelect()
{
    $( "#multiSelectButton" ).click(function( event )
    {
        $( document ).one( "click", function()
        {
            $( "#multiSelectMenu" ).css( "visibility", "hidden" );
        });

        var $multiMenu = $( "#multiSelectMenu" );

        if( $multiMenu.css( "visibility" ) !== "visible" )
        {
            $multiMenu.css( "visibility", "visible" );
        }
        else
        {
            $multiMenu.css( "visibility", "hidden" );
        }
        event.stopPropagation();
    });

    $( "#multiSelectMenu" )
        .css( "visibility", "hidden" )
        .position(
        {
            my: "left top",
            at: "left bottom",
            of: "#multiSelectButton"
        });

    $( "#multiSelectCheckbox" ).click( function( event )
    {
        if( typeof $( this ).attr( "checked" ) !== "undefined" ) {
            $( "#messages input:checkbox" ).attr( "checked" , "checked" );
        }
        else {
            $( "#messages input:checkbox:checked" ).removeAttr( "checked" );
        }
        event.stopPropagation();
    });

    $( "#multiSelectMenu li, #multiSelectMenu li a" ).click(function()
    {
        var messages = {};
        messages.uid = [];
        $( "#messages input:checkbox:checked" ).each(function() {
            messages.uid.push(this.value);
        });

        if( messages.uid.length < 1 )
        {
            showErrorMessage(i18n_no_messages_selected);
            return;
        }

        var action = $(this).attr( "data-action" );

        if( action === "delete" ) {
            removeMessages(messages);
        }
        else if( action === "markRead" ) {
            markMessagesRead(messages);
        }
        else if( action === "markUnread" ) {
            markMessagesUnread(messages);
        }
    });

    /* Checkboxes all selected/deselected trigger select/deselect of multi select checkbox */

    var numCheckboxes = $( "#messages input:checkbox" ).length;

    $( "#messages input:checkbox" ).click( function()
    {
        var checked = $( "#messages input:checkbox:checked" );
        if( checked.length < 1 ) {
        $( "#multiSelectCheckbox" ).removeAttr( "checked" );
        }
        else if( checked.length === numCheckboxes ) {
        $( "#multiSelectCheckbox" ).attr( "checked", "checked" );
        }
    });
}