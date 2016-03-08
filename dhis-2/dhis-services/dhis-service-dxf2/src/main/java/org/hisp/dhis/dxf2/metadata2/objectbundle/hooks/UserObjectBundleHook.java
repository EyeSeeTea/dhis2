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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata2.objectbundle.ObjectBundle;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class UserObjectBundleHook extends AbstractObjectBundleHook
{
    @Autowired
    private UserService userService;

    private UserCredentials userCredentials;

    @Override
    public void preCreate( IdentifiableObject identifiableObject, ObjectBundle objectBundle )
    {
        if ( !User.class.isInstance( identifiableObject ) || ((User) identifiableObject).getUserCredentials() == null )
        {
            return;
        }

        User user = (User) identifiableObject;
        userCredentials = user.getUserCredentials();
        user.setUserCredentials( null );

        if ( objectBundle.getPreheat().getUsernames().containsKey( userCredentials.getUsername() ) )
        {
            // Username exists, throw validation error
        }

        if ( !StringUtils.isEmpty( userCredentials.getPassword() ) )
        {
            userService.encodeAndSetPassword( userCredentials, userCredentials.getPassword() );
        }

        preheatService.connectReferences( userCredentials, objectBundle.getPreheat(), objectBundle.getPreheatIdentifier() );
    }

    @Override
    public void postCreate( IdentifiableObject identifiableObject, ObjectBundle objectBundle )
    {
        if ( !User.class.isInstance( identifiableObject ) || userCredentials == null )
        {
            return;
        }

        User user = (User) identifiableObject;
        userCredentials.setUserInfo( user );
        sessionFactory.getCurrentSession().save( userCredentials );
        user.setUserCredentials( userCredentials );
        sessionFactory.getCurrentSession().update( userCredentials );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void postImport( ObjectBundle objectBundle )
    {
        if ( !objectBundle.getObjects().containsKey( User.class ) )
        {
            return;
        }

        List<IdentifiableObject> objects = objectBundle.getObjects().get( User.class );
        Map<String, Map<String, Object>> references = objectBundle.getObjectReferences( User.class );

        if ( references == null || references.isEmpty() )
        {
            return;
        }

        for ( IdentifiableObject identifiableObject : objects )
        {
            Map<String, Object> referenceMap = references.get( identifiableObject.getUid() );

            if ( referenceMap.isEmpty() )
            {
                continue;
            }

            User user = (User) identifiableObject;
            user.setOrganisationUnits( (Set<OrganisationUnit>) referenceMap.get( "organisationUnits" ) );
            user.setDataViewOrganisationUnits( (Set<OrganisationUnit>) referenceMap.get( "dataViewOrganisationUnits" ) );

            preheatService.connectReferences( identifiableObject, objectBundle.getPreheat(), objectBundle.getPreheatIdentifier() );
            sessionFactory.getCurrentSession().update( identifiableObject );
        }
    }
}
