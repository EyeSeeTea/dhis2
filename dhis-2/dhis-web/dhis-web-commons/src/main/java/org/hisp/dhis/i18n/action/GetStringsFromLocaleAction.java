package org.hisp.dhis.i18n.action;

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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.setting.SystemSetting;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class GetStringsFromLocaleAction
    implements Action
{    
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_ACCOUNT = "account";
    private static final String PAGE_RECOVERY = "recovery";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private I18nManager manager;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private String language;

    public void setLanguage( String language )
    {
        this.language = language;
    }

    private String country;

    public void setCountry( String country )
    {
        this.country = country;
    }

    private String page;

    public void setPage( String page )
    {
        this.page = page;
    }

    private I18n i18nObject;

    public I18n getI18nObject()
    {
        return i18nObject;
    }

    private Map<String, String> translations = new Hashtable<String, String>();

    public Map<String, String> getTranslations()
    {
        return translations;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        // Resource Locale
        Locale locale;

        if ( language != null )
        {
            if ( country != null )
            {
                locale = new Locale( language, country );
            }
            else
            {
                locale = new Locale( language );
            }
        }
        else
        {
            locale = null;
        }

        i18nObject = manager.getI18n( this.getClass(), locale );

        
        // Database Locale

        //if ( page.compareTo( PAGE_LOGIN) == 0 ) 
        
        // TODO - i18nLocale create it by using i18nLocaleService?         
        I18nLocale i18nLocale = new I18nLocale("", language, I18nLocale.DEFAULT_COUNTRY);
                        
        translations = convertTranslations_SystemSetting( translationService.getTranslations( SystemSetting.class.getSimpleName(), i18nLocale ) );
                        
        return SUCCESS;
    }

    // TODO: Place in DefaultI18nService??  Or here?
    private Map<String, String> convertTranslations_SystemSetting( Collection<Translation> translations )
    {

        Map<String, String> translationMap = new Hashtable<String, String>();

        for ( Translation translation : translations )
        {
                        
            if ( translation.getProperty() != null 
                && translation.getProperty().compareTo( SystemSetting.SYSTEMSETTING_PROPERTY_VALUE ) == 0
                && translation.getValue() != null )
            {
                
                SystemSetting systemSetting = systemSettingManager.getSystemSettingObject( translation.getId() );
                
                if( systemSetting != null )
                {
                    translationMap.put( systemSetting.getName(), translation.getValue() );
                }                
            }
        }

        return translationMap;
    }
    
    
    
    
}
