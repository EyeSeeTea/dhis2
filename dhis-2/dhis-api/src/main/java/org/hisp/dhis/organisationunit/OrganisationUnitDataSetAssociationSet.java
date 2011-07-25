package org.hisp.dhis.organisationunit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataset.DataSet;

public class OrganisationUnitDataSetAssociationSet
{
    private List<List<DataSet>> dataSetAssociationSets = new ArrayList<List<DataSet>>();
    
    private Map<Integer, Integer> organisationUnitAssociationSetMap = new HashMap<Integer, Integer>();

    public OrganisationUnitDataSetAssociationSet()
    {
    }

    public List<List<DataSet>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    public void setDataSetAssociationSets( List<List<DataSet>> dataSetAssociationSets )
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
