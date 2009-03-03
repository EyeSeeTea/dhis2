package org.hisp.dhis.settings.action.system;

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

import java.util.List;
import java.util.SortedMap;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.webportal.module.Module;
import org.hisp.dhis.webportal.module.ModuleManager;

import com.opensymphony.xwork.Action;

import static org.hisp.dhis.options.SystemSettingManager.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetSystemSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private ModuleManager moduleManager;

    public void setModuleManager( ModuleManager moduleManager )
    {
        this.moduleManager = moduleManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private SortedMap<String, String> flags;

    public SortedMap<String, String> getFlags()
    {
        return flags;
    }
    
    private List<Module> modules;
    
    public List<Module> getModules()
    {
        return modules;
    }
    
    private String currentStartModule;

    public String getCurrentStartModule()
    {
        return currentStartModule;
    }

    private Boolean zeroValueSaveMode;

    public Boolean getZeroValueSaveMode()
    {
        return zeroValueSaveMode;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
    {    	
    	flags = systemSettingManager.getFlags();
        
        modules = moduleManager.getMenuModules();
        
        currentStartModule = (String) systemSettingManager.getSystemSetting( KEY_START_MODULE );
        
        zeroValueSaveMode = (Boolean) systemSettingManager.getSystemSetting( KEY_ZERO_VALUE_SAVE_MODE, false );
        
        return SUCCESS;
    }
}
