package org.hisp.dhis.dashboard.usergroup.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

/**
 * @author Halvdan Hoem Grelland
 */
public class ChangeUserGroupMembershipAction
    implements Action
{
    private static final String KEY_JOIN_GROUP = "join";
    private static final String KEY_LEAVE_GROUP = "leave";

    // -----------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    // -----------------------------------------------------------------------------
    // Input
    // -----------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private String userGroupUid;

    public void setUserGroupUid( String userGroupUid )
    {
        this.userGroupUid = userGroupUid;
    }

    // -----------------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        UserGroup userGroup = userGroupService.getUserGroup( userGroupUid );

        if( currentUser == null || userGroup == null || key == null ||
            !securityService.canUpdate( userGroup ) )
        {
            // TODO Better error checking/handling, maybe?
            return ERROR;
        }

        switch( key )
        {
            case KEY_JOIN_GROUP:
                userGroup.addUser( currentUser );
                break;
            case KEY_LEAVE_GROUP:
                userGroup.removeUser( currentUser );
                break;
            default: // Empty or non-valid key
                return ERROR; // TODO explicit error message plz
        }

        userGroupService.updateUserGroup( userGroup );

        return SUCCESS;
    }
}
