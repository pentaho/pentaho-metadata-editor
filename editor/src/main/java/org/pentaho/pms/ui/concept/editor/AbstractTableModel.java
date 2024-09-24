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
