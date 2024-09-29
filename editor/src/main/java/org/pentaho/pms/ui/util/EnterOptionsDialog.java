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


package org.pentaho.pms.ui.util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Props;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.ui.util.Const;

/**
 * Allows you to set the configurable options for the Pentaho Metadata environment
 * 
 * @author Matt
 * @since 15-12-2003
 */
public class EnterOptionsDialog extends Dialog
{
	private Display      display; 

	private CTabFolder   wTabFolder;
	private FormData     fdTabFolder;

	private CTabItem     wLookTab, wGeneralTab;

    private ScrolledComposite sLookComp, sGeneralComp;
	private Composite    wLookComp, wGeneralComp;
	private FormData     fdLookComp, fdGeneralComp;

	private FontData     fixedFontData, graphFontData, noteFontData;
	private Font         fixedFont, graphFont, noteFont;

	private RGB          backgroundRGB, graphColorRGB, tabColorRGB;	
	private Color        background, graphColor, tabColor;

	private Label        wlFFont;
	private Canvas       wFFont;
	private Button       wbFFont, wdFFont;
    private FormData     fdlFFont, fdbFFont, fddFFont, fdFFont;

	private Label        wlGFont;
	private Canvas       wGFont;
	private Button       wbGFont, wdGFont;
	private FormData     fdlGFont, fdbGFont, fddGFont, fdGFont;

	private Label        wlNFont;
	private Canvas       wNFont;
	private Button       wbNFont, wdNFont;
	private FormData     fdlNFont, fdbNFont, fddNFont, fdNFont;

	private Label        wlBGColor;
	private Canvas       wBGColor;
	private Button       wbBGColor, wdBGcolor;
	private FormData     fdlBGColor, fdbBGColor, fddBGColor, fdBGColor;

	private Label        wlGrColor;
	private Canvas       wGrColor;
	private Button       wbGrColor, wdGrColor;
	private FormData     fdlGrColor, fdbGrColor, fddGrColor, fdGrColor;

	private Label        wlTabColor;
	private Canvas       wTabColor;
	private Button       wbTabColor, wdTabColor;
	private FormData     fdlTabColor, fdbTabColor, fddTabColor, fdTabColor;

	private Label        wlIconsize;
	private Text         wIconsize;
	private FormData     fdlIconsize, fdIconsize;

	private Label        wlLineWidth;
	private Text         wLineWidth;
	private FormData     fdlLineWidth, fdLineWidth;

	private Label        wlShadowSize;
	private Text         wShadowSize;
	private FormData     fdlShadowSize, fdShadowSize;

    /*
	private Label        wlMaxUndo;
	private Text         wMaxUndo;
	private FormData     fdlMaxUndo, fdMaxUndo;
    
    private Label        wlDefaultPreview;
    private Text         wDefaultPreview;
    private FormData     fdlDefaultPreview, fdDefaultPreview;
    */

	private Label        wlMiddlePct;
	private Text         wMiddlePct;
	private FormData     fdlMiddlePct, fdMiddlePct;
    
    private Label        wlAntiAlias;
    private Button       wAntiAlias;
    private FormData     fdlAntiAlias, fdAntiAlias;

    private Label        wlOriginalLook;
    private Button       wOriginalLook;
    private FormData     fdlOriginalLook, fdOriginalLook;

    /*
	private Label        wlShowTips;
	private Button       wShowTips;
	private FormData     fdlShowTips, fdShowTips;
    */
    
	private Label        wlUseCache;
	private Button       wUseCache;
	private FormData     fdlUseCache, fdUseCache;

	private Label        wlOpenLast;
	private Button       wOpenLast;
	private FormData     fdlOpenLast, fdOpenLast;
		
	private Label        wlAutoSave;
	private Button       wAutoSave;
	private FormData     fdlAutoSave, fdAutoSave;

    /*
    private Label        wlDBConnXML;
    private Button       wDBConnXML;
    private FormData     fdlDBConnXML, fdDBConnXML;
    
    private Label        wlAskReplaceDB;
    private Button       wAskReplaceDB;
    private FormData     fdlAskReplaceDB, fdAskReplaceDB;

    private Label        wlReplaceDB;
    private Button       wReplaceDB;
    private FormData     fdlReplaceDB, fdReplaceDB;
    */

	private Label        wlSaveConf;
	private Button       wSaveConf;
	private FormData     fdlSaveConf, fdSaveConf;

    /*
	private Label        wlAutoSplit;
	private Button       wAutoSplit;
	private FormData     fdlAutoSplit, fdAutoSplit;

    private Label        wlShowRep;
    private Button       wShowRep;
    private FormData     fdlShowRep, fdShowRep;
    */
    
    private Label        wlExitWarning;
    private Button       wExitWarning;
    private FormData     fdlExitWarning, fdExitWarning;

    /*
    private Label        wlClearCustom;
    private Button       wClearCustom;
    private FormData     fdlClearCustom, fdClearCustom;

    private Label        wlDefaultLocale;
    private Combo        wDefaultLocale;
    private FormData     fdlDefaultLocale, fdDefaultLocale;

    private Label        wlFailoverLocale;
    private Combo        wFailoverLocale;
    private FormData     fdlFailoverLocale, fdFailoverLocale;
    */

	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private Shell  shell;
	private SelectionAdapter lsDef;
	private PropsUI propsUI;
	
	public EnterOptionsDialog(Shell parent, PropsUI pr)
	{
		super(parent, SWT.NONE);
		propsUI=pr;
	}

	public Props open()
	{
		Shell parent = getParent();
		display = parent.getDisplay();

		getData();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
        propsUI.setLook(shell);
		
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
        shell.setText(Messages.getString("EnterOptionsDialog.USER_PME_OPTIONS")); //$NON-NLS-1$
		
		int middle = propsUI.getMiddlePct();
		int margin = Const.MARGIN;
		
		int h = 40;

		wTabFolder = new CTabFolder(shell, SWT.BORDER);
        propsUI.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

		//////////////////////////
		// START OF GENERAL TAB///
		///
		wGeneralTab=new CTabItem(wTabFolder, SWT.NONE);
		wGeneralTab.setText(Messages.getString("EnterOptionsDialog.USER_GENERAL")); //$NON-NLS-1$

		FormLayout generalLayout = new FormLayout ();
		generalLayout.marginWidth  = 3;
		generalLayout.marginHeight = 3;
		
        sGeneralComp = new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL );
        sGeneralComp.setLayout(new FillLayout());
        
		wGeneralComp = new Composite(sGeneralComp, SWT.NONE);
		propsUI.setLook(wGeneralComp);
		wGeneralComp.setLayout(generalLayout);

