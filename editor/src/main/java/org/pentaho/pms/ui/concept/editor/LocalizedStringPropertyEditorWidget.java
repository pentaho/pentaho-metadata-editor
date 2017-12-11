/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.pms.ui.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.locale.Locales;

public class LocalizedStringPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(LocalizedStringPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private LocalizedStringTableWidget table;

  // ~ Constructors ====================================================================================================

  public LocalizedStringPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    Locales locales = (Locales) getContext().get("locales");
    if (null == locales) {
      if (logger.isWarnEnabled()) {
        logger.warn("missing required context attribute 'locales'; continuing anyway");
      }
      locales = new Locales();
    }
    table = new LocalizedStringTableWidget(parent, SWT.NONE, getConceptModel(), getPropertyId(), locales);

    FormData fdTable = new FormData();
    fdTable.left = new FormAttachment(0, 0);
    fdTable.top = new FormAttachment(0, 0);
    fdTable.right = new FormAttachment(100, 0);
    table.setLayoutData(fdTable);
    table.setEnabled(isEditable());
  }

  /**
   * Not used since the table widget persists values to model.
   */
  public Object getValue() {
    throw new UnsupportedOperationException();
  }

  public String validate() {
    return null;
  }

  public void cleanup() {
  }

  public void refresh() {
    refreshOverrideButton();
    table.setEnabled(isEditable());
    table.refresh();
  }

}
