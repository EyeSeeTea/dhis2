package org.hisp.dhis.de.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitDataSetAssociationSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class PageInitAction
    implements Action
{
    private static final Comparator<DataSet> COMPARATOR_DATASET = new DataSetNameComparator();
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DataElement> significantZeros;

    public Collection<DataElement> getSignificantZeros()
    {
        return significantZeros;
    }
    
    private Collection<DataElement> dataElements;
    
    public Collection<DataElement> getDataElements()
    {
        return dataElements;
    }

    private Collection<Indicator> indicators;

    public Collection<Indicator> getIndicators()
    {
        return indicators;
    }
    
    private Collection<DataSet> dataSets;

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private List<List<DataSet>> dataSetAssociationSets;
    
    public List<List<DataSet>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    private Map<OrganisationUnit, Integer> organisationUnitAssociationSetMap;

    public Map<OrganisationUnit, Integer> getOrganisationUnitAssociationSetMap()
    {
        return organisationUnitAssociationSetMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        selectionManager.clearSelectedOrganisationUnits();
        
        significantZeros = dataElementService.getDataElementsByZeroIsSignificant( true );
        
        dataElements = dataElementService.getDataElementsWithDataSets();
        
        indicators = indicatorService.getIndicatorsWithDataSets();
        
        dataSets = dataSetService.getAllDataSets();
        
        OrganisationUnitDataSetAssociationSet organisationUnitSet = organisationUnitService.getOrganisationUnitDataSetAssociationSet();
        
        dataSetAssociationSets = organisationUnitSet.getDataSetAssociationSets();
        
        organisationUnitAssociationSetMap = organisationUnitSet.getOrganisationUnitAssociationSetMap();
        
        for ( Indicator indicator : indicators )
        {
            indicator.setExplodedNumerator( expressionService.explodeExpression( indicator.getNumerator() ) );
            indicator.setExplodedDenominator( expressionService.explodeExpression( indicator.getDenominator() ) );
        }

        return SUCCESS;
    }
}
