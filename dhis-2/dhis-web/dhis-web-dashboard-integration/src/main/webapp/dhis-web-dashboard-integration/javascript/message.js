
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
                setHeaderDelayMessage( i18n_messages_were_deleted );
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
function multiSelect2( buttonId, menuId, checkboxContainerId )
{
    var $button = $( buttonId );
    var $menu = $( menuId );
    var $multiCheckbox = $button.children( "input:checkbox" );
    var $checkboxes = $( checkboxContainerId + " input:checkbox" );

    $button.click( function( event )
    {
        $( document ).one( "click", function()
        {
            $menu.css( "visibility", "hidden" );
        });

        if( $menu.css( "visibility" ) !== "visible" )
        {
            $menu.css( "visibility", "visible" );
        }
        else
        {
            $menu.css( "visibility", "hidden" );
        }
        event.stopPropagation();
    });

    $menu.css( "visibility", "hidden" );

    $menu.position(
    {
        my: "left top",
        at: "left bottom",
        of: $button
    });

    $multiCheckbox.click( function( event )
    {
        if( typeof $( this ).attr( "checked" ) !== "undefined" )
        {
            $checkboxes.attr( "checked" , "checked" );
        }
        else
        {
            $checkboxes.removeAttr( "checked" );
        }
        event.stopPropagation();
    });

    $menu.find( "li, a" ).click( function()
    {
        var messages = {};
        messages.uid = [];

        $checkboxes.filter( ":checked" ).each( function()
        {
            messages.uid.push( this.value );
        });

        if( messages.uid.length < 1 )
        {
            showErrorMessage( i18n_no_messages_selected );
            return;
        }

        var action = $( this ).attr( "data-action" );

        if( action === "delete" )
        {
            removeMessages( messages );
        }
        else if( action === "markRead" )
        {
            markMessagesRead( messages );
        }
        else if( action === "markUnread" )
        {
            markMessagesUnread( messages );
        }
    });

    /* Checkboxes all selected/deselected trigger select/deselect of multi select checkbox */

    $checkboxes.click( function()
    {
        var checked = $checkboxes.filter( ":checked" );

        if( checked.length < 1 )
        {
            $multiCheckbox.removeAttr( "checked" );
        }
        else if( checked.length === $checkboxes.length )
        {
            $multiCheckbox.attr( "checked", "checked" );
        }
    });
}

// Experimental
jQuery.fn.extend(
{
    multiSelect: function( options )
    {
        return this.each( function( )
        {
            // Ref to all checkboxes
            var $checkboxes = $( "#" + options.checkboxContainerId + " input:checkbox" );

            // Build button
            var $button = $( '<a href="#"></a>' );
            $( this ).append( $button );

            var $multiCheckbox = $('<input type="checkbox" id="multiCheckbox"/>');
            $button.append( $multiCheckbox );
            $button.append( '<span class="downArrow"></span>' );

            $button.addClass( options.buttonClass );

            // Build menu
            var $menu = $( '<ul id="multiSelectMenu" class="multiSelectMenu"></ul>' );
            $( document.body ).append( $menu );

            // Menu elements
            $( options.menuElements ).each( function()
            {
                var el = $('<li><a href="#">' + this.displayName + '</a></li>');

                var checked = [];
                $checkboxes.filter(":checked").each( function()
                {
                    checked.push( this.value );
                });

                var action = this.action;

                el.click( function()
                {
                    var checked = [];
                    $checkboxes.filter( ":checked" ).each( function()
                    {
                        checked.push( this.value );
                    });

                    action( checked );
                    $multiCheckbox.removeAttr( "checked" );
                });

                $menu.append( el );
            });

            // Position menu
            $menu.css( "visibility", "hidden" );
            $menu.position( { my: "left top", at: "left bottom", of: $button } );

            // Button click handler (show/hide dropdown menu)
            $button.click( function( event )
            {
                $( document ).one( "click", function()
                {
                    $menu.css( "visibility", "hidden" );
                });

                if( $menu.css( "visibility" ) !== "visible" )
                {
                    $menu.css( "visibility", "visible" );
                }
                else
                {
                    $menu.css( "visibility", "hidden" );
                }
                event.stopPropagation();
            });

            // Multi-checkbox (select/deselect all)
            $multiCheckbox.click( function( event )
            {
                if( typeof $( this ).attr( "checked" ) !== "undefined" )
                {
                    $checkboxes.attr( "checked" , "checked" );
                }
                else
                {
                    $checkboxes.removeAttr( "checked" );
                }
                event.stopPropagation();
            });

            // Checkboxes all selected/deselected trigger select/deselect of multi select checkbox
            $checkboxes.click( function()
            {
                var checked = $checkboxes.filter( ":checked" );

                if( checked.length < 1 )
                {
                    $multiCheckbox.removeAttr( "checked" );
                }
                else if( checked.length === $checkboxes.length )
                {
                    $multiCheckbox.attr( "checked", "checked" );
                }
            });
        });
    }
});

/*    $("#exampleButtonContainer").multiSelect2(
 {
 buttonClass: "multiSelectButton",
 checkboxContainerId: "messages",
 menuElements:
 [
 {
 displayName: "Delete",
 action: function( checked )
 {
 console.log(checked);
 }
 },
 {
 displayName: "Mark read",
 action: function( checked )
 {
 console.log(checked);
 }
 }
 ]
 });
 */