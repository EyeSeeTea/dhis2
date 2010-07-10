package org.hisp.dhis.dd.action.dataelement;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.datadictionary.comparator.DataDictionaryNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;
import org.hisp.dhis.options.datadictionary.DataDictionaryModeManager;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.system.paging.Paging;
import org.hisp.dhis.util.RequestUtil;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: GetDataElementListAction.java 5573 2008-08-22 03:39:55Z
 *          ch_bharath1 $
 */
public class GetDataElementListAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataDictionaryModeManager dataDictionaryModeManager;

    public void setDataDictionaryModeManager( DataDictionaryModeManager dataDictionaryModeManager )
    {
        this.dataDictionaryModeManager = dataDictionaryModeManager;
    }

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<DataDictionary> dataDictionaries;

    public List<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer dataDictionaryId;

    public Integer getDataDictionaryId()
    {
        return dataDictionaryId;
    }

    public void setDataDictionaryId( Integer dataDictionaryId )
    {
        this.dataDictionaryId = dataDictionaryId;
    }

    private Integer dataElementGroupId;

    public void setDataElementGroupId( Integer dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    public Integer getDataElementGroupId()
    {
        return dataElementGroupId;
    }

    private Integer currentPage;
    
    public void setCurrentPage( Integer currentPage )
    {
        this.currentPage = currentPage;
    }

    private Integer pageSize;
    
    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }
    
    private Integer defaultPageSize = 50;
    
    public void setDefaultPageSize( Integer defaultPageSize )
    {
        this.defaultPageSize = defaultPageSize;
    }
    
    private Paging paging;
    
    public Paging getPaging()
    {
        return paging;
    }

    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }

    // -------------------------------------------------------------------------
    // Action implemantation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
    {
        paging = new Paging( RequestUtil.getCurrentLink(ServletActionContext.getRequest()) + "?listAll=true", pageSize == null ? defaultPageSize : pageSize );
        
        paging.setCurrentPage( currentPage == null ? 0 : currentPage );
        
        
        if ( dataDictionaryId == null ) // None, get current data dictionary
        {
            dataDictionaryId = dataDictionaryModeManager.getCurrentDataDictionary();
        }
        else if ( dataDictionaryId == -1 ) // All, reset current data
        // dictionary
        {
            dataDictionaryModeManager.setCurrentDataDictionary( null );

            dataDictionaryId = null;
        }
        else
        // Specified, set current data dictionary
        {
            dataDictionaryModeManager.setCurrentDataDictionary( dataDictionaryId );
        }

        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );

        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );

        dataDictionaries = new ArrayList<DataDictionary>( dataDictionaryService.getAllDataDictionaries() );

        Collections.sort( dataDictionaries, new DataDictionaryNameComparator() );

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        List<DataElement> allResult;
        
        if ( dataDictionaryId != null && dataElementGroupId == null )
        {
            allResult = new ArrayList<DataElement>( dataDictionaryService
                .getDataElementsByDictionaryId( dataDictionaryId ) );
        }
        else if ( dataDictionaryId == null && dataElementGroupId != null )
        {
            allResult = new ArrayList<DataElement>( dataElementService.getDataElementsByGroupId( dataElementGroupId ) );
        }
        else if ( dataDictionaryId != null && dataElementGroupId != null )
        {
            Collection<DataElement> dictionary = dataDictionaryService.getDataElementsByDictionaryId( dataDictionaryId );

            Collection<DataElement> members = dataElementService.getDataElementsByGroupId( dataElementGroupId );

            allResult = new ArrayList<DataElement>( CollectionUtils.intersection( dictionary, members ) );
        }
        else
        {
            allResult = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        }

        Collections.sort( allResult, dataElementComparator );

        total = allResult.size();
            
        paging.setTotal( total );

        dataElements = getBlockElement( allResult, paging.getStartPos(), paging.getPageSize() );

        displayPropertyHandler.handle( dataElements );

        return SUCCESS;
    }

    private List<DataElement> getBlockElement( List<DataElement> dataElementList, int startPos, int pageSize )
    {
        List<DataElement> returnList;

        try
        {
            returnList = dataElementList.subList( startPos, startPos + pageSize );
        }
        catch ( IndexOutOfBoundsException ex )
        {
            returnList = dataElementList.subList( startPos, dataElementList.size() );
        }

        return returnList;
    }
}
