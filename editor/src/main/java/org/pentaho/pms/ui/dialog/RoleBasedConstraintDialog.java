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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.concept.editor.AvailSecurityOwnersTableViewer;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel;

public class RoleBasedConstraintDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(RoleBasedConstraintDialog.class);

  public static enum Mode {
    ADD, EDIT
  }

  // ~ Instance fields ===================================================================================================

  private Mode mode;

  private AvailSecurityOwnersTableViewer availableOwnersViewer;

  private SecurityReference securityReference;

  private Text formulaField;

  private Label ownerField;

  private SecurityOwner ownerToEdit;

  private TableSelectionListener tableSelectionListener;

  private FormulaModifyListener formulaModifyListener;

  private IRowLevelSecurityModel rlsModel;

  private String originalFormula;

  // ~ Constructors ======================================================================================================

  /**
   * Puts the dialog in ADD mode.
   */
  public RoleBasedConstraintDialog(Shell parentShell, SecurityReference securityReference,
      IRowLevelSecurityModel rlsModel) {
    super(parentShell);
    this.rlsModel = rlsModel;
    this.securityReference = securityReference;
    this.mode = Mode.ADD;
  }

  /**
   * Puts the dialog in EDIT mode.
   */
  public RoleBasedConstraintDialog(Shell parentShell, SecurityOwner ownerToEdit, IRowLevelSecurityModel rlsModel) {
    super(parentShell);
    this.rlsModel = rlsModel;
    this.ownerToEdit = ownerToEdit;
    originalFormula = rlsModel.getFormula(ownerToEdit) != null ? rlsModel.getFormula(ownerToEdit) : "";
    this.mode = Mode.EDIT;
  }

  // ~ Methods ===========================================================================================================

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    if (mode == Mode.ADD) {
      shell.setText("Add Role Based Constraint");
    } else {
      shell.setText("Edit Role Based Constraint");
    }
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    c1.setLayoutData(new GridData(GridData.FILL_BOTH));
    c1.setLayout(new FormLayout());
    if (mode == Mode.ADD) {
      setTitle("Add Constraint");
      setMessage("Select one or more roles and enter a constraint formula.");
    } else {
      setTitle("Edit Constraint Formula");
      //      setMessage("Edit the formula associated with a role.");
    }

    if (mode == Mode.ADD) {
      Label availableLabel = new Label(c1, SWT.NULL);
      availableLabel.setText("Role:");
      FormData fdAvailLabel = new FormData();
      fdAvailLabel.left = new FormAttachment(0, 10);
      fdAvailLabel.top = new FormAttachment(0, 10);
      availableLabel.setLayoutData(fdAvailLabel);

      availableOwnersViewer = new AvailSecurityOwnersTableViewer(c1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI
          | SWT.H_SCROLL | SWT.V_SCROLL, securityReference, new ArrayList<SecurityOwner>(rlsModel.getOwners()));
      FormData fdAvailOwners = new FormData();
      fdAvailOwners.left = new FormAttachment(0, 10);
      fdAvailOwners.top = new FormAttachment(availableLabel, 10);
      fdAvailOwners.right = new FormAttachment(40, 0);
      fdAvailOwners.bottom = new FormAttachment(100, -10);
      availableOwnersViewer.getTable().setLayoutData(fdAvailOwners);
    } else {
      Label ownerLabel = new Label(c1, SWT.NULL);
      if (ownerToEdit.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
        ownerLabel.setText("Role:");
      } else {
        ownerLabel.setText("User:");
      }
      FormData fdOwnerLabel = new FormData();
      fdOwnerLabel.left = new FormAttachment(0, 10);
      fdOwnerLabel.top = new FormAttachment(0, 10);
      ownerLabel.setLayoutData(fdOwnerLabel);

      Label icon = new Label(c1, SWT.NULL);
      if (ownerToEdit.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
        icon.setImage(Constants.getImageRegistry(Display.getCurrent()).get("role-icon"));
      } else {
        icon.setImage(Constants.getImageRegistry(Display.getCurrent()).get("user-icon"));
      }
      FormData fdIcon = new FormData();
      fdIcon.left = new FormAttachment(0, 10);
      fdIcon.top = new FormAttachment(ownerLabel, 10);
      icon.setLayoutData(fdIcon);

      ownerField = new Label(c1, SWT.NULL);
      FormData fdOwnerField = new FormData();
      fdOwnerField.left = new FormAttachment(icon, 5);
      fdOwnerField.top = new FormAttachment(ownerLabel, 10);
      fdOwnerField.right = new FormAttachment(40, 0);
      ownerField.setLayoutData(fdOwnerField);
      ownerField.setText(ownerToEdit.getOwnerName());
    }

    Label formulaLabel = new Label(c1, SWT.NULL);
    formulaLabel.setText("Constraint Formula:");
    FormData fdFormulaLabel = new FormData();
    fdFormulaLabel.left = new FormAttachment(mode == Mode.ADD ? availableOwnersViewer.getTable() : ownerField, 10);
    fdFormulaLabel.top = new FormAttachment(0, 10);
    formulaLabel.setLayoutData(fdFormulaLabel);

    formulaField = new Text(c1, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    FormData fdFormulaField = new FormData();
    fdFormulaField.left = new FormAttachment(mode == Mode.ADD ? availableOwnersViewer.getTable() : ownerField, 10);
    fdFormulaField.top = new FormAttachment(formulaLabel, 10);
    fdFormulaField.right = new FormAttachment(100, -10);
    fdFormulaField.bottom = new FormAttachment(100, -10);
    formulaField.setLayoutData(fdFormulaField);
    if (mode == Mode.EDIT) {
      formulaField.setText(rlsModel.getFormula(ownerToEdit));
    }

    formulaField.setFont(Constants.getFontRegistry(Display.getCurrent()).get("formula-editor-font"));

    if (mode == Mode.ADD) {
      tableSelectionListener = new TableSelectionListener();
      availableOwnersViewer.getTable().addSelectionListener(tableSelectionListener);
    }

    formulaModifyListener = new FormulaModifyListener();
    formulaField.addModifyListener(formulaModifyListener);

    return c0;
  }

  protected void validate() {
    if (mode == Mode.ADD) {
      if (availableOwnersViewer.getTable().getSelection() != null
          && availableOwnersViewer.getTable().getSelection().length > 0
          && StringUtils.isNotBlank(formulaField.getText())) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
      } else {
        getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
    } else {
      // in edit mode, only enable OK button if the formula is not blank AND they have made changes to the original 
      // formula
      if (StringUtils.isNotBlank(formulaField.getText()) && !originalFormula.equals(formulaField.getText())) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
      } else {
        getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
    }

  }

  protected Control createContents(Composite parent) {
    // start with the OK button disabled
    Control c = super.createContents(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    return c;
  }

  protected Point getInitialSize() {
    return new Point(570, 500);
  }

  protected void okPressed() {
    // save what's changed in the dialog for when user calls getFormula or getAddedOwners
    if (mode == Mode.ADD) {
      Map<SecurityOwner, String> newEntries = new HashMap<SecurityOwner, String>();
      IStructuredSelection sel = (IStructuredSelection) availableOwnersViewer.getSelection();
      Object[] selectedItems = sel.toArray();
      for (int i = 0; i < selectedItems.length; i++) {
        SecurityOwner owner = (SecurityOwner) selectedItems[i];
        newEntries.put(owner, formulaField.getText());
      }
      rlsModel.putAll(newEntries);
    } else {
      rlsModel.put(ownerToEdit, formulaField.getText());
    }
    super.okPressed();
  }

  private class TableSelectionListener implements SelectionListener {

    public void widgetDefaultSelected(SelectionEvent e) {
      validate();
    }

    public void widgetSelected(SelectionEvent e) {
      validate();
    }

  }

  private class FormulaModifyListener implements ModifyListener {

    public void modifyText(ModifyEvent e) {
      validate();
    }

  }

}