        /*
		// MaxUndo line
		wlMaxUndo=new Label(wGeneralComp, SWT.RIGHT);
		wlMaxUndo.setText("Maximum Undo Level ");
        propsUI.setLook(wlMaxUndo);
		fdlMaxUndo=new FormData();
		fdlMaxUndo.left = new FormAttachment(0, 0);
		fdlMaxUndo.right= new FormAttachment(middle, -margin);
		fdlMaxUndo.top  = new FormAttachment(0, 0);
		wlMaxUndo.setLayoutData(fdlMaxUndo);
		wMaxUndo=new Text(wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wMaxUndo.setText(""+propsUI.getMaxUndo());
        propsUI.setLook(wMaxUndo);
		fdMaxUndo=new FormData();
		fdMaxUndo.left = new FormAttachment(middle, 0);
		fdMaxUndo.right= new FormAttachment(100, -margin);
		fdMaxUndo.top  = new FormAttachment(0, 0);
		wMaxUndo.setLayoutData(fdMaxUndo);

        // Default preview size
        wlDefaultPreview=new Label(wGeneralComp, SWT.RIGHT);
        wlDefaultPreview.setText("Default preview size");
        propsUI.setLook(wlDefaultPreview);
        fdlDefaultPreview=new FormData();
        fdlDefaultPreview.left = new FormAttachment(0, 0);
        fdlDefaultPreview.right= new FormAttachment(middle, -margin);
        fdlDefaultPreview.top  = new FormAttachment(wMaxUndo, margin);
        wlDefaultPreview.setLayoutData(fdlDefaultPreview);
        wDefaultPreview=new Text(wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wDefaultPreview.setText(""+propsUI.getDefaultPreviewSize());
        propsUI.setLook(wDefaultPreview);
        fdDefaultPreview=new FormData();
        fdDefaultPreview.left = new FormAttachment(middle, 0);
        fdDefaultPreview.right= new FormAttachment(100, -margin);
        fdDefaultPreview.top  = new FormAttachment(wMaxUndo, margin);
        wDefaultPreview.setLayoutData(fdDefaultPreview);

		// Show tips on startup?
		wlShowTips=new Label(wGeneralComp, SWT.RIGHT);
		wlShowTips.setText("Show tips at startup? ");
        propsUI.setLook(wlShowTips);
		fdlShowTips=new FormData();
		fdlShowTips.left = new FormAttachment(0, 0);
		fdlShowTips.top  = new FormAttachment(wDefaultPreview, margin);
		fdlShowTips.right= new FormAttachment(middle, -margin);
		wlShowTips.setLayoutData(fdlShowTips);
		wShowTips=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wShowTips);
		wShowTips.setSelection(propsUI.showTips());
		fdShowTips=new FormData();
		fdShowTips.left = new FormAttachment(middle, 0);
		fdShowTips.top  = new FormAttachment(wDefaultPreview, margin);
		fdShowTips.right= new FormAttachment(100, 0);
		wShowTips.setLayoutData(fdShowTips);
         */

