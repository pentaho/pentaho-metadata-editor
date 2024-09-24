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

package org.pentaho.pms.ui;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.util.LegacyLocalizationUtil;

public class LocaleExportProgressDialog {
  
  private Shell shell;
  
  private LegacyLocalizationUtil localizationUtility = new LegacyLocalizationUtil();
  
  private SchemaMeta schemaMeta;
  private String locale;
  private String filename;
  
  /**
   * Creates a new dialog that will handle the wait while exporting a locale...
   */
  public LocaleExportProgressDialog(Shell shell, SchemaMeta schemaMeta, String locale, String filename) {
    this.shell = shell;
    this.schemaMeta = schemaMeta;
    this.locale = locale;
    this.filename = filename;
  }
  
  public void open() throws Exception {
    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
          monitor.beginTask(Messages.getString("LocaleExportProgressDialog.USER_EXPORTING_LOCALE"), 100); //$NON-NLS-1$
          
          monitor.worked(20);
          
          monitor.subTask(Messages.getString("LocaleExportProgressDialog.USER_INITIALIZING")); //$NON-NLS-1$
          Thread.sleep(500);
          monitor.worked(5);
          
          monitor.subTask(Messages.getString("LocaleExportProgressDialog.USER_EXPORTING")); //$NON-NLS-1$
          Properties props = localizationUtility.exportLocalizedProperties(schemaMeta, locale);
          Thread.sleep(500);
          monitor.worked(40);
          
          monitor.subTask(Messages.getString("LocaleExportProgressDialog.USER_WRITING")); //$NON-NLS-1$
          FileOutputStream fos = new FileOutputStream(filename);
          OutputStreamWriter osw = new OutputStreamWriter(fos);
          props.store(osw, null);
          
          osw.flush();
          osw.close();
          
          fos.flush();
          fos.close();
          Thread.sleep(750);
          monitor.worked(30);

          monitor.subTask(Messages.getString("LocaleExportProgressDialog.USER_FINALIZING")); //$NON-NLS-1$
          Thread.sleep(500);
          monitor.worked(5);
          monitor.done();
        } catch (Exception e) {
          throw new InvocationTargetException(e);
        }
      }
    };
    
    ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
    pmd.run(false, false, op);
  }
}
