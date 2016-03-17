package org.hisp.dhis.dxf2.metadata2.objectbundle;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.dxf2.metadata2.FlushMode;
import org.hisp.dhis.dxf2.metadata2.objectbundle.hooks.ObjectBundleHook;
import org.hisp.dhis.feedback.ErrorCode;
import org.hisp.dhis.feedback.ErrorReport;
import org.hisp.dhis.feedback.ObjectErrorReport;
import org.hisp.dhis.feedback.ObjectErrorReports;
import org.hisp.dhis.feedback.TypeReport;
import org.hisp.dhis.preheat.Preheat;
import org.hisp.dhis.preheat.PreheatParams;
import org.hisp.dhis.preheat.PreheatService;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.schema.validation.SchemaValidator;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
@Transactional
public class DefaultObjectBundleService implements ObjectBundleService
{
    private static final Log log = LogFactory.getLog( DefaultObjectBundleService.class );

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PreheatService preheatService;

    @Autowired
    private SchemaValidator schemaValidator;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private DbmsManager dbmsManager;

    @Autowired( required = false )
    private List<ObjectBundleHook> objectBundleHooks = new ArrayList<>();

    @Override
    public ObjectBundle create( ObjectBundleParams params )
    {
        PreheatParams preheatParams = params.getPreheatParams();

        if ( params.getUser() == null )
        {
            params.setUser( currentUserService.getCurrentUser() );
        }

        preheatParams.setUser( params.getUser() );
        preheatParams.setObjects( params.getObjects() );

        ObjectBundle bundle = new ObjectBundle( params, preheatService.preheat( preheatParams ), params.getObjects() );
        bundle.setObjectReferences( preheatService.collectObjectReferences( params.getObjects() ) );

        return bundle;
    }

