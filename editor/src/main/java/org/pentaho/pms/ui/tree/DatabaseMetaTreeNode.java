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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class DatabaseMetaTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
  protected DatabaseMeta databaseMeta = null;
  
  /**
   * @param connectionsLabel
   * @param database
   * @param activeLocale
   */
  public DatabaseMetaTreeNode(ITreeNode parent, SchemaMeta schemaMeta, DatabaseMeta databaseMeta) {
    super(parent);
    this.schemaMeta = schemaMeta;
    this.databaseMeta = databaseMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    PhysicalTable[] physicalTables = schemaMeta.getTablesOnDatabase(databaseMeta);
    for(int i=0; i<physicalTables.length; i++) {
      PhysicalTable physicalTable = physicalTables[i];
      addDomainChild(physicalTable);
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof PhysicalTable){
      addChild(new PhysicalTableTreeNode(this, (PhysicalTable) domainObject, schemaMeta.getActiveLocale()));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof PhysicalTable){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          PhysicalTableTreeNode element = (PhysicalTableTreeNode) iter.next();
          if (element.physicalTable.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync(){

    // TODO: GEM - Because all physical tables are held in a single list in the 
    // schemaMeta, we can't persist the sort order of physical tables, so sort is not 
    // in the Connection tree; hence, the logic for preserving indexes is absent from this 
    // ConceptTreeNode subclass. We should probably fix up the model so that we 
    // can overcome this limitation in the future. 
    
    if (fChildren == null){
      getChildren();
    }
    
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    while ( childIter.hasNext() )
      children.add(childIter.next());
    
    PhysicalTable[] physicalTables = schemaMeta.getTablesOnDatabase(databaseMeta);
    List tables = Arrays.asList(physicalTables);

    for (int c = 0; c < tables.size(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        PhysicalTableTreeNode element = (PhysicalTableTreeNode) iter.next();
        if (element.getDomainObject().equals(tables.get(c)))
          found = true;
      }
      if (!found){
        addDomainChild(tables.get(c));
      }
    }
    
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode)children.get(c);

      if (!tables.contains(node.getDomainObject())){
        removeChild(node);
      }else{
        node.sync();
      }
    }  
    // update this node
    fireTreeNodeUpdated();

  }


   
  public DatabaseMeta getDatabaseMeta(){
    return databaseMeta;
  }

  public String getId() {
    // DatabaseMeta names are unique, and PME searches them by name
    return databaseMeta.getName();
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_DATABASE_CONNECTION;
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return databaseMeta.getName();
  }

  public Object getDomainObject(){
    return databaseMeta;
  }
  
  public Image getImage() {
    return GUIResource.getInstance().getImageConnection();
  }
}
