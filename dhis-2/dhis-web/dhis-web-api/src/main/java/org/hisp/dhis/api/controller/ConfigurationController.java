package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import javax.servlet.http.HttpServletRequest;

import org.hisp.dhis.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( "/configuration" )
public class ConfigurationController
{
    @Autowired
    private ConfigurationService configurationService;
    
    @RequestMapping( value = "/systemId", method = RequestMethod.GET )
    private String getSystemId( Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getSystemId() );
        return "config";
    }

    @RequestMapping( value = "/feedbackRecipients", method = RequestMethod.GET )
    private String getFeedbackRecipients(  Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getFeedbackRecipients() );
        return "config";
    }
    
    @RequestMapping( value = "/offlineOrganisationUnitLevel", method = RequestMethod.GET )
    private String getOfflineOrganisationUnitLevel(  Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getOfflineOrganisationUnitLevel() );
        return "config";
    }

    @RequestMapping( value = "/infrastructuralDataElements", method = RequestMethod.GET )
    private String getInfrastructuralDataElements( Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getInfrastructuralDataElements() );
        return "config";
    }

    @RequestMapping( value = "/infrastructuralPeriodType", method = RequestMethod.GET )
    private String getInfrastructuralPeriodType( Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getInfrastructuralPeriodType() );
        return "config";
    }

    @RequestMapping( value = "/selfRegistrationRole", method = RequestMethod.GET )
    private String getSelfRegistrationRole( Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getSelfRegistrationRole() );
        return "config";
    }

    @RequestMapping( value = "/selfRegistrationOrgUnit", method = RequestMethod.GET )
    private String getSelfRegistrationOrgUnit( Model model, HttpServletRequest request )
    {
        setModel( model, configurationService.getConfiguration().getSelfRegistrationOrgUnit() );
        return "config";
    }

    private void setModel( Model model, Object entity )
    {
        model.addAttribute( "model", entity );
        model.addAttribute( "viewClass", "detailed" );
    }
}
