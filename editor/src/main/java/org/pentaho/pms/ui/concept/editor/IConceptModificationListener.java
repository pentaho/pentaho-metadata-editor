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

import java.util.EventListener;

/**
 * Notified when a concept is modified.
 * @author mlowery
 * @see ConceptModificationEvent
 */
public interface IConceptModificationListener extends EventListener {
  void conceptModified(final ConceptModificationEvent e);
}
