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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * This widget is used to allow the users to enter dates.
 * If you want to create properties like "Creation date", etc.
 * 
 * @author mlowery
 */
public class DatePropertyEditorWidget extends AbstractPropertyEditorWidget {

  public DatePropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
  }

  protected void createContents(final Composite parent) {

    DateTime calendar = new DateTime(parent, SWT.CALENDAR);

    DateTime time = new DateTime(parent, SWT.TIME);

  }

  public Object getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  public String validate() {
    if (isEditable()) {
      return String.format("%s is invalid.", PredefinedVsCustomPropertyHelper.getDescription(getPropertyId()));
    }
    return null;
  }

  protected void setValue(final Object value) {
  }

  public void refresh() {
    refreshOverrideButton();
    setValue(getProperty().getValue());
  }

  public void cleanup() {
  }
}
