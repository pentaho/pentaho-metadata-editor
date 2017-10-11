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

package org.pentaho.pms.ui.tree;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ConceptLabelProvider extends LabelProvider implements ITableLabelProvider {

  public Image getColumnImage(Object element, int index) {

    switch (index) {
      case 1: // parent concept
        return ((ConceptTreeNode) element).getConceptImage();
      default: // name
        return ((ConceptTreeNode) element).getImage();
    }
  }

  public String getColumnText(Object element, int index) {
    switch (index) {
      case 1: // parent concept
        return ((ConceptTreeNode) element).getConceptName();
      default: // name 
        return ((ConceptTreeNode) element).getName();
    }
  }

  public String getText(Object element) {
    return getColumnText(element,0);
  }

  public Image getImage(Object element) {
    return getColumnImage(element,0);
  }

}
