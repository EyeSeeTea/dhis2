package org.hisp.dhis.fileresource;

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

import org.hisp.dhis.hibernate.HibernateConfigurationProvider;
import org.jclouds.domain.Credentials;

import java.util.Properties;

/**
 * @author Halvdan Hoem Grelland
 */
public class S3FileResourceContentStore
    extends BaseJCloudsFileResourceContentStore
{
    // TODO use generic names
    private static final String KEY_S3_BUCKET = "amazon.s3.bucket";
    private static final String KEY_S3_ACCESSKEYID = "amazon.s3.accesskeyid";
    private static final String KEY_S3_SECRET = "amazon.s3.secret";

    private static final String S3_PROVIDER_KEY = "aws-s3";

    private HibernateConfigurationProvider configurationProvider;

    public void setConfigurationProvider( HibernateConfigurationProvider configurationProvider )
    {
        this.configurationProvider = configurationProvider;
    }

    @Override
    protected Credentials getCredentials()
    {
        Properties properties = configurationProvider.getConfiguration().getProperties();

        String accessKeyId = properties.getProperty( KEY_S3_ACCESSKEYID );
        String secretKey = properties.getProperty( KEY_S3_SECRET );

        return new Credentials( accessKeyId, secretKey );
    }

    @Override
    protected String getContainer()
    {
        return "dataValues";
    }

    @Override
    protected String getJCloudsProviderKey()
    {
        return S3_PROVIDER_KEY;
    }
}
