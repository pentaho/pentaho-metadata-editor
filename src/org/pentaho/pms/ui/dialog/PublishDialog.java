/*
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
 * Copyright (c) 2007 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.ui.dialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.platform.util.client.PublisherUtil;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;

/**
 * @author wseyler
 *
 */
public class PublishDialog extends TitleAreaDialog {
  
  private static final String LAST_USED_PROP = "last_used"; //$NON-NLS-1$
  private static final String DEFAULT_SOLUTION = "steel-wheels"; //$NON-NLS-1$
  private static final String URL_PROPS_FILE = "ui/publishUrls.properties"; //$NON-NLS-1$ 
  private static final String DEFAULT_PUBLISH_URL = "http://localhost:8080/pentaho/RepositoryFilePublisher"; //$NON-NLS-1$
  private SchemaMeta schemaMeta;
  
  private LogWriter log;
  private PropsUI props;
  
  private String serverURL;
  private String solutionName;
  private String fileName = "metadata.xmi"; //$NON-NLS-1$
  
  private String userId;
  private String userPassword;
  private String publishPassword;
  
  private Combo tServerURL;
  private Text tSolutionName;
  
  private Text tUserId;
  private Text tUserPassword;
  private Text tPublishPassword;
  
  private Properties publishUrls;

  /**
   * @param parent
   */
  public PublishDialog(Shell parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
    log = LogWriter.getInstance();
    props = PropsUI.getInstance();
  }

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    setMessage(Messages.getString("PublishDialog.USER_DIALOG_MESSAGE")); //$NON-NLS-1$
    setTitle(Messages.getString("PublishDialog.USER_DIALOG_TITLE")); //$NON-NLS-1$
    return contents;
  }

  protected Control createDialogArea(final Composite parent) {
    
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    
    GridLayout gridLayout = new GridLayout ();
    
    c1.setLayout(gridLayout);
    props.setLook(c1);

    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    

    Label label0 = new Label (c1, SWT.NONE);
    label0.setText (Messages.getString("PublishDialog.LABEL_SOLUTION"));
    label0.setLayoutData (data);
    
    c0.setBackground(label0.getBackground());
    c1.setBackground(label0.getBackground());

    tSolutionName = new Text (c1, SWT.BORDER);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    tSolutionName.setText(DEFAULT_SOLUTION); 
    tSolutionName.setLayoutData (data);

    Label label2 = new Label (c1, SWT.NONE);
    label2.setText (Messages.getString("PublishDialog.LABEL_SERVER"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    label2.setLayoutData (data);

    tServerURL = new Combo(c1, SWT.DROP_DOWN);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    tServerURL.setLayoutData (data);
    
    populateServerUrl();
    
    Label label4 = new Label (c1, SWT.NONE);
    label4.setText (Messages.getString("PublishDialog.LABEL_PUBLISH_PASSWORD"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label4.setLayoutData (data);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    
    tPublishPassword = new Text (c1, SWT.BORDER | SWT.PASSWORD);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tPublishPassword.setLayoutData (data);
    // Add code to check for a properties file containing the default
    // publish password. For this to work, the file needs to be located in the
    // lib directory if it is to be found.
    try {
      ResourceBundle bundle = ResourceBundle.getBundle("publishpassword"); //$NON-NLS-1$
      String defaultPassword = bundle.getString("default.password"); //$NON-NLS-1$
      if ( (defaultPassword != null) && (defaultPassword.length() > 0) ) {
        // System.out.println("Default Password:" + defaultPassword);
        tPublishPassword.setText(defaultPassword);
      }
    } catch (Exception ex) {
      // No publishpassword.properties
      // no need to log this, it's not an error if this occurs.
    }

    Label label6 = new Label (c1, SWT.NONE);
    label6.setText (Messages.getString("PublishDialog.LABEL_USER"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label6.setLayoutData (data);

    tUserId = new Text (c1, SWT.BORDER);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tUserId.setLayoutData (data);

    Label label8 = new Label (c1, SWT.NONE);
    label8.setText (Messages.getString("PublishDialog.LABEL_PASSWORD"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    label8.setLayoutData (data);

    tUserPassword = new Text (c1, SWT.BORDER | SWT.PASSWORD);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    tUserPassword.setLayoutData (data);

    return c0;

  }

  public void dispose() {
    props.setScreen(new WindowProperty(getShell()));
    getShell().dispose();
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("PublishDialog.TITLE")); //$NON-NLS-1$
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

//  WG: commented out so we can see the password text field in linux
//  protected Point getInitialSize() {
//    return new Point(524, 400);
//  }

  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  protected void buttonPressed(int buttonId) {

    switch (buttonId) {
      case IDialogConstants.OK_ID:
        ok();
        break;
      case IDialogConstants.CANCEL_ID:
        cancel();
        break;
    }

    setReturnCode(buttonId);
    close();
  }
  
  private void ok() {
    if (!populateStrings()) {
      return;
    }
    
    CWM cwmInstance = CWM.getInstance(schemaMeta.getDomainName());
    try {
      String xmi = cwmInstance.getXMI();
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      out.write(xmi);
      out.close();
      File file = new File(fileName);
      file.deleteOnExit();
      File[] files = {file};
      int result = PublisherUtil.publish(serverURL, solutionName, files, publishPassword, userId, userPassword, false);
      if (result == PublisherUtil.FILE_EXISTS) {
        MessageBox mb = new MessageBox(getShell(), SWT.NO | SWT.YES | SWT.ICON_WARNING);
        mb.setText(Messages.getString("PublishDialog.FILE_EXISTS")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_OVERWRITE")); //$NON-NLS-1$
        if (mb.open() == SWT.YES) {
          result = PublisherUtil.publish(serverURL, solutionName, files, publishPassword, userId, userPassword, true);
        } else {
          return;
        }
      }
      if (result != PublisherUtil.FILE_ADD_SUCCESSFUL) {
        MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
        mb.setText(Messages.getString("PublishDialog.ACTION_FAILED")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_FAILED", fileName)); //$NON-NLS-1$
        mb.open();
      } else {  // We did it!
        MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
        mb.setText(Messages.getString("PublishDialog.ACTION_SUCCEEDED")); //$NON-NLS-1$
        mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_SUCCEEDED", fileName)); //$NON-NLS-1$
        mb.open();
        dispose();
      }
    } catch (Exception e) {
      new ErrorDialog(
          getShell(),
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("PublishDialog.ACTION_FAILED"), e); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    // update the props file even if the connection fails
    updateUrlPropsFile();
  }
  
  private void updateUrlPropsFile() {
    // update publish url file
    boolean lastUsedChanged = false;
    if ((publishUrls.getProperty(LAST_USED_PROP) == null) || 
        !publishUrls.getProperty(LAST_USED_PROP).equals(serverURL)) {
      lastUsedChanged = true;
    }
    publishUrls.setProperty(LAST_USED_PROP, serverURL);
    boolean newURL = true;
    for (Object pname : publishUrls.keySet()) {
      String paramName = pname.toString();
      if (!paramName.equals(LAST_USED_PROP)) {
        if (publishUrls.getProperty(paramName).equals(serverURL)) {
          newURL = false;
        }
      }
    }
    if (newURL) {
      publishUrls.setProperty("url" + publishUrls.size(), serverURL); //$NON-NLS-1$
    }
    
    if (newURL || lastUsedChanged) {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(URL_PROPS_FILE);
        publishUrls.store(fos, "Pentaho Metadata publish urls."); //$NON-NLS-1$
      } catch (IOException e) {
        new ErrorDialog(
            getShell(),
            Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("PublishDialog.ACTION_FAILED"), e); //$NON-NLS-1$ //$NON-NLS-2$

      } finally {
        try {
          if (fos != null) {
            fos.close();
          }
        } catch (Exception e) {
          // ignore any close exceptions
        }
      }
    }
  }

  private void populateServerUrl() {
    String lastUsedUrl = ""; //$NON-NLS-1$
    FileInputStream fis = null;
    publishUrls = new Properties();
    File file = new File(URL_PROPS_FILE);
    if (file.exists()) {
      try {
        fis = new FileInputStream(file);
        publishUrls.load(fis);
      } catch (IOException ex) {
        // populate the dialog with a default value
        tServerURL.setText(DEFAULT_PUBLISH_URL);
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (Exception e) {
            // ignore any close exceptions
          }
        }
      }
      if (publishUrls.size() > 0) {
        List<String> urls = new ArrayList<String>();
        for (Object pname : publishUrls.keySet()) {
          String paramName = pname.toString();
          if (paramName.equals(LAST_USED_PROP)) {
            lastUsedUrl = publishUrls.getProperty(paramName);
          } else {
            urls.add(publishUrls.getProperty(paramName));
          }
        }
        tServerURL.setItems(urls.toArray(new String[0]));
        // set the default value if available
        if (StringUtils.isBlank(lastUsedUrl) && urls.size() > 0) {
          lastUsedUrl = urls.get(0);
        }
        tServerURL.setText(lastUsedUrl);
      } else {
        tServerURL.setText(DEFAULT_PUBLISH_URL);
      }
    } else {
      tServerURL.setText(DEFAULT_PUBLISH_URL);
    }
  }
  
  /**
   * 
   */
  private boolean populateStrings() {
    String seperatorFwd = "/"; //$NON-NLS-1$
    String seperatorBck = "\\"; //$NON-NLS-1$
    
    serverURL = tServerURL.getText();
//    if (!serverURL.endsWith(seperatorFwd)) {
//      serverURL += seperatorFwd;
//    }
    solutionName = tSolutionName.getText();
    if (solutionName.indexOf(seperatorFwd) >= 0 || solutionName.indexOf(seperatorBck) >= 0) {
      MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
      mb.setText(Messages.getString("PublishDialog.LOCATION_ERROR")); //$NON-NLS-1$
      mb.setMessage(Messages.getString("PublishDialog.LOCATION_ERROR_INFO")); //$NON-NLS-1$
      mb.open();
      return false;
    }
   
    userId = tUserId.getText();
    userPassword = tUserPassword.getText();
    publishPassword = tPublishPassword.getText();
    return true;
  }

  private void cancel() {
      dispose();
  }

}
