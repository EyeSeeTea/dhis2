package org.hisp.dhis.reports.dataset.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.DataEntryForm;
import org.hisp.dhis.dataset.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.order.manager.DataElementOrderManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reports.dataset.generators.CustomDataSetReportGenerator;
import org.hisp.dhis.reports.dataset.state.SelectedStateManager;
import org.hisp.dhis.reports.dataset.utils.NumberUtils;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GenerateCustomDataSetReportAction
    extends AbstractAction
{
    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------

    private DataElementOrderManager dataElementOrderManager;

    public void setDataElementOrderManager( DataElementOrderManager dataElementOrderManager )
    {
        this.dataElementOrderManager = dataElementOrderManager;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    private String reportingUnit;

    public String getReportingUnit()
    {
        return this.reportingUnit;
    }

    private String reportingPeriod;

    public String getReportingPeriod()
    {
        return this.reportingPeriod;
    }

    private String selectedUnitOnly;

    public void setSelectedUnitOnly( String selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }

    public String getSelectedUnitOnly()
    {
        return selectedUnitOnly;
    }

    private String organisationUnitGroupId;

    public void setOrganisationUnitGroupId( String organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }

    private List<OrganisationUnit> orgUnit;

    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        OrganisationUnitGroup orgUnitGroup;
        List<OrganisationUnit> orgGroupMembers = new ArrayList<OrganisationUnit>();
        
        if ( organisationUnitGroupId.equals( "ALL" ) || organisationUnitGroupId.equals( "Selected_Only") )
        {
            
        }
        else
        {
            orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer
                .parseInt( organisationUnitGroupId ) );
            
            orgGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        }
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitWithChildren( orgUnit.getId() ) );
        orgGroupMembers.retainAll( orgUnitList );

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        Period period = selectedStateManager.getSelectedPeriod();

        if ( orgUnit != null && dataSet != null && period != null )
        {
            Collection<DataElement> dataElements = dataElementOrderManager.getOrderedDataElements( dataSet );

            // CollectionUtils.filter( dataElements, new
            // AggregateableDataElementPredicate() );

            Map<String, String> aggregatedDataValueMap = new TreeMap<String, String>();

            for ( DataElement dataElement : dataElements )
            {
                DataElementCategoryCombo catCombo = dataElement.getCategoryCombo();

                for ( DataElementCategoryOptionCombo optionCombo : catCombo.getOptionCombos() )
                {
                    String value = null;
                    DataValue dataValue;

                    if ( dataElement.getType().equals( DataElement.TYPE_INT ) )
                    {
                        double aggregatedValue;

                        if ( organisationUnitGroupId.equals( "Selected_Only" ) )
                        {
                            dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, optionCombo );
                            value = (dataValue != null) ? dataValue.getValue() : "";
                        }
                        else if ( organisationUnitGroupId.equals( "ALL" ) )
                        {
                            aggregatedValue = dataMartStore.getAggregatedValue( dataElement, optionCombo, period,
                                orgUnit );
                            value = (aggregatedValue != DataMartStore.NO_VALUES_REGISTERED) ? NumberUtils
                                .formatDataValue( aggregatedValue ) : "";
                        }
                        else
                        {

                            double temp = 0.0;
                            double values = 0.0;
                            
                            System.out.println("The size of OrgUnitGroup Members is" + orgGroupMembers.size() );
                            for ( OrganisationUnit unit : orgGroupMembers )
                            {
                                
                                aggregatedValue = dataMartStore.getAggregatedValue( dataElement, optionCombo, period,
                                    unit );
                                System.out.println("The values are" + dataElement.getId()+ ":" + optionCombo.getId()+ ":" + period.getId()+ ":" + unit.getName() + ":" + aggregatedValue );
                                //value = (aggregatedValue != DataMartStore.NO_VALUES_REGISTERED) ? NumberUtils
                                //    .formatDataValue( aggregatedValue ) : "";
                                
                                if( aggregatedValue == -1 )
                                {
                                    aggregatedValue = 0.0;
                                }
                                
                                values = aggregatedValue;
                                
                                   /*
                                try
                                {
                                    values = Double.valueOf( value );
                                }
                                catch ( Exception e )
                                {
                                    values = 0.0;
                                    System.out.println( e );
                                }
                                */
                                // value = value + temp;
                                temp += values;
                               // System.out.println("Current OrganisationUnit is :" + unit + " and the value is :" + temp );
                            }
                            value = String.valueOf( (int) temp );

                        }

                        aggregatedDataValueMap.put( dataElement.getId() + ":" + optionCombo.getId(), value );
                    }
                    else
                    {
                        if ( organisationUnitGroupId.equals( "Selected_Only" ) )
                        {
                            dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, optionCombo );
                            value = (dataValue != null) ? dataValue.getValue() : "";
                        }
                        else
                        {
                            value = " ";
                        }

                        aggregatedDataValueMap.put( dataElement.getId() + ":" + optionCombo.getId(), value );
                    }
                }
            }

            // -----------------------------------------------------------------
            // Get the custom data entry form if any
            // -----------------------------------------------------------------

            DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );

            customDataEntryFormCode = CustomDataSetReportGenerator.prepareReportContent( dataEntryForm.getHtmlCode(),
                aggregatedDataValueMap );

            reportingUnit = orgUnit.getName();

            reportingPeriod = format.formatPeriod( period );

            return SUCCESS;
        }

        return ERROR;
    }
}
