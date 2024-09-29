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
