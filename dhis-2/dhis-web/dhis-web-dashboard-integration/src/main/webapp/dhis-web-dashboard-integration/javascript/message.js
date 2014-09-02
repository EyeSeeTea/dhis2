
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
    if( messages.length < 1 )
    {
        return;
    }

    var confirmed = window.confirm( i18n_confirm_delete_all_selected_messages );

    if ( confirmed )
    {
        setHeaderWaitMessage( i18n_deleting );

        $.ajax(
        {
            url: "../../api/messageConversations?" + $.param( { mc: messages }, true ),
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
                showErrorMessage( response.message, 3 );
            }
        });
    }
}

function markMessagesRead( messages )
{
    if( messages.length < 1 )
    {
        return;
    }

    $.ajax(
    {
        url: "../../api/messageConversations/read",
        type: "PUT",
        data: JSON.stringify( messages ),
        contentType: "application/json",
        dataType: "json",
        success: function( response )
        {
            toggleMessagesRead( response.markedRead );
        },
        error: function( response )
        {
            showErrorMessage( response.message, 3 );
        }
    });
}

function markMessagesUnread( messages )
{
    if( messages.length < 1 )
    {
        return;
    }

    $.ajax(
    {
        url: "../../api/messageConversations/unread",
        type: "PUT",
        data: JSON.stringify( messages ),
        contentType: "application/json",
        dataType: "json",
        success: function( response )
        {
            toggleMessagesRead( response.markedUnread );
        },
        error: function( response )
        {
            showErrorMessage( response.message, 3 );
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

/**
 * Checkbox/dropdown combo menu for DHIS 2 Dashboard.
 *
 * @author Halvdan Hoem Grelland
 */
( function ( $ ) {
    /*
     Example markup:
     <div>
        <div></div> <!-- Empty, will be filled with button markup -->
        <ul> <!-- Menu markup -->
            <li data-action="actionA">Item A</li>
            <li data-action="actionB">Item B</li>
            <li data-action="actionC">Item C</li>
        </ul>
     </div>
     ...
     <div id="checkboxes">
        ...
        <input type="checkbox" value="someValue" .... />
        ...
     </div>

     The string parameter given in data-action denotes the name of the
     function which is called on click of the menu item.

     The selected checkboxes' values will be given as an array argument
     to the function when called.

     Usage:
        $( "#myDiv" ).multiCheckboxMenu( $( "#myCheckboxContainer" ), {} );

     */

    function getCheckedValues( $checkboxContainer ) {
        var checked = [];
        $checkboxContainer.find( "input:checkbox:checked" ).each( function() {
            checked.push( this.value );
        });
        return checked;
    }

    var multiCheckboxMenu = $.fn.multiCheckboxMenu;

    $.fn.multiCheckboxMenu = function( $checkboxContainer, options ) {
        if( typeof options === "object" ) {
            var $cb = $( "<input>", { type: "checkbox" } );
            options = $.extend( true, options , {
                checkbox: $cb,
                buttonElements: [
                    $( "<span>", { "class": "downArrow" } )
                ],
                menuClass: "multiSelectMenu",
                buttonClass: "multiSelectButton"
            });

            var $checkbox = $( options.checkbox );
            var $slaveCheckboxes = $checkboxContainer.find( "input:checkbox" );

            var $button = $( "<a>", { href: "#" });
            $button.addClass( options.buttonClass );

            $button.append( $( options.checkbox ) );

            $( options.buttonElements ).each( function() {
                $button.append( $( this ) );
            });

            $( this ).find( "div:first" ).append($button);

            var $menu = $( this ).find( "ul" );
            $menu.addClass( options.menuClass );
            $menu.css( "visibility", "hidden" );
            $menu.position({
                my: "left top",
                at: "left bottom",
                of: $button
            });

            $button.click( function ( event ) {
                $( document ).one( "click", function() {
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

            $menu.find( "li" ).each( function() {
                var el = $( this );
                el.action = this.getAttribute( "data-action" );

                el.click( function() {
                    var checked = getCheckedValues( $checkboxContainer );

                    $checkbox.removeAttr( "checked" ); // TODO Use private function
                    $slaveCheckboxes.removeAttr( "checked" );

                    return window[ el.action ]( checked );
                });
            });

            $checkbox.click( function( event ) {
                if( this.checked )
                {
                    $slaveCheckboxes.attr( "checked", "checked" );
                }
                else
                {
                    $slaveCheckboxes.removeAttr( "checked" );
                }
                event.stopPropagation();
            });

            $slaveCheckboxes.click( function() {
                var checked = $slaveCheckboxes.filter( ":checked" );

                if( checked.length < 1 )
                {
                    $checkbox.removeAttr( "checked" );
                }
                else if( checked.length > 0 && checked.length < $slaveCheckboxes.length )
                {
                    $checkbox.removeAttr( "checked" );
                }
                else
                {
                    $checkbox.attr( "checked", "checked" );
                }
            });
        }
    };
})( jQuery );
