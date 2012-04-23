package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeComparator;

import com.opensymphony.xwork2.Action;

public class GetColdChainCatalogTypeAttributeListAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private CatalogTypeAttributeService catalogTypeAttributeService;
    
    public void setCatalogTypeAttributeService( CatalogTypeAttributeService catalogTypeAttributeService )
    {
        this.catalogTypeAttributeService = catalogTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
    
    public List<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>(catalogTypeAttributeService.getAllCatalogTypeAttributes());
        Collections.sort( catalogTypeAttributes, new CatalogTypeAttributeComparator() );
        
        return SUCCESS;
    }
    
}
