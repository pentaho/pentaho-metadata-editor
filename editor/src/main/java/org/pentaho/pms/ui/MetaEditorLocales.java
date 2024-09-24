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
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.ui.util.Const;
import org.safehaus.uuid.Logger;

public class MetaEditorLocales extends Composite
{
	private PropsUI props;
	
	private TableView wLocales;
	private Button wRefresh;
	private Button wApply;
	private Button wExportLocale;

	private SelectionListener lsRefresh, lsApply, lsExportLocale;
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
        
        wExportLocale = new Button(this, SWT.PUSH);
        wExportLocale.setText(Messages.getString("MetaEditorLocales.USER_EXPORT_LOCALE")); //$NON-NLS-1$
        wExportLocale.setEnabled(false);

        BaseStepDialog.positionBottomButtons(this, new Button[] { wApply, wRefresh, wExportLocale }, Const.MARGIN, null);

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
        
        /* Enable ExportLocale button when a new row is created
           Note: This does not get triggered if the user does not select a text box
           we therefore need the SelectionListener as well
        */
        wLocales.getTable().addFocusListener(new FocusListener() {
          protected void enableExportLocale() {
            int selectedLocaleIndex = wLocales.getSelectionIndex();
            // See 'ColumnInfo[] colLocales = new ColumnInfo[]' in constructor for order of creation
            String localeCode = wLocales.getItem(selectedLocaleIndex >= 0 ? selectedLocaleIndex : 0, 1);
            
            wExportLocale.setEnabled(!StringUtils.isEmpty(localeCode));
          }
          
          @Override
          public void focusGained(FocusEvent arg0) {
          }

          @Override
          public void focusLost(FocusEvent arg0) {
            enableExportLocale();
          }
        });
        
        // Enable ExportLocale button when an existing row is selected
        wLocales.getTable().addSelectionListener(new SelectionListener() {
          protected void enableExportLocale() {
            int selectedLocaleIndex = wLocales.getSelectionIndex();
            // See 'ColumnInfo[] colLocales = new ColumnInfo[]' in constructor for order of creation
            String localeCode = wLocales.getItem(selectedLocaleIndex >= 0 ? selectedLocaleIndex : 0, 1);
            
            wExportLocale.setEnabled(!StringUtils.isEmpty(localeCode));
          }
          
          @Override
          public void widgetDefaultSelected(SelectionEvent arg0) {
            enableExportLocale();
          }

          @Override
          public void widgetSelected(SelectionEvent arg0) {
            enableExportLocale();
          }
          
        });

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
		
		lsExportLocale = new SelectionAdapter()
		{
		  public void widgetSelected(SelectionEvent e)
		  {
		    exportLocale();
		  }
		};
		
		wRefresh.addSelectionListener(lsRefresh);
		wApply.addSelectionListener(lsApply);
		wExportLocale.addSelectionListener(lsExportLocale);

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
    
    public void exportLocale()
    {
      int selectedLocaleIndex = wLocales.getSelectionIndex();
      // See 'ColumnInfo[] colLocales = new ColumnInfo[]' in constructor for order of creation
      String localeCode = wLocales.getItem(selectedLocaleIndex, 1);
      String localeDescription = wLocales.getItem(selectedLocaleIndex, 2);
      try {
        metaEditor.exportLocale(localeCode);
      } catch (Exception e) {
        new ErrorDialog(
            getShell(),
            Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("MetaEditor.USER_ERROR_EXPORTING_LOCALE_MESSAGE"), e); //$NON-NLS-1$ //$NON-NLS-2$
        
        Logger.logError(Messages.getString("MetaEditorLocales.USER_EXPORT_FAILED")); //$NON-NLS-1$
        Logger.logError(e.getMessage());
      }
    }
    
}
