/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.integration.components.dxf2;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 *
 * @author bobj
 */
public class Dxf2Endpoint extends DefaultEndpoint
{
    public Dxf2Endpoint() {}
    
    public Dxf2Endpoint(String uri, Dxf2Component component) 
    {
        super(uri, component);
    }

    public Dxf2Endpoint(String endpointUri) {
        super(endpointUri);
    }
    
    @Override
    public Producer createProducer() throws Exception {
        return new Dxf2Producer(this);
    }

    @Override
    public Consumer createConsumer(Processor prcsr) throws Exception {
        throw new UnsupportedOperationException(
                "You can't consume messages from this endpoint: " + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
}
