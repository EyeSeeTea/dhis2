package org.hisp.dhis.attribute;

/*
 * Copyright (c) 2004-2012, University of Oslo All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the HISP project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Iterator;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.deletion.DeletionHandler;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class AttributeValueDeletionHandler
    extends DeletionHandler
{
    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return AttributeValue.class.getSimpleName();
    }

    @Override
    public String allowDeleteAttribute( Attribute attribute )
    {
        String sql = "select count(*) from attributevalue where attributeid=" + attribute.getId();
        
        return jdbcTemplate.queryForInt( sql ) == 0 ? null : ERROR;
    }
        
    @Override
    public void deleteDataElement( DataElement dataElement )
    {
        Iterator<AttributeValue> iterator = dataElement.getAttributeValues().iterator();

        while ( iterator.hasNext() )
        {
            AttributeValue attributeValue = iterator.next();
            iterator.remove();
            attributeService.deleteAttributeValue( attributeValue );
        }
    }

    @Override
    public void deleteIndicator( Indicator indicator )
    {
        Iterator<AttributeValue> iterator = indicator.getAttributeValues().iterator();

        while ( iterator.hasNext() )
        {
            AttributeValue attributeValue = iterator.next();
            iterator.remove();
            attributeService.deleteAttributeValue( attributeValue );
        }
    }

    @Override
    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
        Iterator<AttributeValue> iterator = unit.getAttributeValues().iterator();

        while ( iterator.hasNext() )
        {
            AttributeValue attributeValue = iterator.next();
            iterator.remove();
            attributeService.deleteAttributeValue( attributeValue );
        }
    }

    @Override
    public void deleteUser( User user )
    {
        Iterator<AttributeValue> iterator = user.getAttributeValues().iterator();

        while ( iterator.hasNext() )
        {
            AttributeValue attributeValue = iterator.next();
            iterator.remove();
            attributeService.deleteAttributeValue( attributeValue );
        }
    }
}
