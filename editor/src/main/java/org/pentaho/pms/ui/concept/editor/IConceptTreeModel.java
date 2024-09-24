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

import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.DeleteNotAllowedException;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

/**
 * Wraps the tree of concepts stored in the schema meta. This model is ONLY concerned with querying or changing the
 * structure of the tree.
 *
 * @author mlowery
 */
public interface IConceptTreeModel {

  /**
   * Returns the children of parent.
   */
  ConceptInterface[] getChildren(ConceptInterface parent);

  /**
   * Adds the concept with parent as parent.
   * @throws ObjectAlreadyExistsException if concept with given name already exists anywhere in hierarchy
   */
  void addConcept(ConceptInterface parent, ConceptInterface newChild) throws ObjectAlreadyExistsException;

  /**
   * Removes the concept.
   */
  void removeConcept(ConceptInterface concept) throws DeleteNotAllowedException;

  /**
   * Returns the parent of the concept.
   */
  ConceptInterface getParent(ConceptInterface concept);
  
  public SchemaMeta getSchemaMeta();

  /**
   * Write the changes made since instantiation into the schema meta.
   */
  void save() throws ObjectAlreadyExistsException;

  void addConceptTreeModificationListener(IConceptTreeModificationListener listener);

  void removeConceptTreeModificationListener(IConceptTreeModificationListener listener);
}
