package org.hisp.dhis.reportexcel.excelitem.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;

import com.opensymphony.xwork2.Action;

public class SetupExcelItemFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ExcelItemService excelItemService;

    public void setExcelItemService( ExcelItemService excelItemService )
    {
        this.excelItemService = excelItemService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer excelItemGroupId;

    public void setExcelItemGroupId( Integer excelItemGroupId )
    {
        this.excelItemGroupId = excelItemGroupId;
    }

    public ExcelItemGroup excelItemGroup;

    public void setExcelItemGroup( ExcelItemGroup excelItemGroup )
    {
        this.excelItemGroup = excelItemGroup;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        excelItemGroup = excelItemService.getExcelItemGroup( excelItemGroupId );

        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );

        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );

        return SUCCESS;
    }
}
