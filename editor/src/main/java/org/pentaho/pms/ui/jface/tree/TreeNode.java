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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public abstract class TreeNode implements ITreeNode
{
  protected ITreeNode fParent;
  protected List<ITreeNode> fChildren;
  private transient List<ITreeNodeChangedListener> fModelChangeListeners;
  
  public TreeNode(ITreeNode parent) {
    fParent = parent;
    
  }
  
  public Image getImage() {
    return null; /* TODO */
  }
  
  public boolean hasChildren() {    
    return ((getChildren() != null) && (getChildren().size()>0));
  }
 
  public ITreeNode getParent() {    
    return fParent;
  }
  
  public List<ITreeNode> getChildren() 
  {
    if( fChildren != null )
      return fChildren;
    
    fChildren = new ArrayList<ITreeNode>();
    createChildren(fChildren);
      
    return fChildren;
  }

  public void prune(){
    fChildren = null;
    this.fireTreeNodeUpdated();
  }
  
  public void addChild(ITreeNode node){
    if (fChildren == null)
      fChildren = new ArrayList<ITreeNode>();

    if (!fChildren.contains(node)){
      fChildren.add(node);
    }
    this.fireChildAdded(node);
  }

  public void addChild(int index, ITreeNode node){
    if (fChildren == null)
      fChildren = new ArrayList<ITreeNode>();

    if (!fChildren.contains(node)){
      fChildren.add(index, node);
    }
    this.fireChildAdded(node);
  }
 

  public void removeChild(ITreeNode node){
    if (fChildren.contains(node)){
      fChildren.remove(node);
    }
    ((TreeNode)node).fireTreeNodeDeleted();
    
  }
  
  /* subclasses should override this method and add the child nodes */
  protected abstract void createChildren(List children);
  
  public void addTreeNodeChangeListener(ITreeNodeChangedListener listener)
  {
    if( fModelChangeListeners == null )
      fModelChangeListeners = new ArrayList<ITreeNodeChangedListener>();
 
    /* if listener already exists, then do not add */   
    if( fModelChangeListeners.contains(listener) )
      return;
 
    fModelChangeListeners.add(listener);
  }
 
  public void removeTreeNodeChangeListener(ITreeNodeChangedListener listener)
  {
    if( fModelChangeListeners == null )
      return;
    
    fModelChangeListeners.remove(listener);
  }
 
  protected List<ITreeNodeChangedListener> getModelChangedListeners()
  {
    if( fModelChangeListeners == null )
      return Collections.<ITreeNodeChangedListener>emptyList();
    
    return fModelChangeListeners;
  }
  
  /** Fire methods need to be called in appropriate subclasses of treenode */
   
  protected void fireTreeNodeUpdated()
  {
    Iterator listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      ((ITreeNodeChangedListener)listenerIter.next()).onUpdate(this);
  }
 
  protected void fireChildAdded(ITreeNode child)
  {
    Iterator listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      ((ITreeNodeChangedListener)listenerIter.next()).onAddChild(this, child);
  }
 
  protected void fireTreeNodeDeleted()
  {
    List<ITreeNodeChangedListener> listeners = new ArrayList<ITreeNodeChangedListener>();
    
    // make copy of listener list so removals of listeners 
    // doesn't cause a problem
    Iterator<ITreeNodeChangedListener> listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      listeners.add(listenerIter.next());
    
    Iterator i = listeners.iterator();
    while ( i.hasNext() )
      ((ITreeNodeChangedListener)i.next()).onDelete(this);
    
    
  }

}
