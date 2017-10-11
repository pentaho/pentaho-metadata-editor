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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;



public class BusinessTablesTreeNode extends ConceptTreeNode {
  protected BusinessModel model = null;
  protected String locale = null;
  
  public BusinessTablesTreeNode(ITreeNode parent, BusinessModel model, String locale) {
    super(parent);
    this.model = model;
    this.locale = locale; 
  }

  protected void createChildren(List children) {
    
    for (int c = 0; c < model.nrBusinessTables(); c++) {
        addDomainChild(model.getBusinessTable(c));
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessTable){
      addChild(new BusinessTableTreeNode(this,(BusinessTable)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof BusinessTable){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          BusinessTableTreeNode element = (BusinessTableTreeNode) iter.next();
          if (element.table.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync(){
    sync(model.getBusinessTables());
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageBusinessTablesParent();
  }   

  public String getName() {
    return Messages.getString("MetaEditor.USER_BUSINESS_TABLES");  //$NON-NLS-1$
  }

  public Object getDomainObject(){
    return model;
  }

}
