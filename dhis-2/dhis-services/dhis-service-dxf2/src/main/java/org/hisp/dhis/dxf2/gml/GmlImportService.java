package org.hisp.dhis.dxf2.gml;

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

import org.hisp.dhis.dxf2.common.ImportOptions;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.scheduling.TaskId;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles the transformation, sanitation and merging of geospatial
 * data for OrganisationUnits through processing and importing GML files.
 *
 * @author Halvdan Hoem Grelland
 */
public interface GmlImportService
{
    String ID = GmlImportService.class.getName();

    /**
     * Transform a GML document to MetaData containing the relevant updates
     * to geospatial features (e.g. coordinates, featuretypes). The process
     * filters the input against the database and merges in essential fields
     * needed for the meta data importer to only update the geospatial fields
     * and not nullify any 'missing' fields.
     *
     * @param inputStream the GML document to import.
     * @return a MetaData object reflecting the database content with the GML file changes merged in.
     * @throws IOException on failure to read the InputStream.
     * @throws TransformerException on failure to parse and transform the GML content.
     */
    MetaData fromGml( InputStream inputStream )
        throws IOException, TransformerException;

    /**
     * Imports GML data and merges the geospatial data updates into the database.
     * See {@link #fromGml(InputStream)} for details on the underlying process.
     *
     * @param inputStream the GML document to import.
     * @param userUid the UID of the user performing the import (task owner).
     * @param importOptions the ImportOptions for the MetaData importer.
     * @param taskId the TaskId of the process.
     * @throws IOException on failure to read the InputStream.
     * @throws TransformerException on failure to parse and transform the GML content.
     */
    void importGml( InputStream inputStream, String userUid, ImportOptions importOptions, TaskId taskId )
        throws IOException, TransformerException;

    /**
     * Imports a MetaData object containing geospatial updates.
     * The MetaData should be retrieved using {@link #fromGml(InputStream)}.
     *
     * @param metaData the MetaData reflecting the geospatial updates.
     * @param userUid the UID of the user performing the import (task owner).
     * @param importOptions the ImportOptions for the MetaData importer.
     * @param taskId the TaskId of the process.
     */
    void importGml( MetaData metaData, String userUid, ImportOptions importOptions, TaskId taskId );
}
