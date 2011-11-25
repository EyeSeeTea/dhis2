package org.hisp.dhis.api.controller;

import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitLevels;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
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
@RequestMapping( value = "/organisationUnitLevels" )
public class OrganisationUnitLevelController
{
    @Autowired
    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitLevelController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public OrganisationUnitLevels getOrganisationUnitLevels()
    {
        OrganisationUnitLevels organisationUnitLevels = new OrganisationUnitLevels();
        organisationUnitLevels.setOrganisationUnitLevels( new ArrayList<OrganisationUnitLevel>( organisationUnitService.getOrganisationUnitLevels() ) );

        return organisationUnitLevels;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public OrganisationUnitLevel getOrganisationUnit( @PathVariable( "uid" ) String uid, HttpServletRequest request )
    {
        OrganisationUnitLevel organisationUnitLevel = organisationUnitService.getOrganisationUnitLevel( uid );

        return organisationUnitLevel;
    }
}
