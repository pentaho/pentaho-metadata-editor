/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.pms.ui;

import org.pentaho.pms.mql.MQLQuery;

public interface QueryBuilderDialogListener {

  public void onOk(MQLQuery query);
  
  public void onCancel();
}
