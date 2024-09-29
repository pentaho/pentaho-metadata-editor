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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class TreeContentProvider implements ITreeContentProvider, ITreeNodeChangedListener {
  
    TreeViewer viewer;

    public Object[] getChildren(Object parentElement) {
      return ((ITreeNode)parentElement).getChildren().toArray();
    }
   
    public Object getParent(Object element) {
      return ((ITreeNode)element).getParent();
    }
   
    public boolean hasChildren(Object element) {
      return ((ITreeNode)element).hasChildren();
    }
   
    public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
    }
   
    public void dispose() { 
    }
   
    public void inputChanged(Viewer view, Object oldInput, Object newInput) {
 
      this.viewer = (TreeViewer)view;
      if (null != oldInput){
        ((TreeNode)oldInput).removeTreeNodeChangeListener(this);
      }
    } 

    public void onAddChild(ITreeNode parent, ITreeNode child) {
      child.addTreeNodeChangeListener(this);
      if (!parent.getChildren().contains(child)){
        parent.addChild(child);
      }else{
        if (viewer != null && !viewer.isBusy())
          viewer.refresh(parent,true);        
      }
    }

    public void onDelete(ITreeNode node) {
      node.removeTreeNodeChangeListener(this);
      if (viewer != null)
        viewer.remove(node);
    }

    public void onUpdate(ITreeNode node) {
      if (viewer != null)
        viewer.refresh(node,true);
      
    }


}
