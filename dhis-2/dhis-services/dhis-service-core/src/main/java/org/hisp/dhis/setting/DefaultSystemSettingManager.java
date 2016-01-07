package org.hisp.dhis.setting;

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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.system.util.ValidationUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Stian Strandli
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultSystemSettingManager
    implements SystemSettingManager
{
    /**
     * Cache for system settings. Does not accept nulls.
     */
    private static final Cache<String, Optional<Serializable>> SETTING_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess( 1, TimeUnit.HOURS )
        .initialCapacity( 200 )
        .maximumSize( 400 )
        .build();

    private static final Map<String, SettingKey> NAME_KEY_MAP = Lists.newArrayList(
        SettingKey.values() ).stream().collect( Collectors.toMap( SettingKey::getName, e -> e ) );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingStore systemSettingStore;

    public void setSystemSettingStore( SystemSettingStore systemSettingStore )
    {
        this.systemSettingStore = systemSettingStore;
    }

    private List<String> flags;

    public void setFlags( List<String> flags )
    {
        this.flags = flags;
    }

    @Autowired
    private I18nManager i18nManager;

    @Resource( name = "stringEncryptor" )
    private PBEStringEncryptor pbeStringEncryptor;

    // -------------------------------------------------------------------------
    // SystemSettingManager implementation
    // -------------------------------------------------------------------------

    @Override
    public void saveSystemSetting( String name, Serializable value )
    {
        SETTING_CACHE.invalidate( name );

        SystemSetting setting = systemSettingStore.getByName( name );

        if ( isConfidential( name ) )
        {
            value = pbeStringEncryptor.encrypt( value.toString() );
        }

        if ( setting == null )
        {
            setting = new SystemSetting();

            setting.setName( name );
            setting.setValue( value );

            systemSettingStore.save( setting );
        }
        else
        {
            setting.setValue( value );

            systemSettingStore.update( setting );
        }
    }

    @Override
    public void saveSystemSetting( SettingKey setting, Serializable value )
    {
        saveSystemSetting( setting.getName(), value );
    }

    @Override
    public void deleteSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        if ( setting != null )
        {
            SETTING_CACHE.invalidate( name );

            systemSettingStore.delete( setting );
        }
    }

    @Override
    public void deleteSystemSetting( SettingKey setting )
    {
        deleteSystemSetting( setting.getName() );
    }

    @Override
    public Serializable getSystemSetting( String name )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        if ( isConfidential( name ) )
        {

            setting.setValue( pbeStringEncryptor.decrypt( setting.getValue().toString() ) );

        }

        return setting != null && setting.hasValue() ? setting.getValue() : null;
    }

    @Override
    public Serializable getSystemSetting( SettingKey setting )
    {
        try
        {
            Optional<Serializable> value = SETTING_CACHE.get( setting.getName(),
                () -> getSystemSettingOptional( setting.getName(), setting.getDefaultValue() ) );

            return value.orElse( null );
        }
        catch ( ExecutionException ignored )
        {
            return null;
        }
    }

    @Override
    public Serializable getSystemSetting( SettingKey setting, Serializable defaultValue )
    {
        return getSystemSettingOptional( setting.getName(), defaultValue ).orElse( null );
    }

    private Optional<Serializable> getSystemSettingOptional( String name, Serializable defaultValue )
    {
        SystemSetting setting = systemSettingStore.getByName( name );

        if ( setting != null && setting.hasValue() )
        {
            return isConfidential( name ) ?
                Optional.of( pbeStringEncryptor.decrypt( setting.getValue().toString() ) ) :
                Optional.of( setting.getValue() );
        }
        else
        {
            return Optional.ofNullable( defaultValue );
        }

    }

    @Override
    public List<SystemSetting> getAllSystemSettings()
    {

        /*
         * Remove confidential settings from this list!
         */
        return systemSettingStore.getAll().stream()
            .filter( systemSetting -> !isConfidential( systemSetting.getName() ) )
            .collect( Collectors.toList() );

    }

    @Override
    public Map<String, Serializable> getSystemSettingsAsMap()
    {
        Map<String, Serializable> settingsMap = new HashMap<>();

        Collection<SystemSetting> systemSettings = getAllSystemSettings();

        for ( SystemSetting systemSetting : systemSettings )
        {
            Serializable settingValue = systemSetting.getValue();

            if ( settingValue == null )
            {
                Optional<SettingKey> setting = SettingKey.getByName( systemSetting.getName() );

                if ( setting.isPresent() )
                {
                    settingValue = setting.get().getDefaultValue();
                }
            }

            settingsMap.put( systemSetting.getName(), settingValue );
        }

        return settingsMap;
    }

    @Override
    public Map<String, Serializable> getSystemSettingsAsMap( Set<String> names )
    {
        Map<String, Serializable> map = new HashMap<>();

        for ( String name : names )
        {
            Serializable settingValue = getSystemSetting( name );

            if ( settingValue == null )
            {
                Optional<SettingKey> setting = SettingKey.getByName( name );

                if ( setting.isPresent() )
                {
                    settingValue = setting.get().getDefaultValue();
                }
            }

            if ( settingValue != null )
            {
                map.put( name, settingValue );
            }
        }

        return map;
    }

    @Override
    public Map<String, Serializable> getSystemSettings( Collection<SettingKey> settings )
    {
        Map<String, Serializable> map = new HashMap<>();

        for ( SettingKey setting : settings )
        {
            Serializable value = getSystemSetting( setting );

            if ( value != null )
            {
                map.put( setting.getName(), value );
            }
        }

        return map;
    }

    @Override
    public void invalidateCache()
    {
        SETTING_CACHE.invalidateAll();
    }

    // -------------------------------------------------------------------------
    // Specific methods
    // -------------------------------------------------------------------------

    @Override
    public List<String> getFlags()
    {
        Collections.sort( flags );
        return flags;
    }

    @Override
    public List<StyleObject> getFlagObjects()
    {
        Collections.sort( flags );

        I18n i18n = i18nManager.getI18n();

        List<StyleObject> list = Lists.newArrayList();

        for ( String flag : flags )
        {
            String name = i18n.getString( flag );
            String file = flag + ".png";

            list.add( new StyleObject( name, flag, file ) );
        }

        return list;
    }

    @Override
    public String getFlagImage()
    {
        String flag = (String) getSystemSetting( SettingKey.FLAG );

        return flag != null ? flag + ".png" : null;
    }

    @Override
    public String getEmailHostName()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_HOST_NAME ) );
    }

    @Override
    public int getEmailPort()
    {
        return (Integer) getSystemSetting( SettingKey.EMAIL_PORT );
    }

    @Override
    public String getEmailUsername()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_USERNAME ) );
    }

    @Override
    public boolean getEmailTls()
    {
        return (Boolean) getSystemSetting( SettingKey.EMAIL_TLS );
    }

    @Override
    public String getEmailSender()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_SENDER ) );
    }

    @Override
    public String getInstanceBaseUrl()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.INSTANCE_BASE_URL ) );
    }

    @Override
    public boolean accountRecoveryEnabled()
    {
        return (Boolean) getSystemSetting( SettingKey.ACCOUNT_RECOVERY );
    }

    @Override
    public boolean selfRegistrationNoRecaptcha()
    {
        return (Boolean) getSystemSetting( SettingKey.SELF_REGISTRATION_NO_RECAPTCHA );
    }

    @Override
    public boolean emailEnabled()
    {
        return getEmailHostName() != null;
    }

    @Override
    public boolean systemNotificationEmailValid()
    {
        String address = (String) getSystemSetting( SettingKey.SYSTEM_NOTIFICATIONS_EMAIL );

        return address != null && ValidationUtils.emailIsValid( address );
    }

    @Override
    public boolean hideUnapprovedDataInAnalytics()
    {
        return (Boolean) getSystemSetting( SettingKey.HIDE_UNAPPROVED_DATA_IN_ANALYTICS );
    }

    @Override
    public boolean isOpenIdConfigured()
    {
        return getSystemSetting( SettingKey.OPENID_PROVIDER ) != null &&
            getSystemSetting( SettingKey.OPENID_PROVIDER_LABEL ) != null;
    }

    @Override
    public String googleAnalyticsUA()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.GOOGLE_ANALYTICS_UA ) );
    }

    @Override
    public Integer credentialsExpires()
    {
        return (Integer) getSystemSetting( SettingKey.CREDENTIALS_EXPIRES );
    }

    @Override
    public boolean isConfidential( String name )
    {
        return NAME_KEY_MAP.containsKey( name ) && NAME_KEY_MAP.get( name ).isConfidential();
    }

}
