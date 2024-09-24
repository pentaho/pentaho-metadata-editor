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

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * Encapsulates common logic for deciding between pre-defined (aka default) vs custom properties.
 *
 * <p>Eventually, this class will go away due to improvements from PMD-135.</p>
 * @author mlowery
 */
public class PredefinedVsCustomPropertyHelper {

  /**
   * Given a property id, this method will return either (1) a description if the property is pre-defined or (2) an id
   * if the property is custom.
   * @param propertyId the property id for which to fetch a description
   * @return string description
   */
  public static String getDescription(final String propertyId) {
    DefaultPropertyID defaultPropertyId = DefaultPropertyID.findDefaultPropertyID(propertyId);
    if (null != defaultPropertyId) {
      return defaultPropertyId.getDescription();
    } else {
      return propertyId;
    }
  }

  public static ConceptPropertyInterface createEmptyProperty(final String propertyId, final ConceptPropertyType type) {
    DefaultPropertyID defaultPropertyId = DefaultPropertyID.findDefaultPropertyID(propertyId);
    if (null != defaultPropertyId) {
      return defaultPropertyId.getDefaultValue();
    } else {
      return DefaultPropertyID.getDefaultEmptyProperty(type, propertyId);
    }
  }
}
