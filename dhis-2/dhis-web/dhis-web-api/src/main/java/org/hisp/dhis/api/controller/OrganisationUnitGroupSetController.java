package org.hisp.dhis.api.controller;

import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/organisationUnitGroupSets" )
public class OrganisationUnitGroupSetController
{
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    public OrganisationUnitGroupSetController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public OrganisationUnitGroupSets getOrganisationUnits()
    {
        OrganisationUnitGroupSets organisationUnitGroupSets = new OrganisationUnitGroupSets();
        organisationUnitGroupSets.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() ) );

        return organisationUnitGroupSets;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public OrganisationUnitGroupSet getOrganisationUnit( @PathVariable( "uid" ) String uid, HttpServletRequest request )
    {
        OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( uid );

        return organisationUnitGroupSet;
    }
}
