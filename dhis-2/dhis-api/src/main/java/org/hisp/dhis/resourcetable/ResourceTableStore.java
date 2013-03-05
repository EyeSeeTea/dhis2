package org.hisp.dhis.resourcetable;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

/**
 * @author Lars Helge Overland
 */
public interface ResourceTableStore
{
    String ID = ResourceTableStore.class.getName();

    final String TABLE_NAME_CATEGORY_OPTION_COMBO_NAME = "_categoryoptioncomboname";
    final String TABLE_NAME_ORGANISATION_UNIT_STRUCTURE = "_orgunitstructure";
    final String TABLE_NAME_DATA_ELEMENT_STRUCTURE = "_dataelementstructure";
    final String TABLE_NAME_PERIOD_STRUCTURE = "_periodstructure";
    final String TABLE_NAME_PERIOD_NO_DISAGGREGATION_STRUCTURE = "_period_no_disagg_structure";
    
    /**
     * Performs a batch update.
     * 
     * @param columns the number of columns in the table to update.
     * @param tableName the name of the table to update.
     * @param batchArgs the arguments to use for the update statement.
     */
    void batchUpdate( int columns, String tableName, List<Object[]> batchArgs );
    
    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------
    
    /**
     * Creates a table.
     */
    void createOrganisationUnitStructure( int maxLevel );
    
    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------
    
    /**
     * Creates a table.
     */
    void createDataElementCategoryOptionComboName();
    
    // -------------------------------------------------------------------------
    // GroupSetStructure
    // -------------------------------------------------------------------------

    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createDataElementGroupSetStructure( List<DataElementGroupSet> groupSets );

    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createIndicatorGroupSetStructure( List<IndicatorGroupSet> groupSets );
    
    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createOrganisationUnitGroupSetStructure( List<OrganisationUnitGroupSet> groupSets );
    
    /**
     * Creates table.
     * 
     * @param categories the categories.
     */
    void createCategoryStructure( List<DataElementCategory> categories );
    
    /**
     * Creates table.
     */
    void createDataElementStructure();
    
    /**
     * Creates table.
     */
    void createPeriodStructure();
}
