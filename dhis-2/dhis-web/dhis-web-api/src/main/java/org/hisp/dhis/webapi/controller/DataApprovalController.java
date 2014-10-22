package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.view.BasicView;
import org.hisp.dhis.dataapproval.DataApproval;
import org.hisp.dhis.dataapproval.DataApprovalPermissions;
import org.hisp.dhis.dataapproval.DataApprovalService;
import org.hisp.dhis.dataapproval.DataApprovalState;
import org.hisp.dhis.dataapproval.DataApprovalStateRequest;
import org.hisp.dhis.dataapproval.DataApprovalStateRequests;
import org.hisp.dhis.dataapproval.DataApprovalStateResponse;
import org.hisp.dhis.dataapproval.DataApprovalStateResponses;
import org.hisp.dhis.dataapproval.DataApprovalStatus;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.InputUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = DataApprovalController.RESOURCE_PATH )
public class DataApprovalController
{
    private final static Log log = LogFactory.getLog( DataApprovalController.class );

    public static final String RESOURCE_PATH = "/dataApprovals";

    public static final String ACCEPTANCES_PATH = "/acceptances";

    private static final String STATUS_PATH = "/status";

    private static final String MULTIPLE_SAVE_RESOURCE_PATH = "/multiple";

    private static final String APPROVAL_STATE = "state";

    private static final String APPROVAL_MAY_APPROVE = "mayApprove";

    private static final String APPROVAL_MAY_UNAPPROVE = "mayUnapprove";

    private static final String APPROVAL_MAY_ACCEPT = "mayAccept";

