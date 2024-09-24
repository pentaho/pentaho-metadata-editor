/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.pms.util;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.LocalizationUtil;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.schema.SchemaMeta;

/**
 * This utility class imports and exports all localized strings that exist in a legacy Schema Meta.
 * It is implemented as an adapter to LocalizationUtil, located in the PentahoMetadata project, with
 * legacy to modern construct conversion in place.
 * @author Curtis Boyden (cboyden@petaho.com)
 *
 */
public class LegacyLocalizationUtil {
  private static final Log logger = LogFactory.getLog(LocalizationUtil.class);
  
  protected final LocalizationUtil localizationUtil;
  
  public LegacyLocalizationUtil() {
    localizationUtil = new LocalizationUtil();
  }

  /**
   * Export the localization properties from a legacy SchemaMeta object given a locale to extract
   * 
   * @param schemaMeta The SchemaMeta from which to extract a locle
   * @param locale The string representation of the locale to extract
   * @return A Properties object containing a key / value pair for each locale token and its correlating value
   * @throws IllegalArgumentException if the schemaMeta or locale are null
   */
  @SuppressWarnings("deprecation")
  public Properties exportLocalizedProperties(SchemaMeta schemaMeta, String locale) throws Exception {
    if(schemaMeta == null || locale == null) {
      if(schemaMeta == null)
        throw new IllegalArgumentException("Parameter \"schemaMeta\" MUST not be null");
      if(locale == null)
        throw new IllegalArgumentException("Parameter \"locale\" MUST not be null");
    }
    
    try{
      Domain domain = ThinModelConverter.convertFromLegacy(schemaMeta);
      return localizationUtil.exportLocalizedProperties(domain, locale);
    } catch (Exception e) {
      if(logger.isDebugEnabled()) {
        logger.debug("Failed to export properties from legacy schema [" + schemaMeta.getName() + "]", e);
      }
      throw e;
    }
  }
}
