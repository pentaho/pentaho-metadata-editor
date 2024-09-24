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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PropertyModificationEvent extends ConceptModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyModificationEvent.class);

  private static final long serialVersionUID = -5810693858905811872L;

  // ~ Instance fields =================================================================================================

  private String propertyId;

  // ~ Constructors ====================================================================================================

  public PropertyModificationEvent(final Object source, final String propertyId) {
    super(source);
    this.propertyId = propertyId;
  }

  // ~ Methods =========================================================================================================

  public String getPropertyId() {
    return propertyId;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this).toString();
  }

}
