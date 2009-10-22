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

package org.hisp.dhis.patientdatavalue.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientDataValueStore
    extends HibernateGenericStore<PatientDataValue>
    implements PatientDataValueStore
{
    public void saveVoid( PatientDataValue patientDataValue )
    {
        sessionFactory.getCurrentSession().save( patientDataValue );
    }

    public int delete( ProgramInstance programInstance )
    {
        Query query = getQuery( "delete PatientDataValue where programInstance = :programInstance" );
        query.setEntity( "programInstance", programInstance );
        return query.executeUpdate();
    }

    public int delete( ProgramStage programStage )
    {
        Query query = getQuery( "delete PatientDataValue where programStage = :programStage" );
        query.setEntity( "programStage", programStage );
        return query.executeUpdate();
    }

    public int delete( DataElement dataElement )
    {
        Query query = getQuery( "delete PatientDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );
        return query.executeUpdate();
    }

    public int delete( DataElementCategoryOptionCombo optionCombo )
    {
        Query query = getQuery( "delete PatientDataValue where optionCombo = :optionCombo" );
        query.setEntity( "optionCombo", optionCombo );
        return query.executeUpdate();
    }

    public PatientDataValue get( ProgramInstance programInstance, ProgramStage programStage, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit )
    {
        return (PatientDataValue) getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ), Restrictions.eq( "dataElement", dataElement ),
            Restrictions.eq( "organisationUnit", organisationUnit ), Restrictions.eq( "optionCombo", optionCombo ) )
            .uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramInstance programInstance, ProgramStage programStage,
        DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ), Restrictions.eq( "dataElement", dataElement ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Patient patient, DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "patient", patient ), Restrictions.eq( "dataElement", dataElement ) )
            .list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramInstance programInstance )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Collection<ProgramInstance> programInstances )
    {
        return getCriteria( Restrictions.in( "programInstance", programInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramInstance programInstance, ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElementCategoryOptionCombo optionCombo )
    {
        return getCriteria( Restrictions.eq( "optionCombo", optionCombo ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, ProgramInstance programInstance )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ),
            Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, ProgramInstance programInstance,
        ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ), Restrictions.eq( "organisationUnit", organisationUnit ) )
            .list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, ProgramInstance programInstance,
        ProgramStage programStage, DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ), Restrictions.eq( "dataElement", dataElement ),
            Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ),
            Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit,
        DataElementCategoryOptionCombo optionCombo )
    {
        return getCriteria( Restrictions.eq( "optionCombo", optionCombo ),
            Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

}
