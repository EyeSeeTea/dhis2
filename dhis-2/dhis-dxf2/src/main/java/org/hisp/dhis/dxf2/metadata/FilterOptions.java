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
import org.hisp.dhis.filter.Filter;

import java.util.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class FilterOptions
{
    private Filter filter;

    private Map<String, List<String>> options =  new HashMap<String, List<String>>();

    //--------------------------------------------------------------------------
    // Getters & Setters
    //--------------------------------------------------------------------------

    public Filter getFilter()
    {
        return filter;
    }

    public void setFilter( Filter filter )
    {
        this.filter = filter;
    }

    public Map<String, List<String>> getOptions()
    {
        return options;
    }

    public void setOptions( Map<String, List<String>> options )
    {
        this.options = options;
    }

    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------

    public void addOption( String option, List<String> values )
    {
        options.put( option, values );
    }

    public void addOptions( Map<String, List<String>> newOptions )
    {
        options.putAll( newOptions );
    }

    /**
     * @param json - JSON request processing method for filtered MetaData
     */
    public Map<String, List<String>> processJSON( JSONObject json )
    {
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        Iterator jsonIterator = json.keys();
        while ( jsonIterator.hasNext() )
        {
            List<String> uids = new ArrayList<String>();

            String key = (String) jsonIterator.next();
            JSONArray values = json.getJSONArray( key );
            for(int i = 0; i < values.size(); i++)
            {
                String uid = values.getString( i );
                uids.add( uid );
            }
            resultMap.put( key, uids );
        }

        return resultMap;
    }
}
