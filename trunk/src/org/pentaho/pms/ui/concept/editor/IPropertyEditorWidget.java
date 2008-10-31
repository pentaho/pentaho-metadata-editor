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
