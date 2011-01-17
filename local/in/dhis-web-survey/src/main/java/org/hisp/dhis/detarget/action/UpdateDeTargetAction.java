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
package org.hisp.dhis.detarget.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetService;



import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version UpdateDeTargetAction.java Jan 15, 2011 4:04:58 PM
 */
public class UpdateDeTargetAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
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
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    
    private int deTargetId;
    
    
    public void setDeTargetId( int deTargetId )
    {
        this.deTargetId = deTargetId;
    }

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
    
    private String url;
    
    public void setUrl( String url )
    {
        this.url = url;
    }    
       
    private String description;
    
    public void setDescription( String description )
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    private Collection<String> selectedList = new HashSet<String>();
    
    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }
    
    
    private List<DeTargetMember>  deTargetMemberList;
    
    public List<DeTargetMember> getDeTargetMemberList()
    {
        return deTargetMemberList;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------
    
        if ( shortName != null && shortName.trim().length() == 0 )
        {
                shortName = null;
        }
        
        System.out.println( " \n+++++++++ deTarget Id:" +  deTargetId );
       
        DeTarget deTarget = deTargetService.getDeTarget( deTargetId );
        //deTargetService.getDeTarget( deTargetId );
        deTarget.setName( name );
        deTarget.setShortName( shortName );
        deTarget.setUrl( url );
        deTarget.setDescription( description );
        
        deTargetMemberList = new ArrayList<DeTargetMember>(deTargetService.getDeTargetMembers( deTarget ));
        
        
        //Collection<Indicator> updatedIndicatorList = new HashSet<Indicator>();
        
        deTargetService.deleteDeTargetMembers( deTarget );
       
        /*
        for( DeTargetMember dataElementTarget : deTargetMemberList )
        {
            
            deTargetService.deleteDeTargetMember( dataElementTarget );
            //deTargetService.
            //selectedDeTargetMember.add( dataElementTarget.getDataelements().getId()+":" + dataElementTarget.getDecategoryOptionCombo().getId() );
        }
        */
        
        
        for ( String selectedId : selectedList )
        {
            String[] parts = selectedId.split( ":" );
            DataElement dataElement = dataElementService.getDataElement(  Integer.parseInt( parts[0] ) );
            DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( parts[1] ));
            
            DeTargetMember deTargetMember = new DeTargetMember( deTarget, dataElement, decoc);
            
            deTargetService.addDeTargetMember( deTargetMember );
            //System.out.println( dataElement + ":" +  decoc );
            
           
        }
        return SUCCESS;
    }
}

