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
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientStore;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientStore 
	implements PatientStore 
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
    // Patient
    // -------------------------------------------------------------------------
	

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#addPatient(org.hisp.dhis.chis.patient.Patient)
	 */
	public int addPatient(Patient patient) {
		// TODO Auto-generated method stub
		
		 Session session = sessionFactory.getCurrentSession();
		 
		 return (Integer) session.save( patient );		 
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#deletePatient(org.hisp.dhis.chis.patient.Patient)
	 */
	public void deletePatient(Patient patient) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		session.delete( patient );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#getAllPatients()
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<Patient> getAllPatients() {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Patient.class );
		
		return criteria.list();		
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#getPatiensByGender(int)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<Patient> getPatiensByGender(String gender) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Patient.class );
		criteria.add( Restrictions.eq( "gender", gender ) );
		
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#getPatient(int)
	 */	
	public Patient getPatient(int id) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		return (Patient) session.get( Patient.class, id );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#getPatientsByBirthDate(java.util.Date)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<Patient> getPatientsByBirthDate(Date birthDate) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Patient.class );
		criteria.add( Restrictions.eq( "birthDate", birthDate ) );
		
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#getPatientsByNames(java.lang.String)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<Patient> getPatientsByNames(String name) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( Patient.class );        
        criteria.add( Restrictions.disjunction().add( Restrictions.ilike( "firstName", name+"%" ) ).add( Restrictions.ilike( "middleName", name+"%" ) ).add( Restrictions.ilike("lastName", name+"%" ) ) );        

        return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientStore#updatePatient(org.hisp.dhis.chis.patient.Patient)
	 */
	public void updatePatient(Patient patient) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		session.update( patient );
		
		session.flush();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<Patient> getAllPatients(Boolean isDead) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( Patient.class );
		criteria.add( Restrictions.eq( "isDead", isDead ) );
		
		return criteria.list();
	}

}
