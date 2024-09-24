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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel;

public class GlobalConstraintDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(GlobalConstraintDialog.class);

  public static enum Mode {
    ADD, EDIT
  }

  // ~ Instance fields ===================================================================================================

  private Mode mode;

  private Text formulaField;

  private FormulaModifyListener formulaModifyListener;

  private IRowLevelSecurityModel rlsModel;
  
  private String originalFormula;
  
  // ~ Constructors ======================================================================================================

  /**
   * Puts the dialog in EDIT mode.
   */
  public GlobalConstraintDialog(Shell parentShell, IRowLevelSecurityModel rlsModel) {
    super(parentShell);
    this.rlsModel = rlsModel;
    originalFormula = rlsModel.getGlobalConstraint() != null ? rlsModel.getGlobalConstraint() : "";
  }

  // ~ Methods ===========================================================================================================

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Edit");
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    c1.setLayoutData(new GridData(GridData.FILL_BOTH));
    c1.setLayout(new FormLayout());
    setTitle("Edit Global Constraint");
    setMessage("Edit the global constraint.");

    Label formulaLabel = new Label(c1, SWT.NULL);
    formulaLabel.setText("Constraint:");
    FormData fdFormulaLabel = new FormData();
    fdFormulaLabel.left = new FormAttachment(0, 10);
    fdFormulaLabel.top = new FormAttachment(0, 10);
    formulaLabel.setLayoutData(fdFormulaLabel);

    formulaField = new Text(c1, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    FormData fdFormulaField = new FormData();
    fdFormulaField.left = new FormAttachment(0, 10);
    fdFormulaField.top = new FormAttachment(formulaLabel, 10);
    fdFormulaField.right = new FormAttachment(100, -10);
    fdFormulaField.bottom = new FormAttachment(100, -10);
    formulaField.setLayoutData(fdFormulaField);
    if (null != rlsModel.getGlobalConstraint()) {
      formulaField.setText(rlsModel.getGlobalConstraint());
    }
    formulaField.setFont(Constants.getFontRegistry(Display.getCurrent()).get("formula-editor-font"));
    formulaModifyListener = new FormulaModifyListener();
    formulaField.addModifyListener(formulaModifyListener);

    return c0;
  }

  protected void validate() {
    if (StringUtils.isNotBlank(formulaField.getText()) && !originalFormula.equals(formulaField.getText())) {
      getButton(IDialogConstants.OK_ID).setEnabled(true);
    } else {
      getButton(IDialogConstants.OK_ID).setEnabled(false);
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
    rlsModel.setGlobalConstraint(formulaField.getText());
    super.okPressed();
  }

  private class FormulaModifyListener implements ModifyListener {

    public void modifyText(ModifyEvent e) {
      validate();
    }

  }

}
