package org.hisp.dhis.dxf2.metadata;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.mapping.*;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ovidiu Rosu <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "filter", namespace = DxfNamespaces.DXF_2_0 )
public class Filter
{
    private Date created;

    private List<String> attributeTypesUids = new ArrayList<String>();

    private List<String> dataElementsUids = new ArrayList<String>();

    public Filter()
    {
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    public List<String> getAttributeTypesUids()
    {
        return attributeTypesUids;
    }

    public void setAttributeTypesUids( List<String> attributeTypesUids )
    {
        this.attributeTypesUids = attributeTypesUids;
    }

    public List<String> getDataElementsUids()
    {
        return dataElementsUids;
    }

    public void setDataElementsUids( List<String> dataElementsUids )
    {
        this.dataElementsUids = dataElementsUids;
    }
}
