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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.schema.concept.ConceptInterface;

public class RelatedConceptModificationEvent extends ConceptModificationEvent {

  // ~ Static fields/initializers ======================================================================================


  private static final long serialVersionUID = 4845956108753834446L;

  private static final Log logger = LogFactory.getLog(RelatedConceptModificationEvent.class);

  public static final int ADD_RELATED_CONCEPT = 0;

  public static final int CHANGE_RELATED_CONCEPT = 1;

  public static final int REMOVE_RELATED_CONCEPT = 2;

  // ~ Instance fields =================================================================================================

  private int relatedConcept;

  private int type;

  private ConceptInterface oldValue;

  private ConceptInterface newValue;

  // ~ Constructors ====================================================================================================

  public RelatedConceptModificationEvent(final Object source, final int type, final int relatedConcept,
      final ConceptInterface oldValue, final ConceptInterface newValue) {
    super(source);
    this.type = type;
    this.relatedConcept = relatedConcept;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public ConceptInterface getNewValue() {
    return newValue;
  }

  public ConceptInterface getOldValue() {
    return oldValue;
  }

  /**
   * Returns one of the <code>static final</code> members of <code>IConceptModel</code>. (e.g. <code>REL_*</code>)
   * @return
   */
  public int getRelatedConcept() {
    return relatedConcept;
  }

  public int getType() {
    return type;
  }

  // ~ Methods =========================================================================================================

}
