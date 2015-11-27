package org.hisp.dhis.eventchart;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.analytics.EventOutputType;
import org.hisp.dhis.chart.BaseChart;
import org.hisp.dhis.common.AnalyticsType;
import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DimensionalItemObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.EventAnalyticalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.MergeStrategy;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Jan Henrik Overland
 */
@JacksonXmlRootElement( localName = "eventChart", namespace = DxfNamespaces.DXF_2_0 )
public class EventChart
    extends BaseChart
    implements EventAnalyticalObject
{
    /**
     * Program. Required.
     */
    private Program program;

    /**
     * Program stage.
     */
    private ProgramStage programStage;

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * End date.
     */
    private Date endDate;

    /**
     * Data element value dimension.
     */
    private DataElement dataElementValueDimension;
    
    /**
     * Attribute value dimension.
     */
    private TrackedEntityAttribute attributeValueDimension;

    /**
     * Dimensions to crosstabulate / use as columns.
     */
    private List<String> columnDimensions = new ArrayList<>();

    /**
     * Dimensions to use as rows.
     */
    private List<String> rowDimensions = new ArrayList<>();

    /**
     * Indicates output type.
     */
    private EventOutputType outputType;

    /**
     * Indicates whether to collapse all data dimensions into a single dimension.
     */
    private boolean collapseDataDimensions;

    /**
     * Indicates whether to hide n/a data.
     */
    private boolean hideNaData;   
    
    // -------------------------------------------------------------------------
    // Analytical properties
    // -------------------------------------------------------------------------

    /**
     * Value dimension.
     */
    private transient DimensionalItemObject value;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EventChart()
    {
    }

    public EventChart( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // AnalyticalObject
    // -------------------------------------------------------------------------

    @Override
    public void init( User user, Date date, OrganisationUnit organisationUnit,
        List<OrganisationUnit> organisationUnitsAtLevel, List<OrganisationUnit> organisationUnitsInGroups,
        I18nFormat format )
    {
        this.user = user;
        this.format = format;
    }

    @Override
    public void populateAnalyticalProperties()
    {
        for ( String column : columnDimensions )
        {
            columns.add( getDimensionalObject( column ) );
        }

        for ( String row : rowDimensions )
        {
            rows.add( getDimensionalObject( row ) );
        }

        for ( String filter : filterDimensions )
        {
            filters.add( getDimensionalObject( filter ) );
        }
        
        value = ObjectUtils.firstNonNull( dataElementValueDimension, attributeValueDimension );
    }

    @Override
    public List<DimensionalItemObject> series()
    {
        String series = columnDimensions.get( 0 );

        DimensionalObject object = getDimensionalObject( series, relativePeriodDate, user, true,
            organisationUnitsAtLevel, organisationUnitsInGroups, format );

        DimensionalObjectUtils.setDimensionItemsForFilters( object, dataItemGrid, true );

        return object != null ? object.getItems() : null;
    }

    @Override
    public List<DimensionalItemObject> category()
    {
        String category = rowDimensions.get( 0 );

        DimensionalObject object = getDimensionalObject( category, relativePeriodDate, user, true,
            organisationUnitsAtLevel, organisationUnitsInGroups, format );

        DimensionalObjectUtils.setDimensionItemsForFilters( object, dataItemGrid, true );

        return object != null ? object.getItems() : null;
    }

    @Override
    public AnalyticsType getAnalyticsType()
    {
        return AnalyticsType.EVENT;
    }

    // -------------------------------------------------------------------------
    // Getters and setters properties
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @Override
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    @Override
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    @Override
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    @Override
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DataElement getDataElementValueDimension()
    {
        return dataElementValueDimension;
    }

    @Override
    public void setDataElementValueDimension( DataElement dataElementValueDimension )
    {
        this.dataElementValueDimension = dataElementValueDimension;
    }

    @Override
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityAttribute getAttributeValueDimension()
    {
        return attributeValueDimension;
    }

    @Override
    public void setAttributeValueDimension( TrackedEntityAttribute attributeValueDimension )
    {
        this.attributeValueDimension = attributeValueDimension;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "columnDimensions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "columnDimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getColumnDimensions()
    {
        return columnDimensions;
    }

    public void setColumnDimensions( List<String> columnDimensions )
    {
        this.columnDimensions = columnDimensions;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "rowDimensions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "rowDimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getRowDimensions()
    {
        return rowDimensions;
    }

    public void setRowDimensions( List<String> rowDimensions )
    {
        this.rowDimensions = rowDimensions;
    }

    @Override
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public EventOutputType getOutputType()
    {
        return outputType;
    }

    public void setOutputType( EventOutputType outputType )
    {
        this.outputType = outputType;
    }

    @Override
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isCollapseDataDimensions()
    {
        return collapseDataDimensions;
    }

    public void setCollapseDataDimensions( boolean collapseDataDimensions )
    {
        this.collapseDataDimensions = collapseDataDimensions;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isHideNaData()
    {
        return hideNaData;
    }

    public void setHideNaData( boolean hideNaData )
    {
        this.hideNaData = hideNaData;
    }

    // -------------------------------------------------------------------------
    // Analytical properties
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonDeserialize( as = BaseDimensionalItemObject.class )
    @JsonView( { DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DimensionalItemObject getValue()
    {
        return value;
    }

    public void setValue( DimensionalItemObject value )
    {
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Merge with
    // -------------------------------------------------------------------------

    @Override
    public void mergeWith( IdentifiableObject other, MergeStrategy strategy )
    {
        super.mergeWith( other, strategy );

        if ( other.getClass().isInstance( this ) )
        {
            EventChart chart = (EventChart) other;

            if ( strategy.isReplace() )
            {
                dataElementValueDimension = chart.getDataElementValueDimension();
                attributeValueDimension = chart.getAttributeValueDimension();
                program = chart.getProgram();
                programStage = chart.getProgramStage();
                startDate = chart.getStartDate();
                endDate = chart.getEndDate();
                outputType = chart.getOutputType();
            }
            else if ( strategy.isMerge() )
            {
                dataElementValueDimension = chart.getDataElementValueDimension() == null ? dataElementValueDimension : chart.getDataElementValueDimension();
                attributeValueDimension = chart.getAttributeValueDimension() == null ? attributeValueDimension : chart.getAttributeValueDimension();
                program = chart.getProgram() == null ? program : chart.getProgram();
                programStage = chart.getProgramStage() == null ? programStage : chart.getProgramStage();
                startDate = chart.getStartDate() == null ? startDate : chart.getStartDate();
                endDate = chart.getEndDate() == null ? endDate : chart.getEndDate();
                outputType = chart.getOutputType() == null ? outputType : chart.getOutputType();
            }

            columnDimensions.clear();
            columnDimensions.addAll( chart.getColumnDimensions() );

            rowDimensions.clear();
            rowDimensions.addAll( chart.getRowDimensions() );
        }
    }
}
