/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.pms.ui.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This is the SWT editor for the Aggregation list concept property.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class AggregationListPropertyEditorWidget extends AbstractPropertyEditorWidget implements SelectionListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(AggregationListPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  Label typeLabel;

  List multiSelectList;

  // ~ Constructors ====================================================================================================

  public AggregationListPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created AggregationPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    typeLabel = new Label(parent, SWT.NONE);
    typeLabel.setText("Aggregation List:");
    multiSelectList = new List(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
    
    multiSelectList.addSelectionListener(this);
    multiSelectList.setItems(AggregationSettings.typeDescriptions);

    FormData fdType = new FormData();
    fdType.left = new FormAttachment(typeLabel, 10);
    fdType.top = new FormAttachment(0, 0);
    multiSelectList.setLayoutData(fdType);

    FormData fdTypeLabel = new FormData();
    fdTypeLabel.left = new FormAttachment(0, 0);
    fdTypeLabel.top = new FormAttachment(multiSelectList, 0, SWT.CENTER);
    typeLabel.setLayoutData(fdTypeLabel);
  }

  public Object getValue() {
    int selected[] = multiSelectList.getSelectionIndices();
    if (selected != null && selected.length > 0) {
      java.util.List list = new java.util.ArrayList<AggregationSettings>();
      for (int i = 0; i < selected.length; i++) {
        list.add(AggregationSettings.types[selected[i]]);
      }
      return list;
    }
    return null;
  }

  protected void setValue(final Object value) {
    if (value instanceof java.util.List) {
      java.util.List<AggregationSettings> list = (java.util.List<AggregationSettings>)value;
      int[] selected = new int[list.size()];
      for (int i = 0; i < list.size(); i++) {
        selected[i] = list.get(i).getType();
      }
      multiSelectList.select(selected);
    }
  }

  public String validate() {
    return null;
  }
  
  public void refresh() {
    refreshOverrideButton();
    typeLabel.setEnabled(isEditable());
    multiSelectList.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }

  public void cleanup() {
  }

  public void widgetDefaultSelected(SelectionEvent arg0) {
    putPropertyValue();
    
  }

  public void widgetSelected(SelectionEvent arg0) {
    putPropertyValue();
  }

}
