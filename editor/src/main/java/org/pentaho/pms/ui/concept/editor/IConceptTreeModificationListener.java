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

public interface IConceptTreeModificationListener extends EventListener {
  void conceptTreeModified(final ConceptTreeModificationEvent e);
}
