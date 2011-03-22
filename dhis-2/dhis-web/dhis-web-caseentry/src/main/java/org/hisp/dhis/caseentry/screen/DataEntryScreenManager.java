package org.hisp.dhis.caseentry.screen;

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
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface DataEntryScreenManager 
{
    String getScreenType(ProgramStage programStage);

    boolean hasMixOfDimensions(ProgramStage programStage);

    boolean hasMultiDimensionalDataElement(ProgramStage programStage);

    Collection<Integer> getAllCalculatedDataElements(ProgramStage programStage);

    Map<CalculatedDataElement, Map<DataElement, Double>> getNonSavedCalculatedDataElements(ProgramStage programStage);

    Map<CalculatedDataElement, Integer> populateValuesForCalculatedDataElements(OrganisationUnit organisationUnit,
        ProgramStage programStage, ProgramStageInstance programStageInstance);

    String populateCustomDataEntryScreenForMultiDimensional(String dataEntryFormCode, Collection<PatientDataValue> dataValues,
        Map<CalculatedDataElement, Integer> calculatedValueMap, Map<Integer, MinMaxDataElement> minMaxMap, String disabled, 
        I18n i18n,  ProgramStage programStage, ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit);
    
    
    Collection<ProgramStageDataElement> getProgramStageDataElements( String htmlCode );
}
