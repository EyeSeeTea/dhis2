/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.i18n.locale;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.user.UserSettingService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author James Chang
 * @version $Id$
 */
@Transactional
public class DefaultI18nLocaleService
    implements I18nLocaleService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nLocaleStore i18nLocaleStore;

    public void setI18nLocaleStore( I18nLocaleStore i18nLocaleStore )
    {
        this.i18nLocaleStore = i18nLocaleStore;
    }

    private Map<String, String> languages;

    public void setLanguages( Map<String, String> languages )
    {
        this.languages = languages;
    }
    
    public Map<String, String> getLanguages()
    {
        //Collections.sort( languages );
        return languages;
    }

/*    public String getFlagImage()
    {
        String flag = (String) getSystemSetting( KEY_FLAG );

        return flag != null ? flag + ".png" : null;
    }
*/

    private Map<String, String> countries;


    public void setCountries( Map<String, String> countries )
    {
        this.countries = countries;
    }
    
    public Map<String, String> getCountries()
    {
        //Collections.sort( countries );
        return countries;
    }

    private UserSettingService userSettingService;
    
    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public int saveI18nLocale( I18nLocale i18nlocale )
    {
        return i18nLocaleStore.save( i18nlocale );
    }

    public void updateI18nLocale( I18nLocale i18nlocale )
    {
        i18nLocaleStore.update( i18nlocale );
    }

    public void deleteI18nLocale( I18nLocale i18nlocale )
    {
        i18nLocaleStore.delete( i18nlocale );
    }

    public List<I18nLocale> getAllI18nLocales()
    {
        return i18nLocaleStore.getAll();
    }

    public I18nLocale getI18nLocale( int id )
    {
        return i18nLocaleStore.get( id );
    }

    public I18nLocale getI18nLocaleByName( String name )
    {
        return i18nLocaleStore.getByName( name );
    }

    public I18nLocale getCurrentI18nLocale()
    {
        I18nLocale i18nLocale = null;

        try
        {
            String i18nLocaleId = (String) userSettingService.getUserSetting( UserSettingService.KEY_DB_LOCALE );

            if ( i18nLocaleId != "None" )
            {
                i18nLocale = getI18nLocale( Integer.valueOf( i18nLocaleId ) );
            }
        }
        catch ( Exception ex )
        {
        }

        return i18nLocale;
    }

    public Map<String, String> getAvailableLanguages()
    {
        return languages;
    }

    public Map<String, String> getAvailableCountries()
    {
        return countries;
    }

}
