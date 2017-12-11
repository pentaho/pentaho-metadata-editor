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

package org.pentaho.pms.ui.concept.editor;

import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.util.GUIResource;

public class AddSecurityPermsDialog extends Dialog {

  SecurityReference securityReference;
  Security security;
  AvailSecurityOwnersTableViewer availSecurityOwnersTable;
  SecurityTableViewer securityTableViewer;
  SecurityTablePermEditor securityTablePermEditor;
  
  public AddSecurityPermsDialog(Shell arg0, SecurityReference securityReference, Security security) {
    super(arg0);
    this.securityReference = securityReference;
    this.security = security;
    setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Add Permissions"); 
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);    
    composite.setLayout(new GridLayout(3, false));
    Label label = new Label(composite, SWT.NONE);
    label.setText("Available Users/Roles");
    label = new Label(composite, SWT.NONE);
    label = new Label(composite, SWT.NONE);
    label.setText("Assigned Users/Roles");
    availSecurityOwnersTable = new AvailSecurityOwnersTableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, securityReference, security.getOwners());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    availSecurityOwnersTable.getTable().setLayoutData(gridData);
    
    Composite btnComposite = new Composite(composite, SWT.NONE);
    btnComposite.setLayout(new GridLayout());
    btnComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    Button moveRightBtn = new Button(btnComposite, SWT.PUSH);
    moveRightBtn.setImage(GUIResource.getInstance().getImageArrowRight());
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.verticalAlignment = SWT.CENTER;
    moveRightBtn.setLayoutData(gridData);
    moveRightBtn.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }
      public void widgetSelected(SelectionEvent arg0) {
        addPermsToSelectedUsers();
      }
    });
    Button moveLeftBtn = new Button(btnComposite, SWT.PUSH);
    moveLeftBtn.setImage(GUIResource.getInstance().getImageArrowLeft());
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.verticalAlignment = SWT.CENTER;
    moveLeftBtn.setLayoutData(gridData);
    moveLeftBtn.addSelectionListener(new SelectionListener(){
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }
      public void widgetSelected(SelectionEvent arg0) {
        removePermsFromSelectedUsers();
      }
    });
    
    securityTableViewer = new SecurityTableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 200;
    securityTableViewer.getTable().setLayoutData(gridData);
    return composite;
  }
  
  protected void addPermsToSelectedUsers() {
    
    SecurityOwner[] selectedOwners = availSecurityOwnersTable.getSelectedOwners();
    for (int i = 0; i < selectedOwners.length; i++) {
      securityTableViewer.addOwner(selectedOwners[i]);
      removeOwner(selectedOwners[i]);
    }
    availSecurityOwnersTable.setSelection(new StructuredSelection());
    securityTableViewer.setSelection(new StructuredSelection(selectedOwners));
    if (selectedOwners.length > 0) {
      securityTableViewer.getTable().forceFocus();
    }
  }
  
  protected void removePermsFromSelectedUsers() {
    SecurityOwner[] selectedOwners = securityTableViewer.getSelectedOwners();
    for (int i = 0; i < selectedOwners.length; i++) {
      addOwner(selectedOwners[i]);
      securityTableViewer.removeOwner(selectedOwners[i]);
    }
  }
  
  public Security getSecurity() {
    return security;
  }

  protected void okPressed() {
    Security newSecurity = new Security();
    for (Iterator iterator = securityTableViewer.getSecuritySettings().getOwners().iterator(); iterator.hasNext();) {
      SecurityOwner securityOwner = (SecurityOwner)iterator.next();
      newSecurity.putOwnerRights(securityOwner, 1); // 1 is EXECUTE right.
    }
    security = newSecurity;
    super.okPressed();
  }
  
  public void addOwner(SecurityOwner owner) {
    security.putOwnerRights(owner, 0);
    availSecurityOwnersTable.refresh();
  }
  
  public void removeOwner(SecurityOwner owner) {
    security.removeOwnerRights(owner);
    availSecurityOwnersTable.remove(owner);
  }
  
  
}
