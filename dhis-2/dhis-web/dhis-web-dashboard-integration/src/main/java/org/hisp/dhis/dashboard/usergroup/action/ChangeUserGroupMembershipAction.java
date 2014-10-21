package org.hisp.dhis.dashboard.usergroup.action;

import com.opensymphony.xwork2.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log log = LogFactory.getLog( ChangeUserGroupMembershipAction.class );

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

        if( currentUser == null )
        {
            log.error( "Could not get current user" );
            return ERROR;
        }

        if( userGroup == null )
        {
            log.error( "Could not get user group: " + userGroupUid );
            return ERROR;
        }

        if( key == null )
        {
            log.error( "No key." );
            return ERROR;
        }

        if( !securityService.canUpdate( userGroup ) )
        {
            log.info( "User " + currentUser.getUid() + " cannot update user group " + userGroupUid );
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
                return ERROR;
        }

        userGroupService.updateUserGroup( userGroup );

        return SUCCESS;
    }
}
