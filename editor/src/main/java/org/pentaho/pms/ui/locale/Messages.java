/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.pms.ui.locale;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.pentaho.metadata.messages.LocaleHelper;
//import org.pentaho.pms.messages.MessageUtil;
//import org.pentaho.pms.messages.util.LocaleHelper;

public class Messages extends org.pentaho.metadata.messages.Messages {
    private static final String BUNDLE_NAME = "org.pentaho.pms.ui.locale.messages";//$NON-NLS-1$

    private static final Map<Locale,ResourceBundle> locales = Collections.synchronizedMap(new HashMap<Locale,ResourceBundle>());

    protected static Map<Locale,ResourceBundle> getLocales() {
        return locales;
    }

    private static ResourceBundle getBundle() {
        Locale locale = LocaleHelper.getLocale();
        ResourceBundle bundle = (ResourceBundle) locales.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            locales.put(locale, bundle);
        }
        return bundle;
    }

    public static String getString(String key) {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, String param1) {
        return getString(getBundle(), key, param1);
    }

    public static String getString(String key, String param1, String param2) {
        return getString(getBundle(), key, param1, param2);
    }

    public static String getString(String key, String param1, String param2, String param3) {
        return getString(getBundle(), key, param1, param2, param3);
    }

    public static String getString(String key, String param1, String param2, String param3, String param4) {
        return getString(getBundle(), key, param1, param2, param3, param4);
    }

    public static String getErrorString(String key) {
        return formatErrorMessage(key, getString(key));
    }

    public static String getErrorString(String key, String param1) {
        return getErrorString(getBundle(), key, param1);
    }

    public static String getErrorString(String key, String param1, String param2) {
        return getErrorString(getBundle(), key, param1, param2);
    }

    public static String getErrorString(String key, String param1, String param2, String param3) {
        return getErrorString(getBundle(), key, param1, param2, param3);
    }

}
