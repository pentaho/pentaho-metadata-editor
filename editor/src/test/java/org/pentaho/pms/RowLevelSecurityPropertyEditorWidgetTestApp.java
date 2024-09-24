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

package org.pentaho.pms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.localstring.ConceptPropertyLocalizedString;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.concept.editor.ConceptModel;
import org.pentaho.pms.ui.concept.editor.ConceptModificationEvent;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.IConceptModel;
import org.pentaho.pms.ui.concept.editor.IConceptModificationListener;
import org.pentaho.pms.ui.concept.editor.RowLevelSecurityPropertyEditorWidget;
import org.pentaho.pms.ui.util.Const;

public class RowLevelSecurityPropertyEditorWidgetTestApp extends ApplicationWindow {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(RowLevelSecurityPropertyEditorWidgetTestApp.class);

  // ~ Instance fields =================================================================================================

  private IConceptModel conceptModel;

  private SchemaMeta schemaMeta;
  
  private SecurityReference dummySecurityReference;

  // ~ Constructors ====================================================================================================

  public RowLevelSecurityPropertyEditorWidgetTestApp() {
    super(null);
    initModel();
    conceptModel.addConceptModificationListener(new IConceptModificationListener() {
      public void conceptModified(final ConceptModificationEvent e) {
        RowLevelSecurityPropertyEditorWidgetTestApp.this.conceptModified(e);
      }
    });
  }

  // ~ Methods =========================================================================================================

  protected void conceptModified(final ConceptModificationEvent e) {
    if (logger.isDebugEnabled()) {
      logger.debug("heard concept modified event: " + e);
    }

  }

  protected void initModel() {
    dummySecurityReference = new SecurityReference() {

      @Override
      public List<String> getRoles() {
          List<String> roles = new ArrayList<String>();
          roles.add("Admin");
          roles.add("Anonymous");
          roles.add("Authenticated");
          roles.add("ceo");
          roles.add("cto");
          roles.add("dev");
          roles.add("dev_mgr");
          return roles;
      }

      @Override
      public List<String> getUsers() {
        List<String> users = new ArrayList<String>();
        users.add("joe");
        users.add("suzy");
        users.add("pat");
        users.add("tiffany");
        return users;
      }
      
    };
    
    
    
    conceptModel = new ConceptModel(new Concept());
    LocalizedStringSettings s1 = new LocalizedStringSettings();
    s1.setLocaleString("en_US", "chicken");
    s1.setLocaleString("es_ES", "pollo");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.NAME.getId(), s1));

    // begin setup RLS property
    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_USER, "joe"), "TRUE()");
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, "Admin"), "TRUE()");
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_USER, "suzy"), "TRUE()");
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, "ceo"), "TRUE()");
//    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_USER, "alice"), "TRUE()");
    RowLevelSecurity rls = new RowLevelSecurity(map);
    conceptModel.setProperty(new ConceptPropertyRowLevelSecurity(DefaultPropertyID.ROW_LEVEL_SECURITY.getId(), rls));
    // end setup RLS property
    
    LocalizedStringSettings s2 = new LocalizedStringSettings();
    s2.setLocaleString("en_US", "Where is the library?");
    s2.setLocaleString("es_ES", "¿Dónde está la biblioteca?");
    conceptModel.setProperty(new ConceptPropertyLocalizedString(DefaultPropertyID.DESCRIPTION.getId(), s2));
    conceptModel
        .setProperty(new ConceptPropertyColumnWidth(DefaultPropertyID.COLUMN_WIDTH.getId(), ColumnWidth.INCHES));
    conceptModel.setProperty(new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "overridden_table"));
    Concept parentConcept = new Concept();
    ConceptPropertyInterface prop1 = new ConceptPropertyString(DefaultPropertyID.FORMULA.getId(), "e=mc2");
    parentConcept.addProperty(prop1);
    ConceptPropertyInterface prop2 = new ConceptPropertyString(DefaultPropertyID.TARGET_SCHEMA.getId(), "test_schema");
    parentConcept.addProperty(prop2);
    conceptModel.setRelatedConcept(parentConcept, IConceptModel.REL_PARENT);

    Concept secConcept = new Concept();
    //    ConceptPropertyInterface sec1 = new ConceptPropertySecurity(DefaultPropertyID.SECURITY.getId(), new Security());
    //    secConcept.addProperty(sec1);
    ConceptPropertyInterface sec2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    secConcept.addProperty(sec2);
    conceptModel.setRelatedConcept(secConcept, IConceptModel.REL_INHERITED);

    Concept inConcept = new Concept();
    ConceptPropertyInterface in1 = new ConceptPropertyBoolean(DefaultPropertyID.EXACT.getId(), true);
    inConcept.addProperty(in1);
    ConceptPropertyInterface in2 = new ConceptPropertyString(DefaultPropertyID.TARGET_TABLE.getId(), "test_table");
    inConcept.addProperty(in2);
    conceptModel.setRelatedConcept(inConcept, IConceptModel.REL_SECURITY);

    Locales locales = new Locales();
    //    locales.addLocale(new LocaleMeta(Locales.EN_US, Messages.getString("Locales.USER_LOCALE_DESCRIPTION"), 1, true));
    locales.addLocale(new LocaleMeta("fr_FR", "French (France)", 2, true));
    locales.addLocale(new LocaleMeta("it_IT", "Italian (Italy)", 3, true));
    locales.addLocale(new LocaleMeta("es_ES", "Spanish (Spain)", 4, true));
    locales.addLocale(new LocaleMeta("de_DE", "German (Germany)", 5, true));
    schemaMeta = new SchemaMeta();
    schemaMeta.setLocales(locales);
  }

  public void run() {
    if (!PropsUI.isInitialized()) {
      Const.checkPentahoMetadataDirectory();
      PropsUI.init(new Display(), Const.getPropertiesFile());
    }
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }

  protected Point getInitialSize() {
    return new Point(600, 400);
  }

  protected Control createContents(final Composite parent) {
    RowLevelSecurityPropertyEditorWidget rlsWidget = new RowLevelSecurityPropertyEditorWidget(parent, SWT.NONE,
        conceptModel, "row_level_security", null, dummySecurityReference);
    rlsWidget.refresh();
    return rlsWidget;
  }

  public static void main(final String[] args) {
    new RowLevelSecurityPropertyEditorWidgetTestApp().run();
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Row Level Security Property Editor Widget Test Application");
    shell.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
  }

}
