package org.hisp.dhis.organisationunit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrganisationUnitDataSetAssociationSet
{
    private List<Set<Integer>> dataSetAssociationSets = new ArrayList<Set<Integer>>();
    
    private Map<Integer, Integer> organisationUnitAssociationSetMap = new HashMap<Integer, Integer>();

    public OrganisationUnitDataSetAssociationSet()
    {
    }

    public List<Set<Integer>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    public void setDataSetAssociationSets( List<Set<Integer>> dataSetAssociationSets )
    {
        this.dataSetAssociationSets = dataSetAssociationSets;
    }

    public Map<Integer, Integer> getOrganisationUnitAssociationSetMap()
    {
        return organisationUnitAssociationSetMap;
    }

    public void setOrganisationUnitAssociationSetMap( Map<Integer, Integer> organisationUnitAssociationSetMap )
    {
        this.organisationUnitAssociationSetMap = organisationUnitAssociationSetMap;
    }
}
