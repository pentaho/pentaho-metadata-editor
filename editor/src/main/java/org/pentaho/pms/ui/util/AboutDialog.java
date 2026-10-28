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


package org.pentaho.pms.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

  public AboutDialog( Shell parent ) {
    super( parent );
  }

  public void open() throws Exception {
    Shell splashShell = new Shell( getParent(), SWT.APPLICATION_MODAL + SWT.RESIZE );
    new Splash( getParent().getDisplay(), splashShell );
  }
}
