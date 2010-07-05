package org.hisp.dhis.mapping.action;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetOrganisationUnitChildrenAction
    implements Action
{
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private Integer node;
    
    public void setNode( Integer node )
    {
        this.node = node;
    }

    private Collection<OrganisationUnit> units = new HashSet<OrganisationUnit>();
    
    public Collection<OrganisationUnit> getUnits()
    {
        return units;
    }

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( node );
        
        if ( unit != null )
        {
            units = unit.getChildren();
        }
        
        return SUCCESS;
    }
}
