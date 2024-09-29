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

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.commons.metadata.mqleditor.editor.OldSwtMqlEditor;
import org.pentaho.commons.metadata.mqleditor.editor.service.MQLEditorServiceCWMImpl;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.util.FileUtil;

public class QueryBuilderDialog extends Dialog {

  
  class TextDialog extends Dialog {
    String textMsg;
    String title;

    public TextDialog(Shell parentShell, String title, String text) {
      super(parentShell);
      this.title = title;
      textMsg = text;
      setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    protected Control createDialogArea(Composite arg0) {
      Composite parent = (Composite) super.createDialogArea(arg0);
      GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 5;
      gridLayout.marginHeight = 5;
      parent.setLayout(gridLayout);
      Text text = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      text.setText(textMsg);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.widthHint = 500;
      gridData.heightHint = 500;
      text.setLayoutData(gridData);
      return parent;
    }

    protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    protected void configureShell(Shell arg0) {
      // TODO Auto-generated method stub
      super.configureShell(arg0);
      if (title != null) {
        arg0.setText(title);
      }
    }
  }

  String lastFileName;
  Map columnsMap = null;
  private MQLEditorServiceCWMImpl service;

  private List<QueryBuilderDialogListener> listeners = new ArrayList<QueryBuilderDialogListener>();
  private OldSwtMqlEditor editor;

  public QueryBuilderDialog(Shell parentShell, SchemaMeta schemaMeta) {
    super(parentShell);

    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
    service = new MQLEditorServiceCWMImpl(schemaMeta);
    editor = new OldSwtMqlEditor(service, schemaMeta);
    editor.hidePreview();
  }

  public QueryBuilderDialog(Shell parentShell, SchemaMeta schemaMeta, MQLQuery mqlQuery) {
    this(parentShell, schemaMeta);
    editor.setMqlQuery(mqlQuery);
  }
  
  public void addDialogListener(QueryBuilderDialogListener listener){
    this.listeners.add(listener);
  }


  protected void configureShell(Shell arg0) {
    super.configureShell(arg0);
    arg0.setText("Query Builder");// XXX Hardcoded title
    arg0.setSize(850, 650);
  }

  protected Control createContents(Composite arg0) {
    createMenuBar();
    createToolBar();
    super.createContents(arg0);
    editor.getDialogArea().setParent((Composite) this.getDialogArea());
    return this.getContents();
  }

