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

import java.util.List;

import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;

/**
 * @author wseyler
 *
 */
public class SchemaMetaTreeNode extends ConceptTreeNode {

  public void addDomainChild(Object obj) {
    // Nothing to do here, this is a manually built branch
  }

  protected SchemaMeta schemaMeta = null;
  private ConnectionsTreeNode connectionsTreeNode;
  private BusinessModelsTreeNode businessModelsTreeNode;
    
  public SchemaMetaTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    this.schemaMeta = schemaMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    connectionsTreeNode = new ConnectionsTreeNode(this, schemaMeta);
    businessModelsTreeNode = new BusinessModelsTreeNode(this, schemaMeta);
    addChild(connectionsTreeNode);
    addChild(businessModelsTreeNode);
  }
  
  public BusinessModelsTreeNode getBusinessModelsRoot(){
    return businessModelsTreeNode;
  }
  
  public ConnectionsTreeNode getConnectionsRoot(){
    return connectionsTreeNode;
  }
  
  public void sync(){
    if (fChildren == null){
      getChildren();
    }

    connectionsTreeNode.sync();
    businessModelsTreeNode.sync();
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Object getDomainObject(){
    return schemaMeta;
  }
}
