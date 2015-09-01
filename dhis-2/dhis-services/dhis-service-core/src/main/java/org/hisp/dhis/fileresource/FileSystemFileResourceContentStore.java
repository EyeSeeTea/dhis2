package org.hisp.dhis.fileresource;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.hisp.dhis.external.location.LocationManager;
import org.jclouds.filesystem.reference.FilesystemConstants;

import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Halvdan Hoem Grelland
 */
public class FileSystemFileResourceContentStore
    extends BaseJCloudsFileResourceContentStore
{
    private static final String JCLOUDS_FILESYSTEM_PROVIDER_KEY = "filesystem";

    private String container;

    private String rootLocation;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    FileSystemFileResourceContentStore( String rootLocation, String container )
    {
        this.rootLocation = rootLocation;
        this.container = container;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    @Override
    protected String getContainer()
    {
        return container;
    }

    @Override
    protected String getRootLocation()
    {
        return rootLocation;
    }

    @Override
    protected Properties getOverrides()
    {
        Properties properties = new Properties();

        String fileStoreRootDir = Paths.get( locationManager.getExternalDirectoryPath(), getRootLocation() ).toString();
        properties.setProperty( FilesystemConstants.PROPERTY_BASEDIR, fileStoreRootDir );

        return properties;
    }

    @Override
    protected String getJCloudsProviderKey()
    {
        return JCLOUDS_FILESYSTEM_PROVIDER_KEY;
    }
}
