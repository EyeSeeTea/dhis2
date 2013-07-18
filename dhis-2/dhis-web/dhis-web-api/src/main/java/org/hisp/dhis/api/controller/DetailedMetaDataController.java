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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.metadata.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
@Controller
public class DetailedMetaDataController
{
    public static final String RESOURCE_PATH = "/detailedMetaData";

    private static final Log log = LogFactory.getLog( DetailedMetaDataController.class );

    @Autowired
    private ExportService exportService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private CurrentUserService currentUserService;

    private String detailedMetaDataString;

    private String format;

    //--------------------------------------------------------------------------
    // Getters & Setters
    //--------------------------------------------------------------------------

    public String getFormat()
    {
        return format;
    }

    public void setFormat( String format )
    {
        this.format = format;
    }

    public String getDetailedMetaDataString()
    {
        return detailedMetaDataString;
    }

    public void setDetailedMetaDataString( String detailedMetaDataString )
    {
        this.detailedMetaDataString = detailedMetaDataString;
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - POST Requests
    //--------------------------------------------------------------------------

    @RequestMapping( value = DetailedMetaDataController.RESOURCE_PATH, headers = "Accept=application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void detailedExport( @RequestBody JSONObject json, Model model ) throws IOException
    {
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/setXml",
            headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestBody JSONObject json ) throws IOException
    {
        Filter filter = new Filter();
        filter.addOptions( filter.processJSON( json ) );
        MetaData metaData = exportService.getFilteredMetaData( filter );

        format = ".xml";
        detailedMetaDataString = JacksonUtils.toXmlAsString( metaData );
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/setJson",
            headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportJson( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        Filter filter = new Filter();
        filter.addOptions( filter.processJSON( json ) );
        MetaData metaData = exportService.getFilteredMetaData( filter );

        format = ".json";
        detailedMetaDataString = JacksonUtils.toJsonAsString( metaData );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - GET Requests
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, value = DetailedMetaDataController.RESOURCE_PATH + "/getMetaDataFile" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void getExportXml( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String fileName = "metaData" + format;
        contextUtils.configureResponse( response, ContextUtils.HEADER_CONTENT_DISPOSITION, ContextUtils.CacheStrategy.NO_CACHE, fileName, true );
        OutputStream outputStream = response.getOutputStream();

        try {
            outputStream.write( detailedMetaDataString.getBytes() );
            outputStream.flush();

        } catch ( Exception e )
        {
            log.info( "Error: " + e );

        } finally
        {
            outputStream.close();
        }
    }







    // TODO

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".zip" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZipped( @RequestBody JSONObject json, HttpServletResponse response, HttpServletRequest request ) throws IOException
    {

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".xml.zip" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {

    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".json.zip" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".gz" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZipped( @RequestBody JSONObject json, HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".xml.gz" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedXML( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
    }

    @RequestMapping( value = { DetailedMetaDataController.RESOURCE_PATH + ".json.gz" }, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedJSON( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
    }
}
