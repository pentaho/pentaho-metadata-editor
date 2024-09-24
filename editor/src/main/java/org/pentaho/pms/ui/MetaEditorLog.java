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
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.logging.log4j.Log4jKettleLayout;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.dialog.LogSettingsDialog;
import org.pentaho.platform.api.util.LogUtil;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.ui.util.GUIResource;

public class MetaEditorLog extends Composite
{
	private PropsUI props;
	private Shell shell;
	private Display display;
	private Text   wText;
	private Button wRefresh;
	private Button wClear;
	private Button wLog;

	private FormData fdText, fdRefresh, fdClear, fdLog; 
	
	private SelectionListener lsRefresh, lsClear, lsLog;
	private Appender appender;
	private StringWriter writer;

	public MetaEditorLog(Composite parent, int style, String fname)
	{
    super( parent, style );
    shell = parent.getShell();
    display = shell.getDisplay();
    props = PropsUI.getInstance();
    writer = new StringWriter();
    
    // Create an appender to capture logging events from org.pentaho.di and display in the Log View tab
    Logger logger = LogManager.getLogger( "org.pentaho.di" );
    appender =
        LogUtil.makeAppender( "MetaEditorLog", writer, new Log4jKettleLayout( Charset.forName( "utf-8" ), true ) );
    LogUtil.addAppender( appender, logger, null );
    LogUtil.setLevel( logger, Level.ALL );
    LoggerContext ctx = (LoggerContext) LogManager.getContext( false );
    Configuration config = ctx.getConfiguration();
    LoggerConfig loggerConfig = config.getLoggerConfig( logger.getName() );
    loggerConfig.setAdditive( false );
    
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;
		
		setLayout(formLayout);
		
		setVisible(true);

		wText = new Text(this, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY );
		wText.setBackground(GUIResource.getInstance().getColorBackground());
		wText.setVisible(true);

		fdText=new FormData();
		fdText.left   = new FormAttachment(0, 0);
		fdText.top    = new FormAttachment(0, 0);
		fdText.right  = new FormAttachment(100, 0);
		fdText.bottom = new FormAttachment(100,-40);
		wText.setLayoutData(fdText);
		
		wRefresh = new Button(this, SWT.PUSH);
		wRefresh.setText(Messages.getString("MetaEditorLog.USER_REFRESH_LOG")); //$NON-NLS-1$

		wClear = new Button(this, SWT.PUSH);
		wClear.setText(Messages.getString("MetaEditorLog.USER_CLEAR_LOG")); //$NON-NLS-1$

		wLog = new Button(this, SWT.PUSH);
		wLog.setText(Messages.getString("MetaEditorLog.USER_LOG_SETTINGS")); //$NON-NLS-1$

		fdRefresh  = new FormData(); 
		fdClear    = new FormData(); 
		fdLog      = new FormData(); 

		fdRefresh.left   = new FormAttachment(25, 10);  
		fdRefresh.bottom = new FormAttachment(100, 0);
		wRefresh.setLayoutData(fdRefresh);

		fdClear.left   = new FormAttachment(wRefresh, 10);  
		fdClear.bottom = new FormAttachment(100, 0);
		wClear.setLayoutData(fdClear);

		fdLog.left   = new FormAttachment(wClear, 10);  
		fdLog.bottom = new FormAttachment(100, 0);
		wLog.setLayoutData(fdLog);

		pack();

		lsRefresh = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				readLog();
			}
		};
		
		final Timer tim = new Timer();
		TimerTask timtask = 
			new TimerTask() 
			{
				public void run() 
				{
					if (display!=null && !display.isDisposed())
					display.asyncExec(
						new Runnable() 
						{
							public void run() 
							{
								readLog(); 
							}
						}
					);
				}
			};
		tim.schedule( timtask, 2000L, 2000L);// refresh every 2 seconds... 
		
		lsClear = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				clearLog();
			}
		};
		
		lsLog = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				setLog();
			}
		};
		
		wRefresh.addSelectionListener(lsRefresh);
		wClear.addSelectionListener(lsClear);
		wLog.addSelectionListener(lsLog);

		addDisposeListener(
			new DisposeListener() 
			{
				public void widgetDisposed(DisposeEvent e) 
				{
					tim.cancel();
				}
			}
		);
	}
	
  public void readLog()
  {
    StringBuffer buffer = writer.getBuffer();
    if ( !wText.isDisposed() && buffer.length() > 0 ) {
      wText.setSelection( wText.getText().length() );
      wText.clearSelection();
      wText.insert( buffer.toString() );
    }
    buffer.setLength( 0 );
  }
	
	private void clearLog()
	{
		wText.setText(""); //$NON-NLS-1$
	}
	
	private void setLog()
	{
		LogSettingsDialog lsd = new LogSettingsDialog(shell, SWT.NONE, props);
		lsd.open();
		
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}

}
