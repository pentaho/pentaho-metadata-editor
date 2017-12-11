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
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;


public class BusinessModelTreeNode extends ConceptTreeNode {

  protected BusinessModel model = null;
  protected String locale = null;
  
  // Hold on to strategic nodes in the subtree, to make accessing these nodes 
  // a bit more efficient
  
  private BusinessTablesTreeNode businessTablesNode;
  private RelationshipsTreeNode relationshipsNode;
  private BusinessViewTreeNode businessViewNode;
  
  public BusinessModelTreeNode(ITreeNode parent, BusinessModel model, String locale) {
    super(parent);
    this.model = model;
    this.locale = locale;
  }

  protected void createChildren(List children) {
    businessTablesNode = new BusinessTablesTreeNode(this, model, locale);
    addChild(businessTablesNode);
    
    relationshipsNode = new RelationshipsTreeNode(this, model);
    addChild(relationshipsNode);
    
    businessViewNode = new BusinessViewTreeNode(this, model.getRootCategory(), locale);
    addChild(businessViewNode);
  }
  
  public void sync(){
    if (fChildren == null){
      getChildren();
    }
    
    businessTablesNode.sync();
    relationshipsNode.sync();
    businessViewNode.sync();
    
    fireTreeNodeUpdated();
  }

  public Image getImage(){
    return GUIResource.getInstance().getImageBusinessModel();
  }   

  public String getName() {
    String displayConnection = model.hasConnection() ? 
        model.getConnection().getName() : Messages.getString("BusinessModeltreeNode.NO_CONNECTION_DEFINED"); //$NON-NLS-1$ 
    displayConnection = " (" + displayConnection + ")";  //$NON-NLS-1$ //$NON-NLS-2$
    return model.getDisplayName(locale) + displayConnection;
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_BUSINESS_MODEL;
  }

  public String getConceptName(){

    ConceptInterface concept = model.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }

  public Object getDomainObject(){
    return model;
  }

  public BusinessModel getBusinessModel() {
    return model;
  }
  
  public BusinessTablesTreeNode getBusinessTablesRoot(){
    if (businessTablesNode == null){
      businessTablesNode = new BusinessTablesTreeNode(this, model, locale);
    }
    return businessTablesNode;
  }

  public RelationshipsTreeNode getRelationshipsRoot(){
    if (relationshipsNode == null){
      relationshipsNode = new RelationshipsTreeNode(this, model);      
    }
    return relationshipsNode;
  }
  
  public BusinessViewTreeNode getBusinessViewRoot(){
    if (businessViewNode == null){
      businessViewNode = new BusinessViewTreeNode(this, model.getRootCategory(), locale);
    }
    return businessViewNode;
  }

  public void addDomainChild(Object obj) {
    // no children allowed here, this is a manuallsy built branch
    
  }
  
}
