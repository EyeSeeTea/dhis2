package org.hisp.dhis.patient;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

public class PatientStoreTest
    extends DhisSpringTest
{
    private PatientStore patientStore;
    
    private Patient patientA;
    private Patient patientB;
    
    @Override
    public void setUpTest()
    {
        patientStore = (PatientStore) getBean( PatientStore.ID );
        
        patientA = createPerson( 'A' );
        patientB = createPerson( 'B' );        
    }
    
    protected static Patient createPerson( char uniqueChar )
    {
        Patient patient = new Patient();
        
        patient.setFirstName( "FirstName" + uniqueChar );
        patient.setMiddleName( "MiddleName" + uniqueChar );
        patient.setLastName( "LastName" + uniqueChar );
        patient.setGender( "Male" );
        patient.setBirthDate( getDate( 1970, 1, 1 ) );
        
        return patient;
    }
    
    @Test
    public void addGet()
    {
        int idA = patientStore.addPatient( patientA );
        int idB = patientStore.addPatient( patientB );
        
        assertEquals( patientA.getFirstName(), patientStore.getPatient( idA ).getFirstName() );
        assertEquals( patientB.getFirstName(), patientStore.getPatient( idB ).getFirstName() );        
    }
}
