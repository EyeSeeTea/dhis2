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
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientIdentifierService
    implements PatientIdentifierService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientIdentifierStore patientIdentifierStore;

    public void setPatientIdentifierStore( PatientIdentifierStore patientIdentifierStore )
    {
        this.patientIdentifierStore = patientIdentifierStore;
    }

    // -------------------------------------------------------------------------
    // PatientIdentifier
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientIdentifierService#addPatientIdentifier
     * (org.hisp.dhis.chis.patient.PatientIdentifier)
     */
    public int addPatientIdentifier( PatientIdentifier patientIdentifier )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.addPatientIdentifier( patientIdentifier );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientIdentifierService#deletePatientIdentifier
     * (org.hisp.dhis.chis.patient.PatientIdentifier)
     */
    public void deletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        // TODO Auto-generated method stub

        patientIdentifierStore.deletePatientIdentifier( patientIdentifier );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientIdentifierService#getAllPatientIdentifiers
     * ()
     */
    public Collection<PatientIdentifier> getAllPatientIdentifiers()
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getAllPatientIdentifiers();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.hisp.dhis.chis.patient.PatientIdentifierService#
     * getPatienIdentifiersByIdentifier(java.lang.String)
     */
    public Collection<PatientIdentifier> getPatienIdentifiersByIdentifier( String identifier )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getPatienIdentifiersByIdentifier( identifier );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientIdentifierService#getPatientIdentifier
     * (java.lang.String, org.hisp.dhis.organisationunit.OrganisationUnit)
     */
    public PatientIdentifier getPatientIdentifier( String identifier, OrganisationUnit organisationUnit )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getPatientIdentifier( identifier, organisationUnit );
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.hisp.dhis.chis.patient.PatientIdentifierService#
     * getPatientIdentifiersByOrgUnit
     * (org.hisp.dhis.organisationunit.OrganisationUnit)
     */
    public Collection<PatientIdentifier> getPatientIdentifiersByOrgUnit( OrganisationUnit organisationUnit )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getPatientIdentifiersByOrgUnit( organisationUnit );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.patient.PatientIdentifierService#updatePatientIdentifier
     * (org.hisp.dhis.chis.patient.PatientIdentifier)
     */
    public void updatePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        // TODO Auto-generated method stub

        patientIdentifierStore.updatePatientIdentifier( patientIdentifier );
    }

    public PatientIdentifier getPatientIdentifier( Patient patient )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getPatientIdentifier( patient );
    }

    public PatientIdentifier getPatientIdentifier( int id )
    {
        // TODO Auto-generated method stub

        return patientIdentifierStore.getPatientIdentifier( id );
    }

    public String getNextIdentifierForOrgUnit( OrganisationUnit orgUnit )
    {
        // TODO Auto-generated method stub

        Collection<PatientIdentifier> patientIdentifiers = patientIdentifierStore
            .getPatientIdentifiersByOrgUnit( orgUnit );

        List<String> sortedIdentifiers = new ArrayList<String>();

        String lastAssignedIdentifier = null;

        String nextIdentifier = null;

        for ( PatientIdentifier patientIdentifier : patientIdentifiers )
        {
            sortedIdentifiers.add( patientIdentifier.getIdentifier() );
        }

        Collections.sort( sortedIdentifiers, Collections.reverseOrder() );

        if ( sortedIdentifiers.size() > 0 )
        {
            lastAssignedIdentifier = sortedIdentifiers.get( 0 );

            lastAssignedIdentifier = lastAssignedIdentifier.substring( orgUnit.getShortName().length() + 1 );
        }

        if ( lastAssignedIdentifier == null )
        {
            // Not patient is registered yet and take the first index

            nextIdentifier = orgUnit.getShortName() + PatientIdentifier.FIRST_INDEX;

            return nextIdentifier;
        }

        Integer nextIndex = Integer.parseInt( lastAssignedIdentifier ) + 1;

        String nextIdentifierIndex = nextIndex.toString();

        if ( nextIdentifierIndex.length() < PatientIdentifier.IDENTIFIER_INDEX_LENGTH )
        {
            String prefix = "0";

            for ( int i = 1; i < PatientIdentifier.IDENTIFIER_INDEX_LENGTH - nextIdentifierIndex.length(); i++ )
            {
                prefix += "0";
            }

            nextIdentifier = orgUnit.getShortName() + "." + prefix + nextIdentifierIndex;
        }

        return nextIdentifier;

    }

}
