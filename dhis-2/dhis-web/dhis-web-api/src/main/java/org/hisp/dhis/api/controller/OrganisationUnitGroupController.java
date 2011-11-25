package org.hisp.dhis.api.controller;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
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
@RequestMapping( value = "/organisationUnitGroups" )
public class OrganisationUnitGroupController
{
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    public OrganisationUnitGroupController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public OrganisationUnitGroups getOrganisationUnits()
    {
        OrganisationUnitGroups organisationUnitGroups = new OrganisationUnitGroups();
        organisationUnitGroups.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );

        return organisationUnitGroups;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public OrganisationUnitGroup getOrganisationUnit( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( uid );

        return organisationUnitGroup;
    }
}
