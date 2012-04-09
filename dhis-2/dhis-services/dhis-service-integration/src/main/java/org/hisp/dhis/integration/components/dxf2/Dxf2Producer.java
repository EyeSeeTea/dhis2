/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.integration.components.dxf2;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author bobj
 */
public class Dxf2Producer extends DefaultProducer {

    private static final transient Log log = LogFactory.getLog(Dxf2Producer.class);
    
    private Dxf2Endpoint endpoint;

    public Dxf2Producer( Dxf2Endpoint endpoint)
    {
        super(endpoint);
        this.endpoint = endpoint;    
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }
    
}
