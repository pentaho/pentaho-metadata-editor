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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StringPropertyEditorWidget extends AbstractPropertyEditorWidget implements ModifyListener {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(StringPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Text string;

  private Label stringLabel;

  // ~ Constructors ====================================================================================================

  public StringPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created StringPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        StringPropertyEditorWidget.this.widgetDisposed(e);
      }
    });

    stringLabel = new Label(parent, SWT.NONE);
    stringLabel.setText("Value:");

    string = new Text(parent, SWT.BORDER);

    FormData fd1 = new FormData();
    fd1.left = new FormAttachment(0, 0);
    fd1.top = new FormAttachment(string, 0, SWT.CENTER);
    stringLabel.setLayoutData(fd1);

    FormData fd2 = new FormData();
    fd2.left = new FormAttachment(stringLabel, 10);
    fd2.top = new FormAttachment(0, 0);
    fd2.right = new FormAttachment(100, 0);
    string.setLayoutData(fd2);

    string.addModifyListener(this);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    return string.getText();
  }

  protected void setValue(final Object value) {
    string.setText(value.toString());
  }

  public void refresh() {
    refreshOverrideButton();
    string.removeModifyListener(this);
    stringLabel.setEnabled(isEditable());
    string.setEnabled(isEditable());
    setValue(getProperty().getValue());
    string.addModifyListener(this);
  }

  public String validate() {
    return null;
  }

  public void modifyText(ModifyEvent arg0) {
    putPropertyValue();

  }

  public void cleanup() {
    string.removeModifyListener(this);
  }
}
