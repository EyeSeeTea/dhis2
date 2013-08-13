package org.hisp.dhis.i18n;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.i18n.locale.I18nLocale;

public interface I18nLocaleStore
    extends GenericNameableObjectStore<I18nLocale>
{
    I18nLocale getI18nLocaleByLocale( String language, String country );
}
