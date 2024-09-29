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

import java.lang.reflect.Field;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;

public class PropertyExistenceModificationEvent extends PropertyModificationEvent {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyExistenceModificationEvent.class);

  private static final long serialVersionUID = -5810693858905811872L;

  /**
   * A property addition event.
   */
  public static final int ADD_PROPERTY = 0;

  /**
   * A modification of an existing property event.
   */
  public static final int CHANGE_PROPERTY = 1;

  /**
   * A property removal event.
   */
  public static final int REMOVE_PROPERTY = 2;

  /**
   * A special type of property addition where a child property obscures a property from a parent/inherited/security
   * concept.
   */
  public static final int OVERRIDE_PROPERTY = 3;

  /**
   * A special type of property removal where a child property is removed revealing a property that is inherited from
   * a parent/inherited/security concept.
   */
  public static final int INHERIT_PROPERTY = 4;

  // ~ Instance fields =================================================================================================

  private int type;

  private ConceptPropertyInterface oldValue;

  private ConceptPropertyInterface newValue;

  // ~ Constructors ====================================================================================================

  public PropertyExistenceModificationEvent(final Object source, final String propertyId, final int type,
      final ConceptPropertyInterface oldValue, final ConceptPropertyInterface newValue) {
    super(source, propertyId);
    this.type = type;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  // ~ Methods =========================================================================================================

  /**
   * Returns one of the <code>static final</code> members of this class.
   * @return
   */
  public int getType() {
    return type;
  }

  public ConceptPropertyInterface getOldValue() {
    return oldValue;
  }

  public ConceptPropertyInterface getNewValue() {
    return newValue;
  }

  public String toString() {
    return (new ReflectionToStringBuilder(this) {
      protected Object getValue(Field f) {
        if (f.getName().equals("type")) {
          switch (type) {
            case ADD_PROPERTY: {
              return "PropertyModificationEvent.ADD_PROPERTY";
            }
            case CHANGE_PROPERTY: {
              return "PropertyModificationEvent.CHANGE_PROPERTY";
            }
            case REMOVE_PROPERTY: {
              return "PropertyModificationEvent.REMOVE_PROPERTY";
            }
            default:
              return "<unknown>";
          }
        } else {
          try {
            return super.getValue(f);
          } catch (IllegalArgumentException e) {
            return "<exception occurred>";
          } catch (IllegalAccessException e) {
            return "<exception occurred>";
          }
        }

      }
    }).toString();
  }

}
