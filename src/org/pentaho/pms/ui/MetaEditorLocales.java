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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.ui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.ui.util.Const;

public class MetaEditorLocales extends Composite
{
	private PropsUI props;
	
	private TableView wLocales;
	private Button wRefresh;
	private Button wApply;

	private SelectionListener lsRefresh, lsApply;
    private MetaEditor metaEditor;

	public MetaEditorLocales(Composite parent, int style, MetaEditor metaEditor)
	{
		super(parent, style);
		this.metaEditor = metaEditor;

        props = PropsUI.getInstance();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		
		setLayout(formLayout);
		
        props.setLook(this);

        // Buttons at the bottom to form a line of reference...
        //
        wRefresh = new Button(this, SWT.PUSH);
        wRefresh.setText(Messages.getString("MetaEditorLocales.USER_REFRESH")); //$NON-NLS-1$
        wApply = new Button(this, SWT.PUSH);
        wApply.setText(Messages.getString("MetaEditorLocales.USER_APPLY_CHANGES")); //$NON-NLS-1$
        wApply.setEnabled(false);

        BaseStepDialog.positionBottomButtons(this, new Button[] { wApply, wRefresh }, Const.MARGIN, null);

        ModifyListener lsMod = new ModifyListener()
        {
            public void modifyText(ModifyEvent arg0)
            {
                wApply.setEnabled(true);
            }
        };

        // Show the parent properties in a grid...
        //
        Label wlLocales = new Label(this, SWT.LEFT);
        props.setLook(wlLocales);
        wlLocales.setText(Messages.getString("MetaEditorLocales.USER_LOCALES_TO_USE")); //$NON-NLS-1$
        FormData fdlLocales = new FormData();
        fdlLocales.left  = new FormAttachment(0, 0);
        fdlLocales.top   = new FormAttachment(0, 0);
        wlLocales.setLayoutData(fdlLocales);

        ColumnInfo[] colLocales = new ColumnInfo[]
          {
            new ColumnInfo(Messages.getString("MetaEditorLocales.USER_CODE"),                  ColumnInfo.COLUMN_TYPE_TEXT, false, false), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorLocales.USER_DESCRIPTION"),           ColumnInfo.COLUMN_TYPE_TEXT, false, false), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorLocales.USER_ORDER"),                 ColumnInfo.COLUMN_TYPE_TEXT, false, false), //$NON-NLS-1$
            new ColumnInfo(Messages.getString("MetaEditorLocales.USER_ACTIVE"),                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "Y", "N" }, false), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          };
        wLocales=new TableView(new Variables(),this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colLocales, 1, false, lsMod, props );
        FormData fdLocales = new FormData();
        fdLocales.left   = new FormAttachment(0,0);
        fdLocales.right  = new FormAttachment(100, 0);
        fdLocales.top    = new FormAttachment(wlLocales, Const.MARGIN);
        fdLocales.bottom = new FormAttachment(wApply, -Const.MARGIN);
        wLocales.setLayoutData(fdLocales);

		lsRefresh = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				refreshScreen();
			}
		};
		
		lsApply = new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				apply();
			}
		};
		
		wRefresh.addSelectionListener(lsRefresh);
		wApply.addSelectionListener(lsApply);

        getData();
	}
	
    public void refreshScreen()
    {
        Locales locales = metaEditor.getSchemaMeta().getLocales();
        
        wLocales.clearAll(false);
        
        for (int i=0;i<locales.nrLocales();i++)
        {
            LocaleInterface locale = locales.getLocale(i);
            TableItem item = new TableItem(wLocales.table, SWT.NONE);
            
            if (locale.getCode()!=null) item.setText(1, locale.getCode());
            if (locale.getDescription()!=null) item.setText(2, locale.getDescription());
            if (locale.getOrder()>=0) item.setText(3, Integer.toString(locale.getOrder()));
            item.setText(4, locale.isActive()?"Y":"N"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        wLocales.removeEmptyRows();
        wLocales.setRowNums();
        wLocales.optWidth(true);
    }

    public void apply()
    {
        Locales locales = metaEditor.getSchemaMeta().getLocales();
        locales.getLocaleList().clear();
        
        for (int i=0;i<wLocales.nrNonEmpty();i++)
        {
            TableItem item = wLocales.getNonEmpty(i);
            
            String code   = item.getText(1);
            String desc   = item.getText(2);
            String order  = item.getText(3);
            String active = item.getText(4);
            
            if (!Const.isEmpty(code))
            {
                LocaleInterface locale = new LocaleMeta(code, desc, Const.toInt(order, -1), "Y".equalsIgnoreCase(active)); //$NON-NLS-1$
                locales.addLocale(locale);
            }
        }
        
        metaEditor.refreshAll();
        refreshScreen();
        wApply.setEnabled(false);
    }
}
