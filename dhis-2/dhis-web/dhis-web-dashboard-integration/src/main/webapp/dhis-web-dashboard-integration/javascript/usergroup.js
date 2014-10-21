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
// User group functionality
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
  $.ajax( {
    type: 'POST',
    url: '../api/me/groups',
    data: JSON.stringify( context.uid ),
    contentType: 'application/json',
    success: function( data ) {
      var $userGroup = $( "#tr" + context.id );
      $userGroup.find( ".memberIcon" ).show();
      $userGroup.data( "can-join", false );
      $userGroup.data( "can-leave", true );
    },
    error: function( data ) {
      console.log( data );
    }
  });
}

function leaveUserGroup( context ) {
  $.ajax( {
    type: 'DELETE',
    url: '../api/me/groups/' + context.uid,
    contentType: 'application/json',
    success: function( data ) {
      var $userGroup = $( "#tr" + context.id );
      $userGroup.find( ".memberIcon" ).hide();
      $userGroup.data( "can-join", true );
      $userGroup.data( "can-leave", false );
    },
    error: function( data ) {
      console.log( data );
    }
  });
}