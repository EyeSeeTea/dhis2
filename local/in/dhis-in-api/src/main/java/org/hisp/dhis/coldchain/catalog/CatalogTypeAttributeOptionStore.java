package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeAttributeOptionStore
{
    String ID = CatalogTypeAttributeOptionStore.class.getName();
    
    int addCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void updateCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void deleteCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    Collection<CatalogTypeAttributeOption> getAllCatalogTypeAttributeOptions();

}
