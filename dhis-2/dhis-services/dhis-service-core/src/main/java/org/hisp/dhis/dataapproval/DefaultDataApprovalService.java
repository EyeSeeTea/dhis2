package org.hisp.dhis.dataapproval;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalService
    implements DataApprovalService
{
    private final static Log log = LogFactory.getLog( DefaultDataApprovalService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    public void setDataApprovalStore( DataApprovalStore dataApprovalStore )
    {
        this.dataApprovalStore = dataApprovalStore;
    }

    private DataApprovalLevelService dataApprovalLevelService;

    public void setDataApprovalLevelService( DataApprovalLevelService dataApprovalLevelService )
    {
        this.dataApprovalLevelService = dataApprovalLevelService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    // -------------------------------------------------------------------------
    // DataApproval
    // -------------------------------------------------------------------------

    public void approveData( DataApproval dataApproval,
                             Set<DataSet> dataSets,
                             Set<CategoryOptionGroup> attributeOptionGroups,
                             Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException
    {
        doApprove( makeApprovalsList( dataApproval, dataSets, attributeOptionGroups, attributeOptions, false ) );
    }

    public void approveData( List<DataApproval> dataApprovalList )
            throws ApprovalException
    {
        doApprove( checkApprovalsList( dataApprovalList, false ) );
    }

    public void unapproveData( DataApproval dataApproval,
                               Set<DataSet> dataSets,
                               Set<CategoryOptionGroup> attributeOptionGroups,
                               Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException
    {
        doUnapprove( makeApprovalsList( dataApproval, dataSets, attributeOptionGroups, attributeOptions, false ) );
    }

    public void unapproveData( List<DataApproval> dataApprovalList )
            throws ApprovalException
    {
        doUnapprove( checkApprovalsList( dataApprovalList, false ) );
    }

    public void acceptData( DataApproval dataApproval,
                            Set<DataSet> dataSets,
                            Set<CategoryOptionGroup> attributeOptionGroups,
                            Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException
    {
        doAcceptOrUnaccept( makeApprovalsList( dataApproval, dataSets, attributeOptionGroups, attributeOptions, false ), true );
    }

    public void acceptData( List<DataApproval> dataApprovalList )
            throws ApprovalException
    {
        doAcceptOrUnaccept( checkApprovalsList( dataApprovalList, false ), true );
    }

    public void unacceptData( DataApproval dataApproval,
                              Set<DataSet> dataSets,
                              Set<CategoryOptionGroup> attributeOptionGroups,
                              Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException
    {
        doAcceptOrUnaccept( makeApprovalsList( dataApproval, dataSets, attributeOptionGroups, attributeOptions, false ), false );
    }

    public void unacceptData( List<DataApproval> dataApprovalList )
            throws ApprovalException
    {
        doAcceptOrUnaccept( checkApprovalsList( dataApprovalList, false ), false );
    }

    /*
    public void deleteDataApproval( DataApproval dataApproval )
    {
        boolean mayUnapprove = mayUnapprove( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit(), dataApproval.isAccepted() )
            || mayAcceptOrUnaccept( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() );

        if ( ( dataApproval.getCategoryOptionGroup() == null || securityService.canRead( dataApproval.getCategoryOptionGroup() ) )
            && mayUnapprove )
        {
            PeriodType selectionPeriodType = dataApproval.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = dataApproval.getDataSet().getPeriodType();

            if ( selectionPeriodType.equals( dataSetPeriodType ) )
            {
                dataApprovalStore.deleteDataApproval( dataApproval );

                for ( OrganisationUnit ancestor : dataApproval.getOrganisationUnit().getAncestors() )
                {
                    DataApproval ancestorApproval = dataApprovalStore.getDataApproval(
                        dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor, dataApproval.getCategoryOptionGroup() );

                    if ( ancestorApproval != null )
                    {
                        dataApprovalStore.deleteDataApproval( ancestorApproval );
                    }
                }
            }
            else if ( selectionPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                log.warn( "Attempted data unapproval for period " + dataApproval.getPeriod().getIsoDate()
                    + " is incompatible with data set period type " + dataSetPeriodType.getName() + "." );
            }
            else
            {
                unapproveCompositePeriod( dataApproval );
            }
        }
        else
        {
            warnNotPermitted( dataApproval, "unapprove", mayUnapprove );
        }
    } */

    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
            throws ApprovalException
    {
        DataApproval da = new DataApproval( null, dataSet, period, organisationUnit, attributeOptionCombo, false, null, null );

        Set<DataElementCategoryOption> attributeCategoryOptions = ( attributeOptionCombo == null || attributeOptionCombo.equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) )
                ? null : attributeOptionCombo.getCategoryOptions();

        List<DataApproval> dataApprovalList = makeApprovalsList( da, null, null, attributeCategoryOptions, true );

        return ( doGetDataApprovalStatus( dataApprovalList, da ) );
    }

    public DataApprovalStatusAndPermissions getDataApprovalPermissions( Set<DataSet> dataSets, Period period,
        OrganisationUnit organisationUnit, Set<CategoryOptionGroup> categoryOptionGroups,
        Set<DataElementCategoryOption> attributeCategoryOptions, DataApprovalLevel dataApprovalLevel )
            throws ApprovalException
    {
        DataApproval da = new DataApproval( null, null, period, organisationUnit, null, false, null, null );

        DataApprovalStatus status = doGetDataApprovalStatus( makeApprovalsList( da, dataSets, categoryOptionGroups, attributeCategoryOptions, true ), da );

        DataApprovalStatusAndPermissions permissions = new DataApprovalStatusAndPermissions();

        permissions.setDataApprovalStatus( status );

        DataApprovalLevel dal = status.getDataApprovalLevel();

        if ( dal != null && securityService.canRead( dal )
            && ( dal.getCategoryOptionGroupSet() == null || securityService.canRead( dal.getCategoryOptionGroupSet() ) )
            && canReadOneCategoryOptionGroup( categoryOptionGroups ) )
        {
            DataApprovalState state = status.getDataApprovalState();

            permissions.setMayApprove( state.isApprovable() && mayApprove( status.getDataApprovalLevel(), organisationUnit ) );
            permissions.setMayUnapprove( state.isUnapprovable() && mayUnapprove( status.getDataApprovalLevel(), organisationUnit, status.getDataApprovalState().isAccepted() ) );
            permissions.setMayAccept( state.isAcceptable() && mayAcceptOrUnaccept( status.getDataApprovalLevel(), organisationUnit ) );
            permissions.setMayUnaccept( state.isUnacceptable() && mayAcceptOrUnaccept( status.getDataApprovalLevel(), organisationUnit ) );
        }

        log.debug( "Returning permissions for " + organisationUnit.getName()
                + " " + status.getDataApprovalState().name()
                + " may approve = " + permissions.isMayApprove()
                + " may unapprove = " + permissions.isMayUnapprove()
                + " may accept = " + permissions.isMayAccept()
                + " may unaccept = " + permissions.isMayUnaccept() );

        return permissions;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Makes a list of DataApprovals from a single, prototype DataApproval
     * object by expanding over the user-specified category option groups
     * and/or category options.
     * <p>
     * If the user specified category option groups, then each attribute
     * option combo must have at least one option from these groups.
     * <p>
     * If the user specified category options, then every attribute
     * option combo must include the category option.
     *
     * @param dataApproval the prototype DataApproval object
     * @param dataSets data sets to check for approval, if any
     * @param attributeOptionGroups attribute option groups, if any
     * @param attributeOptions attribute options, if any
     * @param isGetStatus true if get, false if action
     * @return list of DataApprovals
     * @throws ApprovalException
     */
    private List<DataApproval> makeApprovalsList( DataApproval dataApproval,
                                                  Set<DataSet> dataSets,
                                                  Set<CategoryOptionGroup> attributeOptionGroups,
                                                  Set<DataElementCategoryOption> attributeOptions,
                                                  boolean isGetStatus )
            throws ApprovalException
    {
        if ( ( attributeOptionGroups == null || attributeOptionGroups.isEmpty() )
            && attributeOptions == null || attributeOptions.isEmpty() )
        {
            return checkApprovalsList( org.hisp.dhis.system.util.CollectionUtils.asList( dataApproval ), isGetStatus );
        }

        DataApproval da = checkDataApproval( dataApproval, false );

        if ( isGetStatus ) // For getStatus, patch approval level back into the (constructed) original.
        {
            dataApproval.setDataApprovalLevel( da.getDataApprovalLevel() );
        }

        Set<DataElementCategoryOption> groupOptions = null;

        if ( attributeOptionGroups != null && !attributeOptionGroups.isEmpty() )
        {
            groupOptions = optionsFromAttributeOptionGroups( da, attributeOptionGroups );

            if ( groupOptions.isEmpty() )
            {
                throw new NoAttributeOptionsFoundInGroupsException();
            }
        }

        Set<DataElementCategoryOptionCombo> combos = new HashSet<>();

        for ( DataElementCategoryOptionCombo combo : da.getDataSet().getCategoryCombo().getOptionCombos() )
        {
            Set<DataElementCategoryOption> comboOptions = combo.getCategoryOptions();

            if ( ( groupOptions == null || CollectionUtils.containsAny( comboOptions, groupOptions ) )
                && ( attributeOptions == null || attributeOptions.isEmpty() || CollectionUtils.isSubCollection( attributeOptions, comboOptions ) )
                && userCanReadAny( comboOptions ) )
            {
                combos.add( combo );
            }
        }

        List<DataApproval> daList = new ArrayList<>();

        DataApproval daPrototype = new DataApproval( da );

        for ( DataElementCategoryOptionCombo combo : combos )
        {
            daPrototype.setAttributeOptionCombo( combo );

            daList.add( new DataApproval( daPrototype ) );
        }

        return expandApprovalsList( daList, dataSets );
    }

    private Set<DataElementCategoryOption> optionsFromAttributeOptionGroups(  DataApproval dataApproval,
                                                                              Set<CategoryOptionGroup> attributeOptionGroups )
    {
        Set<DataElementCategoryOption> options = new HashSet<>();

        Iterator<CategoryOptionGroup> it = attributeOptionGroups.iterator();

        if ( it.hasNext() )
        {
            for ( DataElementCategoryOption co : it.next().getMembers() )
            {
                if ( co.includes( dataApproval.getPeriod() ) && co.includes( dataApproval.getOrganisationUnit() ) )
                {
                    options.add( co );
                }
            }
        }

        while ( it.hasNext() )
        {
            options.retainAll( it.next().getMembers() );
        }

        return options;
    }

    private List<DataApproval> checkApprovalsList( List<DataApproval> dataApprovalList, boolean isGetStatus )
            throws ApprovalException
    {
        List<DataApproval> daList = new ArrayList<>();

        for ( DataApproval dataApproval : dataApprovalList )
        {
            DataApproval da = checkDataApproval( dataApproval, isGetStatus );

            if ( !userCanReadAny( da.getAttributeOptionCombo().getCategoryOptions() ) )
            {
                throw new UserCannotApproveAttributeComboException();
            }

            daList.add( da );
        }

        return expandApprovalsList( dataApprovalList, null );
    }

    private boolean userCanReadAny ( Set<DataElementCategoryOption> options )
    {
        for ( DataElementCategoryOption option : options )
        {
            if ( securityService.canRead( option ) )
            {
                return true;
            }
        }

        return false;
    }

    private DataApproval checkDataApproval( DataApproval dataApproval, boolean includeDataViewOrgUnits )
            throws ApprovalException
    {
        DataApproval da = new DataApproval ( dataApproval ); // Defensive copy so we can change it.

        if ( !da.getDataSet().isApproveData() )
        {
            throw new DataSetNotMarkedForApprovalException();
        }

        if ( da.getAttributeOptionCombo() == null )
        {
            da.setAttributeOptionCombo( categoryService.getDefaultDataElementCategoryOptionCombo() );
        }

        DataApprovalLevel userLevel = dataApprovalLevelService.getUserApprovalLevel( da.getOrganisationUnit(), includeDataViewOrgUnits );

        if ( userLevel != null )
        {
            if ( userLevel.equals( da.getDataApprovalLevel() ) )
            {
                return da;
            }
            else if ( da.getDataApprovalLevel() == null )
            {
                da.setDataApprovalLevel( userLevel );

                return da;
            }
            else if ( userLevel.getLevel() > da.getDataApprovalLevel().getLevel() )
            {
                User user = currentUserService.getCurrentUser();

                boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

                if ( mayApproveAtLowerLevels )
                {
                    return da;
                }
            }
        }

        throw new UserCannotAccessApprovalLevelException();
    }

    private List<DataApproval> expandApprovalsList ( List<DataApproval> approvalsList, Set<DataSet> dataSets )
            throws PeriodShorterThanDataSetPeriodException
    {
        return expandPeriods( expandDataSets( approvalsList, dataSets ) );
    }

    private List<DataApproval> expandDataSets ( List<DataApproval> approvalsList, Set<DataSet> dataSets )
    {
        List<DataApproval> returnList = approvalsList;

        if ( dataSets != null )
        {
            returnList = new ArrayList<>();

            for ( DataApproval da : approvalsList )
            {
                for ( DataSet set : dataSets )
                {
                    da.setDataSet( set );

                    returnList.add( new DataApproval( da ) );
                }
            }
        }

        return returnList;
    }

    private List<DataApproval> expandPeriods ( List<DataApproval> approvalsList )
            throws PeriodShorterThanDataSetPeriodException
    {
        List<DataApproval> expandedApprovals = new ArrayList<>();

        for ( DataApproval da : approvalsList )
        {
            PeriodType selectedPeriodType = da.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = da.getDataSet().getPeriodType();

            if ( selectedPeriodType.equals( dataSetPeriodType ) )
            {
                expandedApprovals.add( da ); // No expansion needed.
            }
            else if ( selectedPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                throw new PeriodShorterThanDataSetPeriodException();
            }
            else
            {
                Collection<Period> periods = periodService.getPeriodsBetweenDates(
                        da.getDataSet().getPeriodType(),
                        da.getPeriod().getStartDate(),
                        da.getPeriod().getEndDate() );

                for ( Period period : periods )
                {
                    da.setPeriod( period ); // OK because it's a defensive copy.

                    expandedApprovals.add( new DataApproval( da ) );
                }
            }
        }

        return expandedApprovals;
    }

    private void doApprove( List<DataApproval> dataApprovalList )
            throws ApprovalActionNotAllowedException
    {
        for ( Iterator<DataApproval> it = dataApprovalList.iterator(); it.hasNext(); )
        {
            DataApproval da = it.next();

            DataApprovalStatus status = getStatus( da );

            if ( status.getDataApprovalState().isApproved() )
            {
                it.remove();
            }
            else if ( !status.getDataApprovalState().isApprovable() || !mayApprove( da, status ) )
            {
                throw new ApprovalActionNotAllowedException();
            }
        }

        for ( DataApproval da : dataApprovalList )
        {
            dataApprovalStore.addDataApproval( da );
        }
    }

    private void doUnapprove( List<DataApproval> dataApprovalList )
            throws ApprovalActionNotAllowedException
    {
        for ( Iterator<DataApproval> it = dataApprovalList.iterator(); it.hasNext(); )
        {
            DataApproval da = it.next();

            DataApprovalStatus status = getStatus( da );

            if ( !status.getDataApprovalState().isApproved() )
            {
                it.remove();
            }
            else if ( !status.getDataApprovalState().isUnapprovable() || !mayUnapprove( da, status ) )
            {
                throw new ApprovalActionNotAllowedException();
            }
        }

        for ( DataApproval da : dataApprovalList )
        {
            dataApprovalStore.deleteDataApproval( da );
        }
    }

    private void doAcceptOrUnaccept( List<DataApproval> dataApprovalList, boolean accepted )
            throws ApprovalActionNotAllowedException
    {
        for ( Iterator<DataApproval> it = dataApprovalList.iterator(); it.hasNext(); )
        {
            DataApproval da = it.next();

            DataApprovalStatus status = getStatus( da );

            if ( accepted == status.getDataApprovalState().isAccepted() )
            {
                it.remove();
            }
            else if ( !mayAcceptOrUnaccept( da, status )
                    || accepted ?
                    !status.getDataApprovalState().isAcceptable() :
                    !status.getDataApprovalState().isUnacceptable() )
            {
                throw new ApprovalActionNotAllowedException();
            }
        }

        for ( DataApproval da : dataApprovalList )
        {
            da.setAccepted( accepted );

            dataApprovalStore.updateDataApproval( da );
        }
    }

    private boolean mayApprove( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        DataApprovalLevel userDal = dataApprovalLevelService.getUserApprovalLevel( dataApproval.getOrganisationUnit(), false );

        if ( userDal != null )
        {
            User user = currentUserService.getCurrentUser();

            boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
            boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( ( mayApprove && userDal.getLevel() == dataApprovalStatus.getDataApprovalLevel().getLevel() )
                || ( mayApproveAtLowerLevels && userDal.getLevel() > dataApprovalStatus.getDataApprovalLevel().getLevel() ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean mayUnapprove( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        return mayApprove( dataApproval, dataApprovalStatus ) &&
                ( !dataApprovalStatus.getDataApproval().isAccepted() || mayAcceptOrUnaccept( dataApproval, dataApprovalStatus ) );
    }

    private boolean mayAcceptOrUnaccept( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        DataApprovalLevel userDal = dataApprovalLevelService.getUserApprovalLevel( dataApproval.getOrganisationUnit(), false );

        if ( userDal != null )
        {
            User user = currentUserService.getCurrentUser();

            boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS )
                    || user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( mayAcceptAtLowerLevels && userDal.getLevel() > dataApprovalStatus.getDataApprovalLevel().getLevel() )
            {
                return true;
            }
        }

        return false;
    }

    private DataApprovalStatus getStatus( DataApproval dataApproval )
    {
        return doGetDataApprovalStatus( org.hisp.dhis.system.util.CollectionUtils.asList( dataApproval ), dataApproval );
    }

    private DataApprovalStatus doGetDataApprovalStatus( List<DataApproval> dataApprovals, DataApproval originalDataApproval )
    {
        DataApprovalSelection dataApprovalSelection = new DataApprovalSelection( dataApprovals, originalDataApproval,
                dataApprovalStore, dataApprovalLevelService,
                organisationUnitService, categoryService, periodService );

        return dataApprovalSelection.getDataApprovalStatus();
    }

    private DataApprovalLevel checkApprovalLevel( DataApprovalLevel dataApprovalLevel, DataApprovalLevel userApprovalLevel )
    {
        if ( dataApprovalLevel.getLevel() == userApprovalLevel.getLevel() )
        {
            return dataApprovalLevel;
        }

        User user = currentUserService.getCurrentUser();

        boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        if ( mayApproveAtLowerLevels && dataApprovalLevel.getLevel() < userApprovalLevel.getLevel() )
        {
            return dataApprovalLevel;
        }

        return null;
    }

    /**
     * Return true if there are no category option groups, or if there is
     * one and the user can read it.
     *
     * @param categoryOptionGroups option groups (if any) for data selection
     * @return true if at most 1 option group and user can read, else false
     */
    boolean canReadOneCategoryOptionGroup( Collection<CategoryOptionGroup> categoryOptionGroups )
    {
        if ( categoryOptionGroups == null || categoryOptionGroups.size() == 0 )
        {
            return true;
        }

        if ( categoryOptionGroups.size() != 1 )
        {
            return false;
        }

        return (securityService.canRead( (CategoryOptionGroup) categoryOptionGroups.toArray()[0] ));
    }

    /**
     * Return true if there are no category option groups, or if the user
     * can read any category option group from the collection.
     *
     * @param categoryOptionGroups option groups (if any) for data selection
     * @return true if at most 1 option group and user can read, else false
     */
    boolean canReadSomeCategoryOptionGroup( Collection<CategoryOptionGroup> categoryOptionGroups )
    {
        if ( categoryOptionGroups == null )
        {
            return true;
        }

        for ( CategoryOptionGroup cog : categoryOptionGroups )
        {
            if ( securityService.canRead( cog ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see whether a user may approve data for a given
     * organisation unit.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve, otherwise false
     */
    private boolean mayApprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();

        if ( user != null )
        {
            boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
            boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( mayApprove && user.getOrganisationUnits().contains( organisationUnit ) )
            {
                if ( !mayApproveAtLowerLevels && mayApproveAtNextHigherLevelOnly( dataApprovalLevel, organisationUnit ) )
                {
                    log.debug( "mayApprove = false because user may approve at the next higher level from approval level "
                        + dataApprovalLevel.getLevel() + " with " + organisationUnit.getName() );

                    return false;
                }

                log.debug( "mayApprove = true because organisation unit " + organisationUnit.getName()
                    + " is assigned to user and user may approve at same level." );

                return true;
            }

            if ( mayApproveAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(),
                organisationUnit.getAncestors() ) )
            {
                log.debug( "mayApprove = true because organisation unit " + organisationUnit.getName()
                    + " is under user and user may approve at lower levels." );

                return true;
            }
        }

        log.debug( "mayApprove = false for organisation unit " + organisationUnit.getName() );

        return false;
    }

    /**
     * Checks to see whether a user may approve data at the next higher
     * approval level for this orgnaisation unit -- because they can
     * approve only at that next higher level (and not at lower levels.)
     * <p/>
     * It is assumed that the user has the authority to approve at their
     * level -- and not the authority to approve at lower levels.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve at the next higher level.
     */
    private boolean mayApproveAtNextHigherLevelOnly( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        if ( dataApprovalLevel.getLevel() > 1 )
        {
            DataApprovalLevel nextLevel = dataApprovalLevelService.getDataApprovalLevelByLevelNumber( dataApprovalLevel.getLevel() - 1 );

            if ( securityService.canRead( nextLevel )
                && (nextLevel.getCategoryOptionGroupSet() == null ||
                (securityService.canRead( nextLevel.getCategoryOptionGroupSet() )
                    && canReadSomeCategoryOptionGroup( nextLevel.getCategoryOptionGroupSet().getMembers() ))) )
            {
                OrganisationUnit acceptOrgUnit = organisationUnit;
                for ( int i = nextLevel.getOrgUnitLevel(); i < dataApprovalLevel.getOrgUnitLevel(); i++ )
                {
                    acceptOrgUnit = acceptOrgUnit.getParent();
                }

                if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( acceptOrgUnit ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may unapprove a data approval.
     * <p/>
     * A user may unapprove data for organisation unit A if they have the
     * authority to approve data for organisation unit B, and B is an
     * ancestor of A.
     * <p/>
     * A user may also unapprove data for organisation unit A if they have
     * the authority to approve data for organisation unit A, and A has no
     * ancestors.
     * <p/>
     * But a user may not unapprove data for an organisation unit if the data
     * has been approved already at a higher level for the same period and
     * data set, and the user is not authorized to remove that approval as well.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @param isAccepted whether the data is also accepted
     * @return true if the user may unapprove, otherwise false
     */
    private boolean mayUnapprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit, boolean isAccepted )
    {
        if ( isAuthorizedToUnapprove( dataApprovalLevel, organisationUnit ) )
        {
            if ( !isAccepted || mayAcceptOrUnaccept( dataApprovalLevel, organisationUnit ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may accept or unaccept an approval.
     * (For this, they will need access to the next higher approval level.)
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may accept or unaccept, otherwise false.
     */
    private boolean mayAcceptOrUnaccept( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();

        if ( dataApprovalLevel != null && user != null )
        {
            boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

            if ( mayAcceptAtLowerLevels && mayAccessNextHigherLevel( dataApprovalLevel, organisationUnit ) )
            {
                log.debug( "User may accept or unaccept for organisation unit " + organisationUnit.getName()
                    + " and approval level " + dataApprovalLevel.getLevel() );

                return true;
            }
        }

        log.debug( "User with AUTH_ACCEPT_LOWER_LEVELS " + user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS )
            + " with " + user.getOrganisationUnits().size() + " org units"
            + " may not accept or unaccept for organisation unit "
            + (organisationUnit == null ? "(null)" : organisationUnit.getName()) );

        return false;
    }

    /**
     * Checks to see whether a user may access the next higher approval
     * level to see if they can accept at this approval level.
     * <p/>
     * It is assumed that the user has the authority to accept at lower levels.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve at the next higher level.
     */
    private boolean mayAccessNextHigherLevel( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        if ( dataApprovalLevel.getLevel() > 1 )
        {
            DataApprovalLevel nextLevel = dataApprovalLevelService.getDataApprovalLevelByLevelNumber( dataApprovalLevel.getLevel() - 1 );

            if ( securityService.canRead( nextLevel )
                && (nextLevel.getCategoryOptionGroupSet() == null ||
                (securityService.canRead( nextLevel.getCategoryOptionGroupSet() )
                    && canReadSomeCategoryOptionGroup( nextLevel.getCategoryOptionGroupSet().getMembers() ))) )
            {
                OrganisationUnit acceptOrgUnit = organisationUnit;
                for ( int i = nextLevel.getOrgUnitLevel(); i < dataApprovalLevel.getOrgUnitLevel(); i++ )
                {
                    acceptOrgUnit = acceptOrgUnit.getParent();
                }

                User user = currentUserService.getCurrentUser();

                if ( user.getOrganisationUnits().contains( acceptOrgUnit ) ||
                    CollectionUtils.containsAny( user.getOrganisationUnits(), acceptOrgUnit.getAncestors() ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Tests whether the user is authorized to unapprove for this organisation
     * unit.
     * <p/>
     * Whether the user actually may unapprove an existing approval depends
     * also on whether there are higher-level approvals that the user is
     * authorized to unapprove.
     *
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return true if the user may approve, otherwise false
     */
    private boolean isAuthorizedToUnapprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        log.debug( "isAuthorizedToUnapprove( " + organisationUnit.getName() + ")" );

        if ( mayApprove( dataApprovalLevel, organisationUnit ) )
        {
            log.debug( "User may unapprove at " + organisationUnit.getName() );

            return true;
        }

        for ( OrganisationUnit ancestor : organisationUnit.getAncestors() )
        {
            if ( mayApprove( dataApprovalLevel, ancestor ) )
            {
                log.debug( "User may unapprove at " + ancestor.getName() );

                return true;
            }
        }

        log.debug( "User may not unapprove at " + organisationUnit.getName() );

        return false;
    }
}