		// Use DB Cache?
		wlUseCache=new Label(wGeneralComp, SWT.RIGHT);
		wlUseCache.setText(Messages.getString("EnterOptionsDialog.USER_USE_DATABASE)CACHE")); //$NON-NLS-1$
        propsUI.setLook(wlUseCache);
		fdlUseCache=new FormData();
		fdlUseCache.left = new FormAttachment(0, 0);
		fdlUseCache.top  = new FormAttachment(0, 0);
		fdlUseCache.right= new FormAttachment(middle, -margin);
		wlUseCache.setLayoutData(fdlUseCache);
		wUseCache=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wUseCache);
		wUseCache.setSelection(propsUI.useDBCache());
		fdUseCache=new FormData();
		fdUseCache.left = new FormAttachment(middle, 0);
		fdUseCache.top  = new FormAttachment(0, 0);
		fdUseCache.right= new FormAttachment(100, 0);
		wUseCache.setLayoutData(fdUseCache);


		// Auto load last file at startup?
		wlOpenLast=new Label(wGeneralComp, SWT.RIGHT);
		wlOpenLast.setText(Messages.getString("EnterOptionsDialog.USER_OPEN_LAST_FILE_ON_STARTUP")); //$NON-NLS-1$
        propsUI.setLook(wlOpenLast);
		fdlOpenLast=new FormData();
		fdlOpenLast.left = new FormAttachment(0, 0);
		fdlOpenLast.top  = new FormAttachment(wUseCache, margin);
		fdlOpenLast.right= new FormAttachment(middle, -margin);
		wlOpenLast.setLayoutData(fdlOpenLast);
		wOpenLast=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wOpenLast);
		wOpenLast.setSelection(propsUI.openLastFile());
		fdOpenLast=new FormData();
		fdOpenLast.left = new FormAttachment(middle, 0);
		fdOpenLast.top  = new FormAttachment(wUseCache, margin);
		fdOpenLast.right= new FormAttachment(100, 0);
		wOpenLast.setLayoutData(fdOpenLast);

		// Auto save changed files?
		wlAutoSave=new Label(wGeneralComp, SWT.RIGHT);
		wlAutoSave.setText(Messages.getString("EnterOptionsDialog.USER_AUTO_SAVE")); //$NON-NLS-1$
        propsUI.setLook(wlAutoSave);
		fdlAutoSave=new FormData();
		fdlAutoSave.left = new FormAttachment(0, 0);
		fdlAutoSave.top  = new FormAttachment(wOpenLast, margin);
		fdlAutoSave.right= new FormAttachment(middle, -margin);
		wlAutoSave.setLayoutData(fdlAutoSave);
		wAutoSave=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wAutoSave);
		wAutoSave.setSelection(propsUI.getAutoSave());
		fdAutoSave=new FormData();
		fdAutoSave.left = new FormAttachment(middle, 0);
		fdAutoSave.top  = new FormAttachment(wOpenLast, margin);
		fdAutoSave.right= new FormAttachment(100, 0);
		wAutoSave.setLayoutData(fdAutoSave);

        /*
        // Only save used connections to XML?
        wlDBConnXML=new Label(wGeneralComp, SWT.RIGHT);
        wlDBConnXML.setText("Only save used connections to XML? ");
        propsUI.setLook(wlDBConnXML);
        fdlDBConnXML=new FormData();
        fdlDBConnXML.left = new FormAttachment(0, 0);
        fdlDBConnXML.top  = new FormAttachment(wAutoSave, margin);
        fdlDBConnXML.right= new FormAttachment(middle, -margin);
        wlDBConnXML.setLayoutData(fdlDBConnXML);
        wDBConnXML=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wDBConnXML);
        wDBConnXML.setSelection(propsUI.areOnlyUsedConnectionsSavedToXML());
        fdDBConnXML=new FormData();
        fdDBConnXML.left = new FormAttachment(middle, 0);
        fdDBConnXML.top  = new FormAttachment(wAutoSave, margin);
        fdDBConnXML.right= new FormAttachment(100, 0);
        wDBConnXML.setLayoutData(fdDBConnXML);

        // Ask about replacing existing connections?
        wlAskReplaceDB=new Label(wGeneralComp, SWT.RIGHT);
        wlAskReplaceDB.setText("Ask about replacing existing connections on open/import? ");
        propsUI.setLook(wlAskReplaceDB);
        fdlAskReplaceDB=new FormData();
        fdlAskReplaceDB.left = new FormAttachment(0, 0);
        fdlAskReplaceDB.top  = new FormAttachment(wDBConnXML, margin);
        fdlAskReplaceDB.right= new FormAttachment(middle, -margin);
        wlAskReplaceDB.setLayoutData(fdlAskReplaceDB);
        wAskReplaceDB=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wAskReplaceDB);
        wAskReplaceDB.setSelection(propsUI.askAboutReplacingDatabaseConnections());
        fdAskReplaceDB=new FormData();
        fdAskReplaceDB.left = new FormAttachment(middle, 0);
        fdAskReplaceDB.top  = new FormAttachment(wDBConnXML, margin);
        fdAskReplaceDB.right= new FormAttachment(100, 0);
        wAskReplaceDB.setLayoutData(fdAskReplaceDB);

        // Only save used connections to XML?
        wlReplaceDB=new Label(wGeneralComp, SWT.RIGHT);
        wlReplaceDB.setText("Replace existing database connections on open/import? ");
        propsUI.setLook(wlReplaceDB);
        fdlReplaceDB=new FormData();
        fdlReplaceDB.left = new FormAttachment(0, 0);
        fdlReplaceDB.top  = new FormAttachment(wAskReplaceDB, margin);
        fdlReplaceDB.right= new FormAttachment(middle, -margin);
        wlReplaceDB.setLayoutData(fdlReplaceDB);
        wReplaceDB=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wReplaceDB);
        wReplaceDB.setSelection(propsUI.replaceExistingDatabaseConnections());
        fdReplaceDB=new FormData();
        fdReplaceDB.left = new FormAttachment(middle, 0);
        fdReplaceDB.top  = new FormAttachment(wAskReplaceDB, margin);
        fdReplaceDB.right= new FormAttachment(100, 0);
        wReplaceDB.setLayoutData(fdReplaceDB);
         */
        
		// Show confirmation after save?
		wlSaveConf=new Label(wGeneralComp, SWT.RIGHT);
		wlSaveConf.setText(Messages.getString("EnterOptionsDialog.USER_SHOW_SAVE_CONFIRMATION")); //$NON-NLS-1$
        propsUI.setLook(wlSaveConf);
		fdlSaveConf=new FormData();
		fdlSaveConf.left = new FormAttachment(0, 0);
		fdlSaveConf.top  = new FormAttachment(wAutoSave, margin);
		fdlSaveConf.right= new FormAttachment(middle, -margin);
		wlSaveConf.setLayoutData(fdlSaveConf);
		wSaveConf=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wSaveConf);
		wSaveConf.setSelection(propsUI.getSaveConfirmation());
		fdSaveConf=new FormData();
		fdSaveConf.left = new FormAttachment(middle, 0);
		fdSaveConf.top  = new FormAttachment(wAutoSave, margin);
		fdSaveConf.right= new FormAttachment(100, 0);
		wSaveConf.setLayoutData(fdSaveConf);

        /*
		// Automatically split hops?
		wlAutoSplit=new Label(wGeneralComp, SWT.RIGHT);
		wlAutoSplit.setText("Automatically split hops? ");
        propsUI.setLook(wlAutoSplit);
		fdlAutoSplit=new FormData();
		fdlAutoSplit.left = new FormAttachment(0, 0);
		fdlAutoSplit.top  = new FormAttachment(wSaveConf, margin);
		fdlAutoSplit.right= new FormAttachment(middle, -margin);
		wlAutoSplit.setLayoutData(fdlAutoSplit);
		wAutoSplit=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wAutoSplit);
		wAutoSplit.setSelection(propsUI.getAutoSplit());
		fdAutoSplit=new FormData();
		fdAutoSplit.left = new FormAttachment(middle, 0);
		fdAutoSplit.top  = new FormAttachment(wSaveConf, margin);
		fdAutoSplit.right= new FormAttachment(100, 0);
		wAutoSplit.setLayoutData(fdAutoSplit);
        
        // Show repository dialog at startup?
        wlShowRep=new Label(wGeneralComp, SWT.RIGHT);
        wlShowRep.setText("Show the repository dialog at startup? ");
        propsUI.setLook(wlShowRep);
        fdlShowRep=new FormData();
        fdlShowRep.left = new FormAttachment(0, 0);
        fdlShowRep.top  = new FormAttachment(wSaveConf, margin);
        fdlShowRep.right= new FormAttachment(middle, -margin);
        wlShowRep.setLayoutData(fdlShowRep);
        wShowRep=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wShowRep);
        wShowRep.setSelection(propsUI.showRepositoriesDialogAtStartup());
        fdShowRep=new FormData();
        fdShowRep.left = new FormAttachment(middle, 0);
        fdShowRep.top  = new FormAttachment(wSaveConf, margin);
        fdShowRep.right= new FormAttachment(100, 0);
        wShowRep.setLayoutData(fdShowRep);
        */

        // Show exit warning?
        wlExitWarning=new Label(wGeneralComp, SWT.RIGHT);
        wlExitWarning.setText(Messages.getString("EnterOptionsDialog.USER_SHOW_WARNING_ON_EXIT")); //$NON-NLS-1$
        propsUI.setLook(wlExitWarning);
        fdlExitWarning=new FormData();
        fdlExitWarning.left = new FormAttachment(0, 0);
        fdlExitWarning.top  = new FormAttachment(wSaveConf, margin);
        fdlExitWarning.right= new FormAttachment(middle, -margin);
        wlExitWarning.setLayoutData(fdlExitWarning);
        wExitWarning=new Button(wGeneralComp, SWT.CHECK);
        propsUI.setLook(wExitWarning);
        wExitWarning.setSelection(propsUI.showExitWarning());
        fdExitWarning=new FormData();
        fdExitWarning.left = new FormAttachment(middle, 0);
        fdExitWarning.top  = new FormAttachment(wSaveConf, margin);
        fdExitWarning.right= new FormAttachment(100, 0);
        wExitWarning.setLayoutData(fdExitWarning);

        /*
        // Clear custom parameters. (from step)
        wlClearCustom=new Label(wGeneralComp, SWT.RIGHT);
        wlClearCustom.setText("Clear custom parameters (steps/plugins)");
        propsUI.setLook(wlClearCustom);
        fdlClearCustom=new FormData();
        fdlClearCustom.left = new FormAttachment(0, 0);
        fdlClearCustom.top  = new FormAttachment(wExitWarning, margin);
        fdlClearCustom.right= new FormAttachment(middle, -margin);
        wlClearCustom.setLayoutData(fdlClearCustom);
        wClearCustom=new Button(wGeneralComp, SWT.PUSH);
        propsUI.setLook(wClearCustom);
        wClearCustom.setText("Clear custom flags and parameters");
        fdClearCustom=new FormData();
        fdClearCustom.left = new FormAttachment(middle, 0);
        fdClearCustom.top  = new FormAttachment(wExitWarning, margin);
        fdClearCustom.right= new FormAttachment(100, 0);
        wClearCustom.setLayoutData(fdClearCustom);
        wClearCustom.setToolTipText("Clicking this button will erase all the custom flags and parameters"+Const.CR+"that are used in the dialogs of the steps and plugins. ");
        wClearCustom.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                mb.setMessage("Are you sure you want to clear the custom step/plugin parameters and flags?");
                mb.setText("Question");
                int id = mb.open();
                if (id==SWT.YES)
                {
                    propsUI.clearCustomParameters();
                    propsUI.saveProps();
                    MessageBox ok = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
                    ok.setMessage("The custom step/plugin parameters and flags were cleared");
                    ok.open();
                }
            }
        });
        */

		fdGeneralComp = new FormData();
		fdGeneralComp.left  = new FormAttachment(0, 0);
		fdGeneralComp.right = new FormAttachment(100, 0);
		fdGeneralComp.top   = new FormAttachment(0, 0);
		fdGeneralComp.bottom= new FormAttachment(100, 100);
		wGeneralComp.setLayoutData(fdGeneralComp);

		wGeneralComp.pack();
        
		Rectangle bounds = wGeneralComp.getBounds();
        
        sGeneralComp.setContent(wGeneralComp);
        sGeneralComp.setExpandHorizontal(true);
        sGeneralComp.setExpandVertical(true);
        sGeneralComp.setMinWidth(bounds.width);
        sGeneralComp.setMinHeight(bounds.height);
        
        wGeneralTab.setControl(sGeneralComp);

		/////////////////////////////////////////////////////////////
		/// END OF GENERAL TAB
		/////////////////////////////////////////////////////////////

		//////////////////////////
		// START OF LOOK TAB///
		///
		wLookTab=new CTabItem(wTabFolder, SWT.NONE);
		wLookTab.setText(Messages.getString("EnterOptionsDialog.USER_LOOK_AND_FEEL")); //$NON-NLS-1$
		
        sLookComp = new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL );
        sLookComp.setLayout(new FillLayout());
        
		wLookComp = new Composite(sLookComp, SWT.NONE);
        propsUI.setLook(wLookComp);
		
		FormLayout lookLayout = new FormLayout();
		lookLayout.marginWidth  = 3;
		lookLayout.marginHeight = 3;
		wLookComp.setLayout(lookLayout);

        
		// Fixed font
        int nr = 0;
		wlFFont=new Label(wLookComp, SWT.RIGHT);
		wlFFont.setText(Messages.getString("EnterOptionsDialog.uSER_FIXED_WIDTH_FONT")); //$NON-NLS-1$
        propsUI.setLook(wlFFont);
		fdlFFont=new FormData();
		fdlFFont.left  = new FormAttachment(0, 0);
		fdlFFont.right = new FormAttachment(middle, -margin);
		fdlFFont.top   = new FormAttachment(0, nr*h + margin+10);
		wlFFont.setLayoutData(fdlFFont);

        wdFFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdFFont.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdFFont);
        fddFFont = new FormData();
        fddFFont.right= new FormAttachment(100, 0);
        fddFFont.top  = new FormAttachment(0, nr*h + margin);
        fddFFont.bottom  = new FormAttachment(0, (nr+1)*h + margin);
        wdFFont.setLayoutData(fddFFont);
        wdFFont.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    fixedFontData = new FontData(Const.FONT_FIXED_NAME, Const.FONT_FIXED_SIZE, Const.FONT_FIXED_TYPE);
                    fixedFont.dispose();
                    fixedFont = new Font(display, fixedFontData);
                    wFFont.redraw();
                }
            }
        );

		wbFFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
		wbFFont.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbFFont);
        fdbFFont = new FormData();
		fdbFFont.right= new FormAttachment(wdFFont, -margin);
		fdbFFont.top  = new FormAttachment(0, nr*h + margin);
        fdbFFont.bottom  = new FormAttachment(0, (nr+1)*h + margin);
		wbFFont.setLayoutData(fdbFFont);
		wbFFont.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					FontDialog fd = new FontDialog(shell);
                    fd.setFontList(new FontData[] { fixedFontData });
					FontData newfd = fd.open();
					if (newfd!=null)
					{
						fixedFontData = newfd;
						fixedFont.dispose();
						fixedFont = new Font(display, fixedFontData);
						wFFont.redraw();
					}
				}
			}
		);

        wFFont = new Canvas(wLookComp, SWT.BORDER );
        propsUI.setLook(wFFont);
        fdFFont=new FormData();
        fdFFont.left   = new FormAttachment(middle, 0);
        fdFFont.right  = new FormAttachment(wbFFont, -margin);
        fdFFont.top    = new FormAttachment(0, margin);
        fdFFont.bottom = new FormAttachment(0, h    );
        wFFont.setLayoutData(fdFFont);
        wFFont.addPaintListener(new PaintListener() 
            {
                public void paintControl(PaintEvent pe) 
                {
                    pe.gc.setFont(fixedFont);
                    Rectangle max = wFFont.getBounds();
                    String name = fixedFontData.getName();
                    Point size = pe.gc.textExtent(name);
                    
                    pe.gc.drawText(name, (max.width-size.x)/2, (max.height-size.y)/2 );
                }
            }
        );

		// Graph font
        nr++;
		wlGFont=new Label(wLookComp, SWT.RIGHT);
		wlGFont.setText(Messages.getString("EnterOptionsDialog.USER_GRAPH_FONT")); //$NON-NLS-1$
        propsUI.setLook(wlGFont);
		fdlGFont=new FormData();
		fdlGFont.left  = new FormAttachment(0, 0);
		fdlGFont.right = new FormAttachment(middle, -margin);
		fdlGFont.top   = new FormAttachment(0, nr*h+margin+10);
		wlGFont.setLayoutData(fdlGFont);

        wdGFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdGFont.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdGFont);
        fddGFont=new FormData();
        fddGFont.right= new FormAttachment(100, 0);
        fddGFont.top  = new FormAttachment(0, nr*h+margin);
        fddGFont.bottom = new FormAttachment(0, (nr+1)*h+margin);
        wdGFont.setLayoutData(fddGFont);
        wdGFont.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    graphFont.dispose();
                    
                    graphFontData = propsUI.getDefaultFontData();
                    graphFont = new Font(display, graphFontData);
                    wGFont.redraw();
                }
            }
        );

		wbGFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
		wbGFont.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbGFont);
		fdbGFont=new FormData();
		fdbGFont.right= new FormAttachment(wdGFont, -margin);
		fdbGFont.top  = new FormAttachment(0, nr*h+margin);
        fdbGFont.bottom = new FormAttachment(0, (nr+1)*h+margin);
		wbGFont.setLayoutData(fdbGFont);
		wbGFont.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					FontDialog fd = new FontDialog(shell);
                    fd.setFontList(new FontData[] { graphFontData });
					FontData newfd = fd.open();
					if (newfd!=null)
					{
						graphFontData = newfd;
						graphFont.dispose();
						graphFont = new Font(display, graphFontData);
						wGFont.redraw();
					}
				}
			}
		);

        wGFont = new Canvas(wLookComp, SWT.BORDER );
        propsUI.setLook(wGFont);
        fdGFont=new FormData();
        fdGFont.left   = new FormAttachment(middle, 0);
        fdGFont.right  = new FormAttachment(wbGFont, -margin);
        fdGFont.top    = new FormAttachment(0, nr*h+margin);
        fdGFont.bottom = new FormAttachment(0, (nr+1)*h+margin );
        wGFont.setLayoutData(fdGFont);
        wGFont.addPaintListener(new PaintListener() 
            {
                public void paintControl(PaintEvent pe) 
                {
                    pe.gc.setFont(graphFont);
                    Rectangle max = wGFont.getBounds();
                    String name = graphFontData.getName();
                    Point size = pe.gc.textExtent(name);
                    
                    pe.gc.drawText(name, (max.width-size.x)/2, (max.height-size.y)/2 );
                }
            }
        );
        
        

		// Note font
        nr++;
		wlNFont = new Label(wLookComp, SWT.RIGHT);
		wlNFont.setText(Messages.getString("EnterOptionsDialog.USER_NOTE_FONT")); //$NON-NLS-1$
        propsUI.setLook(wlNFont);
		fdlNFont = new FormData();
		fdlNFont.left  = new FormAttachment(0, 0);
		fdlNFont.right = new FormAttachment(middle, -margin);
		fdlNFont.top   = new FormAttachment(0, nr*h + margin + 10);
		wlNFont.setLayoutData(fdlNFont);

		wdNFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdNFont.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdNFont);
		fddNFont = new FormData();
        fddNFont.right = new FormAttachment(100, 0);
        fddNFont.top = new FormAttachment(0, nr*h + margin);
        fddNFont.bottom = new FormAttachment(0, (nr+1)*h + margin);
		wdNFont.setLayoutData(fddNFont);
		wdNFont.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
				    noteFontData = propsUI.getDefaultFontData();
					noteFont.dispose();
					noteFont = new Font(display, noteFontData);
					wNFont.redraw();
				}
			}
		);

        wbNFont = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wbNFont.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbNFont);
        fdbNFont = new FormData();
        fdbNFont.right = new FormAttachment(wdNFont, -margin);
        fdbNFont.top = new FormAttachment(0, nr*h + margin);
        fdbNFont.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wbNFont.setLayoutData(fdbNFont);
        wbNFont.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    FontDialog fd = new FontDialog(shell);
                    fd.setFontList(new FontData[] { noteFontData });
                    FontData newfd = fd.open();
                    if (newfd != null) 
                    {
                        noteFontData = newfd;
                        noteFont.dispose();
                        noteFont = new Font(display, noteFontData);
                        wNFont.redraw();
                    }
                }
            }
        );

        wNFont = new Canvas(wLookComp, SWT.BORDER);
        propsUI.setLook(wNFont);
        fdNFont = new FormData();
        fdNFont.left = new FormAttachment(middle, 0);
        fdNFont.right = new FormAttachment(wbNFont, -margin);
        fdNFont.top    = new FormAttachment(0, nr*h + margin);
        fdNFont.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wNFont.setLayoutData(fdNFont);
        wNFont.addPaintListener(new PaintListener() 
        {
                public void paintControl(PaintEvent pe) 
                {
                    pe.gc.setFont(noteFont);
                    Rectangle max = wNFont.getBounds();
                    String name = noteFontData.getName();
                    Point size = pe.gc.textExtent(name);
    
                    pe.gc.drawText(
                        name,
                        (max.width - size.x) / 2,
                        (max.height - size.y) / 2);
                }
            }
        );


		// Background color
        nr++;
		wlBGColor = new Label(wLookComp, SWT.RIGHT);
		wlBGColor.setText(Messages.getString("EnterOptionsDialog.USER_BACKGROUND_COLOR")); //$NON-NLS-1$
        propsUI.setLook(wlBGColor);
		fdlBGColor = new FormData();
		fdlBGColor.left = new FormAttachment(0, 0);
		fdlBGColor.right = new FormAttachment(middle, -margin);
		fdlBGColor.top = new FormAttachment(0, nr*h + margin + 10);
		wlBGColor.setLayoutData(fdlBGColor);

        wdBGcolor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdBGcolor.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdBGcolor);
        fddBGColor = new FormData();
        fddBGColor.right = new FormAttachment(100, 0);  // to the right of the dialog
        fddBGColor.top = new FormAttachment(0, nr*h + margin);
        fddBGColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wdBGcolor.setLayoutData(fddBGColor);
        wdBGcolor.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    background.dispose();
    
                    backgroundRGB = new RGB(Const.COLOR_BACKGROUND_RED, Const.COLOR_BACKGROUND_GREEN, Const.COLOR_BACKGROUND_BLUE);
                    background=new Color(display, backgroundRGB);
                    wBGColor.setBackground(background);
                    wBGColor.redraw();
                }
            }
        );

        wbBGColor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wbBGColor.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbBGColor);
        fdbBGColor = new FormData();
        fdbBGColor.right = new FormAttachment(wdBGcolor, -margin); // to the left of the "default" button
        fdbBGColor.top = new FormAttachment(0, nr*h + margin);
        fdbBGColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wbBGColor.setLayoutData(fdbBGColor);
        wbBGColor.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    ColorDialog cd = new ColorDialog(shell);
                    cd.setRGB(propsUI.getBackgroundRGB());
                    RGB newbg = cd.open();
                    if (newbg != null) 
                    {
                        backgroundRGB = newbg;
                        background.dispose();
                        background=new Color(display, backgroundRGB);
                        wBGColor.setBackground(background);
                        wBGColor.redraw();
                    }
                }
            }
        );

        wBGColor = new Canvas(wLookComp, SWT.BORDER);
        propsUI.setLook(wBGColor);
        wBGColor.setBackground(background);
        fdBGColor = new FormData();
        fdBGColor.left = new FormAttachment(middle, 0);
        fdBGColor.right = new FormAttachment(wbBGColor, -margin);
        fdBGColor.top    = new FormAttachment(0, nr*h + margin);
        fdBGColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wBGColor.setLayoutData(fdBGColor);


        
        
		// Graph background color
        nr++;
		wlGrColor = new Label(wLookComp, SWT.RIGHT);
		wlGrColor.setText(Messages.getString("EnterOptionsDialog.USER_GRAPH_BACKGROUND_COLOR")); //$NON-NLS-1$
        propsUI.setLook(wlGrColor);
		fdlGrColor = new FormData();
		fdlGrColor.left = new FormAttachment(0, 0);
		fdlGrColor.right = new FormAttachment(middle, -margin);
		fdlGrColor.top = new FormAttachment(0, nr*h + margin + 10);
		wlGrColor.setLayoutData(fdlGrColor);

        wdGrColor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdGrColor.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdGrColor);
        fddGrColor = new FormData();
        fddGrColor.right = new FormAttachment(100, 0);
        fddGrColor.top = new FormAttachment(0, nr*h + margin);
        fddGrColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wdGrColor.setLayoutData(fddGrColor);
        wdGrColor.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    graphColor.dispose();

                    graphColorRGB = new RGB(Const.COLOR_GRAPH_RED, Const.COLOR_GRAPH_GREEN, Const.COLOR_GRAPH_BLUE);
                    graphColor=new Color(display, graphColorRGB);
                    wGrColor.setBackground(graphColor);
                    wGrColor.redraw();
                }
            }
        );

		wbGrColor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
		wbGrColor.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbGrColor);
		fdbGrColor = new FormData();
		fdbGrColor.right = new FormAttachment(wdGrColor, -margin);
		fdbGrColor.top = new FormAttachment(0, nr*h + margin);
        fdbGrColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
		wbGrColor.setLayoutData(fdbGrColor);
		wbGrColor.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					ColorDialog cd = new ColorDialog(shell);
					cd.setRGB(propsUI.getGraphColorRGB());
					RGB newbg = cd.open();
					if (newbg != null) 
					{
						graphColorRGB = newbg;
						graphColor.dispose();
						graphColor=new Color(display, graphColorRGB);
						wGrColor.setBackground(graphColor);
						wGrColor.redraw();
					}
				}
			}
		);

        wGrColor = new Canvas(wLookComp, SWT.BORDER);
        propsUI.setLook(wGrColor);
        wGrColor.setBackground(graphColor);
        fdGrColor = new FormData();
        fdGrColor.left = new FormAttachment(middle, 0);
        fdGrColor.right = new FormAttachment(wbGrColor, -margin);
        fdGrColor.top    = new FormAttachment(0, nr*h + margin);
        fdGrColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wGrColor.setLayoutData(fdGrColor);


		// Tab selected color
        nr++;
		wlTabColor = new Label(wLookComp, SWT.RIGHT);
		wlTabColor.setText(Messages.getString("EnterOptionsDialog.USER_COLOR_SELECTED_TABS")); //$NON-NLS-1$
        propsUI.setLook(wlTabColor);
		fdlTabColor = new FormData();
		fdlTabColor.left = new FormAttachment(0, 0);
		fdlTabColor.right = new FormAttachment(middle, -margin);
		fdlTabColor.top = new FormAttachment(0, nr*h + margin + 10);
		wlTabColor.setLayoutData(fdlTabColor);

        wdTabColor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wdTabColor.setText(Messages.getString("EnterOptionsDialog.USER_DEFAULT")); //$NON-NLS-1$
        propsUI.setLook(wdTabColor);
        fddTabColor = new FormData();
        fddTabColor.right = new FormAttachment(100, 0);
        fddTabColor.top = new FormAttachment(0, nr*h + margin);
        fddTabColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wdTabColor.setLayoutData(fddTabColor);
        wdTabColor.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent arg0) 
                {
                    tabColor.dispose();
                    
                    tabColorRGB = new RGB(Const.COLOR_TAB_RED, Const.COLOR_TAB_GREEN, Const.COLOR_TAB_BLUE);
                    tabColor=new Color(display, tabColorRGB);
                    wTabColor.setBackground(tabColor);
                    wTabColor.redraw();
                }
            }
        );

		wbTabColor = new Button(wLookComp, SWT.PUSH | SWT.BORDER | SWT.CENTER);
        wbTabColor.setText(Messages.getString("EnterOptionsDialog.USER_CHANGE")); //$NON-NLS-1$
        propsUI.setLook(wbTabColor);
		fdbTabColor = new FormData();
		fdbTabColor.right = new FormAttachment(wdTabColor, -margin);
		fdbTabColor.top = new FormAttachment(0, nr*h + margin);
        fdbTabColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
		wbTabColor.setLayoutData(fdbTabColor);
		wbTabColor.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					ColorDialog cd = new ColorDialog(shell);
					cd.setRGB(propsUI.getTabColorRGB());
					RGB newbg = cd.open();
					if (newbg != null) 
					{
						tabColorRGB = newbg;
						tabColor.dispose();
						tabColor=new Color(display, tabColorRGB);
						wTabColor.setBackground(tabColor);
						wTabColor.redraw();
					}
				}
			}
		);

        wTabColor = new Canvas(wLookComp, SWT.BORDER);
        propsUI.setLook(wTabColor);
        wTabColor.setBackground(tabColor);
        fdTabColor = new FormData();
        fdTabColor.left = new FormAttachment(middle, 0);
        fdTabColor.right = new FormAttachment(wbTabColor, -margin);
        fdTabColor.top    = new FormAttachment(0, nr*h + margin);
        fdTabColor.bottom = new FormAttachment(0, (nr+1)*h + margin);
        wTabColor.setLayoutData(fdTabColor);


		
		// Iconsize line
		wlIconsize=new Label(wLookComp, SWT.RIGHT);
		wlIconsize.setText(Messages.getString("EnterOptionsDialog.USER_ICON_SIZE")); //$NON-NLS-1$
        propsUI.setLook(wlIconsize);
		fdlIconsize=new FormData();
		fdlIconsize.left = new FormAttachment(0, 0);
		fdlIconsize.right= new FormAttachment(middle, -margin);
		fdlIconsize.top  = new FormAttachment(wTabColor, margin);
		wlIconsize.setLayoutData(fdlIconsize);
		wIconsize=new Text(wLookComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wIconsize.setText(""+propsUI.getIconSize()); //$NON-NLS-1$
        propsUI.setLook(wIconsize);
		fdIconsize=new FormData();
		fdIconsize.left = new FormAttachment(middle, 0);
		fdIconsize.right= new FormAttachment(100, -margin);
		fdIconsize.top  = new FormAttachment(wTabColor, margin);
		wIconsize.setLayoutData(fdIconsize);

		// LineWidth line
		wlLineWidth=new Label(wLookComp, SWT.RIGHT);
		wlLineWidth.setText(Messages.getString("EnterOptionsDialog.USER_LINE_WIDTH")); //$NON-NLS-1$
        propsUI.setLook(wlLineWidth);
		fdlLineWidth=new FormData();
		fdlLineWidth.left = new FormAttachment(0, 0);
		fdlLineWidth.right= new FormAttachment(middle, -margin);
		fdlLineWidth.top  = new FormAttachment(wIconsize, margin);
		wlLineWidth.setLayoutData(fdlLineWidth);
		wLineWidth=new Text(wLookComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wLineWidth.setText(""+propsUI.getLineWidth()); //$NON-NLS-1$
        propsUI.setLook(wLineWidth);
		fdLineWidth=new FormData();
		fdLineWidth.left = new FormAttachment(middle, 0);
		fdLineWidth.right= new FormAttachment(100, -margin);
		fdLineWidth.top  = new FormAttachment(wIconsize, margin);
		wLineWidth.setLayoutData(fdLineWidth);

		// ShadowSize line
		wlShadowSize=new Label(wLookComp, SWT.RIGHT);
		wlShadowSize.setText(Messages.getString("EnterOptionsDialog.USER_SHANDOW_SIZE")); //$NON-NLS-1$
        propsUI.setLook(wlShadowSize);
		fdlShadowSize=new FormData();
		fdlShadowSize.left = new FormAttachment(0, 0);
		fdlShadowSize.right= new FormAttachment(middle, -margin);
		fdlShadowSize.top  = new FormAttachment(wLineWidth, margin);
		wlShadowSize.setLayoutData(fdlShadowSize);
		wShadowSize=new Text(wLookComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wShadowSize.setText(""+propsUI.getShadowSize()); //$NON-NLS-1$
        propsUI.setLook(wShadowSize);
		fdShadowSize=new FormData();
		fdShadowSize.left = new FormAttachment(middle, 0);
		fdShadowSize.right= new FormAttachment(100, -margin);
		fdShadowSize.top  = new FormAttachment(wLineWidth, margin);
		wShadowSize.setLayoutData(fdShadowSize);

		// MiddlePct line
		wlMiddlePct=new Label(wLookComp, SWT.RIGHT);
		wlMiddlePct.setText(Messages.getString("EnterOptionsDialog.USER_DIALOG_MIDDLE_PERCENTAGE")); //$NON-NLS-1$
        propsUI.setLook(wlMiddlePct);
		fdlMiddlePct=new FormData();
		fdlMiddlePct.left = new FormAttachment(0, 0);
		fdlMiddlePct.right= new FormAttachment(middle, -margin);
		fdlMiddlePct.top  = new FormAttachment(wShadowSize, margin);
		wlMiddlePct.setLayoutData(fdlMiddlePct);
		wMiddlePct=new Text(wLookComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wMiddlePct.setText(""+propsUI.getMiddlePct()); //$NON-NLS-1$
        propsUI.setLook(wMiddlePct);
		fdMiddlePct=new FormData();
		fdMiddlePct.left = new FormAttachment(middle, 0);
		fdMiddlePct.right= new FormAttachment(100, -margin);
		fdMiddlePct.top  = new FormAttachment(wShadowSize, margin);
		wMiddlePct.setLayoutData(fdMiddlePct);
        
        
        // Enable anti-aliasing
        wlAntiAlias=new Label(wLookComp, SWT.RIGHT);
        wlAntiAlias.setText(Messages.getString("EnterOptionsDialog.USER_CANVAS_ANTIALIASING_ENABLED")); //$NON-NLS-1$
        propsUI.setLook(wlAntiAlias);
        fdlAntiAlias=new FormData();
        fdlAntiAlias.left = new FormAttachment(0, 0);
        fdlAntiAlias.top  = new FormAttachment(wMiddlePct, margin);
        fdlAntiAlias.right= new FormAttachment(middle, -margin);
        wlAntiAlias.setLayoutData(fdlAntiAlias);
        wAntiAlias=new Button(wLookComp, SWT.CHECK);
        propsUI.setLook(wAntiAlias);
        wAntiAlias.setSelection(propsUI.isAntiAliasingEnabled());
        fdAntiAlias=new FormData();
        fdAntiAlias.left = new FormAttachment(middle, 0);
        fdAntiAlias.top  = new FormAttachment(wMiddlePct, margin);
        fdAntiAlias.right= new FormAttachment(100, 0);
        wAntiAlias.setLayoutData(fdAntiAlias);

        // Show original look
        wlOriginalLook=new Label(wLookComp, SWT.RIGHT);
        wlOriginalLook.setText(Messages.getString("EnterOptionsDialog.USER_TAKE_OVER_LOOK_FROM_OS")); //$NON-NLS-1$
        propsUI.setLook(wlOriginalLook);
        fdlOriginalLook=new FormData();
        fdlOriginalLook.left = new FormAttachment(0, 0);
        fdlOriginalLook.top  = new FormAttachment(wAntiAlias, margin);
        fdlOriginalLook.right= new FormAttachment(middle, -margin);
        wlOriginalLook.setLayoutData(fdlOriginalLook);
        wOriginalLook=new Button(wLookComp, SWT.CHECK);
        propsUI.setLook(wOriginalLook);
        wOriginalLook.setSelection(propsUI.isOSLookShown());
        fdOriginalLook=new FormData();
        fdOriginalLook.left = new FormAttachment(middle, 0);
        fdOriginalLook.top  = new FormAttachment(wAntiAlias, margin);
        fdOriginalLook.right= new FormAttachment(100, 0);
        wOriginalLook.setLayoutData(fdOriginalLook);
        
        /*
        // DefaultLocale line
        wlDefaultLocale=new Label(wLookComp, SWT.RIGHT);
        wlDefaultLocale.setText("Default locale to use ");
        propsUI.setLook(wlDefaultLocale);
        fdlDefaultLocale=new FormData();
        fdlDefaultLocale.left = new FormAttachment(0, 0);
        fdlDefaultLocale.right= new FormAttachment(middle, -margin);
        fdlDefaultLocale.top  = new FormAttachment(wOriginalLook, margin);
        wlDefaultLocale.setLayoutData(fdlDefaultLocale);
        wDefaultLocale=new Combo(wLookComp, SWT.SINGLE | SWT.READ_ONLY | SWT.LEFT | SWT.BORDER);
        wDefaultLocale.setItems(GlobalMessages.localeDescr);
        // wDefaultLocale.setText(""+LanguageChoice.getInstance().getDefaultLocale().toString());
        propsUI.setLook(wDefaultLocale);
        fdDefaultLocale=new FormData();
        fdDefaultLocale.left = new FormAttachment(middle, 0);
        fdDefaultLocale.right= new FormAttachment(100, -margin);
        fdDefaultLocale.top  = new FormAttachment(wOriginalLook, margin);
        wDefaultLocale.setLayoutData(fdDefaultLocale);
        // language selections...
        int idxDefault = Const.indexOfString(LanguageChoice.getInstance().getDefaultLocale().toString(), GlobalMessages.localeCodes);
        if (idxDefault>=0) wDefaultLocale.select(idxDefault);

        // FailoverLocale line
        wlFailoverLocale=new Label(wLookComp, SWT.RIGHT);
        wlFailoverLocale.setText("Failover locale to use ");
        propsUI.setLook(wlFailoverLocale);
        fdlFailoverLocale=new FormData();
        fdlFailoverLocale.left = new FormAttachment(0, 0);
        fdlFailoverLocale.right= new FormAttachment(middle, -margin);
        fdlFailoverLocale.top  = new FormAttachment(wDefaultLocale, margin);
        wlFailoverLocale.setLayoutData(fdlFailoverLocale);
        wFailoverLocale=new Combo(wLookComp, SWT.SINGLE | SWT.READ_ONLY | SWT.LEFT | SWT.BORDER);
        wFailoverLocale.setItems(GlobalMessages.localeDescr);
        // setText(""+LanguageChoice.getInstance().getFailoverLocale().toString());
        propsUI.setLook(wFailoverLocale);
        fdFailoverLocale=new FormData();
        fdFailoverLocale.left = new FormAttachment(middle, 0);
        fdFailoverLocale.right= new FormAttachment(100, -margin);
        fdFailoverLocale.top  = new FormAttachment(wDefaultLocale, margin);
        wFailoverLocale.setLayoutData(fdFailoverLocale);
        int idxFailover = Const.indexOfString(LanguageChoice.getInstance().getFailoverLocale().toString(), GlobalMessages.localeCodes);
        if (idxFailover>=0) wFailoverLocale.select(idxFailover);
        */

		fdLookComp=new FormData();
		fdLookComp.left  = new FormAttachment(0, 0);
		fdLookComp.right = new FormAttachment(100, 0);
		fdLookComp.top   = new FormAttachment(0, 0);
		fdLookComp.bottom= new FormAttachment(100, 100);
		wLookComp.setLayoutData(fdLookComp);
	
		wLookComp.pack();
        
        bounds = wLookComp.getBounds();
        sLookComp.setContent(wLookComp);
        sLookComp.setExpandHorizontal(true);
        sLookComp.setExpandVertical(true);
        sLookComp.setMinWidth(bounds.width);
        sLookComp.setMinHeight(bounds.height);
        
        wLookTab.setControl(sLookComp);

		/////////////////////////////////////////////////////////////
		/// END OF LOOK TAB
		/////////////////////////////////////////////////////////////


		// Some buttons
		wOK=new Button(shell, SWT.PUSH );
		wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$
		
		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);
		
		fdTabFolder = new FormData();
		fdTabFolder.left  = new FormAttachment(0, 0);
		fdTabFolder.top   = new FormAttachment(0, 0);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom= new FormAttachment(wOK, -margin);
		wTabFolder.setLayoutData(fdTabFolder);

		/////////////////////////////////////////////////////////////
		/// END OF TABS
		/////////////////////////////////////////////////////////////


		
		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wOK.addListener    (SWT.Selection, lsOK     );
		wCancel.addListener(SWT.Selection, lsCancel );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		wIconsize.addSelectionListener  (lsDef);
		wLineWidth.addSelectionListener (lsDef);
		wShadowSize.addSelectionListener(lsDef);
		// wMaxUndo.addSelectionListener   (lsDef);
		wMiddlePct.addSelectionListener (lsDef);
        // wDefaultPreview.addSelectionListener (lsDef);
		
		// Detect [X] or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		wTabFolder.setSelection(0);
		
		BaseStepDialog.setSize(shell);

		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return propsUI;
	}

	public void dispose()
	{
        fixedFont.dispose();
        graphFont.dispose();
        noteFont.dispose();
        
        background.dispose();
        graphColor.dispose();
        tabColor.dispose();
        
		shell.dispose();
	}
	
	public void getData()
	{
		fixedFontData = propsUI.getFixedFont();
		fixedFont = new Font(display, fixedFontData);
		
		graphFontData = propsUI.getGraphFont();
		graphFont = new Font(display, graphFontData);
		
		noteFontData = propsUI.getNoteFont();
		noteFont = new Font(display, noteFontData);
		
		backgroundRGB = propsUI.getBackgroundRGB();
		if (backgroundRGB==null) backgroundRGB = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB();
        background = new Color(display, backgroundRGB);

		graphColorRGB = propsUI.getGraphColorRGB();
		graphColor = new Color(display, graphColorRGB);

		tabColorRGB = propsUI.getTabColorRGB();
		tabColor = new Color(display, tabColorRGB);
	}
	
	private void cancel()
	{
		propsUI.setScreen(new WindowProperty(shell));
		propsUI=null;
		dispose();
	}
	
	private void ok()
	{        
		propsUI.setFixedFont     ( fixedFontData );
		propsUI.setGraphFont     ( graphFontData );
		propsUI.setNoteFont      ( noteFontData  );
		propsUI.setBackgroundRGB ( backgroundRGB  );
		propsUI.setGraphColorRGB ( graphColorRGB    );
		propsUI.setTabColorRGB   ( tabColorRGB      );
		propsUI.setIconSize      ( Const.toInt(wIconsize.getText(), propsUI.getIconSize() ));
		propsUI.setLineWidth     ( Const.toInt(wLineWidth.getText(), propsUI.getLineWidth() ));
		propsUI.setShadowSize    ( Const.toInt(wShadowSize.getText(), propsUI.getShadowSize() ));
		propsUI.setMiddlePct     ( Const.toInt(wMiddlePct.getText(), propsUI.getMiddlePct() ));
        
        // propsUI.setDefaultPreviewSize( Const.toInt( wDefaultPreview.getText(), propsUI.getDefaultPreviewSize() ));

		// propsUI.setMaxUndo                             ( Const.toInt(wMaxUndo.getText(), propsUI.getMaxUndo() ));
		// propsUI.setShowTips                            ( wShowTips.getSelection()    );
		propsUI.setUseDBCache                          ( wUseCache.getSelection()    );
		propsUI.setOpenLastFile                        ( wOpenLast.getSelection()    );
		propsUI.setAutoSave                            ( wAutoSave.getSelection()    );
        // propsUI.setOnlyUsedConnectionsSavedToXML       ( wDBConnXML.getSelection()   );
        // propsUI.setAskAboutReplacingDatabaseConnections( wAskReplaceDB.getSelection());
        // propsUI.setReplaceDatabaseConnections          ( wReplaceDB.getSelection()   );
		propsUI.setSaveConfirmation                    ( wSaveConf.getSelection()    );
		// propsUI.setAutoSplit                           ( wAutoSplit.getSelection()   );
        // propsUI.setRepositoriesDialogAtStartupShown    ( wShowRep.getSelection()     );
        propsUI.setAntiAliasingEnabled                 ( wAntiAlias.getSelection()   );
        propsUI.setExitWarningShown                    ( wExitWarning.getSelection() );
        propsUI.setOSLookShown                         ( wOriginalLook.getSelection());

        /*
        int defaultLocaleIndex = wDefaultLocale.getSelectionIndex();
        if ( defaultLocaleIndex < 0 || 
             defaultLocaleIndex >= GlobalMessages.localeCodes.length )
        {
        	// Code hardening, when the combo-box ever gets in a strange state,
        	// use the first language as default (should be English)
            defaultLocaleIndex = 0;
        }
        int failoverLocaleIndex = wFailoverLocale.getSelectionIndex();
        if ( failoverLocaleIndex < 0  ||
        	 failoverLocaleIndex >= GlobalMessages.localeCodes.length )
        {
        	failoverLocaleIndex = 0;
        }
       
        LanguageChoice.getInstance().setDefaultLocale( new Locale( GlobalMessages.localeCodes[defaultLocaleIndex]) );
        LanguageChoice.getInstance().setFailoverLocale( new Locale( GlobalMessages.localeCodes[failoverLocaleIndex]) );
        LanguageChoice.getInstance().saveSettings();
        */
        
        propsUI.saveProps();
        
		dispose();
	}
}
