package org.hisp.dhis.mapping.export;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class SVGDocument
{
    private String svg;

    public SVGDocument()
    {
    }

    public StringBuffer getSVGForImage()
    {
        return new StringBuffer( this.svg );
    }

    public StringBuffer getSVGForExcel()
    {/*
        String svg_ = doctype + this.svg;

        svg_ = svg_.replaceFirst( "<svg", "<svg " + namespace );

        if ( this.includeLegends )
        {
            svg_ = svg_.replaceFirst( "</svg>", this.getLegendScript( 10, 10 ) + "</svg>" );
        }

        return new StringBuffer( svg_ );*/
        return new StringBuffer();
    }

    public String getLegendScriptForExcel()
    {/*
        JSONObject legend;

        JSONObject json = (JSONObject) JSONSerializer.toJSON( this.legends );

        JSONArray jsonLegends = json.getJSONArray( "legends" );

        String result = doctype;

        result += "<svg width='100%' height='100%' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' ";

        result += "xmlns:attrib='http://www.carto.net/attrib/' viewBox='0 0 1 " + jsonLegends.size() + "'>";

        result += "<g id='legend'>";

        int x = 0;

        int y = 0;

        for ( int i = 0; i < jsonLegends.size(); i++ )
        {
            legend = jsonLegends.getJSONObject( i );
            
            String label = StringEscapeUtils.escapeXml( legend.getString( "label" ) );
            
            String color = StringEscapeUtils.escapeXml( legend.getString( "color" ) );
            
            result += "<rect x='" + x + "' y='" + (y + 1) + "' height='1' width='1' fill='" + color
                + "' stroke='#000000' stroke-width='0.001'/>";

            result += "<text id=\"indicator\" x='" + (x + 1.5) + "' y='" + (y + 1) + "' font-size=\"10\"><tspan>"
                + label + "</tspan></text>";

            y += 1;
        }

        result += "</g>";

        result += "</svg>";

        return result;*/
        return "";
    }

    private String getLegendScript( int x, int y )
    {/*
        String result = "<g id='legend'>";

        JSONObject legend;
        
        JSONObject json = (JSONObject) JSONSerializer.toJSON( this.legends );

        JSONArray jsonLegends = json.getJSONArray( "legends" );

        for ( int i = 0; i < jsonLegends.size(); i++ )
        {
            legend = jsonLegends.getJSONObject( i );
            
            String label = StringEscapeUtils.escapeXml( legend.getString( "label" ) );
            
            String color = StringEscapeUtils.escapeXml( legend.getString( "color" ) );
            
            result += "<rect x='" + x + "' y='" + (y + 15) + "' height='15' width='30' fill='" + color
                + "' stroke='#000000' stroke-width='1'/>";

            result += "<text id=\"indicator\" x='" + (x + 40) + "' y='" + (y + 27) + "' font-size=\"12\"><tspan>"
                + label + "</tspan></text>";

            y += 15;
        }

        result += "</g>";

        return result;*/
        return "";
    }

    private String getLegendScript2( int x, int y )
    {/*
        String result = "<g id='legend2'>";

        JSONObject legend;
        
        JSONObject json = (JSONObject) JSONSerializer.toJSON( this.legends2 );

        JSONArray jsonLegends = json.getJSONArray( "legends" );

        for ( int i = 0; i < jsonLegends.size(); i++ )
        {
            legend = jsonLegends.getJSONObject( i );
            
            String label = StringEscapeUtils.escapeXml( legend.getString( "label" ) );
            
            String color = StringEscapeUtils.escapeXml( legend.getString( "color" ) );
            
            result += "<rect x='" + x + "' y='" + (y + 15) + "' height='15' width='30' fill='" + color
                + "' stroke='#000000' stroke-width='1'/>";

            result += "<text id=\"indicator\" x='" + (x + 40) + "' y='" + (y + 27) + "' font-size=\"12\"><tspan>"
                + label + "</tspan></text>";

            y += 15;
        }

        result += "</g>";

        return result;*/
        return "";
    }

    @Override
    public String toString()
    {
        return svg;
    }

    public String getSvg()
    {
        return svg;
    }

    public void setSvg( String svg )
    {
        this.svg = svg;
    }
}
