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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;


public class BusinessTableModel extends AbstractTableModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(BusinessTableModel.class);

  // ~ Instance fields =================================================================================================

  private BusinessTable table;

  private PhysicalTable physicalTable;
  
  private BusinessModel businessModel;

  // ~ Constructors ====================================================================================================

  public BusinessTableModel(final BusinessTable table, final BusinessModel businessModel) {
    this(table, table.getPhysicalTable(), businessModel);
  }

  public BusinessTableModel(final BusinessTable table, final PhysicalTable physicalTable, final BusinessModel businessModel) {
    super();
    this.table = table;
    setParent(physicalTable);
    this.businessModel=businessModel;
  }

  // ~ Methods =========================================================================================================

  public void addAllColumns(final ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException {
    // TODO mlowery should make this rollback on exception
    for (int i = 0; i < columns.length; i++) {
      table.addBusinessColumn((BusinessColumn) columns[i]);
      fireTableModificationEvent(createAddColumnEvent(columns[i].getId()));
    }
  }

  /**
   * Here the id is the name of the physical column on which to base the new business column.
   */
  public void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException {
    if (id != null) {
      PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn(localeCode, id);

      UniqueList columns;
      if (businessModel != null) {
        // merge all business columns + business columns in this table (in case the table has yet to be added to the 
        // model) into one UniqueList
        columns = new UniqueArrayList<BusinessColumn>();
        Set<BusinessColumn> allColumns = new HashSet<BusinessColumn>(businessModel.getAllBusinessColumns().getList());
        Set<BusinessColumn> newTableColumns = new HashSet<BusinessColumn>(table.getBusinessColumns().getList());
        Set<BusinessColumn> mergedColumns = new HashSet<BusinessColumn>();
        mergedColumns.addAll(allColumns);
        mergedColumns.addAll(newTableColumns);
        columns.addAll(mergedColumns);

      } else {
        columns = table.getBusinessColumns();
      }
      String newBusinessColumnId = BusinessColumn.proposeId(localeCode, table, physicalColumn, columns);
      BusinessColumn businessColumn = new BusinessColumn(newBusinessColumnId, physicalColumn, table);
      
      businessColumn.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(table.getBusinessColumns()));
      table.addBusinessColumn(businessColumn);
      fireTableModificationEvent(createAddColumnEvent(newBusinessColumnId));
    }
  }

  public ConceptUtilityInterface[] getColumns() {
    return (ConceptUtilityInterface[]) table.getBusinessColumns().toArray(new ConceptUtilityInterface[0]);
  }

  public ConceptInterface getConcept() {
    return table.getConcept();
  }

  public String getId() {
    return table.getId();
  }

  public ConceptUtilityInterface getWrappedTable() {
    return table;
  }

  public boolean isColumn(final ConceptUtilityInterface column) {
    return column instanceof BusinessColumn;
  }

  public void removeAllColumns() {
    String[] ids = table.getColumnIDs();
    for (int i = 0; i < ids.length; i++) {
      removeColumn(ids[i]);
    }
  }

  public void removeColumn(final String id) {
    BusinessColumn column = table.findBusinessColumn(id);
    if (null != column) {
      int index = table.indexOfBusinessColumn(column);
      table.removeBusinessColumn(index);
      fireTableModificationEvent(createRemoveColumnEvent(id));
    }
  }

  public void setParent(final ConceptUtilityInterface parent) {
    if (null == parent) {
      return;
    }
    if (parent instanceof PhysicalTable) {
      physicalTable = (PhysicalTable) parent;
      table.setPhysicalTable(physicalTable);
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("BusinessTableModel.DEBUG_SET_PARENT_TABLE", physicalTable.toString())); //$NON-NLS-1$
      }
    } else {
      throw new IllegalArgumentException(Messages.getString("BusinessTableModel.ERROR_0001_ARGUMENT_MUST_BE_PHYSICAL_TABLE")); //$NON-NLS-1$
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return new BusinessTableModel((BusinessTable) table.clone(), (PhysicalTable) physicalTable.clone(), businessModel);
  }

  public ITableModel getParentAsTableModel() {
    if (null != physicalTable) {
      return new PhysicalTableModel(physicalTable);
    } else {
      return null;
    }
  }

  public ConceptUtilityInterface getParent() {
    return physicalTable;
  }
}
