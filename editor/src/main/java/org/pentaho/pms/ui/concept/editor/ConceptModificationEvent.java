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



package org.pentaho.pms.ui.concept.editor;

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An event fired from the concept model when modifications are made.
 * @author mlowery
 * @see IConceptModificationListener
 */
public abstract class ConceptModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptModificationEvent.class);

  private static final long serialVersionUID = 3595437838419848420L;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public ConceptModificationEvent(final Object source) {
    super(source);
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
