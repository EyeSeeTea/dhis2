package org.hisp.dhis.document.impl;

import java.util.Collection;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.document.DocumentStore;

public class DefaultDocumentService
    implements DocumentService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DocumentStore documentStore;

    public void setDocumentStore( DocumentStore documentStore )
    {
        this.documentStore = documentStore;
    }

    // -------------------------------------------------------------------------
    // DocumentService implementation
    // -------------------------------------------------------------------------

    public int saveDocument( Document document )
    {
        return documentStore.saveDocument( document );
    }

    public Document getDocument( int id )
    {
        return documentStore.getDocument( id );
    }

    public void deleteDocument( Document document )
    {
        documentStore.deleteDocument( document );
    }
    
    public Collection<Document> getAllDocuments()
    {
        return documentStore.getAllDocuments();
    }

    public Document getDocumentByName( String name )
    {
        return documentStore.getDocumentByName( name );
    }
}
