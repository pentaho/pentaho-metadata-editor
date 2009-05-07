package org.pentaho.pms.ui;

import org.pentaho.pms.mql.MQLQuery;

public interface QueryBuilderDialogListener {

  public void onOk(MQLQuery query);
  
  public void onCancel();
}
