// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRoleDetails( context ) {
  jQuery.post('getRole.action', { id: context.id }, function( json ) {
    setInnerHTML('nameField', json.userRole.name);
    setInnerHTML('membersField', json.userRole.members);
    setInnerHTML('dataSetsField', json.userRole.dataSets);

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Remove role
// -----------------------------------------------------------------------------

function removeRole( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeRole.action');
}
