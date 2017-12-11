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

package org.pentaho.pms.ui.concept.editor.rls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.RowLevelSecurityPropertyEditorWidget;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel.IRlsModelListener;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel.RlsModelEvent;
import org.pentaho.pms.ui.dialog.GlobalConstraintDialog;

/**
 * Part of {@link RowLevelSecurityPropertyEditorWidget}.
 * 
 * @author mlowery
 */
public class RlsGlobalConstraintWidget extends Composite {

  private ToolBar toolBar;

  private ToolItem editButton;

  private Text formulaField;

  private IRowLevelSecurityModel rlsModel;

  public RlsGlobalConstraintWidget(final Composite parent, final int style, IRowLevelSecurityModel rlsModel) {
    super(parent, style);
    this.rlsModel = rlsModel;
    createContents();
  }

  private void createContents() {
    setLayout(new FormLayout());
    createToolBar();
    createFormulaField();

    rlsModel.addRlsModelListener(new IRlsModelListener() {

      public void rlsModelModified(RlsModelEvent e) {
        // have to have the 'if' to prevent ping-pong action between the RlsModelEvent and the ModifyEvent
        if (!rlsModel.getGlobalConstraint().equals(formulaField.getText())) {
          formulaField.setText(rlsModel.getGlobalConstraint());
        }
      }

    });
  }

  protected final void createToolBar() {
    if (null != toolBar) {
      toolBar.dispose();
    }
    toolBar = new ToolBar(this, SWT.FLAT);

    FormData fdToolBar = new FormData();
    fdToolBar.top = new FormAttachment(0, 0);
    fdToolBar.right = new FormAttachment(100, 0);
    toolBar.setLayoutData(fdToolBar);

    editButton = new ToolItem(toolBar, SWT.PUSH);
    editButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("concept-editor-app"));
    editButton.setToolTipText("Edit");
    editButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        editButtonPressed();
      }
    });
  }

  protected void createFormulaField() {
    if (null != formulaField) {
      formulaField.dispose();
    }
    formulaField = new Text(this, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    formulaField.setFont(Constants.getFontRegistry(Display.getCurrent()).get("formula-editor-font"));
    FormData fdFormula = new FormData();
    fdFormula.top = new FormAttachment(toolBar, 10);
    fdFormula.right = new FormAttachment(100, 0);
    fdFormula.left = new FormAttachment(0, 0);
    fdFormula.height = 50;
    formulaField.setLayoutData(fdFormula);
    if (rlsModel.getGlobalConstraint() != null) {
      formulaField.setText(rlsModel.getGlobalConstraint());
    }
    
    formulaField.addModifyListener(new ModifyListener() {

      public void modifyText(ModifyEvent e) {
        // have to have the 'if' to prevent ping-pong action between the RlsModelEvent and the ModifyEvent
        if (!rlsModel.getGlobalConstraint().equals(formulaField.getText())) {
          rlsModel.setGlobalConstraint(formulaField.getText());
        }
      }
      
    });
  }

  protected void editButtonPressed() {
    new GlobalConstraintDialog(getShell(), rlsModel).open();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    toolBar.setEnabled(enabled);
    formulaField.setEnabled(enabled);
  }

}
