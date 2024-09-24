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
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;


/**
 * @author wseyler
 *
 */
public class BusinessModelsTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
 
  /**
   * @param node
   * @param schemaMeta
   */
  public BusinessModelsTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getBusinessModels().iterator();
    while(iter.hasNext()) {
      BusinessModel businessModel = (BusinessModel) iter.next();
      addDomainChild(businessModel);
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessModel){
      addChild(new BusinessModelTreeNode(this, (BusinessModel) domainObject, schemaMeta.getActiveLocale()));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof BusinessModel){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          BusinessModelTreeNode element = (BusinessModelTreeNode) iter.next();
          if (element.model.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync(){
    sync(schemaMeta.getBusinessModels());
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return Messages.getString("MetaEditor.USER_BUSINESS_MODELS"); //$NON-NLS-1$
  }

  public Image getImage(){
    return GUIResource.getInstance().getImageBol();
  }   
  
  public Object getDomainObject(){
    return schemaMeta;
  }
}
