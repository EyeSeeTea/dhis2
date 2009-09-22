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
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Encounter;
import org.hisp.dhis.patient.EncounterStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.dataset.DataSet;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernateEncounterStore 
	implements EncounterStore 
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
    // Encounter
    // -------------------------------------------------------------------------
    
	public int addEncounter( Encounter encounter ) 
	{
		Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( encounter );
	}

	public void deleteEncounter( Encounter encounter ) 
	{
		Session session = sessionFactory.getCurrentSession();

        session.delete( encounter );
	}
	
	/*
	public int deleteEncounterByDataSet( DataSet dataSet ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete Encounter where dataSet = :datSet" );
        query.setEntity( "dataSet", dataSet );

        return query.executeUpdate();
	}

	public int deleteEncounterByDate( Date encounterDateTime ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete Encounter where encounterDateTime = :encounterDateTime" );
        query.setEntity( "encounterDateTime", encounterDateTime );

        return query.executeUpdate();
	}

	public int deleteEncounterByPatient( Patient patient ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete Encounter where patient = :patient" );
        query.setEntity( "patient", patient );

        return query.executeUpdate();
	}

	public int deleteEncounterBySource( Source source ) 
	{
		Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete Encounter where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
	}
	*/
	
	public Encounter getEncounter( int id )
	{
		Session session = sessionFactory.getCurrentSession();

        return (Encounter) session.get( Encounter.class, id );
	}

	public Encounter getEncounter( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit, DataSet dataSet ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "encounterDateTime", encounterDateTime ) );
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return (Encounter) criteria.uniqueResult();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient, OrganisationUnit organisationUnit ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "encounterDateTime", encounterDateTime ) );
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Patient patient, DataSet dataSet ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );        
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Date encounterDateTime, Patient patient ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "encounterDateTime", encounterDateTime ) );
        criteria.add( Restrictions.eq( "patient", patient ) );        
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Date encounterDateTime, DataSet dataSet ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "encounterDateTime", encounterDateTime ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );       
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( OrganisationUnit organisationUnit, Patient patient ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
		criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "patient", patient ) );       
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( DataSet dataSet, Patient patient ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );        
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( OrganisationUnit organisationUnit ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
		criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );      
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Date encounterDateTime ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "encounterDateTime", encounterDateTime ) );      
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Patient patient ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "patient", patient ) );      
        
		return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( DataSet dataSet ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );      
        
		return criteria.list();
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters( Boolean isExecuted ) 
	{
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "isExecuted", isExecuted ) );      
        
		return criteria.list();	
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getAllEncounters() 
	{
		Session session = sessionFactory.getCurrentSession();		
		Criteria criteria = session.createCriteria( Encounter.class );          
        
		return criteria.list();
	}
	
	public void updateEncounter( Encounter encounter ) 
	{
		Session session = sessionFactory.getCurrentSession();

        session.update( encounter );
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<Encounter> getEncounters(DataSet dataSet, OrganisationUnit organisationUnit) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Encounter.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
		return criteria.list();
	}
	
}
