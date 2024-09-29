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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;

public class DataTypePropertyEditorWidget extends AbstractPropertyEditorWidget implements FocusListener,
    ISelectionChangedListener {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(DataTypePropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private ComboViewer typeComboViewer;

  private Text length;

  private Text precision;

  Label typeLabel;

  Combo type;

  Label lengthLabel;

  Label precisionLabel;

  // ~ Constructors ====================================================================================================

  public DataTypePropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created DataTypePropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        DataTypePropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    typeLabel = new Label(parent, SWT.NONE);
    typeLabel.setText("Data Type:");

    type = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

    typeComboViewer = new ComboViewer(type);

    typeComboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(final Object inputElement) {
        return (DataTypeSettings[]) inputElement;
      }

      public void dispose() {
      }

      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
    });

    typeComboViewer.setInput(DataTypeSettings.types);

    typeComboViewer.setLabelProvider(new LabelProvider() {
      public Image getImage(final Object element) {
        // no images in this combo
        return null;
      }

      public String getText(final Object element) {
        return ((DataTypeSettings) element).getDescription();
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

    // Length
    lengthLabel = new Label(parent, SWT.NONE);
    length = new Text(parent, SWT.BORDER);

    lengthLabel.setText("Length:");
    //    lengthLabel.setText(Messages.getString("ConceptPropertyDataTypeWidget.USER_LENGTH"));  //$NON-NLS-1$
    FormData fdLengthLabel = new FormData();
    fdLengthLabel.left = new FormAttachment(0, 0);
    fdLengthLabel.top = new FormAttachment(length, 0, SWT.CENTER);
    lengthLabel.setLayoutData(fdLengthLabel);

    FormData fdLength = new FormData();
    fdLength.left = new FormAttachment(lengthLabel, 10);
    fdLength.top = new FormAttachment(type, 10);
    fdLength.right = new FormAttachment(100, 0);
    length.setLayoutData(fdLength);

    // Precision
    precisionLabel = new Label(parent, SWT.NONE);
    precision = new Text(parent, SWT.BORDER);

    //    precisionLabel.setText(Messages.getString("ConceptPropertyDataTypeWidget.USER_PRECISION")); //$NON-NLS-1$
    precisionLabel.setText("Precision:");
    FormData fdPrecisionLabel = new FormData();
    fdPrecisionLabel.left = new FormAttachment(0, 0);
    fdPrecisionLabel.top = new FormAttachment(precision, 0, SWT.CENTER);
    precisionLabel.setLayoutData(fdPrecisionLabel);

    FormData fdPrecision = new FormData();
    fdPrecision.left = new FormAttachment(precisionLabel, 10);
    fdPrecision.top = new FormAttachment(length, 10);
    fdPrecision.right = new FormAttachment(100, 0);
    precision.setLayoutData(fdPrecision);

    typeComboViewer.addSelectionChangedListener(this);
    length.addFocusListener(this);
    precision.addFocusListener(this);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    IStructuredSelection selection = (IStructuredSelection) typeComboViewer.getSelection();
    DataTypeSettings dataTypeSettings = (DataTypeSettings) selection.getFirstElement();
    int dataType = dataTypeSettings.getType();
    int lengthInt = 0;
    try {
      lengthInt = Integer.parseInt(length.getText());
    } catch (NumberFormatException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("length field (int) could not be parsed");
      }
    }
    int precisionInt = 0;
    try {
      precisionInt = Integer.parseInt(precision.getText());
    } catch (NumberFormatException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("precision field (int) could not be parsed");
      }
    }
    return new DataTypeSettings(dataType, lengthInt, precisionInt);
  }

  protected void setValue(final Object value) {
    if (value instanceof DataTypeSettings) {
      DataTypeSettings dataTypeSettings = (DataTypeSettings) value;
      typeComboViewer.setSelection(new StructuredSelection(DataTypeSettings.getType(dataTypeSettings.getCode())));
      length.setText(Integer.toString(dataTypeSettings.getLength()));
      precision.setText(Integer.toString(dataTypeSettings.getPrecision()));
    }
  }

  public String validate() {
    if (isEditable()) {
      try {
        new Integer(precision.getText());
        new Integer(length.getText());
      } catch (NumberFormatException e) {
        return String.format("%s is not a valid number.", PredefinedVsCustomPropertyHelper
            .getDescription(getPropertyId()));
      }
    }
    return null;
  }

  public void focusGained(FocusEvent arg0) {
  }

  public void focusLost(FocusEvent arg0) {
    if (!getValue().equals(getProperty().getValue())) {
      putPropertyValue();
    }
  }

  public void selectionChanged(SelectionChangedEvent arg0) {
    putPropertyValue();
  }

  public void refresh() {
    refreshOverrideButton();
    typeComboViewer.removeSelectionChangedListener(this);
    precisionLabel.setEnabled(isEditable());
    precision.setEnabled(isEditable());
    typeLabel.setEnabled(isEditable());
    type.setEnabled(isEditable());
    lengthLabel.setEnabled(isEditable());
    length.setEnabled(isEditable());
    setValue(getProperty().getValue());
    typeComboViewer.addSelectionChangedListener(this);
  }

  public void cleanup() {
    typeComboViewer.removeSelectionChangedListener(this);
    length.removeFocusListener(this);
    precision.removeFocusListener(this);
  }
}
