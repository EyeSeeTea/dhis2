/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.integration.routes;

import java.io.IOException;
import java.io.InputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisp.dhis.dxf2.utils.JacksonUtils;

import org.apache.camel.builder.RouteBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author bobj
 */
public class DHISRouteBuilder extends RouteBuilder {

    @Autowired
    protected OrganisationUnitService ouService;

    public void setOuService(OrganisationUnitService ouService) {
        this.ouService = ouService;
    }

    @Override
    public void configure() throws Exception {
        from("direct:dxf2_in").process(new Processor() {

            @Transactional
            public void process(Exchange exchange) throws IOException {
                OrganisationUnit ou = JacksonUtils.fromXml((InputStream) exchange.getIn().getBody(), OrganisationUnit.class);
                System.out.println("Importing " + ou.getName());
                
                OrganisationUnit parent = ou.getParent();
                ou.setParent(null);
                
                try {
                    ouService.addOrganisationUnit(ou);
                } catch (Exception oops) {
                    System.out.println(oops.getMessage());
                }
                
                if (parent != null) {
                    parent = ouService.getOrganisationUnit(parent.getUid());
                    ou.setParent(parent);
                }

                ouService.updateOrganisationUnit(ou);

            }
        });
    }
}
