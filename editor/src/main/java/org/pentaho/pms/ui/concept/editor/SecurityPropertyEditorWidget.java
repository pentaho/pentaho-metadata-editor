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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.util.GUIResource;

public class SecurityPropertyEditorWidget extends AbstractPropertyEditorWidget {

  private static final Log logger = LogFactory.getLog(SecurityPropertyEditorWidget.class);

  private SecurityTableViewer securityTableViewer;

  private SecurityTablePermEditor securityTablePermEditor;

  private SecurityReference securityReference;

  private ToolItem addPermsToolItem;

  private ToolItem removePermsToolItem;

  private boolean contentsCreated = false;

  private boolean securityOK;

  public SecurityPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context, SecurityReference securityReference) {
    // delay call to createContents!
    super(parent, style, conceptModel, propertyId, context, true);
    this.securityReference = securityReference;
    createContents();
    refresh();
  }

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        SecurityPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    securityOK = true;
    try {
      securityReference.getAcls();
    } catch (Exception e) {
      if (logger.isWarnEnabled()) {
        logger.warn("exception during connection to security service; ignoring");
      }
      securityOK = false;
    }

    if (!securityOK) {
      parent.setLayout(new GridLayout(1, false));
      Label label = new Label(parent, SWT.NONE);
      label
          .setText("Unable to display widget. Either your security \nconfiguration is incorrect or your Pentaho BI \nServer is not running or not accessible.");
    } else {

      parent.setLayout(new GridLayout(3, false));

      Label label = new Label(parent, SWT.NONE);
      label.setText("Selected Users/Groups");

      ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalAlignment = SWT.END;
      toolBar.setLayoutData(gridData);

      addPermsToolItem = new ToolItem(toolBar, SWT.NULL);
      addPermsToolItem.setImage(GUIResource.getInstance().getImageGenericAdd());
      addPermsToolItem.setToolTipText("Add New Users/Groups");
      addPermsToolItem.addSelectionListener(new SelectionListener() {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
          addNewUsersOrGroups();
        }
      });

      removePermsToolItem = new ToolItem(toolBar, SWT.NULL);
      removePermsToolItem.setImage(GUIResource.getInstance().getImageGenericDelete());
      removePermsToolItem.setToolTipText("Remove All Permissions From Selected Users/Groups");
      removePermsToolItem.addSelectionListener(new SelectionListener() {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
          removeSelectedUsersAndGroups();
        }
      });

      securityTableViewer = new SecurityTableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = 200;
      gridData.widthHint = 590;
      gridData.horizontalSpan = 2;
      securityTableViewer.getTable().setLayoutData(gridData);
    }
    contentsCreated = true;
  }

  protected void widgetDisposed(final DisposeEvent e) {
    // TODO Auto-generated method stub

  }

  public String validate() {
    return null;
  }

  public Object getValue() {
    return securityTableViewer.getSecuritySettings();
  }

  protected void setValue(final Object value) {
    securityTableViewer.setSecuritySettings((Security) value);
  }

  protected void addNewUsersOrGroups() {
    AddSecurityPermsDialog addSecurityPermsDialog = new AddSecurityPermsDialog(securityTableViewer.getTable()
        .getShell(), securityReference, securityTableViewer.getSecuritySettings());
    if (addSecurityPermsDialog.open() == Window.OK) {
      Security security = addSecurityPermsDialog.getSecurity();
      java.util.List owners = security.getOwners();
      for (Iterator iter = owners.iterator(); iter.hasNext();) {
        SecurityOwner element = (SecurityOwner) iter.next();
        securityTableViewer.addOwner(element, security.getOwnerRights(element));
      }
    }
  }

  protected void removeSelectedUsersAndGroups() {
    securityTableViewer.removeSelectedOwners();
  }

  public void refresh() {
    refreshOverrideButton();

    if (securityOK) {
      addPermsToolItem.setEnabled(isEditable());
      removePermsToolItem.setEnabled(isEditable());
      securityTableViewer.setSelection(new StructuredSelection());
      securityTableViewer.getTable().setEnabled(isEditable());
      setValue(getProperty().getValue());
    }

  }

  public void cleanup() {
  }
}
