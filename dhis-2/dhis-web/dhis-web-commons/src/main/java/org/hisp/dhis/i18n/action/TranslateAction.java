package org.hisp.dhis.i18n.action;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.i18n.locale.I18nLocaleService;

import com.opensymphony.xwork2.Action;

/**
 * @author Oyvind Brucker
 * @modifier Dang Duy Hieu
 */
public class TranslateAction
    implements Action
{
    private static final Log log = LogFactory.getLog( TranslateAction.class );

    private String className;

    private String objectId;

    private String loc;

    private String returnUrl;

    private String message;

    private String locale;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    private I18nLocaleService i18nLocaleService;

    public void setI18nLocaleService( I18nLocaleService i18nLocaleService )
    {
        this.i18nLocaleService = i18nLocaleService;
    }

    private IdentifiableObjectManager identifiableObjectManager;

    public void setIdentifiableObjectManager( IdentifiableObjectManager identifiableObjectManager )
    {
        this.identifiableObjectManager = identifiableObjectManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
    }

    public void setLoc( String loc )
    {
        this.loc = loc;
    }

    public void setReturnUrl( String returnUrl )
    {
        this.returnUrl = returnUrl;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getClassName()
    {
        return className;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getLocale()
    {
        return locale;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        I18nLocale localeObj = null;

        if ( loc != null && !loc.equals( "NONE" ) )
        {
            localeObj = i18nLocaleService.getI18nLocale( Integer.valueOf( loc ) );

            locale = localeObj.getName();
        }

        log.info( "Classname: " + className + ", id: " + objectId + ", loc: " + locale );

        IdentifiableObject object = identifiableObjectManager.getObject( Integer.parseInt( objectId ), className );

        List<String> propertyNames = i18nService.getObjectPropertyNames( object );

        HttpServletRequest request = ServletActionContext.getRequest();

        Map<String, String> translations = new Hashtable<String, String>();

        for ( String propertyName : propertyNames )
        {
            String[] translation = request.getParameterValues( propertyName );

            if ( translation != null && translation.length > 0 && translation[0] != null
                && !translation[0].trim().isEmpty() )
            {
                translations.put( propertyName, translation[0] );
            }
        }

        log.info( "Translations: " + translations );

        if ( localeObj != null )
        {
            i18nService.updateTranslation( className, Integer.parseInt( objectId ), localeObj, translations );
        }

        return SUCCESS;
    }
}
