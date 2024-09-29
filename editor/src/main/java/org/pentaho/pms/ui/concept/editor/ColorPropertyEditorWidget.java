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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;

public class ColorPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================
  private static final Log logger = LogFactory.getLog(ColorPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Color color = null;

  private Button button;

  Label colorLabel;

  // ~ Constructors ====================================================================================================

  public ColorPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created ColorPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(final DisposeEvent e) {
        if (null != color) {
          color.dispose();
        }
      }
    });
    colorLabel = new Label(parent, SWT.NONE);
    colorLabel.setText("Color:");

    button = new Button(parent, SWT.PUSH);
    button.setText("      ");

    FormData fdButton = new FormData();
    fdButton.left = new FormAttachment(colorLabel, 10);
    fdButton.top = new FormAttachment(0, 0);
    button.setLayoutData(fdButton);

    FormData fdColorLabel = new FormData();
    fdColorLabel.left = new FormAttachment(0, 0);
    fdColorLabel.top = new FormAttachment(button, 0, SWT.CENTER);
    colorLabel.setLayoutData(fdColorLabel);

    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        ColorDialog dlg = new ColorDialog(Display.getCurrent().getActiveShell());

        // Set the selected color in the dialog from user's selected color
        if (color != null) {
          dlg.setRGB(color.getRGB());
        }

        dlg.setText("Choose a Color");

        // Open the dialog and retrieve the selected color
        RGB rgb = dlg.open();
        if (logger.isDebugEnabled()) {
          logger.debug("User selected color: " + rgb);
        }
        if (rgb != null) {
          if (null != color) {
            color.dispose();
          }
          color = new Color(Display.getCurrent(), rgb);
          setValue(color);
          putPropertyValue();
          button.redraw();
        }
      }
    });

    button.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent event) {
        if (color != null) {
          Rectangle rect = button.getBounds();
          if (logger.isDebugEnabled()) {
            logger.debug("rect: " + rect);
          }
          event.gc.setBackground(color);
          event.gc.fillRectangle(5, 5, rect.width - 10, rect.height - 10);
        }
      }
    });

  }

  public Object getValue() {
    return color != null ? new ColorSettings(color.getRed(), color.getGreen(), color.getBlue()) : null;
  }

  protected void setValue(final Object value) {
    if (value instanceof ColorSettings) {
      ColorSettings colorSettings = (ColorSettings) value;
      color = new Color(Display.getCurrent(), colorSettings.getRed(), colorSettings.getGreen(), colorSettings.getBlue());
    } else {
      this.color = (Color) value;
    }
    button.redraw();
  }

  public void refresh() {
    refreshOverrideButton();
    colorLabel.setEnabled(isEditable());
    button.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }

  public String validate() {
    return null;
  }

  public void cleanup() {
    // TODO Auto-generated method stub

  }

}
