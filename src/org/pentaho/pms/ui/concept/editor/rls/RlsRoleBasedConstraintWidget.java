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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.RowLevelSecurityPropertyEditorWidget;
import org.pentaho.pms.ui.concept.editor.rls.RlsRoleBasedConstraintTableWidget.ConstraintEntry;
import org.pentaho.pms.ui.dialog.RoleBasedConstraintDialog;

/**
 * Part of {@link RowLevelSecurityPropertyEditorWidget}.
 * 
 * @author mlowery
 */
public class RlsRoleBasedConstraintWidget extends Composite {

  private ToolBar toolBar;

  private ToolItem editButton;

  private ToolItem addButton;

  private ToolItem removeButton;

  private RlsRoleBasedConstraintTableWidget tableWidget;

  private SecurityReference securityReference;

  private SelectionListener tableSelectionListener;

  private IRowLevelSecurityModel rlsModel;

  public RlsRoleBasedConstraintWidget(final Composite parent, final int style, SecurityReference securityReference,
      IRowLevelSecurityModel rlsModel) {
    super(parent, style);
    this.securityReference = securityReference;
    this.rlsModel = rlsModel;
    createContents();
  }

  private void createContents() {
    setLayout(new FormLayout());
    createToolBar();
    createTable();
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
    addButton = new ToolItem(toolBar, SWT.PUSH);
    addButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-add-button"));
    addButton.setToolTipText("Add");
    addButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        addButtonPressed();
      }
    });
    removeButton = new ToolItem(toolBar, SWT.PUSH);
    removeButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-del-button"));
    removeButton.setToolTipText("Remove");
    removeButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        removeButtonPressed();
      }
    });

  }

  protected void createTable() {
    if (null != tableWidget) {
      tableWidget.dispose();
    }
    tableWidget = new RlsRoleBasedConstraintTableWidget(this, SWT.NONE, rlsModel);
    FormData fdFormula = new FormData();
    fdFormula.top = new FormAttachment(toolBar, 10);
    fdFormula.right = new FormAttachment(100, 0);
    fdFormula.left = new FormAttachment(0, 0);
    fdFormula.height = 100;
    tableWidget.setLayoutData(fdFormula);
    tableSelectionListener = new TableSelectionListener();
    tableWidget.getTableViewer().getTable().addSelectionListener(tableSelectionListener);
  }

  protected void editButtonPressed() {
    IStructuredSelection selection = (IStructuredSelection) tableWidget.getTableViewer().getSelection();
    ConstraintEntry entry = (ConstraintEntry) selection.getFirstElement();
    SecurityOwner owner = new SecurityOwner(entry.getOwnerType(), entry.getOwnerName());
    new RoleBasedConstraintDialog(getShell(), owner, rlsModel).open();
  }

  protected void addButtonPressed() {
    try {
      securityReference.getUsers();
    } catch (Exception e) {
      MessageDialog.openError(getShell(), "Error", "Unable to display dialog. Either your security configuration "
          + "is incorrect or your Pentaho BI Server is not " + "running or not accessible.");
      return;
    }
    new RoleBasedConstraintDialog(getShell(), securityReference, rlsModel).open();
  }

  protected void removeButtonPressed() {
    IStructuredSelection sel = (IStructuredSelection) tableWidget.getTableViewer().getSelection();
    ConstraintEntry entry = (ConstraintEntry) sel.getFirstElement();
    SecurityOwner owner = entry.getOwner();

    boolean delete = MessageDialog.openConfirm(getShell(), "Confirm", String.format(
        "Are you sure you want to remove the entry for %s '%s'?",
        owner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE ? "role" : "user", owner.getOwnerName()));

    if (delete) {
      rlsModel.removeRoleBasedConstraint(owner);
      rowNotSelected();
    }
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    toolBar.setEnabled(enabled);
    tableWidget.setEnabled(enabled);
    if (tableWidget.getTableViewer().getTable().getSelectionCount() != 0) {
      rowSelected();
    } else {
      rowNotSelected();
    }
  }

  protected void rowSelected() {
    editButton.setEnabled(true);
    removeButton.setEnabled(true);
  }

  protected void rowNotSelected() {
    editButton.setEnabled(false);
    removeButton.setEnabled(false);
  }

  private class TableSelectionListener implements SelectionListener {

    public void widgetDefaultSelected(SelectionEvent e) {
      rowSelected();
    }

    public void widgetSelected(SelectionEvent e) {
      rowSelected();
    }

  }

}
