package org.hisp.dhis.web.webapi.v1.validation.constraint;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.web.webapi.v1.validation.constraint.annotation.ValidUidReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class UidReferenceValidator implements ConstraintValidator<ValidUidReference, String>
{
    @Autowired
    @Qualifier( "org.hisp.dhis.common.IdentifiableObjectManager" )
    private IdentifiableObjectManager identifiableObjectManager;

    private Class<? extends IdentifiableObject> identifiableObjectClass;

    @Override
    public void initialize( ValidUidReference constraintAnnotation )
    {
        identifiableObjectClass = constraintAnnotation.value();
    }

    @Override
    public boolean isValid( String value, ConstraintValidatorContext context )
    {
        IdentifiableObject identifiableObject = identifiableObjectManager.get( identifiableObjectClass, value );

        boolean isValid = identifiableObject != null;

        if ( !isValid )
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate( String.format( "No object found with ID %s.", value ) )
                .addConstraintViolation();
        }

        return isValid;
    }
}
