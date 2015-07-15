package org.hisp.dhis.hibernate;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.hibernate.engine.spi.SessionImplementor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enum UserType which never provides a null value to Hibernate.
 * On null values present in the database, the default value is substituted.
 *
 * Useful for extending existing schemas with enums and still being
 * able to require non-null values (works like default).
 *
 * @author Halvdan Hoem Grelland
 */
public abstract class NotNullEnumUserType<E extends Enum<E>>
    extends EnumUserType<E>
{
    private Class<E> clazz = null;

    protected NotNullEnumUserType( Class<E> clazz )
    {
        super( clazz );
        this.clazz = clazz;
    }

    @Override
    public Object nullSafeGet( ResultSet resultSet, String[] names, SessionImplementor implementor, Object owner )
    {
        Object value;

        try
        {
            value = super.nullSafeGet( resultSet, names, implementor, owner );
        }
        catch ( SQLException e )
        {
            value = Enum.valueOf( clazz, getDefaultValue().name() );
        }

        return value;
    }

    protected abstract E getDefaultValue();
}
