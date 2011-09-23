package org.hisp.dhis.dd.action.datadictionary;

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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.datadictionary.comparator.DataDictionaryNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetDataDictionaryListAction
    extends ActionPagingSupport<DataDictionary>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<DataDictionary> dataDictionaries;

    public List<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }  
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( dataDictionaryService.getDataDictionaryCountByName( key ) );
            
            dataDictionaries = new ArrayList<DataDictionary>( dataDictionaryService.getDataDictionarysBetweenByName( key, paging.getStartPos(), paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( dataDictionaryService.getDataDictionaryCount() );
            
            dataDictionaries = new ArrayList<DataDictionary>( dataDictionaryService.getDataDictionarysBetween( paging.getStartPos(), paging.getPageSize() ) );
        }
        
        Collections.sort( dataDictionaries, new DataDictionaryNameComparator() );        
        
        return SUCCESS;
    }
}
