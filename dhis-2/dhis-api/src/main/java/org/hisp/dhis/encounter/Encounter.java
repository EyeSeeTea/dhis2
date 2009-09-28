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

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class Encounter
    implements Serializable
{
    private int id;

    private Patient patient;

    private OrganisationUnit organisationUnit;

    private DataSet dataSet;

    private Date encounterDateTime;

    private Boolean isExecuted = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Encounter()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int prime = 31;
        int result = 1;

        result = result * prime + patient.hashCode();
        result = result * prime + organisationUnit.hashCode();
        result = result * prime + dataSet.hashCode();
        result = result * prime + encounterDateTime.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Encounter) )
        {
            return false;
        }

        final Encounter other = (Encounter) o;

        return patient.equals( other.getPatient() ) && organisationUnit.equals( other.getOrganisationUnit() )
            && dataSet.equals( other.getDataSet() ) && encounterDateTime.equals( other.getEncounterDateTime() );
    }

    @Override
    public String toString()
    {
        return "[" + dataSet.getName() + ":" + patient + ":" + encounterDateTime + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setId( int id )
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public Date getEncounterDateTime()
    {
        return encounterDateTime;
    }

    public void setEncounterDateTime( Date encounterDateTime )
    {
        this.encounterDateTime = encounterDateTime;
    }

    public Boolean getIsExecuted()
    {
        return isExecuted;
    }

    public void setIsExecuted( Boolean isExecuted )
    {
        this.isExecuted = isExecuted;
    }

}
