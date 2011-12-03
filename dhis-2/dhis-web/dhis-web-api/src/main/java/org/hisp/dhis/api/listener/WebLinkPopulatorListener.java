package org.hisp.dhis.api.listener;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElements;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.Indicators;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnits;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebLinkPopulatorListener extends Marshaller.Listener
{
    private HttpServletRequest request;

    private static Map<Class, String> resourcePaths = new HashMap<Class, String>();

    static
    {
        resourcePaths.put( Charts.class, "charts" );
        resourcePaths.put( Chart.class, "charts" );
        resourcePaths.put( Indicators.class, "indicators" );
        resourcePaths.put( Indicator.class, "indicators" );
        resourcePaths.put( DataElements.class, "dataElements" );
        resourcePaths.put( DataElement.class, "dataElements" );
        resourcePaths.put( OrganisationUnits.class, "organisationUnits" );
        resourcePaths.put( OrganisationUnit.class, "organisationUnits" );
        resourcePaths.put( DataSets.class, "dataSets" );
        resourcePaths.put( DataSet.class, "dataSets" );
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
    }

    private void populateCharts( Charts charts, boolean root )
    {
        if ( root )
        {
            charts.setLink( getBasePath( Chart.class ) + "/" );

            for ( Chart chart : charts.getCharts() )
            {
                populateChart( chart, false );
            }
        }
        else
        {
            charts.setLink( getBasePath( Chart.class ) + "/" );
        }
    }

    private void populateChart( Chart chart, boolean root )
    {
        if ( root )
        {
            chart.setLink( getBasePath( Chart.class ) + "/" + chart.getUid() );

            handleIdentifiableObjectCollection( chart.getIndicators() );
            handleIdentifiableObjectCollection( chart.getDataElements() );
            handleIdentifiableObjectCollection( chart.getOrganisationUnits() );
            handleIdentifiableObjectCollection( chart.getAllOrganisationUnits() );
            handleIdentifiableObjectCollection( chart.getDataSets() );
            handleIdentifiableObjectCollection( chart.getPeriods() );
            handleIdentifiableObjectCollection( chart.getAllPeriods() );
        }
        else
        {
            chart.setLink( getBasePath( Chart.class ) + "/" + chart.getUid() );
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
        baseIdentifiableObject.setLink( getBasePath( baseIdentifiableObject.getClass() ) + "/" + baseIdentifiableObject.getUid() );
    }

    private String getBasePath( Class clazz )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( request.getScheme() );

        buffer.append( "://" + request.getServerName() );

        if ( request.getServerPort() != 80 || request.getServerPort() != 443 )
        {
            buffer.append( ":" + request.getServerPort() );
        }

        buffer.append( request.getServletPath() );
        buffer.append( "/" + resourcePaths.get( clazz ) );

        return buffer.toString();
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
