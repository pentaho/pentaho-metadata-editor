package org.pentaho.pms.ui.concept.editor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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

/**
 * Part of {@link RowLevelSecurityPropertyEditorWidget}.
 * 
 * @author mlowery
 */
public class RlsGlobalConstraintWidget extends Composite {

  private ToolBar toolBar;

  private ToolItem editButton;
  
  private Text formulaField;

  public RlsGlobalConstraintWidget(final Composite parent, final int style) {
    super(parent, style);
    createContents();
  }

  private void createContents() {
    setLayout(new FormLayout());
    createToolBar();
    createFormulaField();
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
    formulaField = new Text(this, SWT.WRAP | SWT.MULTI | SWT.BORDER  | SWT.H_SCROLL | SWT.V_SCROLL);
    formulaField.setFont(Constants.getFontRegistry(Display.getCurrent()).get("formula-editor-font"));
    FormData fdFormula = new FormData();
    fdFormula.top = new FormAttachment(toolBar, 10);
    fdFormula.right = new FormAttachment(100, 0);
    fdFormula.left = new FormAttachment(0, 0);
    fdFormula.height = 50;
    formulaField.setLayoutData(fdFormula);
  }

  protected void editButtonPressed() {
    MessageDialog.openInformation(getShell(), "Place Holder", "Not implemented yet.");
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    toolBar.setEnabled(enabled);
    formulaField.setEnabled(enabled);
  }
  
}