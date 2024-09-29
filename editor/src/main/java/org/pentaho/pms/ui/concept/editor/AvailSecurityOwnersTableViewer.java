/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.pms.ui.concept.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.util.GUIResource;

public class AvailSecurityOwnersTableViewer extends TableViewer {

  static final int TYPE_COLUMN_ID = 0;

  static final int NAME_COLUMN_ID = 1;

  List<SecurityOwner> allUnassignedUsersAndRoles;

  SecurityReference securityReference;

  public AvailSecurityOwnersTableViewer(Composite parent, int style, SecurityReference securityReference,
      List<SecurityOwner> usedOwners) {
    super(parent, style);
    this.securityReference = securityReference;
    allUnassignedUsersAndRoles = findUnused(usedOwners, securityReference.getUsers(), securityReference.getRoles());
    initTable();
  }

  protected List<SecurityOwner> findUnused(List<SecurityOwner> usedOwners, List<String> users, List<String> roles) {
    List<SecurityOwner> allOwners = new ArrayList<SecurityOwner>();
    // make a single list with SecurityOwner objects
    for (String user : users) {
      allOwners.add(new SecurityOwner(SecurityOwner.OWNER_TYPE_USER, user));
    }
    for (String role : roles) {
      allOwners.add(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, role));
    }
    return new ArrayList<SecurityOwner>(CollectionUtils.subtract(allOwners, usedOwners));
  }

  class MyContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object arg0) {
      return allUnassignedUsersAndRoles.toArray();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
    }
  }

  class MyLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage(Object secOwner, int column) {
      Image image = null;
      if ((secOwner instanceof SecurityOwner) && (column == TYPE_COLUMN_ID)) {
        SecurityOwner securityOwner = (SecurityOwner) secOwner;
        if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_USER) {
          image = GUIResource.getInstance().getImageUser();
        } else if (securityOwner.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
          image = GUIResource.getInstance().getImageRole();
        }
      }
      return image;
    }

    public String getColumnText(Object secOwner, int column) {
      String text = null;
      if ((secOwner instanceof SecurityOwner) && (column == NAME_COLUMN_ID)) {
        SecurityOwner securityOwner = (SecurityOwner) secOwner;
        text = securityOwner.getOwnerName();
      }
      return text;
    }
  }

  private void initTable() {
    Table table = getTable();

    TableColumn column = new TableColumn(table, SWT.LEFT);
    column.setText("Type");
    column.setWidth(30);

    column = new TableColumn(table, SWT.LEFT);
    column.setText("Name");
    column.setWidth(180);

    setContentProvider(new MyContentProvider());
    setLabelProvider(new MyLabelProvider());
    setSorter(new ViewerSorter() {

      public int category(Object arg0) {
        return -((SecurityOwner) arg0).getOwnerType();
      }

    });
    setInput("hello");
  }

  public SecurityOwner[] getSelectedOwners() {
    StructuredSelection selection = (StructuredSelection) getSelection();
    @SuppressWarnings("all")
    Collection<SecurityOwner> c = selection.toList();
    return c.toArray(new SecurityOwner[0]);
  }

}
