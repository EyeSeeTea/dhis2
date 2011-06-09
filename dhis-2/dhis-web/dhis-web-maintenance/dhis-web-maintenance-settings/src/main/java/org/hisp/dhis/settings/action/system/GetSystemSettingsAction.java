package org.hisp.dhis.settings.action.system;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.options.style.StyleManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.webportal.module.Module;
import org.hisp.dhis.webportal.module.ModuleManager;
import org.hisp.dhis.webportal.module.StartableModuleFilter;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetSystemSettingsAction
    implements Action
{
    private static final Filter<Module> startableFilter = new StartableModuleFilter();
    
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
    
    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }
    
    private ConfigurationService configurationService;
    
    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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

    private SortedMap<String, String> styles;

    public SortedMap<String, String> getStyles()
    {
        return styles;
    }
    
    private String currentStyle;

    public String getCurrentStyle()
    {
        return currentStyle;
    }
    
    private Collection<String> aggregationStrategies;

    public Collection<String> getAggregationStrategies()
    {
        return aggregationStrategies;
    }
    
    private Configuration configuration;
    
    public Configuration getConfiguration()
    {
        return configuration;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {    	
    	flags = systemSettingManager.getFlags();
        
        modules = moduleManager.getMenuModules();
        
        FilterUtils.filter( modules, startableFilter );
        
        styles = styleManager.getStyles();
        
        currentStyle = styleManager.getCurrentStyle();
        
        aggregationStrategies = systemSettingManager.getAggregationStrategies();
        
        configuration = configurationService.getConfiguration();
        
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        
        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );
        
        return SUCCESS;
    }
}
