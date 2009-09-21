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
package org.hisp.dhis.patient.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.encounter.Encounter;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientDataValueStore 
	implements PatientDataValueStore 
{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // PatientDataValue
    // -------------------------------------------------------------------------
    
	public void addPatientDataValue( PatientDataValue patientDataValue ) 
	{
        Session session = sessionFactory.getCurrentSession();

        session.save( patientDataValue );        
	}

	public void deletePatientDataValue( PatientDataValue patientDataValue ) 
	{
		Session session = sessionFactory.getCurrentSession();

        session.delete( patientDataValue );
	}
	
	public int deletePatientDataValue( Encounter encounter ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete PatientDataValue where encounter = :encounter" );
        query.setEntity( "encounter", encounter );

        return query.executeUpdate();
	}

	public int deletePatientDataValue( DataElement dataElement ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete PatientDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );

        return query.executeUpdate();
	}

	public int deletePatientDataValue(  DataElementCategoryOptionCombo optionCombo ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete PatientDataValue where optionCombo = :optionCombo" );
        query.setEntity( "optionCombo", optionCombo );

        return query.executeUpdate();
	}
	
	public PatientDataValue getPatientDataValue(Encounter encounter,
			DataElement dataElement, DataElementCategoryOptionCombo optionCombo) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( PatientDataValue.class );
        criteria.add( Restrictions.eq( "encounter", encounter ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (PatientDataValue) criteria.uniqueResult();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<PatientDataValue> getPatientDataValues( Encounter encounter, DataElement dataElement ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PatientDataValue.class );
        criteria.add( Restrictions.eq( "encounter", encounter ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<PatientDataValue> getPatientDataValues( Encounter encounter ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PatientDataValue.class );
        criteria.add( Restrictions.eq( "encounter", encounter ) );        

        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<PatientDataValue> getPatientDataValues( DataElementCategoryOptionCombo optionCombo ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PatientDataValue.class );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );        

        return criteria.list();
	}
	
	public void updatePatientDataValue( PatientDataValue patientDataValue ) 
	{
		Session session = sessionFactory.getCurrentSession();

        session.update( patientDataValue );
	}

	@SuppressWarnings( "unchecked" )
	public Collection<PatientDataValue> getAllPatientDataValues() 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientDataValue.class );
		
		 return criteria.list();
	}

}
