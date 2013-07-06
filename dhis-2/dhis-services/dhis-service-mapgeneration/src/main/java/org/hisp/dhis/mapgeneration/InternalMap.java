package org.hisp.dhis.mapgeneration;

/*
 * Copyright (c) 2011, University of Oslo
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An internal representation of a map.
 * 
 * It encapsulates all the information of a map built by adding layers to it. It
 * may then create an image representing the map by a call to render.
 * 
 * Finally, one should extend this class with an implementation that uses a
 * specific platform, e.g. GeoTools to draw the map.
 * 
 * @author Kjetil Andresen <kjetand@ifi.uio.no>
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class InternalMap
{
    protected Color backgroundColor = null;

    protected boolean isAntiAliasingEnabled = true;
    
    private List<InternalMapObject> mapObjects = new ArrayList<InternalMapObject>();

    public InternalMap()
    {
    }
    
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor )
    {
        this.backgroundColor = backgroundColor;
    }

    public boolean isAntiAliasingEnabled()
    {
        return isAntiAliasingEnabled;
    }

    public void setAntiAliasingEnabled( boolean isAntiAliasingEnabled )
    {
        this.isAntiAliasingEnabled = isAntiAliasingEnabled;
    }

    public List<InternalMapObject> getMapObjects()
    {
        return mapObjects;
    }

    public void setMapObjects( List<InternalMapObject> mapObjects )
    {
        this.mapObjects = mapObjects;
    }
    
    //TODO remove

    public InternalMap( InternalMapLayer layer )
    {
        this.mapObjects = new LinkedList<InternalMapObject>();
        this.addMapLayer( layer );
    }

    public void addMapLayer( InternalMapLayer layer )
    {
        for ( InternalMapObject mapObject : layer.getAllMapObjects() )
        {
            this.mapObjects.add( mapObject );
        }
    }
}
