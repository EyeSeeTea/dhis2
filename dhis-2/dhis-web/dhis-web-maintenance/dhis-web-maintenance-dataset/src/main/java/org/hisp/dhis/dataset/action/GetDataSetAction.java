package org.hisp.dhis.dataset.action;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: GetDataSetAction.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class GetDataSetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private int dataSetId;

    public int getDataSetId()
    {
        return dataSetId;
    }

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private List<DataElement> dataSetDataElements;

    public List<DataElement> getDataSetDataElements()
    {
        return dataSetDataElements;
    }

    private DataEntryForm dataEntryForm;
    
    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    
    private DataElementCategoryCombo categoryCombo;

    public DataElementCategoryCombo getCategoryCombo()
    {
    	return categoryCombo;
    }

    private List<OrganisationUnitLevel> levels;
    
    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }

    private List<OrganisationUnitGroup> groups;

    public List<OrganisationUnitGroup> getGroups()
    {
        return groups;
    }

    private Integer level;

    public Integer getLevel()
    {
        return level;
    }
    
    public void setLevel( Integer level )
    {
        this.level = level;
    }

    private Integer organisationUnitGroupId;

    public Integer getOrganisationUnitGroupId()
    {
        return organisationUnitGroupId;
    }

    public void setOrganisationUnitGroupId( Integer organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        dataSet = dataSetService.getDataSet( dataSetId );

        dataSetDataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        Collections.sort( dataSetDataElements, dataElementComparator );       
                	
        displayPropertyHandler.handle( dataSetDataElements );

        dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        
        levels = organisationUnitService.getOrganisationUnitLevels();

        groups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );
        
        Collections.sort( groups, new OrganisationUnitGroupNameComparator() );
        
        return SUCCESS;
    }
}
