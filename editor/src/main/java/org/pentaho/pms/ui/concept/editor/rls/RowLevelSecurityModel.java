/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
