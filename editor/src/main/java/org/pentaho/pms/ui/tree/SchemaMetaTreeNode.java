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
