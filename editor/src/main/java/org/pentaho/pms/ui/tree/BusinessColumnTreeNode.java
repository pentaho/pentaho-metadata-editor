/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;


public class BusinessColumnTreeNode extends ConceptTreeNode {

  public void addDomainChild(Object obj) {
    // No children, nothing to do here
    
  }

  protected BusinessColumn column = null;
  protected String locale = null;
  
  public BusinessColumnTreeNode(ITreeNode parent, final BusinessColumn column, final String locale) {
    super(parent);
    this.column = column;
    this.locale = locale; 
  }

  protected void createChildren(List children) {
    // Category columns have no children under the default implementation
  }
  
  public void sync(){
    // intentional nothing to do here 
  }

  public Image getImage() {
    return GUIResource.getInstance().getImageBusinessColumn();
  }

  public String getName() {
     return column.getDisplayName(locale);
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_BUSINESS_COLUMN;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.TreeNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }
  
  public Object getDomainObject(){
    return column;
  }
  
  public String getConceptName(){

    ConceptInterface concept = column.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }

  public BusinessColumn getBusinessColumn() {
    return column;
  }

}
