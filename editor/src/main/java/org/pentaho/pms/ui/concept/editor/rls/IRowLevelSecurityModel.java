/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.pms.ui.concept.editor.rls;

import java.util.EventObject;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.RowLevelSecurity.Type;

/**
 * Wraps {@link org.pentaho.pms.schema.security.RowLevelSecurity} and fires events on modification.
 * 
 * @author mlowery
 */
public interface IRowLevelSecurityModel {

  /**
   * Adds given listener to list of listeners.
   */
  void addRlsModelListener(final IRlsModelListener rlsModelListener);

  /**
   * Removes given listener from list of listeners.
   */
  void removeRlsModelListener(final IRlsModelListener rlsModelListener);

  /**
   * Replaces the whole global formula.
   */
  void setGlobalConstraint(String globalConstraint);

  /**
   * Replaces the whole map.
   */
  void setRoleBasedConstraintMap(Map<SecurityOwner, String> roleBasedConstraintMap);

  /**
   * Changes the type.
   */
  void setType(Type type);

  /**
   * Adds an entry to the map. 
   */
  void put(SecurityOwner owner, String formula);

  /**
   * Merges the given map with the existing map.
   */
  void putAll(Map<SecurityOwner, String> roleBasedConstraintMap);

  /**
   * Removes an entry from the map.
   */
  void removeRoleBasedConstraint(SecurityOwner owner);

  /**
   * Returns a set of owners used in role-based constraint.
   */
  Set<SecurityOwner> getOwners();
  
  /**
   * Returns the formula associated with the given user.
   */
  String getFormula(SecurityOwner owner);
  
  /**
   * Returns a clone of the RowLevelSecurity instance wrapped by this object.
   */
  RowLevelSecurity getWrappedRowLevelSecurity();
  
  /**
   * Returns the global constraint.  (It's immutable of course.)
   */
  String getGlobalConstraint();
  
  public static interface IRlsModelListener {
    void rlsModelModified(final RlsModelEvent e);
  }

  public static class RlsModelEvent extends EventObject {

    private static final long serialVersionUID = 5227609355645634530L;

    public RlsModelEvent(final Object source) {
      super(source);
    }

    public String toString() {
      return ReflectionToStringBuilder.toString(this);
    }

  }
}
