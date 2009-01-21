package org.pentaho.pms.ui.jface.tree;

public interface ITreeNodeChangedListener {

  public void onUpdate(ITreeNode node);
  
  public void onAddChild(ITreeNode parent, ITreeNode child);
 
  public void onDelete(ITreeNode node);
}
