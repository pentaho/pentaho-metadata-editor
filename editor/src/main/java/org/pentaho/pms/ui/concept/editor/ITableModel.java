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

import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;


/**
 * An abstraction of a <code>PhysicalTable</code> and <code>BusinessTable</code>.
 * @author mlowery
 *
 */
public interface ITableModel {
  /**
   * Returns the id of this table.
   * @return the id
   */
  String getId();

  /**
   * Returns the concept associated with this table.
   * @return the concept
   */
  ConceptInterface getConcept();

  /**
   * Adds a column (with default values).
   * @param id the id of the new column
   * @param localeCode the code for the active locale (used for default column name)
   * @throws ObjectAlreadyExistsException if wrapped table throws it
   */
  void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException;

  /**
   * Removes a column with the given id.
   * @param id the id of new column
   */
  void removeColumn(final String id);

  /**
   * Returns the columns (which are of type <code>ConceptUtilityInterface</code>.
   * @return the columns
   */
  ConceptUtilityInterface[] getColumns();

  void addTableModificationListener(final ITableModificationListener tableModelListener);

  void removeTableModificationListener(final ITableModificationListener tableModelListener);

  /**
   * Returns the table object wrapped by this model.
   * @return the table object
   */
  ConceptUtilityInterface getWrappedTable();

  /**
   * Returns true if argument is an instance of a column type handled by this model.
   * @param column object to test
   * @return true if argument is an instance of a column type handled by this model
   */
  boolean isColumn(final ConceptUtilityInterface column);

  /**
   * Removes all columns.
   */
  void removeAllColumns();

  /**
   * Adds all given columns to end of list of columns maintained by this class.
   * @param columns columns to add
   * @throws ObjectAlreadyExistsException if wrapped table throws it
   */
  void addAllColumns(ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException;

  /**
   * Sets the parent table of this table. (Not applicable to physical tables.)
   */
  void setParent(ConceptUtilityInterface parent);

  /**
   * Returns the column names given the locale.
   */
  String[] getColumnNames(String locale);

  /**
   * Returns the parent of this table.
   */
  ConceptUtilityInterface getParent();

  /**
   * Returns the parent of this table as an <code>ITableModel</code>.
   */
  ITableModel getParentAsTableModel();
}
