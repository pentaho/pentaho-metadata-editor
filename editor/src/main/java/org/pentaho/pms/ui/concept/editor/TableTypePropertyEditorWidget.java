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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;

public class TableTypePropertyEditorWidget extends AbstractPropertyEditorWidget implements ISelectionChangedListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TableTypePropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private ComboViewer typeComboViewer;

  Label typeLabel;

  Combo type;

  // ~ Constructors ====================================================================================================

  public TableTypePropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created TableTypePropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        TableTypePropertyEditorWidget.this.widgetDisposed(e);
      }
    });
    typeLabel = new Label(parent, SWT.NONE);
    typeLabel.setText("Table Type:");

    type = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

    typeComboViewer = new ComboViewer(type);

    typeComboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        return (TableTypeSettings[]) inputElement;
      }

      public void dispose() {
      }

      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
    });

    typeComboViewer.setInput(TableTypeSettings.types);

    typeComboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(final Object element) {
        // no images in this combo
        return null;
      }

      public String getText(final Object element) {
        return ((TableTypeSettings) element).getDescription();
      }
    });

    FormData fdType = new FormData();
    fdType.left = new FormAttachment(typeLabel, 10);
    fdType.top = new FormAttachment(0, 0);
    type.setLayoutData(fdType);

    FormData fdTypeLabel = new FormData();
    fdTypeLabel.left = new FormAttachment(0, 0);
    fdTypeLabel.top = new FormAttachment(type, 0, SWT.CENTER);
    typeLabel.setLayoutData(fdTypeLabel);

    typeComboViewer.addSelectionChangedListener(this);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    IStructuredSelection selection = (IStructuredSelection) typeComboViewer.getSelection();
    TableTypeSettings tableTypeSettings = (TableTypeSettings) selection.getFirstElement();
    int aggType = tableTypeSettings.getType();
    return new TableTypeSettings(aggType);
  }

  protected void setValue(final Object value) {
    if (value instanceof TableTypeSettings) {
      TableTypeSettings tableTypeSettings = (TableTypeSettings) value;
      typeComboViewer.setSelection(new StructuredSelection(TableTypeSettings.getType(tableTypeSettings.getCode())));
    }
  }

  public void refresh() {
    refreshOverrideButton();
    typeComboViewer.removeSelectionChangedListener(this);
    typeLabel.setEnabled(isEditable());
    type.setEnabled(isEditable());
    setValue(getProperty().getValue());
    typeComboViewer.addSelectionChangedListener(this);
  }

  public String validate() {
    return null;
  }

  public void selectionChanged(SelectionChangedEvent arg0) {
    putPropertyValue();
  }

  public void cleanup() {
    typeComboViewer.removeSelectionChangedListener(this);
  }

}
