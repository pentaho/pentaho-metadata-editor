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
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class ConnectionsTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
  
  public ConnectionsTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getDatabases().iterator();
    while(iter.hasNext()) {
      DatabaseMeta databaseMeta = (DatabaseMeta) iter.next();
      addDomainChild(databaseMeta);
    }
  }

  public void addDomainChild(Object domainObject){
    if (domainObject instanceof DatabaseMeta){
      addChild(new DatabaseMetaTreeNode(this, schemaMeta, (DatabaseMeta) domainObject));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof DatabaseMeta){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          DatabaseMetaTreeNode element = (DatabaseMetaTreeNode) iter.next();
          if (element.databaseMeta.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync() {
    sync(schemaMeta.getDatabases());
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return Messages.getString("MetaEditor.USER_CONNECTIONS"); //$NON-NLS-1$
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageConnectionsParent();
  }

  public Object getDomainObject(){
    return schemaMeta;
  }

}
