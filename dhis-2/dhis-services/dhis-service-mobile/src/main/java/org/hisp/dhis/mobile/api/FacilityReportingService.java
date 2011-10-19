package org.hisp.dhis.mobile.api;

/*
 * Copyright (c) 2010, University of Oslo
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
import java.util.Locale;

import org.hisp.dhis.mobile.api.model.DataSet;
import org.hisp.dhis.mobile.api.model.DataSetList;
import org.hisp.dhis.mobile.api.model.DataSetValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * Provides services for doing facility reporting
 */
public interface FacilityReportingService
{

    public List<DataSet> getMobileDataSetsForUnit( OrganisationUnit unit, String localeString );

    public DataSet getDataSet( int id );

    public DataSet getDataSetForLocale( int dataSetId, Locale locale );

    /** Save {@link DataSetValue} to given {@link OrganisationUnit}
     * @param unit - the Organisation unit to save to
     * @param dataSetValue - the data set value to save
     * @throws NotAllowedException if saving is not allowed
     */
    public void saveDataSetValues( OrganisationUnit unit, DataSetValue dataSetValue )
        throws NotAllowedException;
    
    public DataSetList getUpdatedDataSet(DataSetList dataSetList, OrganisationUnit unit, String locale);
    
    public DataSetList getDataSetsForLocale( OrganisationUnit unit, String locale );


}
