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
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

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
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.dialog.LogSettingsDialog;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.ui.util.GUIResource;

public class MetaEditorLog extends Composite
{
	private PropsUI props;
	private Shell shell;
	private Display display;
	private LogWriter log;
	
	private Text   wText;
	private Button wRefresh;
	private Button wClear;
	private Button wLog;

	private FormData fdText, fdRefresh, fdClear, fdLog; 
	
	private SelectionListener lsRefresh, lsClear, lsLog;
	private StringBuffer message;

	private InputStream in;

	public MetaEditorLog(Composite parent, int style, String fname)
	{
		super(parent, style);
		shell=parent.getShell();
		log=LogWriter.getInstance();
		display=shell.getDisplay();
        props = PropsUI.getInstance();
		
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
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

		try
		{
			in = log.getFileInputStream();
		}
		catch(Exception e)
		{
		  // Do nothing. This error will be reported in the readLog() method
		}
		
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
		int i, n;

		if (message==null)  message = new StringBuffer(); else message.setLength(0);
		if (in == null) {
		  message.append(Messages.getString("MetaEditorLog.DEBUG_CANT_CREATE_CONNECTION"));
		} else {
	    try
	    { 
	      n = in.available();
	          
	      if (n>0)
	      {
	        byte buffer[] = new byte[n];
	        int c = in.read(buffer, 0, n);
	        for (i=0;i<c;i++) message.append((char)buffer[i]);
	      }
	            
	    }
	    catch(Exception ex)
	    {
	      message.append(ex.toString());
	    }
		}

		if (!wText.isDisposed() && message.length()>0) 
		{
			wText.setSelection(wText.getText().length());
			wText.clearSelection();
			wText.insert(message.toString());
		} 
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
