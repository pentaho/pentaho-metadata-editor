package org.pentaho.pms.ui.concept.editor;

import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.schema.security.SecurityOwner;

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
  
  private RlsRoleBasedConstraintTableWidget table;
  
  private Map<SecurityOwner, String> map;

  public RlsRoleBasedConstraintWidget(final Composite parent, final int style, Map<SecurityOwner, String> map) {
    super(parent, style);
    this.map = map;
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
    if (null != table) {
      table.dispose();
    }
    table = new RlsRoleBasedConstraintTableWidget(this, SWT.NONE, map);
    FormData fdFormula = new FormData();
    fdFormula.top = new FormAttachment(toolBar, 10);
    fdFormula.right = new FormAttachment(100, 0);
    fdFormula.left = new FormAttachment(0, 0);
    fdFormula.height = 100;
    table.setLayoutData(fdFormula);
  }

  protected void editButtonPressed() {
    MessageDialog.openInformation(getShell(), "Place Holder", "Not implemented yet.");
  }

  protected void addButtonPressed() {
    MessageDialog.openInformation(getShell(), "Place Holder", "Not implemented yet.");
  }
  
  protected void removeButtonPressed() {
    MessageDialog.openInformation(getShell(), "Place Holder", "Not implemented yet.");
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    toolBar.setEnabled(enabled);
    table.setEnabled(enabled);
  }
  
}