/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.integration.components.dxf2;

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * A DHIS2 specific camel component for creating dxf2 endpoints
 * @author bobj
 */
public class Dxf2Component extends DefaultComponent 
{

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception 
    {
        Endpoint endpoint = new Dxf2Endpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
    
}
