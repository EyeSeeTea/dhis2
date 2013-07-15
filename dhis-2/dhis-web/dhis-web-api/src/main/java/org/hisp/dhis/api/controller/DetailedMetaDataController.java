package org.hisp.dhis.api.controller;

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

import net.sf.json.JSONObject;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.metadata.FilterOptions;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;
import java.util.Map;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
@Controller
@RequestMapping( method = RequestMethod.POST )
public class DetailedMetaDataController
{
    public static final String RESOURCE_PATH = "/detailedMetaData";

    @Autowired
    private ExportService exportService;

    @Autowired
    private ImportService importService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private CurrentUserService currentUserService;



    //--------------------------------------------------------------------------
    // Detailed MetaData Export
    //--------------------------------------------------------------------------

    @RequestMapping( value = DetailedMetaDataController.RESOURCE_PATH, headers = "Accept=application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void detailedExport( @RequestBody String requestJson, Model model ) throws IOException
    {
    }

    @RequestMapping( value = DetailedMetaDataController.RESOURCE_PATH + ".xml", headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public String exportXml( @RequestBody String requestJson, HttpServletResponse response ) throws IOException
    {
        JSONObject json = JSONObject.fromObject( requestJson );
        FilterOptions filterOptions = new FilterOptions();
        filterOptions.addFilterOptions( filterOptions.processJSON( json ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

//        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, ContextUtils.CacheStrategy.NO_CACHE, "detailedMetaData.xml", true );
//
//        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, ExportView.class );

        return JacksonUtils.toXmlWithViewAsString( metaData, ExportView.class );
    }

    @RequestMapping( value = DetailedMetaDataController.RESOURCE_PATH + ".json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportJson( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".zip" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZipped( @RequestParam Map<String, String> parameters, HttpServletResponse response, HttpServletRequest request ) throws IOException
    {

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".xml.zip" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".json.zip" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        System.out.println("\n 1. DETAILED EXPORT ENTERED \n");

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".gz" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZipped( @RequestParam Map<String, String> parameters, HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        System.out.println("\n 1. DETAILED EXPORT ENTERED \n");

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".xml.gz" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedXML( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        System.out.println("\n 1. DETAILED EXPORT ENTERED \n");

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".json.gz" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedJSON( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        System.out.println("\n 1. DETAILED EXPORT ENTERED \n");

    }
}
