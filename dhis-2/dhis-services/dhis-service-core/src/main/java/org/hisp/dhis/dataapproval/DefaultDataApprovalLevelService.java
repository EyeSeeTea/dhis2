package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
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
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalLevelService
        implements DataApprovalLevelService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalLevelStore dataApprovalLevelStore;

    public void setDataApprovalLevelStore( DataApprovalLevelStore dataApprovalLevelStore )
    {
        this.dataApprovalLevelStore = dataApprovalLevelStore;
    }

    // -------------------------------------------------------------------------
    // DataApprovalLevel
    // -------------------------------------------------------------------------

    public List<DataApprovalLevel> getAllDataApprovalLevels()
    {
        return dataApprovalLevelStore.getAllDataApprovalLevels();
    }

    public boolean canMoveDown( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index < 0 || index + 1 >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel next = dataApprovalLevels.get( index + 1 );

        if ( test.getOrganisationUnitLevel().getLevel() == next.getOrganisationUnitLevel().getLevel()
                && test.getCategoryOptionGroupSet() != null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean canMoveUp( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index <= 0 || index >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel previous = dataApprovalLevels.get( index - 1 );

        if ( test.getOrganisationUnitLevel().getLevel() == previous.getOrganisationUnitLevel().getLevel()
                && previous.getCategoryOptionGroupSet() != null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void moveDown( int level )
    {
        if ( canMoveDown( level ) )
        {
            List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

            int index = level - 1;

            DataApprovalLevel next = dataApprovalLevels.get( index + 1 );
            dataApprovalLevels.set( index + 1, dataApprovalLevels.get( index ) );
            dataApprovalLevels.set( index, next );

            save( dataApprovalLevels );
        }
    }

    public void moveUp( int level )
    {
        if ( canMoveUp( level ) )
        {
            List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

            int index = level - 1;

            DataApprovalLevel previous = dataApprovalLevels.get( index - 1 );
            dataApprovalLevels.set( index - 1, dataApprovalLevels.get( index ) );
            dataApprovalLevels.set( index, previous );

            save( dataApprovalLevels );
        }
    }

    public boolean exists ( DataApprovalLevel testLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        for ( DataApprovalLevel dataApprovalLevel : dataApprovalLevels )
        {
            if ( testLevel.getOrganisationUnitLevel() == dataApprovalLevel.getOrganisationUnitLevel()
                    && testLevel.getCategoryOptionGroupSet() == dataApprovalLevel.getCategoryOptionGroupSet() )
            {
                return true;
            }
        }

        return false;
    }

    public boolean add( DataApprovalLevel newLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        if ( newLevel.getOrganisationUnitLevel() == null )
        {
            return false;
        }

        int i = dataApprovalLevels.size() - 1;

        while ( i >= 0 )
        {
            DataApprovalLevel test = dataApprovalLevels.get( i );

            int orgLevelDifference = newLevel.getOrganisationUnitLevel().getLevel() - test.getOrganisationUnitLevel().getLevel();

            if ( orgLevelDifference > 0 )
            {
                break;
            }

            if ( orgLevelDifference == 0 )
            {
                if ( newLevel.getCategoryOptionGroupSet() == test.getCategoryOptionGroupSet() )
                {
                    return false; // Attempt to insert a duplicate approval level.
                }

                if ( test.getCategoryOptionGroupSet() == null )
                {
                    break;
                }

            }

            i--;
        }

        dataApprovalLevels.add( i  + 1, newLevel );

        return true;
    }

    public void remove( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index >= 0 & index < dataApprovalLevels.size() )
        {
            dataApprovalLevels.remove( index );

            save( dataApprovalLevels );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Tag each data approval level with the current timestamp,
     * re-number the levels, and save all of them. Also copy them
     * to another List, so Hibernate doesn't get upset about changing
     * the level numbers.
     *
     * @param dataApprovalLevels The list of data approval levels to save.
     */
    private void save( List<DataApprovalLevel> dataApprovalLevels )
    {
        List<DataApprovalLevel> copy = new ArrayList<DataApprovalLevel>();

        Date now = new Date();

        int level = 1;

        for ( DataApprovalLevel old : dataApprovalLevels )
        {
            DataApprovalLevel d = new DataApprovalLevel( level++,
                    old.getOrganisationUnitLevel(),
                    old.getCategoryOptionGroupSet(), now );

            copy.add( d );
        }

        dataApprovalLevelStore.updateAllDataApprovalLevels( copy );
    }
}