    @Override
    public ObjectBundleValidation validate( ObjectBundle bundle )
    {
        ObjectBundleValidation validation = new ObjectBundleValidation();

        List<Class<? extends IdentifiableObject>> klasses = getSortedClasses( bundle );

        for ( Class<? extends IdentifiableObject> klass : klasses )
        {
            TypeReport typeReport = new TypeReport( klass );
            ObjectErrorReports objectErrorReports = typeReport.getObjectErrorReports();

            if ( bundle.getImportMode().isCreateAndUpdate() )
            {
                TypeReport validateBySchemas1 = validateBySchemas( klass, bundle.getObjects( klass, false ), bundle );
                TypeReport validateBySchemas2 = validateBySchemas( klass, bundle.getObjects( klass, true ), bundle );

                typeReport.getStats().merge( validateBySchemas1.getStats() );
                typeReport.getStats().merge( validateBySchemas2.getStats() );

                objectErrorReports.addObjectErrorReports( validateBySchemas1.getObjectErrorReports() );
                objectErrorReports.addObjectErrorReports( validateBySchemas2.getObjectErrorReports() );

                List<ObjectErrorReport> checkUniqueness1 = preheatService.checkUniqueness( bundle.getObjects( klass, false ), bundle.getPreheat(), bundle.getPreheatIdentifier() );
                List<ObjectErrorReport> checkUniqueness2 = preheatService.checkUniqueness( bundle.getObjects( klass, true ), bundle.getPreheat(), bundle.getPreheatIdentifier() );

                typeReport.getStats().incIgnored( checkUniqueness1.size() );
                typeReport.getStats().incIgnored( checkUniqueness2.size() );

                objectErrorReports.addObjectErrorReports( checkUniqueness1 );
                objectErrorReports.addObjectErrorReports( checkUniqueness2 );

                List<ObjectErrorReport> checkReferences = preheatService.checkReferences( bundle.getObjectMap().get( klass ), bundle.getPreheat(), bundle.getPreheatIdentifier() );

                if ( bundle.getImportMode().isAtomic() ) // if mode is atomic, report reference errors as ignored objects
                {
                    typeReport.getStats().incIgnored( checkReferences.size() );
                }

                objectErrorReports.addObjectErrorReports( checkReferences );
            }

            if ( bundle.getImportMode().isCreate() )
            {
                TypeReport validateForCreate = validateForCreate( klass, bundle.getObjects( klass, true ), bundle );
                TypeReport validateBySchemas = validateBySchemas( klass, bundle.getObjects( klass, false ), bundle );

                typeReport.getStats().merge( validateForCreate.getStats() );
                typeReport.getStats().merge( validateBySchemas.getStats() );

                objectErrorReports.addObjectErrorReports( validateBySchemas.getObjectErrorReports() );
                objectErrorReports.addObjectErrorReports( validateForCreate.getObjectErrorReports() );

                List<ObjectErrorReport> checkUniqueness = preheatService.checkUniqueness( bundle.getObjects( klass, false ), bundle.getPreheat(),
                    bundle.getPreheatIdentifier() );
                typeReport.getStats().incIgnored( checkUniqueness.size() );
                objectErrorReports.addObjectErrorReports( checkUniqueness );

                List<ObjectErrorReport> checkReferences = preheatService.checkReferences( bundle.getObjectMap().get( klass ),
                    bundle.getPreheat(), bundle.getPreheatIdentifier() );

                if ( bundle.getImportMode().isAtomic() ) // if mode is atomic, report reference errors as ignored objects
                {
                    typeReport.getStats().incIgnored( checkReferences.size() );
                }

                objectErrorReports.addObjectErrorReports( checkReferences );
            }

            if ( bundle.getImportMode().isUpdate() )
            {
                TypeReport validateForUpdate = validateForUpdate( klass, bundle.getObjects( klass, false ), bundle );
                TypeReport validateBySchemas = validateBySchemas( klass, bundle.getObjects( klass, true ), bundle );

                typeReport.getStats().merge( validateForUpdate.getStats() );
                typeReport.getStats().merge( validateBySchemas.getStats() );

                objectErrorReports.addObjectErrorReports( validateForUpdate.getObjectErrorReports() );
                objectErrorReports.addObjectErrorReports( validateBySchemas.getObjectErrorReports() );

                List<ObjectErrorReport> checkUniqueness = preheatService.checkUniqueness( bundle.getObjects( klass, true ), bundle.getPreheat(),
                    bundle.getPreheatIdentifier() );
                typeReport.getStats().incIgnored( checkUniqueness.size() );
                objectErrorReports.addObjectErrorReports( checkUniqueness );

                List<ObjectErrorReport> checkReferences = preheatService.checkReferences( bundle.getObjectMap().get( klass ),
                    bundle.getPreheat(), bundle.getPreheatIdentifier() );

                if ( bundle.getImportMode().isAtomic() ) // if mode is atomic, report reference errors as ignored objects
                {
                    typeReport.getStats().incIgnored( checkReferences.size() );
                }

                objectErrorReports.addObjectErrorReports( checkReferences );
            }

            if ( bundle.getImportMode().isDelete() )
            {
                TypeReport validateForDelete = validateForDelete( klass, bundle.getObjects( klass, false ), bundle );
                typeReport.getStats().merge( validateForDelete.getStats() );
                objectErrorReports.addObjectErrorReports( validateForDelete.getObjectErrorReports() );
            }

            validation.addTypeReport( typeReport );
        }

        bundle.setObjectBundleStatus( ObjectBundleStatus.VALIDATED );

        return validation;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Map<Class<?>, TypeReport> commit( ObjectBundle bundle )
    {
        Map<Class<?>, TypeReport> typeReports = new HashMap<>();

        if ( ObjectBundleMode.VALIDATE == bundle.getObjectBundleMode() )
        {
            return typeReports; // skip if validate only
        }

        List<Class<? extends IdentifiableObject>> klasses = getSortedClasses( bundle );
        Session session = sessionFactory.getCurrentSession();

        objectBundleHooks.forEach( hook -> hook.preImport( bundle ) );

        for ( Class<? extends IdentifiableObject> klass : klasses )
        {
            List<IdentifiableObject> persistedObjects = bundle.getObjects( klass, true );
            List<IdentifiableObject> nonPersistedObjects = bundle.getObjects( klass, false );

            if ( bundle.getImportMode().isCreateAndUpdate() )
            {
                TypeReport createTypeReport = handleCreates( session, klass, nonPersistedObjects, bundle );
                TypeReport updateTypeReport = handleUpdates( session, klass, persistedObjects, bundle );

                TypeReport typeReport = new TypeReport( klass );
                typeReport.getStats().merge( createTypeReport.getStats() );
                typeReport.getStats().merge( updateTypeReport.getStats() );

                typeReports.put( klass, typeReport );
            }
            else if ( bundle.getImportMode().isCreate() )
            {
                typeReports.put( klass, handleCreates( session, klass, nonPersistedObjects, bundle ) );
            }
            else if ( bundle.getImportMode().isUpdate() )
            {
                typeReports.put( klass, handleUpdates( session, klass, persistedObjects, bundle ) );
            }
            else if ( bundle.getImportMode().isDelete() )
            {
                typeReports.put( klass, handleDeletes( session, klass, persistedObjects, bundle ) );
            }

            if ( FlushMode.AUTO == bundle.getFlushMode() ) session.flush();
        }

        objectBundleHooks.forEach( hook -> hook.postImport( bundle ) );
        session.flush();

        dbmsManager.clearSession();
        bundle.setObjectBundleStatus( ObjectBundleStatus.COMMITTED );

        return typeReports;
    }

    private TypeReport handleCreates( Session session, Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );
        if ( objects.isEmpty() ) return typeReport;

        log.info( "Creating " + objects.size() + " object(s) of type " + objects.get( 0 ).getClass().getSimpleName() );

        for ( IdentifiableObject object : objects )
        {
            if ( Preheat.isDefault( object ) ) continue;

            objectBundleHooks.forEach( hook -> hook.preCreate( object, bundle ) );

            preheatService.connectReferences( object, bundle.getPreheat(), bundle.getPreheatIdentifier() );

            prepare( object, bundle );
            session.save( object );
            typeReport.getStats().incCreated();

            bundle.getPreheat().replace( bundle.getPreheatIdentifier(), object );

            objectBundleHooks.forEach( hook -> hook.postCreate( object, bundle ) );

            if ( log.isDebugEnabled() )
            {
                String msg = "Created object '" + bundle.getPreheatIdentifier().getIdentifiersWithName( object ) + "'";
                log.debug( msg );
            }

            if ( FlushMode.OBJECT == bundle.getFlushMode() ) session.flush();
        }

        return typeReport;
    }

