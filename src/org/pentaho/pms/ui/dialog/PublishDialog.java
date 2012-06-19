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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * @author wseyler
 *
 */
public class PublishDialog extends TitleAreaDialog {
  
  private static final String LAST_USED_METADATA_FILE = "metadata.xmi";
  private static final String LAST_USED_PROP = "last_used"; //    //publishPassword = tPublishPassword.getText();$NON-NLS-1$
  private static final String URL_PROPS_FILE = "ui/publishUrls.properties"; //$NON-NLS-1$ 
  private static final String METADATA_FILES_FILE ="ui/publishMdFiles.properties"; 
  private static final String SOLUTIONS_PROPS_FILE = "ui/publishSolutions.properties";
  private static final String DEFAULT_PUBLISH_URL = "http://localhost:8080/pentaho/plugin/data-access/api/metadata/import"; //$NON-NLS-1$
  private static final String DEFAULT_METADATA_FILE= "metadata.xmi";
  private SchemaMeta schemaMeta;
  
  private LogWriter log;
  private PropsUI props;
  
  private String serverURL;
  private String userId;
  private String userPassword;
  
  private Combo tServerURL;
  private Combo tMdFileName;
  
  private Text tUserId;
  private Text tUserPassword;
  
  private Properties publishUrls;
  private Properties solutionFolders;
  private Properties mdFiles; 

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

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;

    Label label2 = new Label (c1, SWT.NONE);
    label2.setText (Messages.getString("PublishDialog.LABEL_SERVER"));
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 470;
    label2.setLayoutData (data);
    
    c0.setBackground(label2.getBackground());
    c1.setBackground(label2.getBackground());
    
    
    tServerURL = new Combo(c1, SWT.DROP_DOWN);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 530;
    tServerURL.setLayoutData (data);
    
    populateServerUrl();
    populateSolutionFolders();
    populateMetadataFiles();
    
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.minimumWidth = 300;
    
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
        if (ok()) {
          setReturnCode(buttonId);
          close();
        }
        break;
      case IDialogConstants.CANCEL_ID:
        cancel();
        setReturnCode(buttonId);
        close();
        break;
    }
  }
  
  private boolean ok() {
    if (!populateStrings()) {
      return false;
    }
    String domainId = schemaMeta.getDomainName();
    CWM cwmInstance = CWM.getInstance(domainId);
    try {
      String xmi = cwmInstance.getXMI();
      BufferedWriter out = new BufferedWriter(new FileWriter(DEFAULT_METADATA_FILE));
      out.write(xmi);
      out.close();
      File file = new File(DEFAULT_METADATA_FILE);
      file.deleteOnExit();
      InputStream stream = new FileInputStream(file);
      FormDataMultiPart part = new FormDataMultiPart().field("domainId", domainId, MediaType.TEXT_PLAIN_TYPE).field("metadataFile", stream, MediaType.APPLICATION_XML_TYPE);
      Client client = Client.create();
      client.addFilter(new HTTPBasicAuthFilter(userId, userPassword));
      WebResource resource = client.resource(serverURL);
      String result = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).put(String.class, part);
      if(result.equals("SUCCESS")) {
    	  MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
          mb.setText(Messages.getString("PublishDialog.ACTION_SUCCEEDED")); //$NON-NLS-1$
          mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_SUCCEEDED", domainId)); //$NON-NLS-1$
          mb.open();
          dispose();
          return true;
      } else {
    	  MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
          mb.setText(Messages.getString("PublishDialog.ACTION_FAILED")); //$NON-NLS-1$
          mb.setMessage(Messages.getString("PublishDialog.FILE_SAVE_FAILED", domainId)); //$NON-NLS-1$
          mb.open();
          return false;
    	  
      }
    } catch (Exception e) {
      new ErrorDialog(
          getShell(),
          Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("PublishDialog.ACTION_FAILED"), e); //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    } finally {
      updateUrlPropsFile();
    }
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
  
  private void populateSolutionFolders() {
	    String lastUsedSolution = ""; //$NON-NLS-1$
	    FileInputStream fis = null;
	    solutionFolders = new Properties();
	    File file = new File(SOLUTIONS_PROPS_FILE);
	    if (file.exists()) {
	      try {
	        fis = new FileInputStream(file);
	        solutionFolders.load(fis);
	      } catch (IOException ex) {
	    	// ignore any exceptions
	      } finally {
	        if (fis != null) {
	          try {
	            fis.close();
	          } catch (Exception e) {
	            // ignore any close exceptions
	          }
	        }
	      }
	      if (solutionFolders.size() > 0) {
	        List<String> folders = new ArrayList<String>();
	        for (Object pname : solutionFolders.keySet()) {
	          String paramName = pname.toString();
	          if (paramName.equals(LAST_USED_PROP)) {
	            lastUsedSolution = solutionFolders.getProperty(paramName);
	          } else {
	            folders.add(solutionFolders.getProperty(paramName));
	          }
	        }
	        // set the default value if available
	        if (StringUtils.isBlank(lastUsedSolution) && folders.size() > 0) {
	          lastUsedSolution = folders.get(0);
	        }
	      }
	    } 
	  }
	  
  private void populateMetadataFiles() {
	    String lastUsedMdFile = ""; //$NON-NLS-1$
	    FileInputStream fis = null;
	    mdFiles = new Properties();
	    File file = new File(METADATA_FILES_FILE);
	    if (file.exists()) {
	      try {
	        fis = new FileInputStream(file);
	        mdFiles.load(fis);
	      } catch (IOException ex) {
	    	    // ignore any exceptions
	      } finally {
	        if (fis != null) {
	          try {
	            fis.close();
	          } catch (Exception e) {
	            // ignore any close exceptions
	          }
	        }
	      }
	      if (mdFiles.size() > 0) {
	        List<String> files = new ArrayList<String>();
	        for (Object pname : mdFiles.keySet()) {
	          String paramName = pname.toString();
	          if (paramName.equals(LAST_USED_METADATA_FILE)) {
	            lastUsedMdFile = mdFiles.getProperty(paramName);
	          } else {
	            files.add(mdFiles.getProperty(paramName));
	          }
	        }
	        tMdFileName.setItems(files.toArray(new String[0]));
	        // set the default value if available
	        if (StringUtils.isBlank(lastUsedMdFile) && files.size() > 0) {
	          lastUsedMdFile = files.get(0);
	        }
	        tMdFileName.setText(lastUsedMdFile);
	      } 
	    } 
	  }

  
  /**
   * 
   */
  private boolean populateStrings() {
    serverURL = tServerURL.getText();
    userId = tUserId.getText();
    userPassword = tUserPassword.getText();
    return !StringUtils.isEmpty(serverURL) && !StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userPassword);
  }

  private void cancel() {
      dispose();
  }

}
