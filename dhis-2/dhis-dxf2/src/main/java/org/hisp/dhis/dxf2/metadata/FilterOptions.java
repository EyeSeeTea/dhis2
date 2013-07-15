package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class FilterOptions
{
    private Map<String, List<String>> filterOptions =  new HashMap<String, List<String>>();

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------

    public FilterOptions()
    {
    }

    //--------------------------------------------------------------------------
    // Getters & Setters
    //--------------------------------------------------------------------------

    public Map<String, List<String>> getFilterOptions()
    {
        return filterOptions;
    }

    public void setFilterOptions( Map<String, List<String>> filterOptions )
    {
        this.filterOptions = filterOptions;
    }

    //--------------------------------------------------------------------------
    // Filter Options
    //--------------------------------------------------------------------------

    public void addFilterOption( String filterOption, List<String> values )
    {
        filterOptions.put( filterOption, values );
    }

    public void addFilterOptions( Map<String, List<String>> newFilterOptions )
    {
        filterOptions.putAll( newFilterOptions );
    }

    public Map<String, List<String>> processJSON( JSONObject json )
    {
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        Iterator jsonIterator = json.keys();
        while ( jsonIterator.hasNext() )
        {
            String key = (String) jsonIterator.next();
            List<String> values = new ArrayList<String>();
            JSONArray uids = json.getJSONArray( key );

            for ( int i = 0; i < uids.size(); i++ )
            {
                JSONObject uid = uids.getJSONObject( i );
                String uidKey = key.substring( 0, key.length() - 1 );

                values.add( uid.getString( uidKey ) );
            }

            resultMap.put( key, values );
        }

        return resultMap;
    }
}