    private static final String APPROVAL_MAY_UNACCEPT = "mayUnaccept";

    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private InputUtils inputUtils;

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON )
    public void getApprovalState(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) Set<String> cog,
        @RequestParam( required = false ) String cp, HttpServletResponse response )
        throws IOException
    {
        log.info( "GET " + RESOURCE_PATH + "?ds=" + ds + "&pe=" + pe + "&ou=" + ou
            + (cog == null || cog.isEmpty() ? "" : ("&cog=" + Arrays.toString( cog.toArray() )))
            + (cp == null ? "" : ("&cp=" + cp)) );

        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        Set<CategoryOptionGroup> categoryOptionGroups = null;

        if ( cog != null && !cog.isEmpty() )
        {
            categoryOptionGroups = inputUtils.getAttributeOptionGroups( response, cog );

            if ( categoryOptionGroups == null )
            {
                return;
            }
        }

        Set<DataElementCategoryOption> categoryOptions = inputUtils.getAttributeOptions( response, cp );

        if ( categoryOptions != null && categoryOptions.isEmpty() )
        {
            return;
        }

        DataApprovalPermissions permissions = dataApprovalService
            .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, categoryOptions );

        Map<String, Object> approvalState = new HashMap<>();
        approvalState.put( APPROVAL_STATE, permissions.getDataApprovalStatus().getDataApprovalState().toString() );
        approvalState.put( APPROVAL_MAY_APPROVE, permissions.isMayApprove() );
        approvalState.put( APPROVAL_MAY_UNAPPROVE, permissions.isMayUnapprove() );
        approvalState.put( APPROVAL_MAY_ACCEPT, permissions.isMayAccept() );
        approvalState.put( APPROVAL_MAY_UNACCEPT, permissions.isMayUnaccept() );

        JacksonUtils.toJson( response.getOutputStream(), approvalState );
    }

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON, value = STATUS_PATH )
    public void getApproval(
        @RequestParam Set<String> ds,
        @RequestParam( required = false ) String pe,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> ou,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response )
        throws IOException
    {

        Set<DataSet> dataSets = new HashSet<>();

        dataSets.addAll( manager.getByUid( DataSet.class, ds ) );

        Set<Period> periods = new HashSet<>();

        PeriodType periodType = periodService.getPeriodTypeByName( pe );

        if ( periodType != null )
        {
            periods.addAll( periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
        }
        else
        {
            periods.addAll( periodService.getPeriodsBetweenDates( startDate, endDate ) );
        }

        Set<OrganisationUnit> organisationUnits = new HashSet<>();

        if ( children )
        {
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsWithChildren( ou ) );
        }
        else
        {
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsByUid( ou ) );
        }

        DataApprovalStateResponses dataApprovalStateResponses = new DataApprovalStateResponses();

        for ( DataSet dataSet : dataSets )
        {
            for ( OrganisationUnit organisationUnit : organisationUnits )
            {
                if ( organisationUnit.getDataSets().contains( dataSet ) )
                {
                    for ( Period period : periods )
                    {
                        dataApprovalStateResponses.add(
                            getDataApprovalStateResponse( dataSet, organisationUnit, period ) );
                    }
                }
            }
        }

        JacksonUtils.toJsonWithView( response.getOutputStream(), dataApprovalStateResponses, BasicView.class );
    }

    private DataApprovalStateResponse getDataApprovalStateResponse( DataSet dataSet, OrganisationUnit organisationUnit,
        Period period )
    {
        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period,
            organisationUnit, null, null );

        DataApprovalStatus dataApprovalStatus = permissions.getDataApprovalStatus();

        DataApproval dataApproval = dataApprovalStatus.getDataApproval();
        Date createdDate = (dataApproval == null) ? null : dataApproval.getCreated();
        String createdByUsername = (dataApproval == null) ? null : dataApproval.getCreator().getUsername();

        String state = dataApprovalStatus.getDataApprovalState().toString();

        return new DataApprovalStateResponse( dataSet, period, organisationUnit, state, createdDate, createdByUsername,
            permissions.isMayApprove(), permissions.isMayUnapprove(), permissions.isMayAccept(),
            permissions.isMayUnaccept() );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_APPROVE_DATA') or hasRole('F_APPROVE_DATA_LOWER_LEVELS')" )
    @RequestMapping( method = RequestMethod.POST )
    public void saveApproval(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cog, HttpServletResponse response )
    {
        log.info( "POST " + RESOURCE_PATH + "?ds=" + ds + "&pe=" + pe + "&ou=" + ou
            + (cog == null ? "" : ("&cog=" + cog)) );

        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        Set<CategoryOptionGroup> categoryOptionGroups = null;

        if ( cog != null )
        {
            categoryOptionGroups = inputUtils.getAttributeOptionGroup( response, cog );

            if ( categoryOptionGroups == null )
            {
                return;
            }
        }

        DataApprovalPermissions permissions = dataApprovalService
            .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, null );

        DataApprovalState state = permissions.getDataApprovalStatus().getDataApprovalState();

        if ( state != DataApprovalState.UNAPPROVED_READY &&
            state != DataApprovalState.PARTIALLY_APPROVED_HERE )
        {
            ContextUtils
                .conflictResponse( response, "Data is not ready for approval here, current state is: " + state.name() );
            return;
        }

        if ( !permissions.isMayApprove() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to approve for "
                + approvalParameters( dataSet, period, organisationUnit, categoryOptionGroups ) );
            return;
        }

        User user = currentUserService.getCurrentUser();

        CategoryOptionGroup categoryOptionGroup = categoryOptionGroups == null || categoryOptionGroups.isEmpty() ?
            null : categoryOptionGroups.iterator().next();

        DataApproval approval = new DataApproval( permissions.getDataApprovalStatus().getDataApprovalLevel(),
            dataSet, period, organisationUnit, categoryOptionGroup, false, new Date(), user );

        dataApprovalService.addDataApproval( approval );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_APPROVE_DATA') or hasRole('F_APPROVE_DATA_LOWER_LEVELS')" )
    @RequestMapping( method = RequestMethod.POST, value = MULTIPLE_SAVE_RESOURCE_PATH )
    public void saveApproval(
        @RequestBody DataApprovalStateRequests dataApprovalStateRequests,
        HttpServletResponse response )
    {
        List<DataApproval> dataApprovals = new ArrayList<>();

        for ( DataApprovalStateRequest dataApprovalStateRequest : dataApprovalStateRequests )
        {
            DataSet dataSet = dataSetService.getDataSet( dataApprovalStateRequest.getDs() );

            if ( dataSet == null )
            {
                ContextUtils
                    .conflictResponse( response, "Illegal data set identifier: " + dataApprovalStateRequest.getDs() );
                return;
            }

            Period period = PeriodType.getPeriodFromIsoString( dataApprovalStateRequest.getPe() );

            if ( period == null )
            {
                ContextUtils.conflictResponse( response, "Illegal period identifier: " + dataApprovalStateRequest.getPe() );
                return;
            }

            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(
                dataApprovalStateRequest.getOu() );

            if ( organisationUnit == null )
            {
                ContextUtils.conflictResponse( response,
                    "Illegal organisation unit identifier: " + dataApprovalStateRequest.getOu() );
                return;
            }

            Set<CategoryOptionGroup> categoryOptionGroups = null;

            if ( dataApprovalStateRequest.getCog() != null )
            {
                categoryOptionGroups = inputUtils.getAttributeOptionGroup( response, dataApprovalStateRequest.getCog() );

                if ( categoryOptionGroups == null )
                {
                    return;
                }
            }

            DataApprovalPermissions permissions = dataApprovalService
                .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, null );

            DataApprovalState state = permissions.getDataApprovalStatus().getDataApprovalState();

            if ( state != DataApprovalState.UNAPPROVED_READY &&
                state != DataApprovalState.PARTIALLY_APPROVED_HERE )
            {
                ContextUtils.conflictResponse( response,
                    "Data is not ready for approval here, current state is: " + state.name() );
                return;
            }

            if ( !permissions.isMayApprove() )
            {
                ContextUtils.conflictResponse( response, "Current user is not authorized to approve for "
                    + approvalParameters( dataSet, period, organisationUnit, categoryOptionGroups ) );
                return;
            }

            User user = dataApprovalStateRequest.getAb() == null ?
                currentUserService.getCurrentUser() :
                userService.getUserCredentialsByUsername( dataApprovalStateRequest.getAb() ).getUser();

            Date approvalDate = (dataApprovalStateRequest.getAd() == null) ? new Date() : dataApprovalStateRequest.getAd();

            CategoryOptionGroup categoryOptionGroup =
                categoryOptionGroups == null || categoryOptionGroups.isEmpty() ? null :
                    categoryOptionGroups.iterator().next();

            dataApprovals.add(
                new DataApproval( permissions.getDataApprovalStatus().getDataApprovalLevel(), dataSet, period,
                    organisationUnit, categoryOptionGroup, false, approvalDate, user ) );
        }

        dataApprovalService.addAllDataApprovals( dataApprovals );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_APPROVE_DATA') or hasRole('F_APPROVE_DATA_LOWER_LEVELS')" )
    @RequestMapping( method = RequestMethod.DELETE )
    public void removeApproval(
        @RequestParam Set<String> ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cog, HttpServletResponse response )
    {
        log.info( "DELETE " + RESOURCE_PATH + "?ds=" + ds + "&pe=" + pe + "&ou=" + ou
            + (cog == null ? "" : ("&cog=" + cog)) );

        Set<DataSet> dataSets = new HashSet<>( manager.getByUid( DataSet.class, ds ) );

        if ( dataSets.size() != ds.size() )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier in this list: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        Set<CategoryOptionGroup> categoryOptionGroups = null;

        if ( cog != null )
        {
            categoryOptionGroups = inputUtils.getAttributeOptionGroup( response, cog );

            if ( categoryOptionGroups == null )
            {
                return;
            }
        }

        List<DataApproval> dataApprovalList = newArrayList();

        for ( DataSet dataSet : dataSets )
        {
            DataApprovalPermissions permissions = dataApprovalService
                .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, null );

            DataApprovalState state = permissions.getDataApprovalStatus().getDataApprovalState();

            if ( state != DataApprovalState.APPROVED_HERE && state != DataApprovalState.ACCEPTED_HERE &&
                state != DataApprovalState.PARTIALLY_APPROVED_HERE && state != DataApprovalState.PARTIALLY_ACCEPTED_HERE )
            {
                ContextUtils
                    .conflictResponse( response, "Data is not approved here, current state is: " + state.name() );
                return;
            }

            if ( !permissions.isMayUnapprove() )
            {
                ContextUtils.conflictResponse( response, "Current user is not authorized to unapprove for "
                    + approvalParameters( dataSet, period, organisationUnit, categoryOptionGroups ) );
                return;
            }

            dataApprovalList.add( permissions.getDataApprovalStatus().getDataApproval() );
        }

        dataApprovalService.deleteDataApprovals( dataApprovalList );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCEPT_DATA_LOWER_LEVELS')" )
    @RequestMapping( value = ACCEPTANCES_PATH, method = RequestMethod.POST )
    public void acceptApproval(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cog, HttpServletResponse response )
    {
        log.info( "POST " + RESOURCE_PATH + ACCEPTANCES_PATH + "?ds=" + ds + "&pe=" + pe + "&ou=" + ou
            + (cog == null ? "" : ("&cog=" + cog)) );

        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        Set<CategoryOptionGroup> categoryOptionGroups = null;

        if ( cog != null )
        {
            categoryOptionGroups = inputUtils.getAttributeOptionGroup( response, cog );

            if ( categoryOptionGroups == null )
            {
                return;
            }
        }

        DataApprovalPermissions permissions = dataApprovalService
            .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, null );

        DataApprovalState state = permissions.getDataApprovalStatus().getDataApprovalState();

        if ( state != DataApprovalState.APPROVED_HERE &&
            state != DataApprovalState.PARTIALLY_ACCEPTED_HERE )
        {
            ContextUtils.conflictResponse( response,
                "Data is not ready for accepting here, current state is: " + state.name() );
            return;
        }

        if ( !permissions.isMayAccept() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to accept approval for "
                + approvalParameters( dataSet, period, organisationUnit, categoryOptionGroups ) );
            return;
        }

        dataApprovalService.accept( permissions.getDataApprovalStatus().getDataApproval() );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCEPT_DATA_LOWER_LEVELS')" )
    @RequestMapping( value = ACCEPTANCES_PATH, method = RequestMethod.DELETE )
    public void unacceptApproval(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cog, HttpServletResponse response )
    {
        log.info( "DELETE " + RESOURCE_PATH + ACCEPTANCES_PATH + "?ds=" + ds + "&pe=" + pe + "&ou=" + ou
            + (cog == null ? "" : ("&cog=" + cog)) );

        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        Set<CategoryOptionGroup> categoryOptionGroups = null;

        if ( cog != null )
        {
            categoryOptionGroups = inputUtils.getAttributeOptionGroup( response, cog );

            if ( categoryOptionGroups == null )
            {
                return;
            }
        }

        DataApprovalPermissions permissions = dataApprovalService
            .getDataApprovalPermissions( dataSet, period, organisationUnit, categoryOptionGroups, null );

        DataApprovalState state = permissions.getDataApprovalStatus().getDataApprovalState();

        if ( state != DataApprovalState.ACCEPTED_HERE &&
            state != DataApprovalState.PARTIALLY_ACCEPTED_HERE )
        {
            ContextUtils.conflictResponse( response, "Data is not accepted here, current state is: " + state.name() );
            return;
        }

        if ( !permissions.isMayUnaccept() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to unaccept approval for "
                + approvalParameters( dataSet, period, organisationUnit, categoryOptionGroups ) );
            return;
        }

        dataApprovalService.unaccept( permissions.getDataApprovalStatus().getDataApproval() );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String approvalParameters( DataSet dataSet, Period period, OrganisationUnit organisationUnit,
        Set<CategoryOptionGroup> categoryOptionGroups )
    {
        return "dataSet " + dataSet.getName()
            + ", period " + period.getName()
            + ", org unit " + organisationUnit.getName()
            + ", categoryOptionGroup " + (categoryOptionGroups == null ?
            "null" :
            ((CategoryOptionGroup) categoryOptionGroups.toArray()[0]).getName());
    }
}
