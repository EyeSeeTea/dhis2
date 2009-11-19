/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patientdatavalue.aggregation;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */   
@SuppressWarnings( "unused" ) //TODO
public class PatientDataValueAggregationEngineTest
    extends DhisSpringTest
{
    private PatientDataValueStore patientDataValueStore;
    
    private PatientService patientService;
    
    private PatientDataValueAggregationEngine aggregationEngine;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private Patient patientA;
    private Patient patientB;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;
    
    private PatientDataValue valueA;
    private PatientDataValue valueB;
    private PatientDataValue valueC;    
    private PatientDataValue valueD;  
    
    @Override
    public void setUpTest()
    {
        patientDataValueStore = (PatientDataValueStore) getBean( PatientDataValueStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        patientService = (PatientService) getBean( PatientService.ID );
        
        aggregationEngine = (PatientDataValueAggregationEngine) getBean( PatientDataValueAggregationEngine.ID );
        
        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        
        patientA = createPatient( 'A' );
        patientB = createPatient( 'B' );
        
        patientService.savePatient( patientA );
        patientService.savePatient( patientB );
        
        categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
    }

    protected static Patient createPatient( char uniqueChar )
    {
        Patient patient = new Patient();
        
        patient.setFirstName( "FirstName" + uniqueChar );
        patient.setMiddleName( "MiddleName" + uniqueChar );
        patient.setLastName( "LastName" + uniqueChar );
        patient.setGender( Patient.MALE );
        patient.setBirthDate( getDate( 1970, 1, 1 ) );
        
        return patient;
    }
    
    @Test
    public void aggregate()
    {
         
    }
}
