package org.hisp.dhis.mapping;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.adapter.JacksonPeriodDeserializer;
import org.hisp.dhis.common.adapter.JacksonPeriodSerializer;
import org.hisp.dhis.common.adapter.JacksonPeriodTypeDeserializer;
import org.hisp.dhis.common.adapter.JacksonPeriodTypeSerializer;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;

/**
 * @author Jan Henrik Overland
 */
@JacksonXmlRootElement( localName = "map", namespace = Dxf2Namespace.NAMESPACE )
public class MapView
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 1866358818802275436L;

    private User user;

    private String valueType;

    private IndicatorGroup indicatorGroup;

    private Indicator indicator;

    private DataElementGroup dataElementGroup;

    private DataElement dataElement;

    private PeriodType periodType;

    private Period period;

    private OrganisationUnit parentOrganisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;
    
    private String legendType;

    private Integer method;

    private Integer classes;

    private String colorLow;

    private String colorHigh;

    private MapLegendSet legendSet;

    private Integer radiusLow;

    private Integer radiusHigh;

    private String longitude;

    private String latitude;

    private Integer zoom;
    
    private Integer opacity;
    
    private OrganisationUnitGroupSet organisationUnitGroupSet;

    public MapView()
    {
    }

    public MapView( String name, User user, String valueType, IndicatorGroup indicatorGroup, Indicator indicator,
                    DataElementGroup dataElementGroup, DataElement dataElement, PeriodType periodType,
                    Period period, OrganisationUnit parentOrganisationUnit, OrganisationUnitLevel organisationUnitLevel,
                    String legendType, Integer method, Integer classes, String colorLow, String colorHigh,
                    MapLegendSet legendSet, Integer radiusLow, Integer radiusHigh, String longitude, String latitude, int zoom, int opacity )
    {
        this.name = name;
        this.user = user;
        this.valueType = valueType;
        this.indicatorGroup = indicatorGroup;
        this.indicator = indicator;
        this.dataElementGroup = dataElementGroup;
        this.dataElement = dataElement;
        this.periodType = periodType;
        this.period = period;
        this.parentOrganisationUnit = parentOrganisationUnit;
        this.organisationUnitLevel = organisationUnitLevel;
        this.legendType = legendType;
        this.method = method;
        this.classes = classes;
        this.colorLow = colorLow;
        this.colorHigh = colorHigh;
        this.legendSet = legendSet;
        this.radiusLow = radiusLow;
        this.radiusHigh = radiusHigh;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zoom = zoom;
        this.opacity = opacity;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final MapView other = (MapView) object;

        return name.equals( other.name );
    }

    @Override
    public String toString()
    {
        return "[Name: " + name + ", indicator: " + indicator + ", org unit: " +
            parentOrganisationUnit + ", period: " + period + ", value type: " + valueType + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public IndicatorGroup getIndicatorGroup()
    {
        return indicatorGroup;
    }

    public void setIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        this.indicatorGroup = indicatorGroup;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public DataElementGroup getDataElementGroup()
    {
        return dataElementGroup;
    }

    public void setDataElementGroup( DataElementGroup dataElementGroup )
    {
        this.dataElementGroup = dataElementGroup;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    @JsonProperty
    @JsonSerialize( using = JacksonPeriodTypeSerializer.class )
    @JsonDeserialize( using = JacksonPeriodTypeDeserializer.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    @JsonProperty
    @JsonSerialize( using = JacksonPeriodSerializer.class )
    @JsonDeserialize( using = JacksonPeriodDeserializer.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public OrganisationUnit getParentOrganisationUnit()
    {
        return parentOrganisationUnit;
    }

    public void setParentOrganisationUnit( OrganisationUnit parentOrganisationUnit )
    {
        this.parentOrganisationUnit = parentOrganisationUnit;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public OrganisationUnitLevel getOrganisationUnitLevel()
    {
        return organisationUnitLevel;
    }

    public void setOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getLegendType()
    {
        return legendType;
    }

    public void setLegendType( String legendType )
    {
        this.legendType = legendType;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getMethod()
    {
        return method;
    }

    public void setMethod( Integer method )
    {
        this.method = method;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getClasses()
    {
        return classes;
    }

    public void setClasses( Integer classes )
    {
        this.classes = classes;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getColorLow()
    {
        return colorLow;
    }

    public void setColorLow( String colorLow )
    {
        this.colorLow = colorLow;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getColorHigh()
    {
        return colorHigh;
    }

    public void setColorHigh( String colorHigh )
    {
        this.colorHigh = colorHigh;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public MapLegendSet getLegendSet()
    {
        return legendSet;
    }

    public void setLegendSet( MapLegendSet legendSet )
    {
        this.legendSet = legendSet;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getRadiusLow()
    {
        return radiusLow;
    }

    public void setRadiusLow( Integer radiusLow )
    {
        this.radiusLow = radiusLow;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getRadiusHigh()
    {
        return radiusHigh;
    }

    public void setRadiusHigh( Integer radiusHigh )
    {
        this.radiusHigh = radiusHigh;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude( String longitude )
    {
        this.longitude = longitude;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude( String latitude )
    {
        this.latitude = latitude;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getZoom()
    {
        return zoom;
    }

    public void setZoom( Integer zoom )
    {
        this.zoom = zoom;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public Integer getOpacity()
    {
        return opacity;
    }

    public void setOpacity( Integer opacity )
    {
        this.opacity = opacity;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public OrganisationUnitGroupSet getOrganisationUnitGroupSet()
    {
        return organisationUnitGroupSet;
    }

    public void setOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        this.organisationUnitGroupSet = organisationUnitGroupSet;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            MapView mapView = (MapView) other;

            user = mapView.getUser() == null ? user : mapView.getUser();
            valueType = mapView.getValueType() == null ? valueType : mapView.getValueType();
            indicatorGroup = mapView.getIndicatorGroup() == null ? indicatorGroup : mapView.getIndicatorGroup();
            indicator = mapView.getIndicator() == null ? indicator : mapView.getIndicator();
            dataElementGroup = mapView.getDataElementGroup() == null ? dataElementGroup : mapView.getDataElementGroup();
            dataElement = mapView.getDataElement() == null ? dataElement : mapView.getDataElement();
            periodType = mapView.getPeriodType() == null ? periodType : mapView.getPeriodType();
            period = mapView.getPeriod() == null ? period : mapView.getPeriod();
            parentOrganisationUnit = mapView.getParentOrganisationUnit() == null ? parentOrganisationUnit : mapView.getParentOrganisationUnit();
            organisationUnitLevel = mapView.getOrganisationUnitLevel() == null ? organisationUnitLevel : mapView.getOrganisationUnitLevel();
            legendType = mapView.getLegendType() == null ? legendType : mapView.getLegendType();
            method = mapView.getMethod() == null ? method : mapView.getMethod();
            classes = mapView.getClasses() == null ? classes : mapView.getClasses();
            colorLow = mapView.getColorLow() == null ? colorLow : mapView.getColorLow();
            colorHigh = mapView.getColorHigh() == null ? colorHigh : mapView.getColorHigh();
            legendSet = mapView.getLegendSet() == null ? legendSet : mapView.getLegendSet();
            radiusLow = mapView.getRadiusLow() == null ? radiusLow : mapView.getRadiusLow();
            radiusHigh = mapView.getRadiusHigh() == null ? radiusHigh : mapView.getRadiusHigh();
            longitude = mapView.getLongitude() == null ? longitude : mapView.getLongitude();
            latitude = mapView.getLatitude() == null ? latitude : mapView.getLatitude();
            zoom = mapView.getZoom() == null ? zoom : mapView.getZoom();
        }
    }
}
