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


package org.pentaho.pms.ui.jface.tree;

public interface ITreeNodeChangedListener {

  public void onUpdate(ITreeNode node);
  
  public void onAddChild(ITreeNode parent, ITreeNode child);
 
  public void onDelete(ITreeNode node);
}
