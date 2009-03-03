package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.Map;

import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractGroupMemberConverter;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;

/**
 * @author Lars Helge Overland
 * @version $Id: DataSetMemberConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataSetMemberConverter
    extends AbstractGroupMemberConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "dataSetMembers";
    public static final String ELEMENT_NAME = "dataSetMember";
    
    private static final String FIELD_DATAELEMENT = "dataElement";
    private static final String FIELD_DATASET = "dataSet";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> dataElementMapping;
    
    private Map<Object, Integer> dataSetMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataSetMemberConverter()
    {   
    }
    
    /**
     * Constructor for read operations.
     */
    public DataSetMemberConverter( BatchHandler batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> dataElementMapping, 
        Map<Object, Integer> dataSetMapping )
    {   
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataElementMapping = dataElementMapping;
        this.dataSetMapping = dataSetMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataSet> dataSets = params.getDataSets();
        
        if ( dataSets != null && dataSets.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getDataElements() != null )
                {
                    for ( DataElement element : dataSet.getDataElements() )
                    {
                        if ( params.getDataElements() != null && params.getDataElements().contains( element ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_DATASET, String.valueOf( dataSet.getId() ) );
                            writer.writeElement( FIELD_DATAELEMENT, String.valueOf( element.getId() ) );
                            
                            writer.closeElement();
                        }
                    }
                }
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );
            
            reader.moveToStartElement( FIELD_DATASET );
            association.setGroupId( dataSetMapping.get( Integer.parseInt( reader.getElementValue() ) ) );
            
            reader.moveToStartElement( FIELD_DATAELEMENT );
            association.setMemberId( dataElementMapping.get( Integer.parseInt( reader.getElementValue() ) ) );
            
            read( association, GroupMemberType.DATASET, params );
        }
    }
}
