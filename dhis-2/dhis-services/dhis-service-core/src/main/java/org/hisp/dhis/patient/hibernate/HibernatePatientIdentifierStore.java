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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierStore;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientIdentifierStore implements PatientIdentifierStore {
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // PatientIdentifier
    // -------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#addPatientIdentifier(org.hisp.dhis.chis.patient.PatientIdentifier)
	 */
	public int addPatientIdentifier(PatientIdentifier patientIdentifier) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		return (Integer) session.save( patientIdentifier );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#deletePatientIdentifier(org.hisp.dhis.chis.patient.PatientIdentifier)
	 */
	public void deletePatientIdentifier(PatientIdentifier patientIdentifier) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		session.delete( patientIdentifier );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#getAllPatientIdentifiers()
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<PatientIdentifier> getAllPatientIdentifiers() {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );
		
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#getPatienIdentifiersByIdentifier(java.lang.String)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<PatientIdentifier> getPatienIdentifiersByIdentifier(
			String identifier) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );
		criteria.add( Restrictions.eq( "identifier", identifier ) );
		
		return criteria.list();
	}	

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#getPatientIdentifier(java.lang.String, org.hisp.dhis.organisationunit.OrganisationUnit)
	 */	
	public PatientIdentifier getPatientIdentifier(String identifier,
			OrganisationUnit organisationUnit) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );
		criteria.add( Restrictions.eq( "identifier", identifier ) );
		criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
		
		return (PatientIdentifier) criteria.uniqueResult();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#getPatientIdentifiersByOrgUnit(org.hisp.dhis.organisationunit.OrganisationUnit)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<PatientIdentifier> getPatientIdentifiersByOrgUnit(
			OrganisationUnit organisationUnit) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );
		criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
		
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.patient.PatientIdentifierStore#updatePatientIdentifier(org.hisp.dhis.chis.patient.PatientIdentifier)
	 */
	public void updatePatientIdentifier(PatientIdentifier patientIdentifier) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		session.update( patientIdentifier );
	}

	public PatientIdentifier getPatientIdentifier(Patient patient) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );
		criteria.add( Restrictions.eq( "patient", patient ) );		
		criteria.add( Restrictions.eq( "preferred", true ) );
		
		return (PatientIdentifier) criteria.uniqueResult();
	}

	public PatientIdentifier getPatientIdentifier(int id) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();

        return (PatientIdentifier) session.get( PatientIdentifier.class, id );
	}

	/*public PatientIdentifier getPatientIdentifierLastAssigned(Source source) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( PatientIdentifier.class );		
		criteria.add( Restrictions.eq( "source", source ) );
		criteria.add( Restrictions.eq( "identifier", Restrictions.sqlRestriction("SELECT MAX(identifier) FROM patientidentifier")));	
		
		return (PatientIdentifier) criteria.uniqueResult();
	}*/
}
