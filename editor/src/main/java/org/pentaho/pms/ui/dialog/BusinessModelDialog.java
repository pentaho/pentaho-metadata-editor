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

package org.pentaho.pms.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.ui.concept.editor.ConceptModel;
import org.pentaho.pms.ui.concept.editor.IConceptModel;
import org.pentaho.pms.ui.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.ui.concept.editor.PropertyWidgetManager2;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

/***
 * Represents a business model
 *
 * @since 30-aug-2006
 *
 */
public class BusinessModelDialog extends Dialog {

  private static final Log logger = LogFactory.getLog(BusinessModelDialog.class);

  protected String activeLocale;

  protected Map<String,Locales> propertyEditorContext = new HashMap<String,Locales>();

  private Text wId;

  private Combo conField;

  private IConceptModel conceptModel;

  private ConceptUtilityInterface conceptUtil;

  private Locales locales;
  
  private SchemaMeta schemaMeta;

  private ComboViewer comboViewer;

  private static final String DUMMY_CON_NAME = "^never^going^to^use^this";
  
  private PropertyWidgetManager2 propertyWidgetManager;

  /**
   * mlowery: hack. DatabaseMeta.equals has a bug (PDI-9) which prevents adding a simple string to the comboviewer's
   * input that would serve as a "no selection" option. This dummy instance when detected in the label provider simply
   * returns empty string.
   */
  private DatabaseMeta dummyConInstance = new DatabaseMeta(DUMMY_CON_NAME, "Oracle", "Native", "", "", "1521", "", "");

  public BusinessModelDialog(final Shell parent, final int style, final ConceptUtilityInterface conceptUtil,
      final SchemaMeta schemaMeta) {
    super(parent);
    this.conceptModel = new ConceptModel(conceptUtil.getConcept());
    this.conceptUtil = conceptUtil;
    this.schemaMeta = schemaMeta;
    Locales locales = schemaMeta.getLocales();
    activeLocale = schemaMeta.getLocales().getActiveLocale();
    propertyEditorContext.put("locales", locales);
    this.schemaMeta = schemaMeta;
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Business Model Properties");
  }

  protected Point getInitialSize() {
    return new Point(1000, 800);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    Control top = createTop(container);

    FormData fdTop = new FormData();
    fdTop.left = new FormAttachment(0, 0);
    fdTop.top = new FormAttachment(0, 0);
    fdTop.right = new FormAttachment(100, 0);
    top.setLayoutData(fdTop);

    Composite conceptEditor = new Composite(container, SWT.NONE);
    conceptEditor.setLayout(new FillLayout());

    Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new FillLayout());
    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 10;
    PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE);
    propertyNavigationWidget.setConceptModel(conceptModel);
    propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, propertyEditorContext, schemaMeta.getSecurityReference());
    propertyWidgetManager.setConceptModel(conceptModel);
    propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
    s0.setWeights(new int[] { 1, 2 });
    
    FormData fdConcept = new FormData();
    fdConcept.left = new FormAttachment(0, 0);
    fdConcept.top = new FormAttachment(top, 10);
    fdConcept.right = new FormAttachment(100, 0);
    fdConcept.bottom = new FormAttachment(100, 0);
    conceptEditor.setLayoutData(fdConcept);

    return c0;
  }

  protected Control createTop(final Composite parent) {
    Composite c0 = new Composite(parent, SWT.NONE);
    c0.setLayout(new FormLayout());

    Label wlId = new Label(c0, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    wId = new Text(c0, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

    FormData fdlId = new FormData();
    fdlId.left = new FormAttachment(0, 0);

    fdlId.top = new FormAttachment(wId, 0, SWT.CENTER);
    wlId.setLayoutData(fdlId);

    Label conLabel = new Label(c0, SWT.RIGHT);
    conLabel.setText("Connection:");

    conField = new Combo(c0, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);

    comboViewer = new ComboViewer(conField);

    comboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        List ul = (List) inputElement;
        return ul.toArray();
      }

      public void dispose() {
        if (logger.isDebugEnabled()) {
          logger.debug("Disposing ...");
        }
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (logger.isDebugEnabled()) {
          logger.debug("Input changed: old=" + oldInput + ", new=" + newInput);
        }
      }
    });

    List<Object> list2 = new ArrayList<Object>();
    list2.add(dummyConInstance);
    list2.addAll(schemaMeta.getDatabases().getList());
    
    comboViewer.setInput(list2);

    comboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(Object element) {
        return null;
      }

      public String getText(Object element) {
        DatabaseMeta meta = (DatabaseMeta) element;
        if (DUMMY_CON_NAME.equals(meta.getName())) {
          return "";
        } else {
          return ((DatabaseMeta) element).getName();
        }
      }
    });

    BusinessModel busModel = (BusinessModel) conceptUtil;
    if (null != busModel.getConnection()) {
      comboViewer.setSelection(new StructuredSelection(busModel.getConnection()));
    } else {
      comboViewer.setSelection(new StructuredSelection(dummyConInstance));
    }

    FormData fdId = new FormData();
    fdId.left = new FormAttachment(wlId, 10);
    fdId.top = new FormAttachment(0, 0);
    fdId.right = new FormAttachment(conLabel, -10);
    wId.setLayoutData(fdId);

    FormData fdConLabel = new FormData();
    fdConLabel.left = new FormAttachment(50, 0);
    fdConLabel.top = new FormAttachment(conField, 0, SWT.CENTER);
    conLabel.setLayoutData(fdConLabel);

    FormData fdConField = new FormData();
    fdConField.left = new FormAttachment(conLabel, 10);
    fdConField.top = new FormAttachment(0, 0);
    fdConField.right = new FormAttachment(100, 0);
    conField.setLayoutData(fdConField);

    if (conceptUtil.getId() != null) {
      wId.setText(conceptUtil.getId());
      wId.selectAll();
    }
    return c0;
  }

  protected void okPressed() {
    boolean hasErrors = popupValidationErrorDialogIfNecessary();
      
    if (!hasErrors) {
      try {
        conceptUtil.setId(wId.getText());
      } catch (ObjectAlreadyExistsException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
        MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
            "PhysicalTableDialog.USER_ERROR_PHYSICAL_TABLE_ID_EXISTS", wId.getText()));
        return;
      }

      // attempt to set the connection
      IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
      DatabaseMeta con = (DatabaseMeta) selection.getFirstElement();
      BusinessModel busModel = (BusinessModel) conceptUtil;
      if (!DUMMY_CON_NAME.equals(con.getName())) {
        busModel.setConnection((DatabaseMeta) con);
      } else {
        busModel.clearConnection();
      }      
      
      super.okPressed();
    }
    
  }

  /**
   * Unfortunate duplication of code. (Same method is in AbstractTableDialog.)
   */
  protected boolean popupValidationErrorDialogIfNecessary() {
    List<String> errorMessages = propertyWidgetManager.validateWidgets();
    if (errorMessages.isEmpty()) {
      return false;
    } else {
      StringBuilder buf = new StringBuilder();
      for (String errorMessage : errorMessages) {
        buf.append(errorMessage + "\n");
      }
      MessageDialog.openError(getShell(), "Errors", buf.toString());
      return true;
    }
  }   
}
