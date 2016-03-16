package org.hisp.dhis.dxf2.metadata2.objectbundle.hooks;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hibernate.Session;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata2.objectbundle.ObjectBundle;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupAccess;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Order( 0 )
@Component
public class IdentifiableObjectBundleHook extends AbstractObjectBundleHook
{
    @Override
    public void preCreate( IdentifiableObject identifiableObject, ObjectBundle objectBundle )
    {
        Schema schema = schemaService.getDynamicSchema( identifiableObject.getClass() );
        Session session = sessionFactory.getCurrentSession();
        handleAttributeValues( session, identifiableObject, objectBundle, schema );
        handleUserGroupAccesses( session, identifiableObject, objectBundle, schema );
    }

    @Override
    public void preUpdate( IdentifiableObject identifiableObject, ObjectBundle objectBundle )
    {
        Schema schema = schemaService.getDynamicSchema( identifiableObject.getClass() );
        Session session = sessionFactory.getCurrentSession();
        handleAttributeValues( session, identifiableObject, objectBundle, schema );
        handleUserGroupAccesses( session, identifiableObject, objectBundle, schema );
    }

    public void handleAttributeValues( Session session, IdentifiableObject identifiableObject, ObjectBundle bundle, Schema schema )
    {
        if ( !schema.havePersistedProperty( "attributeValues" ) ) return;

        for ( AttributeValue attributeValue : identifiableObject.getAttributeValues() )
        {
            Attribute attribute = bundle.getPreheat().get( bundle.getPreheatIdentifier(), attributeValue.getAttribute() );
            attributeValue.setAttribute( attribute );
            session.save( attributeValue );
        }
    }

    public void handleUserGroupAccesses( Session session, IdentifiableObject identifiableObject, ObjectBundle bundle, Schema schema )
    {
        if ( !schema.havePersistedProperty( "userGroupAccesses" ) ) return;

        for ( UserGroupAccess userGroupAccess : identifiableObject.getUserGroupAccesses() )
        {
            UserGroup userGroup = bundle.getPreheat().get( bundle.getPreheatIdentifier(), userGroupAccess.getUserGroup() );
            userGroupAccess.setUserGroup( userGroup );
            session.save( userGroupAccess );
        }
    }
}
