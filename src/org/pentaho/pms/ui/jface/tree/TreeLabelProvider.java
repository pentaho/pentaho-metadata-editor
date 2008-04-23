package org.pentaho.pms.ui.jface.tree;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TreeLabelProvider extends LabelProvider {

  public String getText(Object element) {   
      return ((ITreeNode)element).getName();
    }
    
    public Image getImage(Object element) {   
      return ((ITreeNode)element).getImage();
    }
}
