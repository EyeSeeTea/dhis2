package org.hisp.dhis.ll.action.llelements;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class AddLineListElementAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String dataType;

    public void setDataType( String dataType )
    {
        this.dataType = dataType;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String presentationType;

    public void setPresentationType( String presentationType )
    {
        this.presentationType = presentationType;
    }

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    public Collection<String> getSelectedList()
    {
        return selectedList;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        shortName = shortName.replaceAll( " ", "_" );

        LineListElement lineListElement = new LineListElement( name, shortName, dataType, presentationType );
        System.out.println("name, shortName, description,datatype, presentationtype " + name + " " + shortName+" " + description + " " +dataType+ " "+presentationType);

        if ( description != null && ! description.trim().equals( "" ) )
            lineListElement.setDescription( description );

        for ( String id : selectedList )
        {
            LineListOption lineListOption = lineListService.getLineListOption( Integer.parseInt( id ) );
            System.out.println("lineListOption = "+lineListOption);
            lineListElement.getLineListElementOptions().add( lineListOption );
        }

        lineListService.addLineListElement( lineListElement );
       
        return SUCCESS;
    }

}
