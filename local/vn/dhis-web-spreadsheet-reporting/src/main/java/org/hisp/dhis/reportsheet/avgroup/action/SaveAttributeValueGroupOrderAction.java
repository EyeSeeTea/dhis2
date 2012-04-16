package org.hisp.dhis.reportsheet.avgroup.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.List;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportAttribute;
import org.hisp.dhis.reportsheet.ExportReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class SaveAttributeValueGroupOrderAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private Integer attributeId;

    private String name;

    private String clazzName;

    private List<String> attributeValues = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }

    public void setClazzName( String clazzName )
    {
        this.clazzName = clazzName;
    }

    public void setAttributeValues( List<String> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public String execute()
        throws Exception
    {
        AttributeValueGroupOrder attributeValueGroupOrder = new AttributeValueGroupOrder( name );

        Attribute attribute = attributeService.getAttribute( attributeId );

        attributeValueGroupOrder.setAttribute( attribute );

        List<String> finalList = new ArrayList<String>();
        
        removeDuplicatedItems( attributeValues, finalList );
        
        attributeValueGroupOrder.setAttributeValues( finalList );

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            ExportReportAttribute exportReportAttribute = (ExportReportAttribute) exportReportService
                .getExportReport( id );

            List<AttributeValueGroupOrder> attributeValueGroupOrders = exportReportAttribute.getAttributeValueOrders();

            attributeValueGroupOrders.add( attributeValueGroupOrder );

            exportReportAttribute.setAttributeValueOrders( attributeValueGroupOrders );

            exportReportService.updateExportReport( exportReportAttribute );
        }

        attributeValues = null;
        
        return SUCCESS;
    }
    
    private static void removeDuplicatedItems( List<String> a, List<String> b )
    {
        for ( String s1 : a )
        {
            if ( !b.contains( s1 ) )
            {
                b.add( s1 );
            }
        }
    }
}
