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

import java.util.EventListener;

/**
 * Notified when a concept is modified.
 * @author mlowery
 * @see ConceptModificationEvent
 */
public interface IConceptModificationListener extends EventListener {
  void conceptModified(final ConceptModificationEvent e);
}
