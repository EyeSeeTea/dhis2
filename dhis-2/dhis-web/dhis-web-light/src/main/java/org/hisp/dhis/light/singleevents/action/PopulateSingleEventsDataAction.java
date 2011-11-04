/*
 * Copyright (c) 2004-2010, University of Oslo
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

package org.hisp.dhis.light.singleevents.action;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class PopulateSingleEventsDataAction implements Action {
    
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }
	
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	@Override
	public String execute() {
		
		// Create datasets
		PeriodType periodType = periodService.getPeriodTypeByName( "Daily" );
		DataSet dataSet1 = new DataSet( "Dataset1", "DD1", null, periodType );
        dataSet1.setMobile( true );
        dataSet1.setVersion( 1 );
        int datasetId1 = dataSetService.addDataSet( dataSet1 );
		dataSet1 = dataSetService.getDataSet(datasetId1);
		
		DataSet dataSet2 = new DataSet( "Dataset2", "DD2", null, periodType );
        dataSet2.setMobile( true );
        dataSet2.setVersion( 1 );
        int datasetId2 = dataSetService.addDataSet( dataSet2 );
		dataSet2 = dataSetService.getDataSet(datasetId2);
        
        // Create orgunits
		OrganisationUnit organisationUnit1 = new OrganisationUnit( "Andeby", "ab", null, new Date(), null, true, null );
		organisationUnit1.addDataSet(dataSet1);
		organisationUnit1.setHasPatients(true);
		int id1 = organisationUnitService.addOrganisationUnit( organisationUnit1 );
		
		OrganisationUnit organisationUnit2 = new OrganisationUnit( "Gaseby", "gb", null, new Date(), null, true, null );
		organisationUnit2.addDataSet(dataSet2);
		organisationUnit2.setHasPatients(true);
		int id2 = organisationUnitService.addOrganisationUnit( organisationUnit2 );
		
		organisationUnit1 = organisationUnitService.getOrganisationUnit(id1);
		organisationUnit2 = organisationUnitService.getOrganisationUnit(id2);
		
		Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
		organisationUnits.add(organisationUnit1);
		
		// Add orgunits to user
		Collection<User> users = userService.getAllUsers();
		User admin = users.iterator().next();
		
		admin.addOrganisationUnit(organisationUnit1);
		admin.addOrganisationUnit(organisationUnit2);
		userService.updateUser(admin);
		
		// Create Single-event programs
		
		Program program = new Program();
		
        program.setName( "Death" );
        program.setDescription( "Death" );
        program.setVersion( 1 );
        program.setDateOfEnrollmentDescription( "" );
        program.setDateOfIncidentDescription( "Date of Death" );
        program.setMaxDaysAllowedInputData( 60 );
        program.setSingleEvent( true );
        program.setOrganisationUnits(organisationUnits);

        programService.saveProgram( program );
        
        ProgramStage programStage = new ProgramStage();

        programStage.setName( "Single-Event Death" );
        programStage.setDescription( "Death" );
        programStage.setStageInProgram( program.getProgramStages().size() + 1 );
        programStage.setProgram( program );
        programStage.setMinDaysFromStart( 0 );

        int progID = programStageService.saveProgramStage( programStage );
        
		Program program2 = new Program();
		
        program2.setName( "Birth");
        program2.setDescription( "Birth" );
        program2.setVersion( 1 );
        program2.setDateOfEnrollmentDescription( "" );
        program2.setDateOfIncidentDescription( "Date of Birth" );
        program2.setMaxDaysAllowedInputData( 60 );
        program2.setSingleEvent( true );
        program2.setOrganisationUnits(organisationUnits);

        programService.saveProgram( program2 );
        
        ProgramStage programStage2 = new ProgramStage();

        programStage2.setName( "Single-Event Birth" );
        programStage2.setDescription( "Death" );
        programStage2.setStageInProgram( program2.getProgramStages().size() + 1 );
        programStage2.setProgram( program2 );
        programStage2.setMinDaysFromStart( 0 );
        
        programStageService.saveProgramStage( programStage2 );
		
        programStage = programStageService.getProgramStage(progID);
        
        // Create Dataelements
		DataElement dataElement = new DataElement();
        
		DataElementCategoryCombo categoryCombo = dataElementCategoryService.getDataElementCategoryComboByName(DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME);
		
		dataElement.setName( "Date of Death" );
        dataElement.setShortName( "DOD" );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement.setType( DataElement.VALUE_TYPE_DATE );
        dataElement.setCategoryCombo(categoryCombo );
        dataElement.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement.setActive(true);
        
        int elementID = dataElementService.addDataElement( dataElement );
        
        DataElement dataElement2 = new DataElement();
		dataElement2.setName( "Probable Cause" );
        dataElement2.setShortName( "PC" );
        dataElement2.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement2.setType( DataElement.VALUE_TYPE_STRING );
        dataElement2.setCategoryCombo(categoryCombo );
        dataElement2.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement2.setActive(true);
        
        int elementID2 = dataElementService.addDataElement( dataElement2 );
        
        DataElement dataElement3 = new DataElement();
		dataElement3.setName( "Bool" );
        dataElement3.setShortName( "Bo" );
        dataElement3.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement3.setType( DataElement.VALUE_TYPE_BOOL );
        dataElement3.setCategoryCombo(categoryCombo );
        dataElement3.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement3.setActive(true);
        
        int elementID3 = dataElementService.addDataElement( dataElement3 );
        
        DataElement dataElement4 = new DataElement();
		dataElement4.setName( "Positive int" );
        dataElement4.setShortName( "Pos" );
        dataElement4.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement4.setType( DataElement.VALUE_TYPE_POSITIVE_INT );
        dataElement4.setCategoryCombo(categoryCombo );
        dataElement4.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement4.setActive(true);
        
        int elementID4 = dataElementService.addDataElement( dataElement4 );
        
        DataElement dataElement5 = new DataElement();
		dataElement5.setName( "Negative int" );
        dataElement5.setShortName( "Neg" );
        dataElement5.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement5.setType( DataElement.VALUE_TYPE_NEGATIVE_INT );
        dataElement5.setCategoryCombo(categoryCombo );
        dataElement5.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement5.setActive(true);
        
        int elementID5 = dataElementService.addDataElement( dataElement5 );
        
        DataElement dataElement6 = new DataElement();
		dataElement6.setName( "Number" );
        dataElement6.setShortName( "Nu" );
        dataElement6.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement6.setType( DataElement.VALUE_TYPE_NUMBER );
        dataElement6.setCategoryCombo(categoryCombo );
        dataElement6.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement6.setActive(true);
        
        int elementID6 = dataElementService.addDataElement( dataElement6 );
        
        DataElement dataElement7 = new DataElement();
		dataElement7.setName( "Int" );
        dataElement7.setShortName( "In" );
        dataElement7.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        dataElement7.setType( DataElement.VALUE_TYPE_INT );
        dataElement7.setCategoryCombo(categoryCombo );
        dataElement7.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement7.setActive(true);
        
        int elementID7 = dataElementService.addDataElement( dataElement7 );
        
        dataElement = dataElementService.getDataElement(elementID);
        dataElement2 = dataElementService.getDataElement(elementID2);
        dataElement3 = dataElementService.getDataElement(elementID3);
        dataElement4 = dataElementService.getDataElement(elementID4);
        dataElement5 = dataElementService.getDataElement(elementID5);
        dataElement6 = dataElementService.getDataElement(elementID6);
        dataElement7 = dataElementService.getDataElement(elementID7);
        
        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement( programStage, dataElement, true, 1 );
        programStageDataElement.setSortOrder(1);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement2, true, 2 );
        programStageDataElement.setSortOrder(2);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement3, true, 3 );
        programStageDataElement.setSortOrder(4);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement4, true, 4 );
        programStageDataElement.setSortOrder(3);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement5, true, 5 );
        programStageDataElement.setSortOrder(5);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement6, true, 6 );
        programStageDataElement.setSortOrder(6);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        programStageDataElement = new ProgramStageDataElement( programStage, dataElement7, true, 7 );
        programStageDataElement.setSortOrder(7);
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );
        
        class Name {
        	private String firstName;
        	private String middleName;
        	private String lastName;
			
        	public Name(String firstName, String middleName, String lastName){
        		this.firstName = firstName;
        		this.middleName = middleName;
        		this.lastName = lastName;
        	}
        	
        	public Name(String firstName, String lastName){
        		this.firstName = firstName;
        		this.lastName = lastName;
        	}
        	
        	public Name(String firstName){
        		this.firstName = firstName;
        	}
        }
        
        ArrayList<Name> patients = new ArrayList<Name>();
        patients.add(new Name("Donald","Duck"));
        patients.add(new Name("Magica","Fra","Tryll"));
        patients.add(new Name("Dolly","Duck"));
        patients.add(new Name("Doffen","Duck"));
        patients.add(new Name("Dole","Duck"));
        patients.add(new Name("Ole","Duck"));
        patients.add(new Name("Mikke","Mus"));
        patients.add(new Name("Langbein"));
        patients.add(new Name("Petter","Smart"));
        patients.add(new Name("SvartePetter"));
        patients.add(new Name("Onkel","Skrue"));
        patients.add(new Name("Darkwing","Duck"));
        patients.add(new Name("Minne","Mus"));
        patients.add(new Name("Fetter","Anton"));
        patients.add(new Name("Bestemor","Duck"));
        patients.add(new Name("Klodrik"));
        patients.add(new Name("Rikerud"));
        patients.add(new Name("Klasu","Knegg"));
        patients.add(new Name("Pluto"));
        patients.add(new Name("Politimester","Fiks"));
        patients.add(new Name("Anton","Duck"));
        patients.add(new Name("Hetti"));
        patients.add(new Name("Netti"));
        patients.add(new Name("Letti"));
        patients.add(new Name("Nabo","Jensen"));
        patients.add(new Name("Klara","Ku"));
        patients.add(new Name("Rotor","McKvakk"));
        patients.add(new Name("Snipp"));
        patients.add(new Name("Snapp"));
        patients.add(new Name("B-Gjeng","176-167"));
        patients.add(new Name("B-Gjeng","176-671"));
        patients.add(new Name("B-Gjeng","176-761"));
        patients.add(new Name("Bestefar","B"));
        
        // var to check if date sorts correct
        int i = 0;
        // var to alternate which program to enroll
        boolean alternate = true;
        
        for(Name item : patients){
        	
        	alternate = (alternate) ? false : true;
        	i++;
        	
        	Program pro = (alternate) ? program : program2;
        	
	        Patient p = new Patient();
	        p.setIsDead(false);
	        p.setFirstName(item.firstName);
	        p.setMiddleName(item.middleName);
	        p.setLastName(item.lastName);
	        p.setBirthDate(new Date());
	        p.setGender(Patient.MALE);
	        p.setDobType(Patient.DOB_TYPE_VERIFIED);
	        p.setOrganisationUnit(organisationUnit1);
	        p.setRegistrationDate(new Date());
	        
	        patientService.savePatient(p);
	        
	        ProgramInstance programInstance = new ProgramInstance();
	        programInstance.setEnrollmentDate( new Date() );
	        Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DAY_OF_MONTH, -i);
	        date = cal.getTime();
	        programInstance.setDateOfIncident(date);
	        programInstance.setProgram( pro );
	        programInstance.setPatient( p );
	        programInstance.setCompleted( false );
	
	        programInstanceService.addProgramInstance( programInstance );
	
	        p.getPrograms().add( pro );
	        patientService.updatePatient( p );
        }
		
		return SUCCESS;
	}
}
