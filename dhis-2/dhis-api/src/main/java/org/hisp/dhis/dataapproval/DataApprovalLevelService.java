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

import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import java.util.List;

/**
 * @author Jim Grace
 * @version $Id$
 */
public interface DataApprovalLevelService
{
    String ID = DataApprovalLevelService.class.getName();

    /**
     * Gets a list of all data approval levels.
     *
     * @return List of all data approval levels, ordered from 1 to n.
     */
    List<DataApprovalLevel> getAllDataApprovalLevels();

    /**
     * Tells whether a level can move down in the list (can switch places with
     * the level below.)
     *
     * @param level the level to test.
     * @return true if the level can move down, otherwise false.
     */
    boolean canMoveDown( int level );

    /**
     * Tells whether a level can move up in the list (can switch places with
     * the level above.)
     *
     * @param level the level to test.
     * @return true if the level can move up, otherwise false.
     */
    boolean canMoveUp( int level );

    /**
     * Moves a data approval level down in the list (switches places with the
     * level below).
     *
     * @param level the level to move down.
     */
    void moveDown( int level );

    /**
     * Moves a data approval level up in the list (switches places with the
     * level above).
     *
     * @param level the level to move up.
     */
    void moveUp( int level );

    /**
     * Determines whether level already exists with the same organisation
     * unit level and category option group set (but not necessarily the
     * same level number.)
     *
     * @param testLevel Data approval level to test for existence.
     * @return true if it exists, otherwise false.
     */
    public boolean exists ( DataApprovalLevel testLevel );

    /**
     * Adds a new data approval level. Adds the new level at the highest
     * position possible (to facilitate the use case where users add the
     * approval levels from low to high.)
     *
     * @param newLevel the new level to add.
     * @return true if level was added, false if not well formed or duplicate.
     */
    boolean add( DataApprovalLevel newLevel );

    /**
     * Removes a data approval level.
     *
     * @param index index of the level to move up.
     */
    void remove( int index );
}
