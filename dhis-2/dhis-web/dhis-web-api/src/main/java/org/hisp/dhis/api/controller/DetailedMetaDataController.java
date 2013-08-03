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
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.metadata.FilterOptions;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.filter.Filter;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Qualifier( "contextUtils" )
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

    @RequestMapping( value = {DetailedMetaDataController.RESOURCE_PATH + ".zip"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZipped( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
    }

    @RequestMapping( value = {DetailedMetaDataController.RESOURCE_PATH + ".gz"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZipped( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/setXml", headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".xml";
        detailedMetaDataString = processMetaData( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/setJson", headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportJson( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".json";
        detailedMetaDataString = processMetaData( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = {DetailedMetaDataController.RESOURCE_PATH + "/setXmlZip"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".xml.zip";
        detailedMetaDataString = processMetaData( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = {DetailedMetaDataController.RESOURCE_PATH + "/setJsonZip"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".json.zip";
        detailedMetaDataString = processMetaData( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = {DetailedMetaDataController.RESOURCE_PATH + "/setXmlGz"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedXML( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".xml.gz";
        detailedMetaDataString = processMetaData( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = {DetailedMetaDataController.RESOURCE_PATH + "/setJsonGz"}, headers = "Accept=application/json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedJSON( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        format = ".json.gz";
        detailedMetaDataString = processMetaData( json );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - GET Requests
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, value = DetailedMetaDataController.RESOURCE_PATH + "/getMetaDataFile" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void getMetaDataExportFile( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String fileName = "metaData" + format;
        processFileOutput( response, fileName );
    }

    @RequestMapping( method = RequestMethod.GET, value = DetailedMetaDataController.RESOURCE_PATH + "/getFilters" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public @ResponseBody String getFilters( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        List<Filter> filters = exportService.getFilters();
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE );
        return JacksonUtils.toJsonAsString( filters );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - Filter functionality
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/saveFilter" )
    public void saveFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.saveFilter( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/updateFilter" )
    public void updateFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.updateFilter( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = DetailedMetaDataController.RESOURCE_PATH + "/deleteFilter" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void deleteFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.deleteFilter( json );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - Logic
    //--------------------------------------------------------------------------

    private String processMetaData( JSONObject json ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions();
        filterOptions.addFilterRestrictions( filterOptions.processJSON( json ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );
        if ( format.contains( ".xml" ) )
        {
            return detailedMetaDataString = JacksonUtils.toXmlAsString( metaData );
        }
        if ( format.contains( ".json" ) )
        {
            return detailedMetaDataString = JacksonUtils.toJsonAsString( metaData );
        }
        return "";
    }

    private void processFileOutput( HttpServletResponse response, String fileName ) throws IOException
    {
        if ( format.contains( ".zip" ) )
        {
            String zipFileName = fileName.replace( ".zip", "" );
            contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_ZIP, ContextUtils.CacheStrategy.NO_CACHE, fileName, true );
            response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
            zip.putNextEntry( new ZipEntry( zipFileName ) );
            writeToOutputStream( zip, detailedMetaDataString.getBytes() );
        } else if ( format.contains( ".gz" ) )
        {
            contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_GZIP, ContextUtils.CacheStrategy.NO_CACHE, fileName, true );
            response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            GZIPOutputStream gzip = new GZIPOutputStream( response.getOutputStream() );
            writeToOutputStream( gzip, detailedMetaDataString.getBytes() );
        } else
        {
            contextUtils.configureResponse( response, ContextUtils.HEADER_CONTENT_DISPOSITION, ContextUtils.CacheStrategy.NO_CACHE, fileName, true );
            OutputStream outputStream = response.getOutputStream();
            writeToOutputStream( outputStream, detailedMetaDataString.getBytes() );
        }
    }

    private static void writeToOutputStream( OutputStream outputStream, byte[] data ) throws IOException
    {
        try
        {
            outputStream.write( data );
            outputStream.flush();
        } catch ( Exception e )
        {
            log.info( "Error: " + e );
        } finally
        {
            outputStream.close();
        }
    }
}
