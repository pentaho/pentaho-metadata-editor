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

/**
 * A graphical control that edits a property of a concept.
 * @author mlowery
 */
public interface IPropertyEditorWidget {
  /**
   * Returns a value suitable for passing to <code>ConceptPropertyInterface.setValue()</code>.
   */
  Object getValue();

  /**
   * Called just before disposal. Typically used to remove listeners set on child controls.
   */
  void cleanup();

  /**
   * Returns an error message if the widget's value is invalid or <code>null</code> if valid.
   * @return
   */
  String validate();
}
