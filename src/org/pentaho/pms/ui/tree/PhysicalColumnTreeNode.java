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
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class PhysicalColumnTreeNode extends ConceptTreeNode {
  
  public void addDomainChild(Object obj) {
    // physical columns have no children
  }

  protected PhysicalColumn physicalColumn = null;
  protected String locale = null;

  /**
   * @param node
   * @param physicalColumn
   * @param locale
   */
  public PhysicalColumnTreeNode(ITreeNode parent, PhysicalColumn physicalColumn, String locale) {
    super(parent);
    
    this.physicalColumn = physicalColumn;
    this.locale = locale;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {

  }
  
  public void sync(){
    fireTreeNodeUpdated();
   // Intentionally do nothing here 
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    // TODO Auto-generated method stub
    return physicalColumn.getName(locale);
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_PHYSICAL_COLUMN;
  }

  public Object getDomainObject(){
    return physicalColumn;
  }

  public String getConceptName(){

    ConceptInterface concept = physicalColumn.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }

  public Image getImage() {
    return GUIResource.getInstance().getImagePhysicalColumn();
  }
}
