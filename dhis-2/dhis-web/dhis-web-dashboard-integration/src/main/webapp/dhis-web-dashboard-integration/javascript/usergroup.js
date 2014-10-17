$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function editUserGroupForm( context ) {
  location.href = 'editUserGroupForm.action?userGroupId=' + context.id;
}

// -----------------------------------------------------------------------------
// Usergroup functionality
// -----------------------------------------------------------------------------

function showUserGroupDetails( context ) {
  jQuery.post('getUserGroup.action', { userGroupId: context.id },
    function( json ) {
      setInnerHTML('nameField', json.userGroup.name);
      setInnerHTML('noOfGroupField', json.userGroup.noOfUsers);
      setInnerHTML('idField', json.userGroup.uid);

      showDetails();
    });
}

function removeUserGroup( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeUserGroup.action');
}

function joinUserGroup( context ) {
    // TODO pre-checks

    $.ajax( {
        type: "POST",
        dataType: "json",
        url: "joinUserGroup.action",
        data: { userGroupId: context.id },
        accepts: "application/json",
        success: function( json ) {
            if( $.parseJSON( json ).response == "success" )
            {
                console.log( "joined group " + context.id );
                $( "#tr" + context.id ).children().find( ".fa-user" ).show();
            }
        },
        error: function( json ) {

        }
    } );
//    jQuery.postJSON( 'joinUserGroup.action', { userGroupUid: context.uid },
//      function( json ) {
//          if( json.response === "success" )
//          {
//              $( "#tr" + context.id ).children().find( ".fa-user" ).show();
//          }
//          else // Error
//          {
//
//          }
//      }
//    );
}

function leaveUserGroup( context ) {
    jQuery.postJSON( 'leaveUserGroup.action', { userGroupUid: context.uid },
      function( json ) {
        if( json.response === "success" )
        {
            $( "#tr" + context.id ).children().find( ".fa-user" ).hide();
        }
        else // Error
        {

        }
      }
    );
}