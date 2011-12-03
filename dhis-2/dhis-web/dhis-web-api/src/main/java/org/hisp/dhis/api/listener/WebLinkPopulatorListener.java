package org.hisp.dhis.api.listener;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.Attributes;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrations;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.indicator.*;
import org.hisp.dhis.organisationunit.*;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.Users;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebLinkPopulatorListener extends Marshaller.Listener
{
    private HttpServletRequest request;

    private static Map<Class, String> resourcePaths = new HashMap<Class, String>();

    private String rootPath = null;

    static
    {
        resourcePaths.put( Attributes.class, "attributes" );
        resourcePaths.put( Attribute.class, "attributes" );

        resourcePaths.put( Charts.class, "charts" );
        resourcePaths.put( Chart.class, "charts" );

        resourcePaths.put( CompleteDataSetRegistrations.class, "completeDataSetRegistrations" );
        resourcePaths.put( CompleteDataSetRegistration.class, "completeDataSetRegistrations" );

        resourcePaths.put( Indicators.class, "indicators" );
        resourcePaths.put( Indicator.class, "indicators" );
        resourcePaths.put( IndicatorGroups.class, "indicatorGroups" );
        resourcePaths.put( IndicatorGroup.class, "indicatorGroups" );
        resourcePaths.put( IndicatorGroupSets.class, "indicatorGroupSets" );
        resourcePaths.put( IndicatorGroupSet.class, "indicatorGroupSets" );
        resourcePaths.put( IndicatorTypes.class, "indicatorTypes" );
        resourcePaths.put( IndicatorType.class, "indicatorTypes" );

        resourcePaths.put( DataElements.class, "dataElements" );
        resourcePaths.put( DataElement.class, "dataElements" );
        resourcePaths.put( DataElementGroups.class, "dataElementGroups" );
        resourcePaths.put( DataElementGroup.class, "dataElementGroups" );
        resourcePaths.put( DataElementGroupSets.class, "dataElementGroupSets" );
        resourcePaths.put( DataElementGroupSet.class, "dataElementGroupSets" );

        resourcePaths.put( DataElementCategories.class, "dataElementCategories" );
        resourcePaths.put( DataElementCategory.class, "dataElementCategories" );
        resourcePaths.put( DataElementCategoryCombos.class, "dataElementCategoryCombos" );
        resourcePaths.put( DataElementCategoryCombo.class, "dataElementCategoryCombos" );
        resourcePaths.put( DataElementCategoryOptions.class, "dataElementCategoryOptions" );
        resourcePaths.put( DataElementCategoryOption.class, "dataElementCategoryOptions" );
        resourcePaths.put( DataElementCategoryOptionCombos.class, "dataElementCategoryOptionCombos" );
        resourcePaths.put( DataElementCategoryOptionCombo.class, "dataElementCategoryOptionCombos" );

        resourcePaths.put( OrganisationUnits.class, "organisationUnits" );
        resourcePaths.put( OrganisationUnit.class, "organisationUnits" );
        resourcePaths.put( OrganisationUnitGroups.class, "organisationUnitGroups" );
        resourcePaths.put( OrganisationUnitGroup.class, "organisationUnitGroups" );
        resourcePaths.put( OrganisationUnitGroupSets.class, "organisationUnitGroupSets" );
        resourcePaths.put( OrganisationUnitGroupSet.class, "organisationUnitGroupSets" );

        resourcePaths.put( DataSets.class, "dataSets" );
        resourcePaths.put( DataSet.class, "dataSets" );

        resourcePaths.put( Users.class, "users" );
        resourcePaths.put( User.class, "users" );
    }

    public WebLinkPopulatorListener( HttpServletRequest request )
    {
        this.request = request;
    }

    @Override
    public void beforeMarshal( Object source )
    {
        if ( source instanceof Charts )
        {
            populateCharts( (Charts) source, true );
        }
        else if ( source instanceof Chart )
        {
            populateChart( (Chart) source, true );
        }
        else if ( source instanceof DataSets )
        {
            populateDataSets( (DataSets) source, true );
        }
        else if ( source instanceof DataSet )
        {
            populateDataSet( (DataSet) source, true );
        }
        else if ( source instanceof OrganisationUnits )
        {
            populateOrganisationUnits( (OrganisationUnits) source, true );
        }
        else if ( source instanceof OrganisationUnit )
        {
            populateOrganisationUnit( (OrganisationUnit) source, true );
        }
    }

    private void populateOrganisationUnits( OrganisationUnits organisationUnits, boolean root )
    {
        organisationUnits.setLink( getBasePath( OrganisationUnits.class ) );

        if ( root )
        {
            for ( OrganisationUnit organisationUnit : organisationUnits.getOrganisationUnits() )
            {
                populateOrganisationUnit( organisationUnit, false );
            }
        }
    }

    private void populateOrganisationUnit( OrganisationUnit organisationUnit, boolean root )
    {
        organisationUnit.setLink( getPathWithUid( organisationUnit ) );

        if ( root )
        {
            populateIdentifiableObject( organisationUnit.getParent() );
            handleIdentifiableObjectCollection( organisationUnit.getDataSets() );
            handleIdentifiableObjectCollection( organisationUnit.getGroups() );
        }
    }

    private void populateDataSets( DataSets dataSets, boolean root )
    {
        dataSets.setLink( getBasePath( DataSets.class ) );

        if ( root )
        {
            for ( DataSet dataSet : dataSets.getDataSets() )
            {
                populateDataSet( dataSet, false );
            }
        }
    }

    private void populateDataSet( DataSet dataSet, boolean root )
    {
        dataSet.setLink( getPathWithUid( dataSet ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataSet.getDataElements() );
            handleIdentifiableObjectCollection( dataSet.getIndicators() );
            handleIdentifiableObjectCollection( dataSet.getSources() );
        }
    }

    private void populateCharts( Charts charts, boolean root )
    {
        charts.setLink( getBasePath( Chart.class ) );

        if ( root )
        {
            for ( Chart chart : charts.getCharts() )
            {
                populateChart( chart, false );
            }
        }
    }

    private void populateChart( Chart chart, boolean root )
    {
        chart.setLink( getPathWithUid( chart ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( chart.getIndicators() );
            handleIdentifiableObjectCollection( chart.getDataElements() );
            handleIdentifiableObjectCollection( chart.getOrganisationUnits() );
            handleIdentifiableObjectCollection( chart.getAllOrganisationUnits() );
            handleIdentifiableObjectCollection( chart.getDataSets() );
            handleIdentifiableObjectCollection( chart.getPeriods() );
            handleIdentifiableObjectCollection( chart.getAllPeriods() );
        }
    }

    public void handleIdentifiableObjectCollection( Collection<? extends BaseIdentifiableObject> identifiableObjects )
    {
        for ( BaseIdentifiableObject baseIdentifiableObject : identifiableObjects )
        {
            populateIdentifiableObject( baseIdentifiableObject );
        }
    }

    private void populateIdentifiableObject( BaseIdentifiableObject baseIdentifiableObject )
    {
        baseIdentifiableObject.setLink( getPathWithUid( baseIdentifiableObject ) );
    }

    private String getPathWithUid( BaseIdentifiableObject baseIdentifiableObject )
    {
        return getBasePath( baseIdentifiableObject.getClass() ) + "/" + baseIdentifiableObject.getUid();
    }

    private String getBasePath( Class clazz )
    {
        if ( rootPath == null )
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append( request.getScheme() );

            buffer.append( "://" + request.getServerName() );

            if ( request.getServerPort() != 80 || request.getServerPort() != 443 )
            {
                buffer.append( ":" + request.getServerPort() );
            }

            buffer.append( request.getServletPath() );

            rootPath = buffer.toString();
        }

        return rootPath + "/" + resourcePaths.get( clazz );
    }


/*
    private String getCalculatedBasePath()
    {
        String path = request.getRequestURL().toString();
        path = StringUtils.stripFilenameExtension( path );

        while ( path.lastIndexOf( "/" ) == path.length() - 1 )
        {
            path = path.substring( 0, path.length() - 1 );
        }

        return path;
    }
*/
}
