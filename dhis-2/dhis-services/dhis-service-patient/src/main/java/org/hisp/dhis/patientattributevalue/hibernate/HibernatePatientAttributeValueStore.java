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
package org.hisp.dhis.patientattributevalue.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueStore;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class HibernatePatientAttributeValueStore
    extends HibernateGenericStore<PatientAttributeValue>
    implements PatientAttributeValueStore
{

    public void saveVoid( PatientAttributeValue patientAttributeValue )
    {
        sessionFactory.getCurrentSession().save( patientAttributeValue );
    }

    public int deleteByAttribute( PatientAttribute patientAttribute )
    {
        Query query = getQuery( "delete PatientAttributeValue where patientAttribute = :patientAttribute" );
        query.setEntity( "patientAttribute", patientAttribute );
        return query.executeUpdate();
    }

    public int deleteByPatient( Patient patient )
    {
        Query query = getQuery( "delete PatientAttributeValue where patient = :patient" );
        query.setEntity( "patient", patient );
        return query.executeUpdate();
    }

    public PatientAttributeValue get( Patient patient, PatientAttribute patientAttribute )
    {
        return (PatientAttributeValue) getCriteria( Restrictions.eq( "patient", patient ),
            Restrictions.eq( "patientAttribute", patientAttribute ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( Patient patient )
    {
        return getCriteria( Restrictions.eq( "patient", patient ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( PatientAttribute patientAttribute )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( Collection<Patient> patients )
    {
        return getCriteria( Restrictions.in( "patient", patients ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> searchByValue( PatientAttribute patientAttribute, String searchText )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).list();
    }

    public int delete( DataElementCategoryOptionCombo optionCombo )
    {
        Query query = getQuery( "delete PatientDataValue where optionCombo = :optionCombo" );
        query.setEntity( "optionCombo", optionCombo );
        return query.executeUpdate();
    }

    public int countByPatientAttributeoption( PatientAttributeOption attributeOption )
    {
        return (Integer) getCriteria( Restrictions.eq( "patientAttributeOption", attributeOption ) )
                .setProjection(Projections.rowCount() ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getPatient( PatientAttribute attribute, String value )
    {
        return getCriteria( Restrictions.and( Restrictions.eq( "patientAttribute", attribute ), Restrictions.eq( "value", value ) ))
            .setProjection( Projections.property( "patient" ) ).list();
    }
}
