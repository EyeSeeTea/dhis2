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
import java.util.Collection;
import java.util.Date;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class PopulateSingleEventsDataAction implements Action {
    
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
		int id1 = organisationUnitService.addOrganisationUnit( organisationUnit1 );
		
		OrganisationUnit organisationUnit2 = new OrganisationUnit( "Gåseby", "gb", null, new Date(), null, true, null );
		organisationUnit2.addDataSet(dataSet2);
		int id2 = organisationUnitService.addOrganisationUnit( organisationUnit2 );
		
		organisationUnit1 = organisationUnitService.getOrganisationUnit(id1);
		organisationUnit2 = organisationUnitService.getOrganisationUnit(id2);
		
		// Add orgunits to user
		Collection<User> users = userService.getAllUsers();
		User admin = users.iterator().next();
		
		admin.addOrganisationUnit(organisationUnit1);
		admin.addOrganisationUnit(organisationUnit2);
		userService.updateUser(admin);
		
		return SUCCESS;
	}
}
