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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;

public class TableColumnTreeWidget extends TreeViewer implements ISelectionProvider, ITableModificationListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TableColumnTreeWidget.class);

  // ~ Instance fields =================================================================================================

  private ITableModel tableModel;

  private boolean decorate = true;

  private boolean showId = false;
  
  private String locale = null;

  // ~ Constructors ====================================================================================================

  /**
   * Shows only the properties defined in the given concept model. Refreshes itself in reaction to concept model
   * changes.
   */
  public TableColumnTreeWidget(final Composite parent, final int style, final ITableModel tableModel,
      final boolean decorate, final String locale) {
    super(new Tree(parent, style));
    this.tableModel = tableModel;
    this.tableModel.addTableModificationListener(this);
    this.decorate = decorate;
    this.locale = locale;
    createContents();
    showId(showId);
  }

  // ~ Methods =========================================================================================================

  /**
   * Used to toggle the display of IDs vs. localized names. Tree is refreshed as part of this call.
   */
  public void showId(final boolean showId) {
    this.showId = showId;
    refresh(true);
    expandAll();
  }

  public void tableModified(final TableModificationEvent e) {
    if (logger.isDebugEnabled()) {
      logger.debug("heard tableModel modification event: " + e);
    }
    // tree is small enough that we don't need to be smart about painting only changed nodes; paint everything
    refresh(true);
    expandAll();
  }

  protected void createContents() {
    getTree().addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        TableColumnTreeWidget.this.widgetDisposed(e);
      }
    });
    setContentProvider(new TableColumnTreeContentProvider());
    setLabelProvider(new TableColumnTreeLabelProvider());

    setInput("ignored");

    expandAll();
  }

  protected void widgetDisposed(final DisposeEvent e) {
    tableModel.removeTableModificationListener(this);
  }

  private class TableColumnTreeContentProvider implements ITreeContentProvider {
    protected final Object[] EMPTY_ARRAY = new Object[0];

    private TreeViewer viewer;

    protected TreeViewer getViewer() {
      return viewer;
    }

    public Object getParent(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getParent arg is " + element);
      }
      if (tableModel.isColumn((ConceptUtilityInterface) element)) {
        return tableModel.getWrappedTable();
      } else {
        return null;
      }
    }

    public boolean hasChildren(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("hasChildren arg is " + element);
      }
      if (tableModel.isColumn((ConceptUtilityInterface) element)) {
        if (logger.isDebugEnabled()) {
          logger.debug("hasChildren returning false");
        }
        return false;
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("hasChildren returning true");
        }
        return true;
      }
    }

    public void dispose() {
      if (logger.isDebugEnabled()) {
        logger.debug("dispose");
      }
      // nothing to dispose
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      if (logger.isDebugEnabled()) {
        logger.debug("inputChanged");
      }
      this.viewer = (TreeViewer) viewer;
      // no need to adjust listeners
    }

    public Object[] getChildren(final Object parentElement) {
      if (logger.isDebugEnabled()) {
        logger.debug("getChildren arg is " + parentElement);
      }
      if (tableModel.isColumn((ConceptUtilityInterface) parentElement)) {
        return EMPTY_ARRAY;
      } else {
        return tableModel.getColumns();
      }
    }

    public Object[] getElements(final Object inputElement) {
      if (logger.isDebugEnabled()) {
        logger.debug("getElements arg is " + inputElement);
      }
      return new Object[] { tableModel.getWrappedTable() };
    }

  }

  private class TableColumnTreeLabelProvider implements ILabelProvider {

    public Image getImage(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getImage arg is " + element);
      }
      if (decorate) {
        if (tableModel.isColumn((ConceptUtilityInterface) element)) {
          return Constants.getImageRegistry(Display.getCurrent()).get("column");
        } else {
          return Constants.getImageRegistry(Display.getCurrent()).get("table");
        }
      }
      return null;
    }

    public String getText(final Object element) {
      if (logger.isDebugEnabled()) {
        logger.debug("getText arg is " + element);
      }
      ConceptUtilityInterface conceptHolder = (ConceptUtilityInterface) element;
      
      String name = conceptHolder.getConcept().getName(locale);
      if (showId || null == name) {
        return conceptHolder.getId();
      } else {
        return name;
      }
    }

    public void addListener(final ILabelProviderListener listener) {
      // not used
    }

    public void dispose() {
      // not used
    }

    public boolean isLabelProperty(final Object element, final String property) {
      // not used
      return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
      // not used
    }
  }


}
