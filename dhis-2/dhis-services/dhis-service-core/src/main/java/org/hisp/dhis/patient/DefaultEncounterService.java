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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.encounter.Encounter;
import org.hisp.dhis.encounter.EncounterService;
import org.hisp.dhis.encounter.EncounterStore;


/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DefaultEncounterService 
	implements EncounterService 
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EncounterStore encounterStore;

    public void setEncounterStore( EncounterStore encounterStore )
    {
        this.encounterStore = encounterStore;
    }

    // -------------------------------------------------------------------------
    // Encounter
    // -------------------------------------------------------------------------

	public int addEncounter( Encounter encounter ) 
	{
		return encounterStore.addEncounter( encounter );
	}

	public void deleteEncounter( Encounter encounter ) 
	{
		encounterStore.deleteEncounter( encounter );
	}	

	public Encounter getEncounter( int id )
	{
		return encounterStore.getEncounter( id );
	}
	
	public Encounter getEncounter( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit, DataSet dataSet ) 
	{
		return encounterStore.getEncounter( encounterDateTime, patient, organisationUnit, dataSet );
	}

	public Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit ) 
	{
		return encounterStore.getEncounters( encounterDateTime, patient, organisationUnit );
	}

	public Collection<Encounter> getEncounters( Patient patient, DataSet dataSet ) 
	{
		return encounterStore.getEncounters( patient, dataSet );
	}

	public Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient ) 
	{
		return encounterStore.getEncounters( encounterDateTime, patient );
	}
	
	public Collection<Encounter> getEncounters( Date encounterDateTime, DataSet dataSet ) 
	{
		return encounterStore.getEncounters( encounterDateTime, dataSet );
	}

	public Collection<Encounter> getEncounters( OrganisationUnit organisationUnit, Patient patient ) 
	{
		return encounterStore.getEncounters( organisationUnit, patient );
	}

	public Collection<Encounter> getEncounters( DataSet dataSet, Patient patient ) 
	{
		return encounterStore.getEncounters( dataSet, patient );
	}
	
	public Collection<Encounter> getEncounters( OrganisationUnit organisationUnit ) 
	{
		return encounterStore.getEncounters( organisationUnit );
	}

	public Collection<Encounter> getEncounters( Date encounterDateTime ) 
	{
		return encounterStore.getEncounters( encounterDateTime );
	}
	
	public Collection<Encounter> getEncounters( Patient patient ) 
	{
		return encounterStore.getEncounters( patient );
	}

	public Collection<Encounter> getEncounters( DataSet dataSet ) 
	{
		return encounterStore.getEncounters( dataSet );
	}
	
	public Collection<Encounter> getEncounters( Boolean isExecuted ) 
	{
		return encounterStore.getEncounters( isExecuted );
	}
	
	public Collection<Encounter> getAllEncounters() 
	{
		return encounterStore.getAllEncounters();
	}
	
	public void updateEncounter( Encounter encounter ) 
	{
		encounterStore.updateEncounter( encounter );
	}

	public Collection<Encounter> getEncounters(DataSet dataSet, OrganisationUnit organisationUnit) 
	{
		return encounterStore.getEncounters( dataSet, organisationUnit );
	}	
}