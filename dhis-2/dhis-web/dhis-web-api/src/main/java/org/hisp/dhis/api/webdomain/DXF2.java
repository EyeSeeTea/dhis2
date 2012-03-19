package org.hisp.dhis.api.webdomain;

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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class DXF2
{
    private List<User> users = new ArrayList<User>();

    private List<UserAuthorityGroup> userAuthorityGroups = new ArrayList<UserAuthorityGroup>();

    private List<UserGroup> userGroups = new ArrayList<UserGroup>();

    private List<MessageConversation> messageConversations = new ArrayList<MessageConversation>();

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private List<OptionSet> optionSets = new ArrayList<OptionSet>();

    private List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

    private List<DataElementGroupSet> dataElementGroupSets = new ArrayList<DataElementGroupSet>();

    private List<Concept> concepts = new ArrayList<Concept>();

    private List<DataElementCategory> categories = new ArrayList<DataElementCategory>();

    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    private List<DataElementCategoryCombo> categoryCombos = new ArrayList<DataElementCategoryCombo>();

    private List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    private List<Indicator> indicators = new ArrayList<Indicator>();

    private List<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();

    private List<IndicatorGroupSet> indicatorGroupSets = new ArrayList<IndicatorGroupSet>();

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    private List<OrganisationUnitGroupSet> organisationUnitGroupSets = new ArrayList<OrganisationUnitGroupSet>();

    private List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<OrganisationUnitLevel>();

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    private List<ValidationRule> validationRules = new ArrayList<ValidationRule>();

    private List<ValidationRuleGroup> validationRuleGroups = new ArrayList<ValidationRuleGroup>();

    private List<SqlView> sqlViews = new ArrayList<SqlView>();

    private List<Chart> charts = new ArrayList<Chart>();

    private List<Report> reports = new ArrayList<Report>();

    private List<ReportTable> reportTables = new ArrayList<ReportTable>();

    private List<Document> documents = new ArrayList<Document>();

    private List<Constant> constants = new ArrayList<Constant>();

    private List<MapView> maps = new ArrayList<MapView>();

    private List<MapLegend> mapLegends = new ArrayList<MapLegend>();

    private List<MapLegendSet> mapLegendSets = new ArrayList<MapLegendSet>();

    public DXF2()
    {
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "users" )
    @JacksonXmlProperty( localName = "user" )
    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers( List<User> users )
    {
        this.users = users;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userAuthorityGroups" )
    @JacksonXmlProperty( localName = "userAuthorityGroup" )
    public List<UserAuthorityGroup> getUserAuthorityGroups()
    {
        return userAuthorityGroups;
    }

    public void setUserAuthorityGroups( List<UserAuthorityGroup> userAuthorityGroups )
    {
        this.userAuthorityGroups = userAuthorityGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userGroups" )
    @JacksonXmlProperty( localName = "userGroup" )
    public List<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( List<UserGroup> userGroups )
    {
        this.userGroups = userGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "messageConversations" )
    @JacksonXmlProperty( localName = "messageConversation" )
    public List<MessageConversation> getMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( List<MessageConversation> messageConversations )
    {
        this.messageConversations = messageConversations;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElements" )
    @JacksonXmlProperty( localName = "dataElement" )
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "optionSets" )
    @JacksonXmlProperty( localName = "optionSet" )
    public List<OptionSet> getOptionSets()
    {
        return optionSets;
    }

    public void setOptionSets( List<OptionSet> optionSets )
    {
        this.optionSets = optionSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroups" )
    @JacksonXmlProperty( localName = "dataElementGroup" )
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( List<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroupSets" )
    @JacksonXmlProperty( localName = "dataElementGroupSet" )
    public List<DataElementGroupSet> getDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    public void setDataElementGroupSets( List<DataElementGroupSet> dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "concepts" )
    @JacksonXmlProperty( localName = "concept" )
    public List<Concept> getConcepts()
    {
        return concepts;
    }

    public void setConcepts( List<Concept> concepts )
    {
        this.concepts = concepts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categories" )
    @JacksonXmlProperty( localName = "category" )
    public List<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( List<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptions" )
    @JacksonXmlProperty( localName = "categoryOption" )
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryCombos" )
    @JacksonXmlProperty( localName = "categoryCombo" )
    public List<DataElementCategoryCombo> getCategoryCombos()
    {
        return categoryCombos;
    }

    public void setCategoryCombos( List<DataElementCategoryCombo> categoryCombos )
    {
        this.categoryCombos = categoryCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptionCombos" )
    @JacksonXmlProperty( localName = "categoryOptionCombo" )
    public List<DataElementCategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( List<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicators" )
    @JacksonXmlProperty( localName = "indicator" )
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroups" )
    @JacksonXmlProperty( localName = "indicatorGroup" )
    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    public void setIndicatorGroups( List<IndicatorGroup> indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroupSets" )
    @JacksonXmlProperty( localName = "indicatorGroupSet" )
    public List<IndicatorGroupSet> getIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    public void setIndicatorGroupSets( List<IndicatorGroupSet> indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnits" )
    @JacksonXmlProperty( localName = "organisationUnit" )
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroups" )
    @JacksonXmlProperty( localName = "organisationUnitGroup" )
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroupSets" )
    @JacksonXmlProperty( localName = "organisationUnitGroupSet" )
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( List<OrganisationUnitGroupSet> organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitLevels" )
    @JacksonXmlProperty( localName = "organisationUnitLevel" )
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( List<OrganisationUnitLevel> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataSets" )
    @JacksonXmlProperty( localName = "dataSet" )
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRules" )
    @JacksonXmlProperty( localName = "validationRule" )
    public List<ValidationRule> getValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( List<ValidationRule> validationRules )
    {
        this.validationRules = validationRules;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRuleGroups" )
    @JacksonXmlProperty( localName = "validationRuleGroup" )
    public List<ValidationRuleGroup> getValidationRuleGroups()
    {
        return validationRuleGroups;
    }

    public void setValidationRuleGroups( List<ValidationRuleGroup> validationRuleGroups )
    {
        this.validationRuleGroups = validationRuleGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "sqlViews" )
    @JacksonXmlProperty( localName = "sqlView" )
    public List<SqlView> getSqlViews()
    {
        return sqlViews;
    }

    public void setSqlViews( List<SqlView> sqlViews )
    {
        this.sqlViews = sqlViews;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "charts" )
    @JacksonXmlProperty( localName = "chart" )
    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( List<Chart> charts )
    {
        this.charts = charts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reports" )
    @JacksonXmlProperty( localName = "report" )
    public List<Report> getReports()
    {
        return reports;
    }

    public void setReports( List<Report> reports )
    {
        this.reports = reports;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reportTables" )
    @JacksonXmlProperty( localName = "reportTable" )
    public List<ReportTable> getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( List<ReportTable> reportTables )
    {
        this.reportTables = reportTables;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "documents" )
    @JacksonXmlProperty( localName = "document" )
    public List<Document> getDocuments()
    {
        return documents;
    }

    public void setDocuments( List<Document> documents )
    {
        this.documents = documents;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "constants" )
    @JacksonXmlProperty( localName = "constant" )
    public List<Constant> getConstants()
    {
        return constants;
    }

    public void setConstants( List<Constant> constants )
    {
        this.constants = constants;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "maps" )
    @JacksonXmlProperty( localName = "map" )
    public List<MapView> getMaps()
    {
        return maps;
    }

    public void setMaps( List<MapView> maps )
    {
        this.maps = maps;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegends" )
    @JacksonXmlProperty( localName = "mapLegend" )
    public List<MapLegend> getMapLegends()
    {
        return mapLegends;
    }

    public void setMapLegends( List<MapLegend> mapLegends )
    {
        this.mapLegends = mapLegends;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegendSets" )
    @JacksonXmlProperty( localName = "mapLegendSet" )
    public List<MapLegendSet> getMapLegendSets()
    {
        return mapLegendSets;
    }

    public void setMapLegendSets( List<MapLegendSet> mapLegendSets )
    {
        this.mapLegendSets = mapLegendSets;
    }
}
