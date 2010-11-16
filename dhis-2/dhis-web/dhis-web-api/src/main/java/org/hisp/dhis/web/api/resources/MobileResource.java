package org.hisp.dhis.web.api.resources;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.MobileModel;
import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.service.FacilityReportingService;
import org.hisp.dhis.web.api.service.IActivityPlanService;
import org.hisp.dhis.web.api.service.IActivityValueService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Required;

@Path( "/mobile" )
public class MobileResource
{

    // Dependencies

    private IActivityValueService iactivityValueService;

    private IProgramService programService;

    private IActivityPlanService activityPlanService;

    private FacilityReportingService facilityReportingService;

    private CurrentUserService currentUserService;

    @Required
    public void setProgramService( IProgramService programService )
    {
        this.programService = programService;
    }

    @Required
    public void setActivityPlanService( IActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

    @Required
    public void setIactivityValueService( IActivityValueService iactivityValueService )
    {
        this.iactivityValueService = iactivityValueService;
    }

    @Required
    public void setFacilityReportingService( FacilityReportingService facilityReportingService )
    {
        this.facilityReportingService = facilityReportingService;
    }

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // Resource methods

    @GET
    @Produces( MediaType.MOBILE_SERIALIZED )
    public Response getOrgUnitForUser()
    {
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        if ( units.isEmpty() )
        {
            return Response.status( Status.CONFLICT ).entity( "User is not registered to a unit." ).build();
        }
        else if ( units.size() > 1 )
        {
            StringBuilder sb = new StringBuilder( "User is registered to more than one unit: " );

            int i = units.size();
            for ( OrganisationUnit unit : units )
            {
                sb.append( unit.getName() );
                if ( i-- > 1 )
                    sb.append( ", " );
            }

            return Response.status( Status.CONFLICT ).entity( sb.toString() ).build();
        }

        OrganisationUnit unit = units.iterator().next();
        return Response.ok( getOrgUnit( unit ) ).build();
    }

    @GET
    @Path( "all" )
    @Produces( MediaType.MOBILE_SERIALIZED )
    public MobileModel getAllDataForUser( @HeaderParam( "accept-language" ) String locale )
    {
        
        
        MobileModel mobileWrapper = new MobileModel();
        mobileWrapper.setActivityPlan( activityPlanService.getCurrentActivityPlan( locale ) );

        mobileWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );

        Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();

        if ( units.size() == 1 )
        {
            OrganisationUnit unit = units.iterator().next();
            mobileWrapper.setDatasets( facilityReportingService.getMobileDataSetsForUnit( unit, locale )  );
        }
        else
        {
            // FIXME: Should handle multiple explicitly;
        }


        return mobileWrapper;
    }

    @GET
    @Path( "activities/currentplan" )
    @Produces( MediaType.ACTIVITYPLAN_SERIALIZED )
    public ActivityPlan getCurrentActivityPlan( @HeaderParam( "accept-language" ) String locale )
    {
        return activityPlanService.getCurrentActivityPlan( locale );
    }

    @POST
    @Path( "dataSets" )
    @Consumes( MediaType.DATASETVALUE_SERIALIZED )
    @Produces( "application/xml" )
    public String saveDataSetValues( DataSetValue dataSetValue )
    {
        Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();

        if ( units.size() != 1 )
        {
            return "INVALID_REPORTING_UNIT";
        }

        OrganisationUnit unit = units.iterator().next();
        
        return facilityReportingService.saveDataSetValues( unit, dataSetValue );
    }

    @POST
    @Path( "activities" )
    @Consumes( MediaType.ACTIVITYVALUELIST_SERIALIZED )
    @Produces( "application/xml" )
    public String saveActivityReport( ActivityValue activityValue )
    {
        return iactivityValueService.saveValues( activityValue );
    }

    private OrgUnit getOrgUnit( OrganisationUnit unit )
    {
        OrgUnit m = new OrgUnit();

        m.setId( unit.getId() );
        m.setName( unit.getShortName() );

        return m;
    }

}
