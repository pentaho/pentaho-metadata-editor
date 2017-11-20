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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel.IRlsModelListener;
import org.pentaho.pms.ui.concept.editor.rls.IRowLevelSecurityModel.RlsModelEvent;

/**
 * A specialized table for holding localized string values. Automatically persists changes to the model as they occur.
 * @author mlowery
 */
public class RlsRoleBasedConstraintTableWidget extends Composite {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(RlsRoleBasedConstraintTableWidget.class);

  // ~ Instance fields =================================================================================================

  private Table table;

  private TableViewer tableViewer;

  private String[] columnNames = new String[] { "", "Role", "Constraint Formula" };

  private IRowLevelSecurityModel rlsModel;

  // ~ Constructors ====================================================================================================

  public RlsRoleBasedConstraintTableWidget(final Composite parent, final int style,
      final IRowLevelSecurityModel rlsModel) {
    super(parent, style);
    this.rlsModel = rlsModel;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected Map<SecurityOwner, String> cloneRoleBasedConstraintMap(Map<SecurityOwner, String> map) {
    Map<SecurityOwner, String> copy = new HashMap<SecurityOwner, String>();
    for (Map.Entry<SecurityOwner, String> entry : map.entrySet()) {
      SecurityOwner clonedOwner = (SecurityOwner) entry.getKey().clone();
      copy.put(clonedOwner, entry.getValue());
    }
    return copy;
  }

  private List getColumnNames() {
    return Arrays.asList(columnNames);
  }

  private void createContents() {
    setLayout(new FormLayout());

    // Create the table
    createTable(this);

    // Create and setup the TableViewer
    createTableViewer();
    tableViewer.setContentProvider(new RlsRoleBasedConstraintTableContentProvider());
    tableViewer.setLabelProvider(new RlsRoleBasedConstraintTableLabelProvider());

    // TODO change this!
    tableViewer.setInput("hello");
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    table.setEnabled(enabled);
  }

  /**
   * Create the Table
   */
  private void createTable(Composite parent) {
    int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

    table = new Table(parent, style);

    FormData fdTable = new FormData();
    fdTable.top = new FormAttachment(0, 0);
    fdTable.left = new FormAttachment(0, 0);
    fdTable.right = new FormAttachment(100, 0);
    fdTable.bottom = new FormAttachment(100, 0);
    table.setLayoutData(fdTable);

    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    TableColumn column = new TableColumn(table, SWT.CENTER, 0);
    column.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent e) {
        tableViewer.setSorter(new RlsRoleBasedConstraintTableSorter(0));
      }
    });
    column.setText(columnNames[0]);
    column.setWidth(30);

    column = new TableColumn(table, SWT.LEFT, 1);
    column.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent e) {
        tableViewer.setSorter(new RlsRoleBasedConstraintTableSorter(1));
      }
    });
    column.setText(columnNames[1]);
    column.setWidth(100);

    column = new TableColumn(table, SWT.LEFT, 2);
    column.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent e) {
        tableViewer.setSorter(new RlsRoleBasedConstraintTableSorter(2));
      }
    });
    column.setText(columnNames[2]);
    column.setWidth(300);

  }

  /**
   * Create the TableViewer
   */
  private void createTableViewer() {

    tableViewer = new TableViewer(table);
    tableViewer.setUseHashlookup(true);

    tableViewer.setColumnProperties(columnNames);

    // Set the default sorter for the viewer
    tableViewer.setSorter(new RlsRoleBasedConstraintTableSorter(0));

    rlsModel.addRlsModelListener(new IRlsModelListener() {

      public void rlsModelModified(RlsModelEvent e) {
        tableViewer.refresh();
      }

    });
  }

  class RlsRoleBasedConstraintTableSorter extends ViewerSorter {

    private int column;

    public RlsRoleBasedConstraintTableSorter(int column) {
      this.column = column;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      ConstraintEntry entry1 = (ConstraintEntry) e1;
      ConstraintEntry entry2 = (ConstraintEntry) e2;
      switch (column) {
        case 0:
          return compareOwnerType(entry1, entry2);
        case 1:
          return compareOwnerName(entry1.getOwnerName(), entry2.getOwnerName());
        default:
          return compareFormula(entry1.getFormula(), entry2.getFormula());
      }

    }

    private int compareOwnerType(ConstraintEntry entry1, ConstraintEntry entry2) {
      // roles before users then name within roles
      if (entry1.getOwnerType() == entry2.getOwnerType()) {
        return entry1.getOwnerName().compareTo(entry2.getOwnerName());
      } else if (SecurityOwner.OWNER_TYPE_ROLE == entry1.getOwnerType()) {
        return -1;
      } else {
        return 1;
      }
    }

    private int compareFormula(String formula, String formula2) {
      return formula.compareTo(formula2);
    }

    private int compareOwnerName(String string, String string2) {
      return string.compareTo(string2);
    }

  }

  class RlsRoleBasedConstraintTableContentProvider implements IStructuredContentProvider {
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(final Object parent) {
      ConstraintEntry[] entries = new ConstraintEntry[rlsModel.getOwners().size()];
      int i = 0;
      for (SecurityOwner owner : rlsModel.getOwners()) {
        entries[i] = new ConstraintEntry(owner, rlsModel.getFormula(owner));
        i++;
      }
      return entries;
    }
  }

  public static class ConstraintEntry {
    private SecurityOwner owner;

    private String formula;

    public ConstraintEntry(final SecurityOwner owner, final String formula) {
      this.owner = owner;
      this.formula = formula;
    }

    public int getOwnerType() {
      return owner.getOwnerType();
    }

    public void setOwnerType(int type) {
      owner.setOwnerType(type);
    }

    public String getOwnerName() {
      return owner.getOwnerName();
    }

    public SecurityOwner getOwner() {
      return owner;
    }

    public void setOwnerName(String name) {
      owner.setOwnerName(name);
    }

    public String getFormula() {
      return formula;
    }

    public void setFormula(String formula) {
      this.formula = formula;
    }

    public boolean equals(Object obj) {
      if (obj instanceof ConstraintEntry == false) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      ConstraintEntry rhs = (ConstraintEntry) obj;
      return new EqualsBuilder().append(owner, rhs.owner).append(formula, rhs.formula).isEquals();
    }

    public int hashCode() {
      return new HashCodeBuilder(29, 163).append(owner).append(formula).toHashCode();
    }

    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(owner).append(formula).toString();
    }

  }

  private class RlsRoleBasedConstraintTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public String getColumnText(final Object element, final int columnIndex) {
      String result = "";
      ConstraintEntry entry = (ConstraintEntry) element;
      switch (columnIndex) {
        case 0:
          break;
        case 1:
          result = entry.getOwnerName();
          break;
        case 2:
          result = entry.getFormula();
          break;
        default:
          break;
      }
      return result;
    }

    public Image getColumnImage(final Object element, final int columnIndex) {
      if (columnIndex == 0) {
        ConstraintEntry entry = (ConstraintEntry) element;
        if (entry.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
          return Constants.getImageRegistry(Display.getCurrent()).get("role-icon");
        } else {
          return Constants.getImageRegistry(Display.getCurrent()).get("user-icon");
        }
      } else {
        return null;
      }
    }
  }

  public void refresh() {
    tableViewer.refresh();
  }

  // poor way of providing access to selection changed events
  public TableViewer getTableViewer() {
    return tableViewer;
  }

}
