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

import java.util.EventObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An event fired when columns are added or removed from a table. While UI elements should be concerned with name
 * changes on the columns, they can "hear" those events by subscribing to the columns' concept models.
 * @author mlowery
 */
public class TableModificationEvent extends EventObject {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TableModificationEvent.class);

  private static final long serialVersionUID = -7930085568962265187L;

  public static final int ADD_COLUMN = 1;

  public static final int REMOVE_COLUMN = -1;

  // ~ Instance fields =================================================================================================

  private String id;

  private int type;

  // ~ Constructors ====================================================================================================

  public TableModificationEvent(final Object source, final String id, final int type) {
    super(source);
    this.id = id;
    this.type = type;
  }

  // ~ Methods =========================================================================================================

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