    private TypeReport handleUpdates( Session session, Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );
        if ( objects.isEmpty() ) return typeReport;

        log.info( "Updating " + objects.size() + " object(s) of type " + objects.get( 0 ).getClass().getSimpleName() );

        for ( IdentifiableObject object : objects )
        {
            if ( Preheat.isDefault( object ) ) continue;

            objectBundleHooks.forEach( hook -> hook.preUpdate( object, bundle ) );

            preheatService.connectReferences( object, bundle.getPreheat(), bundle.getPreheatIdentifier() );

            IdentifiableObject persistedObject = bundle.getPreheat().get( bundle.getPreheatIdentifier(), object );

            persistedObject.mergeWith( object, bundle.getMergeMode() );
            persistedObject.mergeSharingWith( object );

            prepare( persistedObject, bundle );
            session.update( persistedObject );
            typeReport.getStats().incUpdated();

            objectBundleHooks.forEach( hook -> hook.postUpdate( persistedObject, bundle ) );

            bundle.getPreheat().replace( bundle.getPreheatIdentifier(), persistedObject );

            if ( log.isDebugEnabled() )
            {
                String msg = "Updated object '" + bundle.getPreheatIdentifier().getIdentifiersWithName( persistedObject ) + "'";
                log.debug( msg );
            }

            if ( FlushMode.OBJECT == bundle.getFlushMode() ) session.flush();
        }

