package org.hisp.dhis.datastatistics;
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

import org.hisp.dhis.common.BaseIdentifiableObject;

import java.util.Date;

/**
 * @author Julie Hill Roa
 * @author Yrjan A. F. Fraschetti
 */
public class DataStatistics extends BaseIdentifiableObject
{
    //private int id;
    private String text;

    public DataStatistics(){}
    public DataStatistics(String text)
    {
        this.text = text;
    }
    public DataStatistics(String text, String uid, String code,
        Date created, Date lastUpdated)
    {
        super();
        this.text = text;

        super.setUid( uid );
        super.setCode( code );
        super.setCreated( created );
        super.setLastUpdated( lastUpdated );
    }

    public int getId(){
        return id;
    }

    public String getText(){
        return text;
    }

    public void setText(String s){
        text = s;
    }

    @Override
    public String toString()
    {
        return "{" +
            "\"class\":\"" + getClass() + "\", " +
            "\"id\":\"" + id + "\", " +
            "\"text\":\"" + text + "\", " +
            "\"uid\":\"" + uid + "\", " +
            "\"code\":\"" + code + "\", " +
            "\"name\":\"" + name + "\", " +
            "\"created\":\"" + created + "\", " +
            "\"lastUpdated\":\"" + lastUpdated + "\" " +
            "}";
    }
}
