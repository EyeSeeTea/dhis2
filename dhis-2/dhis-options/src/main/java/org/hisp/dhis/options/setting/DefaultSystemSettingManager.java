package org.hisp.dhis.options.setting;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;

import org.hisp.dhis.options.SystemSetting;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.options.SystemSettingStore;

/**
 * @author Stian Strandli
 * @author Lars Helge Overland
 * @version $Id: DefaultSystemSettingManager.java 4910 2008-04-15 17:55:02Z larshelg $
 */
public class DefaultSystemSettingManager
    implements SystemSettingManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingStore systemSettingStore;

    public void setSystemSettingStore( SystemSettingStore systemSettingStore )
    {
        this.systemSettingStore = systemSettingStore;
    }
    
    private SortedMap<String, String> flags;

    public void setFlags( SortedMap<String, String> flags )
    {
        this.flags = flags;
    }
    
    // -------------------------------------------------------------------------
    // SystemSettingManager implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Generic methods
    // -------------------------------------------------------------------------
    
    public void saveSystemSetting( String name, Serializable value )
    {
        SystemSetting setting = systemSettingStore.getSystemSetting( name );
        
        if ( setting == null )
        {
            setting = new SystemSetting();
            
            setting.setName( name );
            setting.setValue( value );
            
            systemSettingStore.addSystemSetting( setting );
        }
        else
        {
            setting.setValue( value );
            
            systemSettingStore.updateSystemSetting( setting );
        }
    }

    public Serializable getSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getSystemSetting( name );
        
        return setting != null ? setting.getValue() : null;
    }   
    
    public Serializable getSystemSetting( String name, Serializable defaultValue )
    {
        SystemSetting setting = systemSettingStore.getSystemSetting( name );
        
        return setting != null ? setting.getValue() : defaultValue;
    }

    public Collection<SystemSetting> getAllSystemSettings()
    {
        return systemSettingStore.getAllSystemSettings();
    }

    public void deleteSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getSystemSetting( name );
        
        if ( setting != null )
        {
            systemSettingStore.deleteSystemSetting( setting );
        }
    }

    // -------------------------------------------------------------------------
    // Specific methods
    // -------------------------------------------------------------------------
    
    public SortedMap<String, String> getFlags()
    {
        return flags;
    }
}
