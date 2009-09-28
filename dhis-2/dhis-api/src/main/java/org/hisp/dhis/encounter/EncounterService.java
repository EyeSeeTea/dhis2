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

package org.hisp.dhis.encounter;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface EncounterService
{
    String ID = EncounterService.class.getName();

    int addEncounter( Encounter encounter );

    void updateEncounter( Encounter encounter );

    void deleteEncounter( Encounter encounter );

    Encounter getEncounter( int id );

    Encounter getEncounter( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit, DataSet dataSet );

    Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit );

    Collection<Encounter> getEncounters( Patient patient, DataSet dataSet );

    Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient );

    Collection<Encounter> getEncounters( Date encounterDateTime, DataSet dataSet );

    Collection<Encounter> getEncounters( OrganisationUnit organisationUnit, Patient patient );

    Collection<Encounter> getEncounters( DataSet dataSet, Patient patient );

    Collection<Encounter> getEncounters( DataSet dataSet, OrganisationUnit organisationUnit );

    Collection<Encounter> getEncounters( OrganisationUnit organisationUnit );

    Collection<Encounter> getEncounters( Date encounterDateTime );

    Collection<Encounter> getEncounters( Patient patient );

    Collection<Encounter> getEncounters( DataSet dataSet );

    Collection<Encounter> getEncounters( Boolean isExecuted );

    Collection<Encounter> getAllEncounters();

}
