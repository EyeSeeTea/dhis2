package org.hisp.dhis.dashboard.action;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork.Action;

public class RemoveDocumentAction
    implements Action
{
 // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private UserStore userStore;
    
    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    
    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            UserCredentials credentials = userStore.getUserCredentials( user );
            
            Document document = documentService.getDocument( id );
            
            if ( credentials.getDashboardDocuments().remove( document ) )
            {
                userStore.updateUserCredentials( credentials );
            }
        }
        
        return SUCCESS;
    }
}

