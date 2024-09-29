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


package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class PhysicalTableTreeNode extends ConceptTreeNode {
  protected PhysicalTable physicalTable = null;
  protected String locale = null;
  
   /**
   * @param node
   * @param physicalTable
   * @param locale
   */
  public PhysicalTableTreeNode(ITreeNode parent, PhysicalTable physicalTable, String locale) {
    super(parent);
    
    this.physicalTable = physicalTable;
    this.locale = locale;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = physicalTable.getPhysicalColumns().iterator();
    while(iter.hasNext()) {
      PhysicalColumn physicalColumn = (PhysicalColumn) iter.next();
      addDomainChild(physicalColumn);
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof PhysicalColumn){
      addChild(new PhysicalColumnTreeNode(this, (PhysicalColumn)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof PhysicalColumn){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          PhysicalColumnTreeNode element = (PhysicalColumnTreeNode) iter.next();
          if (element.physicalColumn.equals(domainObject))
            removeChild(element);
        }
    }
  }


  public void sync(){
    sync(physicalTable.getPhysicalColumns());
  }
  
  
  public Object getDomainObject(){
    return physicalTable;
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_PHYSICAL_TABLE;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return physicalTable.getName(locale);
  }

  public String getConceptName(){

    ConceptInterface tableConcept = physicalTable.getConcept();
    if (tableConcept != null && tableConcept.findFirstParentConcept() != null) {
      return tableConcept.findFirstParentConcept().getName();
    }
    return null;
  }

  public Image getImage() {
    return GUIResource.getInstance().getImagePhysicalTable();
  }
  
}
