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

package org.hisp.dhis.patientdatavalue;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientDataValueService
    implements PatientDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientDataValueStore patientDataValueStore;

    public void setPatientDataValueStore( PatientDataValueStore patientDataValueStore )
    {
        this.patientDataValueStore = patientDataValueStore;
    }

    // -------------------------------------------------------------------------
    // PatientDataValue
    // -------------------------------------------------------------------------

    public void savePatientDataValue( PatientDataValue patientDataValue )
    {
        if ( patientDataValue.getValue() != null )
        {
            patientDataValueStore.saveVoid( patientDataValue );
        }
    }

    public void deletePatientDataValue( PatientDataValue patientDataValue )
    {
        patientDataValueStore.delete( patientDataValue );
    }

    public int deletePatientDataValue( ProgramInstance programInstance )
    {
        return patientDataValueStore.delete( programInstance );
    }

    public int deletePatientDataValue( ProgramStage programStage )
    {
        return patientDataValueStore.delete( programStage );
    }

    public int deletePatientDataValue( DataElement dataElement )
    {
        return patientDataValueStore.delete( dataElement );
    }

    public int deletePatientDataValue( DataElementCategoryOptionCombo optionCombo )
    {
        return patientDataValueStore.delete( optionCombo );
    }

    public void updatePatientDataValue( PatientDataValue patientDataValue )
    {
        if ( patientDataValue.getValue() == null )
        {
            patientDataValueStore.delete( patientDataValue );
        }
        else
        {
            patientDataValueStore.update( patientDataValue );
        }
    }

    public Collection<PatientDataValue> getAllPatientDataValues()
    {
        return patientDataValueStore.getAll();
    }

    public PatientDataValue getPatientDataValue( ProgramInstance programInstance, ProgramStage programStage,
        DataElement dataElement, DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit )
    {
        return patientDataValueStore.get( programInstance, programStage, dataElement, optionCombo, organisationUnit );
    }

    public Collection<PatientDataValue> getPatientDataValues( ProgramInstance programInstance )
    {
        return patientDataValueStore.get( programInstance );
    }

    public Collection<PatientDataValue> getPatientDataValues( ProgramInstance programInstance, ProgramStage programStage )
    {
        return patientDataValueStore.get( programInstance, programStage );
    }

    public Collection<PatientDataValue> getPatientDataValues( ProgramInstance programInstance,
        ProgramStage programStage, DataElement dataElement )
    {
        return patientDataValueStore.get( programInstance, programStage, dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( DataElement dataElement )
    {
        return patientDataValueStore.get( dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( DataElementCategoryOptionCombo optionCombo )
    {
        return patientDataValueStore.get( optionCombo );
    }    

    public Collection<PatientDataValue> getPatientDataValues( ProgramInstance programInstance,
        ProgramStage programStage, DataElement dataElement, OrganisationUnit organisationUnit )
    {
        return patientDataValueStore.get( programInstance, programStage, dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( OrganisationUnit organisationUnit,
        ProgramInstance programInstance )
    {
        return patientDataValueStore.get( organisationUnit, programInstance );
    }

    public Collection<PatientDataValue> getPatientDataValues( OrganisationUnit organisationUnit,
        ProgramInstance programInstance, ProgramStage programStage )
    {
        return patientDataValueStore.get( organisationUnit, programInstance, programStage );
    }

    public Collection<PatientDataValue> getPatientDataValues( OrganisationUnit organisationUnit,
        ProgramInstance programInstance, ProgramStage programStage, DataElement dataElement )
    {
        return patientDataValueStore.get( organisationUnit, programInstance, programStage, dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return patientDataValueStore.get( organisationUnit, dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( OrganisationUnit organisationUnit,
        DataElementCategoryOptionCombo optionCombo )
    {
        return patientDataValueStore.get( organisationUnit, optionCombo );
    }

}
