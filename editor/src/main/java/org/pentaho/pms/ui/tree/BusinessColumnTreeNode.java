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
