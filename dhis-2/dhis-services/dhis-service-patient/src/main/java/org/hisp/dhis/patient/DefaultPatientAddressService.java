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

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientAddressService
    implements PatientAddressService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAddressStore patientAddressStore;

    public void setPatientAddressStore( PatientAddressStore patientAddressStore )
    {
        this.patientAddressStore = patientAddressStore;
    }

    // -------------------------------------------------------------------------
    // PatientAddress
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientAddressService#addPatientAddress(org
     * .hisp.dhis.chis.patient.PatientAddress)
     */
    public int addPatientAddress( PatientAddress patientAddress )
    {
        // TODO Auto-generated method stub

        return patientAddressStore.addPatientAddress( patientAddress );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientAddressService#deletePatientAddress
     * (org.hisp.dhis.chis.patient.PatientAddress)
     */
    public void deletePatientAddress( PatientAddress patientAddress )
    {
        // TODO Auto-generated method stub

        patientAddressStore.deletePatientAddress( patientAddress );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientAddressService#getPatientAddress(int)
     */
    public PatientAddress getPatientAddress( int id )
    {
        // TODO Auto-generated method stub

        return patientAddressStore.getPatientAddress( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientAddressService#getPatientAddress(org
     * .hisp.dhis.chis.patient.Patient)
     */
    public PatientAddress getPatientAddress( Patient patient )
    {
        // TODO Auto-generated method stub

        return patientAddressStore.getPatientAddress( patient );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientAddressService#updatePatientAddress
     * (org.hisp.dhis.chis.patient.PatientAddress)
     */
    public void updatePatientAddress( PatientAddress patientAddress )
    {
        // TODO Auto-generated method stub

        patientAddressStore.updatePatientAddress( patientAddress );
    }

}
