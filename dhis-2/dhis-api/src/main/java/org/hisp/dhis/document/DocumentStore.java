package org.hisp.dhis.document;

import java.util.Collection;

public interface DocumentStore
{
    String ID = DocumentStore.class.getName();
    
    int saveDocument( Document document );
        
    Document getDocument( int id );
    
    void deleteDocument( Document document );
    
    Collection<Document> getAllDocuments();
    
    Document getDocumentByName( String name );
}
