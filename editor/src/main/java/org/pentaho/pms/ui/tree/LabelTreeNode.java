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

import java.util.List;

import org.pentaho.pms.ui.jface.tree.ITreeNode;

public class LabelTreeNode extends ConceptTreeNode {
  
  public void addDomainChild(Object obj) {
    // Nohting to do here
  }

  protected String labelName = null;

  public LabelTreeNode(ITreeNode parent, String name) {
    super(parent);
    
    labelName = name; 
  }

  protected void createChildren(List children) {
    // Labels have no intuitive children, they are added manually as the tree requirements deem necessary
  }

  public void sync(){
    // intentionally do nothing here
  }
  
  public String getName() {
    return labelName;
  }
  
  public Object getDomainObject(){
    return getName();
  }

}
