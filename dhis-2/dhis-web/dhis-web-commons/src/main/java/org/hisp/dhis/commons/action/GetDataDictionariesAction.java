package org.hisp.dhis.commons.action;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rosu
 * Date: 7/7/13
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetDataDictionariesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<DataDictionary> dataDictionaries;

    public List<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        dataDictionaries = new ArrayList<DataDictionary>( dataDictionaryService.getAllDataDictionaries() );

        ContextUtils.clearIfNotModified(ServletActionContext.getRequest(), ServletActionContext.getResponse(), dataDictionaries);

        Collections.sort(dataDictionaries, IdentifiableObjectNameComparator.INSTANCE);

        return SUCCESS;
    }
}
