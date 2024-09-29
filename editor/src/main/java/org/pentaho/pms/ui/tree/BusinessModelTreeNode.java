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
