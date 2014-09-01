
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

// Experimental jquery extension to provide multi select button and friends
jQuery.fn.extend(
{
    _multiCheckbox: function( $checkboxContainer )
    {
        return this.each( function()
        {
            var $checkbox = $( this );

            if( !$checkbox.is( "input" ) || $checkbox.attr( "type") !== "checkbox" )
            {
                throw new Error( "Can only apply to a checkbox." );
            }

            var $checkboxes = [];

            if( typeof $checkboxContainer !== "undefined" )
            {
                $checkboxes = $checkboxContainer.find( "input:checkbox" );
            }

            $checkbox.click( function( event )
            {
                if( this.checked )
                {
                    $checkboxes.attr( "checked", "checked" );
                }
                else
                {
                    $checkboxes.removeAttr( "checked" );
                }
                event.stopPropagation();
            });

            $checkboxes.click( function()
            {
                var checked = $checkboxes.filter( ":checked" );

                if( checked.length < 1 )
                {
                    $checkbox.removeAttr( "checked" );
                }
                else if( checked.length > 0 && checked.length < $checkboxes.length )
                {
                    $checkbox.removeAttr( "checked" );
                }
                else
                {
                    $checkbox.attr( "checked", "checked" );
                }
            });
        });
    },
    _dropdownMenu: function( menuItems, menuClass, buttonClass, buttonElements )
    {
        // Build menu
        var $menu = $( "<ul>", { "class" : menuClass } );

        $( menuItems ).each( function()
        {
            var el = $( "<li>" );
            $( el ).append( $( "<a>", { href: "#", text: this.displayName } ) );

            el.action = this.action;
            el.onAction = this.onAction;

            if( typeof this.actionArgFunc !== "undefined" )
            {
                el.actionArgFunc = this.actionArgFunc;

                // Create and attach click handler to menu item
                el.click( function()
                {
                    el.onAction();
                    return el.action( el.actionArgFunc() );
                });
            }
            else
            {
                el.click( function()
                {
                    el.onAction();
                    el.action();
                });
            }

            $menu.append( el );
        });

        $( document.body ).append( $menu );

        // Build button
        var $button = $( "<a>", { href: "#", "class" : buttonClass } );

        $( buttonElements ).each( function()
        {
            $button.append( this );
        });

        // Click handler to show/hide menu
        $button.click( function( event )
        {
            $( document ).one( "click", function()
            {
                $menu.css( "visibility", "hidden" );
            });

            if( $menu.css( "visibility") !== "visible" )
            {
                $menu.css( "visibility", "visible" );
            }
            else
            {
                $menu.css( "visibility", "hidden" );
            }
            event.stopPropagation();
        });

        this.append( $button );

        $menu.css( "visibility", "hidden" );
        $menu.position(
            {
                my: "left top",
                at: "left bottom+1",
                of: $button
            }
        );
    },
    multiCheckDropdownCombo: function( $checkboxContainer, menuItems, options )
    {
        var $cb = $( "<input>", { type: "checkbox" } )._multiCheckbox( $checkboxContainer );

        var buttonElements = [
            $cb,
            $( "<span>", { "class" : "downArrow" } )
        ];

        var defaults = {
            menuClass: "multiSelectMenu",
            buttonClass: "multiSelectButton"
        };

        if ( typeof options === "undefined" )
        {
            options = defaults;
        }

        $( menuItems ).each( function()
        {
            this.actionArgFunc = function()
            {
                var checked = [];
                $checkboxContainer.find( "input:checkbox:checked" ).each( function()
                {
                    checked.push( this.value );
                });
                return checked;
            };

            this.onAction = function()
            {
                $cb.removeAttr( "checked" );
            };
        });
        $( this )._dropdownMenu( menuItems, options.menuClass, options.buttonClass, buttonElements );
    }
});
