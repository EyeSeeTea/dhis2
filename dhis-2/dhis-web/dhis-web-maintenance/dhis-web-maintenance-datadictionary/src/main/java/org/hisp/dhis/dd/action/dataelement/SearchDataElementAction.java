package org.hisp.dhis.dd.action.dataelement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Chau Thu Tran
 * @version $Id: SearchDataElementAction.java 5573 20010-07-15 22:39:53Z$
 */

public class SearchDataElementAction
    extends ActionPagingSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    private DataDictionaryService dataDictionaryService;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String key;

    private Integer dataDictionaryId;

    private Integer dataElementGroupId;

    private Collection<DataElement> dataElements;

    private Comparator<DataElement> dataElementComparator;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setDataDictionaryId( Integer dataDictionaryId )
    {
        this.dataDictionaryId = dataDictionaryId;
    }

    public void setDataElementGroupId( Integer dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    public Collection<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        if ( key.isEmpty() )
        {
            return INPUT;
        }

        // Get dataelements by key
        List<DataElement> dataelementsByKey = new ArrayList<DataElement>( dataElementService
            .searchDataElementByName( key ) );

        // Get dataelements by group
        if ( dataElementGroupId != null && dataElementGroupId != -1 )
        {
            List<DataElement> dataElementsByGroup = new ArrayList<DataElement>( dataElementService
                .getDataElementsByGroupId( dataElementGroupId ) );

            CollectionUtils.intersection( dataelementsByKey, dataElementsByGroup );
        }

        // Get dataelements by dictionary
        if ( dataDictionaryId != null && dataDictionaryId != -1 )
        {
            List<DataElement> dataElementsByDictionary = new ArrayList<DataElement>( dataDictionaryService
                .getDataElementsByDictionaryId( dataDictionaryId ) );

            CollectionUtils.intersection( dataelementsByKey, dataElementsByDictionary );
        }

        Collections.sort( dataelementsByKey, dataElementComparator );
        this.paging = createPaging( dataelementsByKey.size() );

        dataElements = getBlockElement( dataelementsByKey, paging.getStartPos(), paging.getPageSize() );

        return SUCCESS;
    }

    private List<DataElement> getBlockElement( List<DataElement> dataElementList, int startPos, int pageSize )
    {
        List<DataElement> returnList;

        try
        {
            returnList = dataElementList.subList( startPos, startPos + pageSize );
        }
        catch ( IndexOutOfBoundsException ex )
        {
            returnList = dataElementList.subList( startPos, dataElementList.size() );
        }

        return returnList;
    }
}
