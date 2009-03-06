package org.hisp.dhis.reporting.document.action;

import org.hisp.dhis.document.DocumentService;

import com.opensymphony.xwork.Action;

public class RemoveDocumentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }
    
    // -------------------------------------------------------------------------
    // Dependencies
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
        if ( id != null )
        {
            documentService.deleteDocument( documentService.getDocument( id ) );
        }
        
        return SUCCESS;
    }
}
