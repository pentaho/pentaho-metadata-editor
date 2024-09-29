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


package org.pentaho.pms.ui;

import org.pentaho.pms.mql.MQLQuery;

public interface QueryBuilderDialogListener {

  public void onOk(MQLQuery query);
  
  public void onCancel();
}
