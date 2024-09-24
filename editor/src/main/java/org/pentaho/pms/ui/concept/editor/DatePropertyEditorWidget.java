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