  private ToolBar createToolBar() {
    Image imFileNew = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "new.png")); //$NON-NLS-1$
    Image imFileOpen = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "open.png")); //$NON-NLS-1$
    Image imFileSave = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "save.png")); //$NON-NLS-1$
    Image imFileSaveAs = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "saveas.png")); //$NON-NLS-1$
    Image imViewMQL = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "view_mql.png")); //$NON-NLS-1$
    Image imViewSQL = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "view_sql.png")); //$NON-NLS-1$
    Image imExecute = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "execute.png")); //$NON-NLS-1$
    Image imReset = new Image(getShell().getDisplay(), getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "reset.png")); //$NON-NLS-1$
    
    ToolBar toolBar = new ToolBar(getShell(), SWT.FLAT);
    ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileNew);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        newQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileOpen);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        openQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileSave);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imFileSaveAs);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQueryAs();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imViewMQL);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewMql();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imViewSQL);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewSql();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imExecute);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        executeQuery();
      }
    });
    toolItem = new ToolItem(toolBar, SWT.PUSH);
    toolItem.setImage(imReset);
    toolItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        editor.setMqlQuery(null);
      }
    });
    return toolBar;
  }

  private Menu createMenuBar() {
    Menu menu = new Menu(getShell(), SWT.BAR);

    MenuItem fileMenuHeader = new MenuItem(menu, SWT.CASCADE);
    fileMenuHeader.setText("File");
    Menu fileMenu = new Menu(getShell(), SWT.DROP_DOWN);
    fileMenuHeader.setMenu(fileMenu);
    MenuItem fileNewItem = new MenuItem(fileMenu, SWT.PUSH);
    fileNewItem.setText("New");
    fileNewItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        newQuery();
      }
    });
    MenuItem fileOpenItem = new MenuItem(fileMenu, SWT.PUSH);
    fileOpenItem.setText("Open...");
    fileOpenItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        openQuery();
      }
    });
    MenuItem fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
    fileSaveItem.setText("Save");
    fileSaveItem.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQuery();
      }
    });
    MenuItem fileSaveAsItem = new MenuItem(fileMenu, SWT.PUSH);
    fileSaveAsItem.setText("Save As...");
    fileSaveAsItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        saveQueryAs();
      }
    });
    new MenuItem(fileMenu, SWT.SEPARATOR);
    MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
    fileExitItem.setText("Exit");
    fileExitItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        exit();
      }
    });

    MenuItem toolsMenuHeader = new MenuItem(menu, SWT.CASCADE);
    toolsMenuHeader.setText("Tools");
    Menu toolsMenu = new Menu(getShell(), SWT.DROP_DOWN);
    toolsMenuHeader.setMenu(toolsMenu);
    MenuItem toolsViewMqlItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsViewMqlItem.setText("View MQL");
    toolsViewMqlItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewMql();
      }
    });
    MenuItem toolsViewSqlItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsViewSqlItem.setText("View SQL");
    toolsViewSqlItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        viewSql();
      }
    });
    MenuItem toolsExecuteQueryItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsExecuteQueryItem.setText("Execute Query");
    toolsExecuteQueryItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        executeQuery();
      }
    });
    new MenuItem(toolsMenu, SWT.SEPARATOR);
    MenuItem toolsResetQueryItem = new MenuItem(toolsMenu, SWT.PUSH);
    toolsResetQueryItem.setText("Reset Query");
    toolsResetQueryItem.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        editor.setMqlQuery(null);
      }
    });
    getShell().setMenuBar(menu);
    return menu;
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, SWT.BORDER);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    return super.createDialogArea(composite);
  }

  //TODO: is this used anywhere?
  public void showSQL() {
    try {
    	@SuppressWarnings("unchecked")
      MQLQuery mqlQuery = editor.getMqlQuery();
      if (mqlQuery != null) {
        
        // Here we will generate the SQL with the truncated column ids, and
        // intentionally show those truncated ids as that IS the SQL that will be executing.
        
        String sql = mqlQuery.getQuery().getQuery();
        if (sql != null) {
          EnterTextDialog showSQL = new EnterTextDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_GENERATED_SQL"), Messages.getString("QueryDialog.USER_GENERATED_SQL"), sql, true); //$NON-NLS-1$ //$NON-NLS-2$
          sql = showSQL.open();
          if (!Const.isEmpty(sql)) {
            DatabaseMeta databaseMeta = ((Selection) mqlQuery.getSelections().get(0)).getBusinessColumn().getPhysicalColumn().getTable().getDatabaseMeta();
            executeSQL(databaseMeta, sql);
          }
        }
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void executeSQL(DatabaseMeta databaseMeta, String sql) {
    Database database = null;
    java.util.List<Object[]> rows = null;
    RowMetaInterface rm = null;
    
    try {
      String path = ""; //$NON-NLS-1$
      try {
        File file = new File("simple-jndi"); //$NON-NLS-1$
        path = file.getCanonicalPath();
      } catch (Exception e) {
        e.printStackTrace();
      }

      System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$
      System.setProperty("org.osjava.sj.root", path); //$NON-NLS-1$
      System.setProperty("org.osjava.sj.delimiter", "/"); //$NON-NLS-1$ //$NON-NLS-2$
      database = new Database(databaseMeta);
      database.connect();
      rows = database.getRows(sql, 5000); // get the first 5000 rows from the query for demo-purposes.
      rm = database.getReturnRowMeta();
    } catch (Exception e) {
      new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_EXECUTING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_EXECUTING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
    } finally {
      if (database != null)
        database.disconnect();
    }
    
    // Show the rows in a dialog.
    if (rows != null && rows.size()>0) {
      
      //Reinstate the actual "as" column identifiers here, before preview. 
      if (columnsMap != null){
        for (int i = 0; i < rm.size(); i++){
          ValueMetaInterface value = rm.getValueMeta(i);
          String colName = (String)columnsMap.get(rm.getValueMeta(i).getName());
          if(!StringUtils.isEmpty(colName)) {
            value.setName(colName);
          }
        }        
      }
      
      PreviewRowsDialog previewRowsDialog = new PreviewRowsDialog(getShell(), new Variables(),SWT.NONE, Messages.getString("QueryDialog.USER_FIRST_5000_ROWS"), rm,rows); //$NON-NLS-1$
      previewRowsDialog.open();
    } else {
      MessageDialog.openInformation(getShell(), Messages.getString("QueryDialog.USER_NO_DATA_TITLE"), Messages.getString("QueryDialog.USER_NO_DATA_INFO"));//$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void newQuery() {
    lastFileName = null;
    editor.setMqlQuery(null);
  }

  private void openQuery() {
    FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
    fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    fileDialog.setFilterNames(new String[] { Messages.getString("QueryDialog.USER_MQL_QUERIES"), Messages.getString("QueryDialog.USER_XML_FILES"), Messages.getString("QueryDialog.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String filename = fileDialog.open();
    if (filename != null) {
      try {
        editor.setMqlQuery(new MQLQueryImpl(FileUtil.readAsXml(filename), null, null, null));
        
      } catch (Exception e) {
        new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  private void executeQuery() {
    try {
    	@SuppressWarnings("unchecked")
      MQLQuery mqlQuery = editor.getMqlQuery();
      if (mqlQuery != null) {
        
        // This map  holds references from the truncated column ids used to the actual column ids; 
        // we'll use the map later to reinstate the real column ids for display. This is a work
        // around for databases that limit the length of column ids in the "as" portion of the SQL.
        
        MappedQuery q = mqlQuery.getQuery();
        String sql = q.getQuery();
        columnsMap = q.getMap();
        DatabaseMeta databaseMeta = mqlQuery.getSelections().get(0).getBusinessColumn().getPhysicalColumn().getTable().getDatabaseMeta();
        executeSQL(databaseMeta, sql);
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void viewMql() {
    try {
      MQLQuery mqlQuery = editor.getMqlQuery();
      if (mqlQuery != null) {
        Document document = DocumentHelper.parseText(mqlQuery.getXML());
        TextDialog textDialog = new TextDialog(getShell(), "MQL Query", prettyPrint(document).getRootElement().asXML()); //$NON-NLS-1$ //$NON-NLS-2$
        textDialog.open();
      }
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  public void viewSql() {
    try {
      MQLQuery mqlQuery = editor.getMqlQuery();
      if (mqlQuery != null) {
        MappedQuery q = mqlQuery.getQuery();
        String sql = q.getQuery();
        columnsMap = q.getMap();
        if (sql != null) {
          TextDialog textDialog = new TextDialog(getShell(), "SQL Query", sql); //$NON-NLS-1$ //$NON-NLS-2$
          textDialog.open();
        }
      }
    } catch (Throwable e) {
      new ErrorDialog(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString("QueryDialog.USER_ERROR_QUERY_GENERATION"), new Exception(e)); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void exit() {
    cancelPressed();
  }

  private void saveQuery() {
    MQLQuery query = editor.getMqlQuery();
    if (query != null) {
      if (lastFileName != null) {
        try {
          FileUtil.saveAsXml(lastFileName, query.getXML());
        } catch (Exception e) {
          new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      } else {
        saveQueryAs();
      }
    }
  }

  private void saveQueryAs() {
    MQLQuery query = editor.getMqlQuery();
    if (query != null) {
      FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
      fileDialog.setFilterExtensions(new String[] { "*.mql", "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      fileDialog
          .setFilterNames(new String[] { Messages.getString("QueryDialog.USER_MQL_QUERIES"), Messages.getString("QueryDialog.USER_XML_FILES"), Messages.getString("QueryDialog.USER_ALL_FILES") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String filename = fileDialog.open();
      if (filename != null) {
        try {
          FileUtil.saveAsXml(filename, query.getXML());
          lastFileName = filename;
        } catch (Exception e) {
          new ErrorDialog(getShell(), Messages.getString("QueryDialog.USER_TITLE_ERROR_LOADING_QUERY"), Messages.getString("QueryDialog.USER_ERROR_LOADING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
  }

  public Document prettyPrint( Document document ) {
    try {
      OutputFormat format = OutputFormat.createPrettyPrint();
      format.setEncoding(document.getXMLEncoding());
      StringWriter stringWriter = new StringWriter();
      XMLWriter writer = new XMLWriter( stringWriter, format );
      // XMLWriter has a bug that is avoided if we reparse the document
      // prior to calling XMLWriter.write()
      writer.write(DocumentHelper.parseText(document.asXML()));
      writer.close();
      document = DocumentHelper.parseText( stringWriter.toString() );
    }
    catch ( Exception e ){
      e.printStackTrace();
            return( null );
    }
    return( document );
  } 
  
  public MQLQuery getMqlQuery(){
    return editor.getMqlQuery();
  }
  
}
