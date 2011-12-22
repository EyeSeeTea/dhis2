package org.hisp.dhis.reportsheet.exportitem.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportItem;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class AddExportItemAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String itemType;

    public void setItemType( String itemType )
    {
        this.itemType = itemType;
    }

    private String expression;

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    private String extraExpression;

    public void setExtraExpression( String extraExpression )
    {
        this.extraExpression = extraExpression;
    }

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    private Integer row;

    public void setRow( Integer row )
    {
        this.row = row;
    }

    private Integer column;

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    private Integer exportReportId;

    public void setExportReportId( Integer exportReportId )
    {
        this.exportReportId = exportReportId;
    }

    public Integer getExportReportId()
    {
        return exportReportId;
    }

    private Integer sheetNo;

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExportItem exportItem = new ExportItem();

        exportItem.setName( name );
        exportItem.setItemType( itemType );
        exportItem.setRow( row );
        exportItem.setColumn( column );
        exportItem.setExpression( expression );
        exportItem.setExtraExpression( extraExpression );
        exportItem.setPeriodType( periodType );
        exportItem.setSheetNo( (sheetNo) );
        exportItem.setExportReport( exportReportService.getExportReport( exportReportId ) );

        exportReportService.addExportItem( exportItem );

        return SUCCESS;
    }
}
