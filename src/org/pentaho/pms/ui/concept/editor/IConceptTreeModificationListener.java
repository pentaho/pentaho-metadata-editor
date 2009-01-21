package org.pentaho.pms.ui.concept.editor;

import java.util.EventListener;

public interface IConceptTreeModificationListener extends EventListener {
  void conceptTreeModified(final ConceptTreeModificationEvent e);
}
