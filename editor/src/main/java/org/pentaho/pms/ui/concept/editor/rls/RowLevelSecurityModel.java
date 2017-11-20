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

package org.pentaho.pms.ui.concept.editor.rls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.RowLevelSecurity.Type;
import org.pentaho.pms.ui.concept.editor.EventSupport;

/**
 * Implementation of {@link IRowLevelSecurityModel}.
 * 
 * @author mlowery
 */
public class RowLevelSecurityModel implements IRowLevelSecurityModel {

  // ~ Static fields/initializers ====================================================================================== 

  private static final Log logger = LogFactory.getLog(RowLevelSecurityModel.class);

  // ~ Instance fields =================================================================================================

  /**
   * Contains event listeners.
   */
  private EventSupport eventSupport = new EventSupport();

  /**
   * The row level security instance to which this class delegates.
   */
  private RowLevelSecurity rls;

  // ~ Constructors ====================================================================================================

  public RowLevelSecurityModel(final RowLevelSecurity rls) {
    super();
    this.rls = rls;
  }

  // ~ Methods =========================================================================================================

  protected Map<SecurityOwner, String> cloneRoleBasedConstraintMap(Map<SecurityOwner, String> map) {
    Map<SecurityOwner, String> copy = new HashMap<SecurityOwner, String>();
    for (Map.Entry<SecurityOwner, String> entry : map.entrySet()) {
      SecurityOwner clonedOwner = (SecurityOwner) entry.getKey().clone();
      copy.put(clonedOwner, entry.getValue());
    }
    return copy;
  }

  public void addRlsModelListener(IRlsModelListener rlsModelListener) {
    eventSupport.addListener(rlsModelListener);
  }

  public void removeRlsModelListener(IRlsModelListener rlsModelListener) {
    eventSupport.removeListener(rlsModelListener);
  }

  protected void fireRlsModelEvent(final RlsModelEvent e) {
    for (Iterator iter = eventSupport.getListeners().iterator(); iter.hasNext();) {
      IRlsModelListener target = (IRlsModelListener) iter.next();
      target.rlsModelModified(e);
    }
  }

  public void put(SecurityOwner owner, String formula) {
    rls.getRoleBasedConstraintMap().put(owner, formula);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public void putAll(Map<SecurityOwner, String> roleBasedConstraintMap) {
    rls.getRoleBasedConstraintMap().putAll(roleBasedConstraintMap);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public void removeRoleBasedConstraint(SecurityOwner owner) {
    rls.getRoleBasedConstraintMap().remove(owner);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public void setGlobalConstraint(String globalConstraint) {
    rls.setGlobalConstraint(globalConstraint);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public void setRoleBasedConstraintMap(Map<SecurityOwner, String> roleBasedConstraintMap) {
    rls.setRoleBasedConstraintMap(roleBasedConstraintMap);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public void setType(Type type) {
    rls.setType(type);
    fireRlsModelEvent(new RlsModelEvent(this));
  }

  public Set<SecurityOwner> getOwners() {
    // defensive copy
    return cloneRoleBasedConstraintMap(rls.getRoleBasedConstraintMap()).keySet();
  }

  public String getFormula(SecurityOwner owner) {
    return rls.getRoleBasedConstraintMap().get(owner);
  }

  public RowLevelSecurity getWrappedRowLevelSecurity() {
    try {
      return (RowLevelSecurity) rls.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("clone not supported", e);
    }
  }

  public String getGlobalConstraint() {
    return rls.getGlobalConstraint();
      
  }

}
