package org.pentaho.pms.ui.concept.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
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
import org.pentaho.pms.ui.concept.editor.RlsRoleBasedConstraintTableWidget.ConstraintEntry;
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

  private Map<SecurityOwner, String> map;

  private SelectionListener tableSelectionListener;

  public RlsRoleBasedConstraintWidget(final Composite parent, final int style, SecurityReference securityReference,
      Map<SecurityOwner, String> map) {
    super(parent, style);
    this.securityReference = securityReference;
    this.map = cloneRoleBasedConstraintMap(map);
    createContents();
  }

  protected Map<SecurityOwner, String> cloneRoleBasedConstraintMap(Map<SecurityOwner, String> map) {
    Map<SecurityOwner, String> copy = new HashMap<SecurityOwner, String>();
    for (Map.Entry<SecurityOwner, String> entry : map.entrySet()) {
      SecurityOwner clonedOwner = (SecurityOwner) entry.getKey().clone();
      copy.put(clonedOwner, entry.getValue());
    }
    return copy;
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
    tableWidget = new RlsRoleBasedConstraintTableWidget(this, SWT.NONE, map);
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
    new RoleBasedConstraintDialog(getShell(), owner, entry.getFormula()).open();
  }

  protected void addButtonPressed() {
    RoleBasedConstraintDialog diag = new RoleBasedConstraintDialog(getShell(), securityReference,
        new ArrayList<SecurityOwner>(map.keySet()));
    int returnCode = diag.open();
    if (Window.OK == returnCode) {
      List<SecurityOwner> owners = diag.getAddedOwners();
      String formula = diag.getFormula();
      // create array of ConstraintEntry
      ConstraintEntry[] entries = new ConstraintEntry[owners.size()];
      for (int i = 0; i < entries.length; i++) {
        entries[i] = new ConstraintEntry(owners.get(i), formula); 
      }
      tableWidget.getTableViewer().add(entries);
    }
  }

  protected void removeButtonPressed() {
    MessageDialog.openInformation(getShell(), "Place Holder", "Not implemented yet.");
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