        return typeReport;
    }

    private TypeReport handleDeletes( Session session, Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );
        if ( objects.isEmpty() ) return typeReport;

        log.info( "Deleting " + objects.size() + " object(s) of type " + objects.get( 0 ).getClass().getSimpleName() );

        List<IdentifiableObject> persistedObjects = bundle.getPreheat().getAll( bundle.getPreheatIdentifier(), objects );

        for ( IdentifiableObject object : persistedObjects )
        {
            objectBundleHooks.forEach( hook -> hook.preDelete( object, bundle ) );
            manager.delete( object, bundle.getUser() );
            typeReport.getStats().incDeleted();

            bundle.getPreheat().remove( bundle.getPreheatIdentifier(), object );

            if ( log.isDebugEnabled() )
            {
                String msg = "Deleted object '" + bundle.getPreheatIdentifier().getIdentifiersWithName( object ) + "'";
                log.debug( msg );
            }

            if ( FlushMode.OBJECT == bundle.getFlushMode() ) session.flush();
        }

        return typeReport;
    }

    //----------------------------------------------------------------------------------------------------------
    // Helpers
    //----------------------------------------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private List<Class<? extends IdentifiableObject>> getSortedClasses( ObjectBundle bundle )
    {
        List<Class<? extends IdentifiableObject>> klasses = new ArrayList<>();

        schemaService.getMetadataSchemas().forEach( schema -> {
            Class<? extends IdentifiableObject> klass = (Class<? extends IdentifiableObject>) schema.getKlass();

            if ( bundle.getObjectMap().containsKey( klass ) )
            {
                klasses.add( klass );
            }
        } );

        return klasses;
    }

    private void prepare( IdentifiableObject object, ObjectBundle bundle )
    {
        BaseIdentifiableObject identifiableObject = (BaseIdentifiableObject) object;

        if ( identifiableObject.getUser() == null ) identifiableObject.setUser( bundle.getUser() );
        if ( identifiableObject.getUserGroupAccesses() == null ) identifiableObject.setUserGroupAccesses( new HashSet<>() );
    }

    public TypeReport validateForCreate( Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );

        if ( objects == null || objects.isEmpty() )
        {
            return typeReport;
        }

        Iterator<IdentifiableObject> iterator = objects.iterator();
        int idx = 0;

        while ( iterator.hasNext() )
        {
            IdentifiableObject identifiableObject = iterator.next();
            IdentifiableObject object = bundle.getPreheat().get( bundle.getPreheatIdentifier(), identifiableObject );

            if ( object != null && object.getId() > 0 )
            {
                ObjectErrorReport objectErrorReport = new ObjectErrorReport( klass, idx );
                objectErrorReport.addErrorReport( new ErrorReport( klass, ErrorCode.E5000, bundle.getPreheatIdentifier(),
                    bundle.getPreheatIdentifier().getIdentifiersWithName( identifiableObject ) ) );

                typeReport.getObjectErrorReports().addObjectErrorReport( objectErrorReport );
                typeReport.getStats().incIgnored();

                iterator.remove();
            }

            idx++;
        }

        return typeReport;
    }

    public TypeReport validateForUpdate( Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );

        if ( objects == null || objects.isEmpty() )
        {
            return typeReport;
        }

        Iterator<IdentifiableObject> iterator = objects.iterator();
        int idx = 0;

        while ( iterator.hasNext() )
        {
            IdentifiableObject identifiableObject = iterator.next();
            IdentifiableObject object = bundle.getPreheat().get( bundle.getPreheatIdentifier(), identifiableObject );

            if ( object == null || object.getId() == 0 )
            {
                if ( Preheat.isDefaultClass( identifiableObject.getClass() ) ) continue;

                ObjectErrorReport objectErrorReport = new ObjectErrorReport( klass, idx );
                objectErrorReport.addErrorReport( new ErrorReport( klass, ErrorCode.E5001, bundle.getPreheatIdentifier(),
                    bundle.getPreheatIdentifier().getIdentifiersWithName( identifiableObject ) ) );

                typeReport.getObjectErrorReports().addObjectErrorReport( objectErrorReport );
                typeReport.getStats().incIgnored();

                iterator.remove();
            }

            idx++;
        }

        return typeReport;
    }

    public TypeReport validateForDelete( Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );

        if ( objects == null || objects.isEmpty() )
        {
            return typeReport;
        }

        Iterator<IdentifiableObject> iterator = objects.iterator();
        int idx = 0;

        while ( iterator.hasNext() )
        {
            IdentifiableObject identifiableObject = iterator.next();
            IdentifiableObject object = bundle.getPreheat().get( bundle.getPreheatIdentifier(), identifiableObject );

            if ( object == null || object.getId() == 0 )
            {
                if ( Preheat.isDefaultClass( identifiableObject.getClass() ) ) continue;

                ObjectErrorReport objectErrorReport = new ObjectErrorReport( klass, idx );
                objectErrorReport.addErrorReport( new ErrorReport( klass, ErrorCode.E5001, bundle.getPreheatIdentifier(),
                    bundle.getPreheatIdentifier().getIdentifiersWithName( identifiableObject ) ) );
                typeReport.getObjectErrorReports().addObjectErrorReport( objectErrorReport );
                typeReport.getStats().incIgnored();

                iterator.remove();
            }

            idx++;
        }

        return typeReport;
    }

    public TypeReport validateBySchemas( Class<? extends IdentifiableObject> klass, List<IdentifiableObject> objects, ObjectBundle bundle )
    {
        TypeReport typeReport = new TypeReport( klass );

        if ( objects == null || objects.isEmpty() )
        {
            return typeReport;
        }

        Iterator<IdentifiableObject> iterator = objects.iterator();
        int idx = 0;

        while ( iterator.hasNext() )
        {
            IdentifiableObject identifiableObject = iterator.next();
            List<ErrorReport> validationErrorReports = schemaValidator.validate( identifiableObject );

            if ( !validationErrorReports.isEmpty() )
            {
                ObjectErrorReport objectErrorReport = new ObjectErrorReport( klass, idx );
                objectErrorReport.addErrorReports( validationErrorReports );

                typeReport.getObjectErrorReports().addObjectErrorReport( objectErrorReport );
                typeReport.getStats().incIgnored();

                iterator.remove();
            }

            idx++;
        }

        return typeReport;
    }
}
