package org.hisp.dhis.organisationunit;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hisp.dhis.common.Dxf2Namespace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "organisationUnitGroupSets", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnitGroupSets
{
    private List<OrganisationUnitGroupSet> organisationUnitGroupSets = new ArrayList<OrganisationUnitGroupSet>();

    public OrganisationUnitGroupSets()
    {

    }

    @XmlElement( name = "organisationUnitGroupSet" )
    @JsonProperty( value = "organisationUnitGroupSets" )
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( List<OrganisationUnitGroupSet> organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }
}
