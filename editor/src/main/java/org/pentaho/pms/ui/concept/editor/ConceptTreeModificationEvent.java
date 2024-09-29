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

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConceptTreeModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModificationEvent.class);

  private static final long serialVersionUID = -5854497170418174812L;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public ConceptTreeModificationEvent(final Object source) {
    super(source);
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
