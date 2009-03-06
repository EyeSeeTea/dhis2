package org.hisp.dhis.dashboard.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.comparator.DocumentNameComparator;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

public class DocumentContentProvider
    implements ContentProvider
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
    
    private String key;
    
    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // ContentProvider implementation
    // -------------------------------------------------------------------------

    public Map<String, Object> provide()
    {
        Map<String, Object> content = new HashMap<String, Object>();

        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            UserCredentials credentials = userStore.getUserCredentials( user );
            
            List<Document> documents = credentials.getDashboardDocuments();
            
            Collections.sort( documents, new DocumentNameComparator() );
            
            content.put( key, documents );
        }
        
        return content;
    }
}
