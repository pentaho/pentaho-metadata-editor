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

package org.pentaho.pms.ui.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyValueModificationEvent extends PropertyModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyValueModificationEvent.class);

  private static final long serialVersionUID = 3440212934863199561L;

  // ~ Instance fields =================================================================================================

  private Object oldValue;

  private Object newValue;

  // ~ Constructors ====================================================================================================

  public PropertyValueModificationEvent(final Object source, final String propertyId, final Object oldValue,
      final Object newValue) {
    super(source, propertyId);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  // ~ Methods =========================================================================================================

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

}
