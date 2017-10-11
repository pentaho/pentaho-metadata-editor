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
        if (viewer != null)
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
