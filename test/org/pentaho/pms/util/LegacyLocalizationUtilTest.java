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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.LocalizationUtil;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.schema.SchemaMeta;

public class LegacyLocalizationUtilTest {
  
  @SuppressWarnings("deprecation")
  private SchemaMeta loadLegacyMetadataModel(String domainName, String file) throws Exception {
    KettleEnvironment.init(false);
    CWM cwm = null;

    cwm = CWM.getInstance(domainName, true);
    Assert.assertNotNull("CWM singleton instance is null", cwm);
    cwm.importFromXMI(file);    

    CwmSchemaFactoryInterface cwmSchemaFactory = new CwmSchemaFactory();

    return cwmSchemaFactory.getSchemaMeta(cwm);
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testLegacyLocalization() throws Exception {
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = localeUtil.exportLocalizedProperties(schemaMeta, locale);
    
    Assert.assertEquals(275, props.size());
    Assert.assertEquals("Customer", props.get("[LogicalModel-BV_ORDERS].[Category-BC_CUSTOMER_W_TER_].[name]"));
    Assert.assertFalse("IPhysicalModel-null should not exist as such",props.containsKey("[IPhysicalModel-null].[PT_DEPARTMENT_MANAGERS].[MANAGER_NAME].[name]"));
  }
  
  @Test
  public void testLegacyLocalizationNullSM() throws Exception {
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = null;
    Exception ex = null;
    
    try {
      localeUtil.exportLocalizedProperties(null, locale);
    } catch(IllegalArgumentException e) {
      ex = e;
    }
    
    Assert.assertNotNull(ex);
    Assert.assertEquals(ex.getClass(), IllegalArgumentException.class);
    Assert.assertEquals("Parameter \"schemaMeta\" MUST not be null", ex.getMessage());
  }
  
  @Test
  public void testLegacyLocalizationNullLocale() throws Exception {
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = null;
    Exception ex = null;
    
    try {
      localeUtil.exportLocalizedProperties(schemaMeta, null);
    } catch(IllegalArgumentException e) {
      ex = e;
    }
    
    Assert.assertNotNull(ex);
    Assert.assertEquals(ex.getClass(), IllegalArgumentException.class);
    Assert.assertEquals("Parameter \"locale\" MUST not be null", ex.getMessage());
  }
  
  @Test
  public void testLegacyLocalizationUnknownLocale() throws Exception {
    // Expected results should be the same as en_US as it is the default for non-overidden values
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "abc_XYZ";
    Properties props = localeUtil.exportLocalizedProperties(schemaMeta, locale);
    
    Assert.assertEquals(275, props.size());
    Assert.assertEquals("Customer", props.get("[LogicalModel-BV_ORDERS].[Category-BC_CUSTOMER_W_TER_].[name]"));
  }
  
  public void buildLargeXmi() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi(new FileInputStream("/temp/metadata.xmi"));
    
    LocalizationUtil util = new LocalizationUtil();
    
    String locale = "en-US";
    Properties enUs = util.exportLocalizedProperties(domain, locale);
    
    Properties temp = null;
    
    Locale[] locales = DateFormat.getAvailableLocales();
    
    for(int i = 0; i < locales.length; i++) {
      temp = new Properties();
      for(Object key : enUs.keySet()) {
        temp.put(key, "[" + locales[i].toString() + "] " + enUs.get(key).toString());
      }
      util.importLocalizedProperties(domain, temp, locales[i].toString().replace("_","-"));
    }
    
    new FileOutputStream("/temp/multi-locale.xmi").write(parser.generateXmi(domain).getBytes("UTF-8"));
  }
  
  public void buildManyPropBundles() throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader("/temp/metadata_en_US.properties"));
    
    HashMap<String, BufferedWriter> outFiles = new HashMap<String, BufferedWriter>();
    
    Locale[] locales = DateFormat.getAvailableLocales();
    
    for(int i = 0; i < locales.length; i++) {
      String locale = locales[i].toString().replace("_","-");
      outFiles.put(locale, new BufferedWriter(new FileWriter("/temp/props/metadata_" + locale + ".properties")));
    }
    
    String line = null;
    while((line = reader.readLine()) != null) {
      for(String locale : outFiles.keySet()) {
        if(line.endsWith("=")) {
          outFiles.get(locale).write(line);
        } else {
          outFiles.get(locale).write(line.replace("=", "=[" + locale + "]"));
        }
        outFiles.get(locale).write("\r\n");
      }
    }
    
    for(String locale : outFiles.keySet()) {
      outFiles.get(locale).close();
    }
  }
}
