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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;

public abstract class AbstractTableModel implements ITableModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AbstractTableModel.class);

  // ~ Instance fields =================================================================================================

  private EventSupport eventSupport = new EventSupport();
  
  // ~ Constructors ====================================================================================================

  public AbstractTableModel() {
    super();
  }


  // ~ Methods =========================================================================================================

  protected void fireTableModificationEvent(final TableModificationEvent e) {
    for (Iterator iter = eventSupport.getListeners().iterator(); iter.hasNext();) {
      ITableModificationListener target = (ITableModificationListener) iter.next();
      target.tableModified(e);
    }
  }

  protected TableModificationEvent createAddColumnEvent(final String id) {
    return new TableModificationEvent(this, id, TableModificationEvent.ADD_COLUMN);
  }

  protected TableModificationEvent createRemoveColumnEvent(final String id) {
    return new TableModificationEvent(this, id, TableModificationEvent.REMOVE_COLUMN);
  }

  public void addTableModificationListener(ITableModificationListener tableModelListener) {
    eventSupport.addListener(tableModelListener);
  }

  public void removeTableModificationListener(ITableModificationListener tableModelListener) {
    eventSupport.removeListener(tableModelListener);
  }

  public String[] getColumnNames(final String locale) {
    ConceptUtilityInterface[] columns = getColumns();
    String[] names = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      names[i] = columns[i].getDisplayName(locale);
    }
    return names;
  }
}
