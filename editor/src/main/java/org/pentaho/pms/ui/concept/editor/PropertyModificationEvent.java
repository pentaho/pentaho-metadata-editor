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


package org.pentaho.pms.ui.concept.editor;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PropertyModificationEvent extends ConceptModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyModificationEvent.class);

  private static final long serialVersionUID = -5810693858905811872L;

  // ~ Instance fields =================================================================================================

  private String propertyId;

  // ~ Constructors ====================================================================================================

  public PropertyModificationEvent(final Object source, final String propertyId) {
    super(source);
    this.propertyId = propertyId;
  }

  // ~ Methods =========================================================================================================

  public String getPropertyId() {
    return propertyId;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this).toString();
  }

}
