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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;

public class PhysicalTableModel extends AbstractTableModel implements Cloneable {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PhysicalTableModel.class);

  // ~ Instance fields =================================================================================================

  private PhysicalTable table;

  // ~ Constructors ====================================================================================================

  public PhysicalTableModel(final PhysicalTable table) {
    super();
    this.table = table;
  }
  
  // ~ Methods =========================================================================================================

  public void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException {
    PhysicalColumn physicalColumn = new PhysicalColumn(id);
    physicalColumn.setTable(table);

    String name = id;
    if (name.startsWith(getColumnIdPrefix())) {
      name = name.substring(getColumnIdPrefix().length());
    }
    name = Const.fromID(name);
    physicalColumn.setName(localeCode, name);
    table.addPhysicalColumn(physicalColumn);
    fireTableModificationEvent(createAddColumnEvent(id));
  }

  private String getColumnIdPrefix() {
    String columnIdPrefix = Settings.getPhysicalColumnIDPrefix();
    if (Settings.isAnIdUppercase()) {
      columnIdPrefix = columnIdPrefix.toUpperCase();
    }
    return columnIdPrefix;
  }

  public ConceptInterface getConcept() {
    return table.getConcept();
  }

  public String getId() {
    return table.getId();
  }

  public void removeColumn(final String id) {
    PhysicalColumn column = table.findPhysicalColumn(id);
    if (null != column) {
      int index = table.indexOfPhysicalColumn(column);
      table.removePhysicalColumn(index);
      fireTableModificationEvent(createRemoveColumnEvent(id));
    }
  }

  public ConceptUtilityInterface[] getColumns() {
    return (ConceptUtilityInterface[]) table.getPhysicalColumns().toArray(new ConceptUtilityInterface[0]);
  }

  public ConceptUtilityInterface getWrappedTable() {
    return table;
  }

  public boolean isColumn(final ConceptUtilityInterface column) {
    return column instanceof PhysicalColumn;
  }

  public void addAllColumns(final ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException {
    // TODO mlowery should make this rollback on exception
    for (int i = 0; i < columns.length; i++) {
      table.addPhysicalColumn((PhysicalColumn) columns[i]);
      fireTableModificationEvent(createAddColumnEvent(columns[i].getId()));
    }
  }

  public void removeAllColumns() {
    String[] ids = table.getColumnIDs();
    for (int i = 0; i < ids.length; i++) {
      removeColumn(ids[i]);
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return new PhysicalTableModel((PhysicalTable) table.clone());
  }

  public void setParent(final ConceptUtilityInterface parent) {
    // do nothing
  }

  public ITableModel getParentAsTableModel() {
    // physical tables do not have parents
    return null;
  }

  public ConceptUtilityInterface getParent() {
    //  physical tables do not have parents
    return null;
  }

}
