package org.hisp.dhis.commons.action;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rosu
 * Date: 7/7/13
 * Time: 1:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetConceptsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConceptService conceptService;

    public void setConceptService( ConceptService conceptService )
    {
        this.conceptService = conceptService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Concept> concepts;

    public List<Concept> getConcepts()
    {
        return concepts;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        concepts = new ArrayList<Concept>( conceptService.getAllConcepts() );

        ContextUtils.clearIfNotModified(ServletActionContext.getRequest(), ServletActionContext.getResponse(), concepts);

        Collections.sort(concepts, IdentifiableObjectNameComparator.INSTANCE);

        return SUCCESS;
    }
}

