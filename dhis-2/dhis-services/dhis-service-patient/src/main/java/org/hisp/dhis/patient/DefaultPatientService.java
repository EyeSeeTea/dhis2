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

package org.hisp.dhis.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.household.HouseHold;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientService
    implements PatientService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientStore patientStore;

    public void setPatientStore( PatientStore patientStore )
    {
        this.patientStore = patientStore;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    // -------------------------------------------------------------------------
    // PatientDataValue
    // -------------------------------------------------------------------------

    public int savePatient( Patient patient )
    {
        return patientStore.save( patient );
    }

    public void deletePatient( Patient patient )
    {
        patientStore.delete( patient );
    }

    public Collection<Patient> getAllPatients()
    {
        return patientStore.getAll();
    }

    public Collection<Patient> getPatiensByGender( String gender )
    {
        return patientStore.getByGender( gender );
    }

    public Patient getPatient( int id )
    {
        return patientStore.get( id );
    }

    public Collection<Patient> getPatientsByBirthDate( Date birthDate )
    {
        return patientStore.getByBirthDate( birthDate );
    }

    public Collection<Patient> getPatientsByNames( String name )
    {
        return patientStore.getByNames( name );
    }

    public void updatePatient( Patient patient )
    {
        patientStore.update( patient );
    }

    public Collection<Patient> getAllPatients( Boolean isDead )
    {
        return patientStore.get( isDead );
    }

    public Collection<Patient> getPatients( String searchText )
    {
        Collection<Patient> patients = new ArrayList<Patient>();

        patients.addAll( getPatientsByNames( searchText ) );

        for ( PatientIdentifier patientIdentifier : patientIdentifierService
            .getPatientIdentifiersByIdentifier( searchText ) )
        {
            patients.add( patientIdentifier.getPatient() );
        }

        return patients;
    }

    public Collection<Patient> getPatientsByHouseHold( HouseHold houseHold )
    {
        return patientStore.getByHouseHold( houseHold );
    }

    public Collection<Patient> getPatients( OrganisationUnit organisationUnit, String searchText )
    {
        Collection<Patient> patients = new ArrayList<Patient>();
               
        Collection<Patient> allPatients = getPatients( searchText );
        
        if( allPatients.retainAll( getPatientsByOrgUnit( organisationUnit ) ) )
        {
            patients = allPatients;
        }
        
        return patients;
    }

    public Collection<Patient> getPatientsByOrgUnit( OrganisationUnit organisationUnit )
    {
        Collection<Patient> patients = new ArrayList<Patient>();

        for ( PatientIdentifier patientIdentifier : patientIdentifierService
            .getPatientIdentifiersByOrgUnit( organisationUnit ) )
        {
            patients.add( patientIdentifier.getPatient() );
        }        
        
        return patients;
    }
}
