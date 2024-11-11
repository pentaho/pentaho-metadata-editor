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
