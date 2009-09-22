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

import java.util.Collection;
import java.util.Date;

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

    // -------------------------------------------------------------------------
    // PatientDataValue
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientService#addPatient(org.hisp.dhis.chis
     * .patient.Patient)
     */
    public int addPatient( Patient patient )
    {
        // TODO Auto-generated method stub

        return patientStore.addPatient( patient );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientService#deletePatient(org.hisp.dhis
     * .chis.patient.Patient)
     */
    public void deletePatient( Patient patient )
    {
        // TODO Auto-generated method stub

        patientStore.deletePatient( patient );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.chis.patient.PatientService#getAllPatients()
     */
    public Collection<Patient> getAllPatients()
    {
        // TODO Auto-generated method stub

        return patientStore.getAllPatients();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.chis.patient.PatientService#getPatiensByGender(int)
     */
    public Collection<Patient> getPatiensByGender( String gender )
    {
        // TODO Auto-generated method stub

        return patientStore.getPatiensByGender( gender );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.chis.patient.PatientService#getPatient(int)
     */
    public Patient getPatient( int id )
    {
        // TODO Auto-generated method stub

        return patientStore.getPatient( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientService#getPatientsByBirthDate(java
     * .util.Date)
     */
    public Collection<Patient> getPatientsByBirthDate( Date birthDate )
    {
        // TODO Auto-generated method stub

        return patientStore.getPatientsByBirthDate( birthDate );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientService#getPatientsByNames(java.lang
     * .String)
     */
    public Collection<Patient> getPatientsByNames( String name )
    {
        // TODO Auto-generated method stub

        return patientStore.getPatientsByNames( name );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientService#updatePatient(org.hisp.dhis
     * .chis.patient.Patient)
     */
    public void updatePatient( Patient patient )
    {
        // TODO Auto-generated method stub

        patientStore.updatePatient( patient );
    }

    public Collection<Patient> getAllPatients( Boolean isDead )
    {
        // TODO Auto-generated method stub

        return patientStore.getAllPatients( isDead );
    }

}
