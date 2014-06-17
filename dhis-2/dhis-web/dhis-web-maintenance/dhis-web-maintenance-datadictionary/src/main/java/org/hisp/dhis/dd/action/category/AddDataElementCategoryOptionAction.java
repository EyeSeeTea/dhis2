package org.hisp.dhis.dd.action.category;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.calendar.CalendarService;
import org.hisp.dhis.calendar.DateUnit;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author Lars Helge Overland
 */
public class AddDataElementCategoryOptionAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private DataElementCategoryOption dataElementCategoryOption;

    public DataElementCategoryOption getDataElementCategoryOption()
    {
        return dataElementCategoryOption;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        code = StringUtils.trimToNull( code );

        Date sDate = null;
        Date eDate = null;

        if ( startDate != null && startDate.trim().length() != 0 )
        {
            DateUnit isoStartDate = calendarService.getSystemCalendar().toIso( startDate );
            sDate = isoStartDate.toJdkCalendar().getTime();
        }

        if ( endDate != null && endDate.trim().length() != 0 )
        {
            DateUnit isoEndDate = calendarService.getSystemCalendar().toIso( endDate );
            eDate = isoEndDate.toJdkCalendar().getTime();
        }

        dataElementCategoryOption = new DataElementCategoryOption( name );
        dataElementCategoryOption.setCode( code );
        dataElementCategoryOption.setStartDate( sDate );
        dataElementCategoryOption.setEndDate( eDate );
        dataElementCategoryOption.getOrganisationUnits().addAll ( selectionManager.getSelectedOrganisationUnits() );

        dataElementCategoryService.addDataElementCategoryOption( dataElementCategoryOption );

        return SUCCESS;
    }
}
