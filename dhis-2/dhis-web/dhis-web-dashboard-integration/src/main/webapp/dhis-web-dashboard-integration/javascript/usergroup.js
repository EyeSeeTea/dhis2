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

// TODO combine functions below?

function joinUserGroup( context ) {
    jQuery.postJSON( 'joinUserGroup.action', { userGroupUid: context.uid },
        function( json ) {
            if( json.response === "success" )
            {
                var $userGroup = $( "#tr" + context.id );
                $userGroup.children().find( ".memberIcon" ).show();
                $userGroup.data( "can-join", false );
                $userGroup.data( "can-leave", true );
            }
            else // Error
            {

            }
        }
    );
}

function leaveUserGroup( context ) {
    jQuery.postJSON( 'leaveUserGroup.action', { userGroupUid: context.uid },
      function( json ) {
        if( json.response === "success" )
        {
            var $userGroup = $( "#tr" + context.id );
            $userGroup.children().find( ".memberIcon" ).hide();
            $userGroup.data("can-join", true );
            $userGroup.data("can-leave", false );
        }
        else // Error
        {

        }
      }
    );
}