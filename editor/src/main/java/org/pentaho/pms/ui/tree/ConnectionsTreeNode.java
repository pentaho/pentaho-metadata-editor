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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class ConnectionsTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
  
  public ConnectionsTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getDatabases().iterator();
    while(iter.hasNext()) {
      DatabaseMeta databaseMeta = (DatabaseMeta) iter.next();
      addDomainChild(databaseMeta);
    }
  }

  public void addDomainChild(Object domainObject){
    if (domainObject instanceof DatabaseMeta){
      addChild(new DatabaseMetaTreeNode(this, schemaMeta, (DatabaseMeta) domainObject));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof DatabaseMeta){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          DatabaseMetaTreeNode element = (DatabaseMetaTreeNode) iter.next();
          if (element.databaseMeta.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync() {
    sync(schemaMeta.getDatabases());
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return Messages.getString("MetaEditor.USER_CONNECTIONS"); //$NON-NLS-1$
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageConnectionsParent();
  }

  public Object getDomainObject(){
    return schemaMeta;
  }

}
