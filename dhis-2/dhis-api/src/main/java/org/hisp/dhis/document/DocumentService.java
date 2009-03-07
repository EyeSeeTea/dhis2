package org.hisp.dhis.document;

import java.util.Collection;

public interface DocumentService
{
    String ID = DocumentService.class.getName();
    String DIR = "documents";
    
    int saveDocument( Document document );
    
    Document getDocument( int id );

    void deleteDocument( Document document );
    
    Collection<Document> getAllDocuments();
    
    Document getDocumentByName( String name );
}
