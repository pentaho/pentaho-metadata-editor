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

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;


public class RelationshipTreeNode extends ConceptTreeNode {

  public void addDomainChild(Object obj) {
    // Nothing to do here
  }

  protected RelationshipMeta relationship = null;
  
  public RelationshipTreeNode(ITreeNode parent, final RelationshipMeta relationship) {
    super(parent);
    this.relationship = relationship;
  }

  protected void createChildren(List children) {
    // As of this impementation, relationships have no children
  }
  
  public void sync(){
    // Intentionally do nothing here
  }
  
  public Image getImage() {
    return GUIResource.getInstance().getImageRelationship();
  }

  public String getName() {
    return relationship.toString();
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.TreeNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }

  public String getId() {
    // No ids on relationships?
    return relationship.toString();
  }

  public int getDragAndDropType() {
    return 0;
  }
  
  public Object getDomainObject(){
    return relationship;
  }
}
