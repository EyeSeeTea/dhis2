
/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.dataadmin.action.locale;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.i18n.locale.*;

import com.opensymphony.xwork2.Action;

/**
 * @author James Chang
 * @version $Id$
 */
public class GetLocaleListAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private I18nLocaleService i18nlocaleService;

    public void setI18nLocaleService( I18nLocaleService i18nlocaleService )
    {
        this.i18nlocaleService = i18nlocaleService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    
    private Collection<I18nLocale> i18nlocales = new ArrayList<I18nLocale>();

    public Collection<I18nLocale> getI18nLocales()
    {
        return i18nlocales;
    }

    public void setI18nLocales( Collection<I18nLocale> i18nlocales )
    {
        this.i18nlocales = i18nlocales;
    }

    
    private String strTestNote = "";

    public String getTestNote()
    {
        return strTestNote;
    }

    public void setTestNote(String strTestNote)
    {
        this.strTestNote = strTestNote;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        i18nlocales = i18nlocaleService.getAllI18nLocales();
        
        // Selection feature - will need to work on it.
        if ( id == null )
        {
        }
        else
        {
            I18nLocale i18nlocale = i18nlocaleService.getI18nLocale( id );
        }
        
        return SUCCESS;
    }
}
