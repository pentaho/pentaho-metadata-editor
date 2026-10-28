/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.pms.ui.jface.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;

public interface ITreeNode {
    public String getName();
    public Image getImage();
    public List getChildren();
    public boolean hasChildren();
    public ITreeNode getParent();
    public void addChild(ITreeNode child);
    public void prune();
    public void addTreeNodeChangeListener(ITreeNodeChangedListener listener);    
    public void removeTreeNodeChangeListener(ITreeNodeChangedListener listener);
    
}
