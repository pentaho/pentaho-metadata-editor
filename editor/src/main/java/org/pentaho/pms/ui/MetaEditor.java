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
 * Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.pms.ui;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.LastUsedFile;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.di.core.dnd.XMLTransfer;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.ui.core.PrintSpool;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.database.dialog.DatabaseDialog;
import org.pentaho.di.ui.core.database.dialog.DatabaseExplorerDialog;
import org.pentaho.di.ui.core.database.dialog.SQLEditor;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.EnterStringDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TreeMemory;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.CWMException;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.mql.MQLQueryFactory;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.SecurityService;
import org.pentaho.pms.ui.concept.editor.ConceptEditorDialog;
import org.pentaho.pms.ui.concept.editor.ConceptTreeModel;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.IConceptTreeModel;
import org.pentaho.pms.ui.concept.editor.PredefinedVsCustomPropertyHelper;
import org.pentaho.pms.ui.dialog.BusinessCategoryDialog;
import org.pentaho.pms.ui.dialog.BusinessModelDialog;
import org.pentaho.pms.ui.dialog.BusinessTableDialog;
import org.pentaho.pms.ui.dialog.CategoryEditorDialog;
import org.pentaho.pms.ui.dialog.PhysicalTableDialog;
import org.pentaho.pms.ui.dialog.PublishDialog;
import org.pentaho.pms.ui.dialog.RelationshipDialog;
import org.pentaho.pms.ui.jface.tree.ITreeNode;
import org.pentaho.pms.ui.jface.tree.ITreeNodeChangedListener;
import org.pentaho.pms.ui.jface.tree.TreeContentProvider;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.ui.security.SecurityDialog;
import org.pentaho.pms.ui.tree.BusinessColumnTreeNode;
import org.pentaho.pms.ui.tree.BusinessModelTreeNode;
import org.pentaho.pms.ui.tree.BusinessModelsTreeNode;
import org.pentaho.pms.ui.tree.BusinessTableTreeNode;
import org.pentaho.pms.ui.tree.BusinessTablesTreeNode;
import org.pentaho.pms.ui.tree.BusinessViewTreeNode;
import org.pentaho.pms.ui.tree.CategoryTreeNode;
import org.pentaho.pms.ui.tree.ConceptLabelProvider;
import org.pentaho.pms.ui.tree.ConceptTreeNode;
import org.pentaho.pms.ui.tree.ConnectionsTreeNode;
import org.pentaho.pms.ui.tree.DatabaseMetaTreeNode;
import org.pentaho.pms.ui.tree.LabelTreeNode;
import org.pentaho.pms.ui.tree.PhysicalColumnTreeNode;
import org.pentaho.pms.ui.tree.PhysicalTableTreeNode;
import org.pentaho.pms.ui.tree.RelationshipTreeNode;
import org.pentaho.pms.ui.tree.RelationshipsTreeNode;
import org.pentaho.pms.ui.tree.SchemaMetaTreeNode;
import org.pentaho.pms.ui.util.AboutDialog;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.ui.util.EnterOptionsDialog;
import org.pentaho.pms.ui.util.GUIResource;
import org.pentaho.pms.ui.util.ListSelectionDialog;
import org.pentaho.pms.ui.util.Splash;
import org.pentaho.pms.util.FileUtil;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to edit the metadata domain (Schema Metadata), load/store into the MDR/CWM model
 *
 * @since 16-may-2003
 */
public class MetaEditor implements SelectionListener {
  private CWM cwm;

  private LogChannelInterface log;

  private Display disp;

  private Shell shell;

  private MetaEditorGraph metaEditorGraph;

  private MetaEditorLog metaEditorLog;

  // private MetaEditorConcepts metaEditorConcept;

  private MetaEditorOlap metaEditorOlap;

  CTabItem tiTabsOlap;

  private SashForm sashform;

  private CTabFolder tabfolder;

  private SchemaMeta schemaMeta;

  private MQLQuery query;

  private ToolBar tBar;

  private Menu mBar;

  private Listener mainListener;

  private MenuItem mFile;

  private Menu msFile;

  private MenuItem miFileOpen, miFileNew, miFileSave, miFileSaveAs, miFileExport, miPublish, miFileImport,
    miFileDelete, miFilePrint, miFileSep3, miFileQuit;

  private MenuItem miNewDomain, miNewConnection, miNewPTable, miNewBTable, miNewBModel, miNewRel, miNewCat;

  private MenuItem miNewDomainTB, miNewConnectionTB, miNewPTableTB, miNewBTableTB, miNewBModelTB, miNewRelTB,
    miNewCatTB;

  private Listener lsDomainNew, lsConnectionNew, lsPTableNew, lsBTableNew, lsBModelNew, lsRelationNew, lsCategoryNew,
    lsFileOpen, lsFileSave, lsFileSaveAs, lsFileExport, lsPublish, lsFileImport, lsFileDelete, lsFilePrint,
    lsFileQuit, lsEditLocales, lsEditConcepts, lsEditCategories, lsAlignRight, lsAlignLeft, lsAlignTop,
    lsAlignBottom, lsDistribHoriz, lsDistribVert;

  private MenuItem mEdit;

  private Menu msEdit;

  // private Menu mPopAD;

  private MenuItem miEditSelectAll, miEditUnselectAll, miEditProperties, miEditOptions, miEditRefresh;

  private Listener lsEditSelectAll, lsEditUnselectAll, lsEditOptions, lsEditProperties, lsEditRefresh;

  private ToolItem tiEditProperties;

  private MenuItem mHelp;

  private Menu msHelp;

  private MenuItem miHelpAbout;

  private Listener lsHelpAbout;

  public static final String STRING_CONNECTIONS = Messages.getString( "MetaEditor.USER_CONNECTIONS" ); //$NON-NLS-1$

  public static final String STRING_BUSINESS_MODELS = Messages.getString( "MetaEditor.USER_BUSINESS_MODELS" );
  //$NON-NLS-1$

  public static final String STRING_BUSINESS_TABLES = Messages.getString( "MetaEditor.USER_BUSINESS_TABLES" );
  //$NON-NLS-1$

  public static final String STRING_RELATIONSHIPS = Messages.getString( "MetaEditor.USER_RELATIONSHIPS" ); //$NON-NLS-1$

  public static final String STRING_CATEGORIES = Messages.getString( "MetaEditor.USER_CATEGORIES" ); //$NON-NLS-1$

  public static final String APPLICATION_NAME = Messages.getString( "MetaEditor.USER_METADATA_EDITOR" ); //$NON-NLS-1$

  private static final String STRING_MAIN_TREE = "MainTree"; //$NON-NLS-1$

  public static final String STRING_CATEGORIES_TREE = "CategoriesTree"; //$NON-NLS-1$

  private TreeViewer treeViewer;

  private SchemaMetaTreeNode mainTreeNode;

  private BusinessModelTreeNode activeModelTreeNode;

  public KeyAdapter defKeys;

  public KeyAdapter modKeys;

  private PropsUI props;

  private MetaEditorLocales metaEditorLocales;

  private MenuItem mTools;

  private Menu msTools;

  private MenuItem miSecurityService, miLocalesEditor, miConceptEditor, miCategoryEditor, miLogging;

  private Listener lsSecurityService;

  private CwmSchemaFactoryInterface cwmSchemaFactory;

  private Menu mainMenu;

  // Use only unit tests
  MetaEditor() {
  }

  public MetaEditor( LogChannelInterface log ) {
    this( log, null );
  }

  public MetaEditor( LogChannelInterface log, Display display ) {
    this.log = log;
    if ( display != null ) {
      disp = display;
    } else {
      disp = new Display();
    }
    shell = new Shell( disp );
    shell.setText( APPLICATION_NAME );

    FormLayout layout = new FormLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    shell.setLayout( layout );

    props = PropsUI.getInstance();

    cwmSchemaFactory = Settings.getCwmSchemaFactory();

    // INIT Data structure
    schemaMeta = new SchemaMeta();
    loadQuery();

    // Load settings in the props
    loadSettings();

    Image icon_small = new Image( display, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "icon.png" ) ); //$NON-NLS-1$
    Image icon_large = new Image( display, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "icon_high.png" ) ); //$NON-NLS-1$
    shell.setImages( new Image[] { icon_small, icon_large } );

    initGlobalKeyBindings();
    initGlobalListeners();
    initToolBar();
    initMainForm();
    initMenu();
    initTree();
    initTabs();

    // In case someone dares to press the [X] in the corner ;-)
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        e.doit = quitFile();
      }
    } );
    int[] weights = props.getSashWeights();
    sashform.setWeights( weights );
    sashform.setVisible( true );

    shell.layout();
    getMainListener().handleEvent( null ); // Force everything to match the
    // current state

    shell.setMaximized( true );

    disp.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( Event arg0 ) {
        if ( ( arg0.keyCode == 'o' ) && ( arg0.stateMask == ( SWT.ALT | SWT.CTRL ) ) ) {
          toggleOlapTab();
        }
      }
    } );
  }

  private void initGlobalKeyBindings() {
    defKeys = new KeyAdapter() {
      public void keyPressed( KeyEvent e ) {

        boolean control = ( e.stateMask & SWT.MOD1 ) != 0;
        boolean alt = ( e.stateMask & SWT.ALT ) != 0;

        if ( e.getSource() == treeViewer.getTree() ) {
          if ( treeViewer.getTree().getSelection().length == 1 ) {
            if ( alt && ( e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN ) ) {
              // support CTRL UP and CTRL DOWN
              final TreeItem ti = treeViewer.getTree().getSelection()[ 0 ];
              final ConceptTreeNode node = (ConceptTreeNode) ti.getData();

              if ( node instanceof BusinessModelTreeNode || node instanceof CategoryTreeNode
                || ( node instanceof BusinessColumnTreeNode && node.getParent() instanceof CategoryTreeNode ) ) {
                final ConceptTreeNode parentNode = (ConceptTreeNode) node.getParent();
                if ( e.keyCode == SWT.ARROW_UP ) {
                  parentNode.moveChildUp( node );
                } else {
                  parentNode.moveChildDown( node );
                }
              }
            }
          }
        }

        BusinessModel activeModel = schemaMeta.getActiveModel();

        // ESC --> Unselect All steps
        if ( e.keyCode == SWT.ESC ) {
          if ( activeModel != null ) {
            activeModel.unselectAll();
            refreshGraph();
          }
          metaEditorGraph.control = false;
        }

        // F5 --> refresh
        if ( e.keyCode == SWT.F5 ) {
          refreshAll();
          metaEditorGraph.control = false;
        }

        // F8 --> generate Mondrian model
        if ( e.keyCode == SWT.F8 ) {
          getMondrianModel();
          metaEditorGraph.control = false;
        }

        // CTRL-A --> Select All steps
        if ( e.character == 1 && control && !alt ) {
          if ( activeModel != null ) {
            activeModel.selectAll();
            refreshGraph();
          }
          metaEditorGraph.control = false;
        }

        // CTRL-E --> Select All steps
        if ( e.character == 5 && control && !alt ) {
          exportToXMI();
          metaEditorGraph.control = false;
        }

        // CTRL-I --> Select All steps
        if ( e.character == 9 && control && !alt ) {
          importFromXMI();
          metaEditorGraph.control = false;
        }

        // CTRL-N --> new
        if ( e.character == 14 && control && !alt ) {
          newFile();
          metaEditorGraph.control = false;
        }
        // CTRL-O --> open
        if ( e.character == 15 && control && !alt ) {
          openFile();
          metaEditorGraph.control = false;
        }
        // CTRL-P --> print
        if ( e.character == 16 && control && !alt ) {
          printFile();
          metaEditorGraph.control = false;
        }
        // CTRL-S --> save
        if ( e.character == 19 && control && !alt ) {
          saveFile();
          metaEditorGraph.control = false;
        }
        // CTRL-T --> Test
        if ( e.character == 20 && control && !alt ) {
          testQR();
          metaEditorGraph.control = false;
        }
      }
    };
    modKeys = new KeyAdapter() {
      public void keyPressed( KeyEvent e ) {
        if ( e.keyCode == SWT.SHIFT ) {
          metaEditorGraph.shift = true;
        }
        if ( e.keyCode == SWT.MOD1 ) {
          metaEditorGraph.control = true;
        }
      }

      public void keyReleased( KeyEvent e ) {
        if ( e.keyCode == SWT.SHIFT ) {
          metaEditorGraph.shift = false;
        }
        if ( e.keyCode == SWT.MOD1 ) {
          metaEditorGraph.control = false;
        }
      }
    };
  }

  private void initGlobalListeners() {
    lsDomainNew = new Listener() {
      public void handleEvent( Event e ) {
        newFile();
      }
    };
    lsConnectionNew = new Listener() {
      public void handleEvent( Event e ) {
        newConnection();
      }
    };
    lsPTableNew = new Listener() {
      public void handleEvent( Event e ) {
        newConnection();
      }
    };
    lsBTableNew = new Listener() {
      public void handleEvent( Event e ) {
        newBusinessTable( null );
      }
    };
    lsBModelNew = new Listener() {
      public void handleEvent( Event e ) {
        newBusinessModel();
      }
    };
    lsRelationNew = new Listener() {
      public void handleEvent( Event e ) {
        newRelationship();
      }
    };
    lsCategoryNew = new Listener() {
      public void handleEvent( Event e ) {
        editBusinessCategories();
      }
    };
    lsFileOpen = new Listener() {
      public void handleEvent( Event e ) {
        openFile();
      }
    };
    lsFileSave = new Listener() {
      public void handleEvent( Event e ) {
        saveFile();
      }
    };
    lsFileSaveAs = new Listener() {
      public void handleEvent( Event e ) {
        saveFileAs();
      }
    };
    lsFileExport = new Listener() {
      public void handleEvent( Event e ) {
        exportToXMI();
      }
    };
    lsPublish = new Listener() {
      public void handleEvent( Event e ) {
        publishXmi();
      }
    };
    lsFileImport = new Listener() {
      public void handleEvent( Event e ) {
        importFromXMI();
      }
    };
    lsFileDelete = new Listener() {
      public void handleEvent( Event e ) {
        deleteFile();
      }
    };
    lsFilePrint = new Listener() {
      public void handleEvent( Event e ) {
        printFile();
      }
    };
    lsFileQuit = new Listener() {
      public void handleEvent( Event e ) {
        quitFile();
      }
    };
    lsEditUnselectAll = new Listener() {
      public void handleEvent( Event e ) {
        editUnselectAll();
      }
    };
    lsEditSelectAll = new Listener() {
      public void handleEvent( Event e ) {
        editSelectAll();
      }
    };
    lsEditOptions = new Listener() {
      public void handleEvent( Event e ) {
        editOptions();
      }
    };
    lsEditProperties = new Listener() {
      public void handleEvent( Event e ) {
        editSelectedProperties();
      }
    };
    lsEditRefresh = new Listener() {
      public void handleEvent( Event e ) {
        refreshAll();
      }
    };
    lsSecurityService = new Listener() {
      public void handleEvent( Event e ) {
        editSecurityService();
      }
    };
    lsEditLocales = new Listener() {
      public void handleEvent( Event e ) {
        tabfolder.setSelection( 1 );
      }
    };
    lsEditConcepts = new Listener() {
      public void handleEvent( Event e ) {
        IConceptTreeModel conceptTreeModel = new ConceptTreeModel( schemaMeta );
        ConceptEditorDialog diag = new ConceptEditorDialog( Display.getCurrent().getActiveShell(), conceptTreeModel );
        diag.open();
      }
    };
    lsEditCategories = new Listener() {
      public void handleEvent( Event e ) {
        editBusinessCategories();
      }
    };
    lsAlignLeft = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.allignleft();
      }
    };
    lsAlignRight = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.allignright();
      }
    };
    lsAlignTop = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.alligntop();
      }
    };
    lsAlignBottom = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.allignbottom();
      }
    };
    lsDistribHoriz = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.distributehorizontal();
      }
    };
    lsDistribVert = new Listener() {
      public void handleEvent( Event e ) {
        metaEditorGraph.distributevertical();
      }
    };

    lsHelpAbout = new Listener() {
      public void handleEvent( Event e ) {
        helpAbout();
      }
    };
  }

  protected void editConcepts() {
    // TODO Auto-generated method stub

  }

  private void initMainForm() {
    sashform = new SashForm( shell, SWT.HORIZONTAL );

    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment( 0, 0 );
    fdSash.top = new FormAttachment( tBar, 0 );
    fdSash.bottom = new FormAttachment( 100, 0 );
    fdSash.right = new FormAttachment( 100, 0 );
    sashform.setLayoutData( fdSash );
  }

  public void exportToXMI() {
    exportToXMI( null );
  }

  public void exportToXMI( String forcedFilename ) {
    boolean goAhead = true;

    if ( Const.isEmpty( schemaMeta.getDomainName() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setMessage( Messages.getString( "MetaEditor.USER_NO_NAME_CAN_NOT_EXPORT" ) ); //$NON-NLS-1$
      mb.setText( Messages.getString( "MetaEditor.USER_SORRY" ) ); //$NON-NLS-1$
      if ( mb.open() != SWT.YES ) {
        goAhead = false;
      }
    }

    if ( schemaMeta.hasChanged() ) {
      MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
      mb.setMessage( Messages.getString( "MetaEditor.USER_SAVE_DOMAIN" ) ); //$NON-NLS-1$
      mb.setText( Messages.getString( "MetaEditor.USER_CONTINUE" ) ); //$NON-NLS-1$
      if ( mb.open() == SWT.YES ) {
        goAhead = saveFile();
      } else {
        goAhead = false;
      }
    }
    if ( goAhead ) {
      String filename;
      if ( forcedFilename == null ) {
        FileDialog dialog = new FileDialog( shell, SWT.SAVE );
        dialog
          .setFilterExtensions( new String[] { "*.xmi", "*.xml", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        dialog
          .setFilterNames( new String[] {
            Messages.getString( "MetaEditor.USER_XMI_FILES" ), Messages.getString( "MetaEditor.USER_XML_FILES" ),
            Messages.getString( "MetaEditor.USER_ALL_FILES" ) } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        filename = dialog.open();
      } else {
        filename = forcedFilename;
      }
      if ( filename != null ) {
        if ( !filename.toLowerCase().endsWith( ".xmi" )
          && !filename.toLowerCase().endsWith( ".xml" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
          filename += ".xmi"; //$NON-NLS-1$
        }

        // Get back the result of the last save operation...
        CWM cwmInstance = CWM.getInstance( schemaMeta.getDomainName() );

        if ( cwmInstance != null ) {
          try {
            cwmInstance.exportToXMI( filename );
          } catch ( Exception e ) {
            new ErrorDialog(
              shell,
              Messages.getString( "General.USER_TITLE_ERROR" ),
              Messages.getString( "MetaEditor.USER_ERROR_EXPORTING_XMI" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }
      }
    }
  }

  public void publishXmi() {
    boolean goAhead = true;
    if ( schemaMeta.hasChanged() ) {
      MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
      mb.setMessage( Messages.getString( "MetaEditor.USER_SAVE_DOMAIN" ) ); //$NON-NLS-1$
      mb.setText( Messages.getString( "MetaEditor.USER_CONTINUE" ) ); //$NON-NLS-1$
      if ( mb.open() == SWT.YES ) {
        goAhead = saveFile();
      } else {
        goAhead = false;
      }
    }
    if ( goAhead ) {
      if ( !validateBusinessModels() ) {
        MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
        mb.setMessage( Messages.getString( "MetaEditor.USER_MODEL_MALFORMED" ) ); //$NON-NLS-1$
        mb.setText( Messages.getString( "MetaEditor.USER_MODEL_VALIDATION_ERROR" ) ); //$NON-NLS-1$
        goAhead = mb.open() == SWT.YES;
      }
    }
    if ( goAhead ) {
      PublishDialog publishDialog = new PublishDialog( shell, schemaMeta );
      publishDialog.open();
    }
  }

  public boolean validateBusinessModels() {
    boolean valid = true;

    Iterator iter = schemaMeta.getBusinessModels().iterator();
    while ( iter.hasNext() && valid ) {
      BusinessModel bm = (BusinessModel) iter.next();
      if ( bm.getBusinessTables().size() > 1 ) {
        valid = bm.getRelationships().size() > 0 && bm.getFlatCategoriesView( schemaMeta.getActiveLocale() ).size() > 1;
      }
    }

    return valid;
  }

  public void importFromXMI() {
    if ( showChangedWarning() ) {
      FileDialog fileDialog = new FileDialog( shell, SWT.OPEN );
      fileDialog
        .setFilterExtensions( new String[] { "*.xmi", "*.xml", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      fileDialog
        .setFilterNames( new String[] {
          Messages.getString( "MetaEditor.USER_XMI_FILES" ), Messages.getString( "MetaEditor.USER_XML_FILES" ),
          Messages.getString( "MetaEditor.USER_ALL_FILES" ) } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      final String filename = fileDialog.open();
      if ( filename != null ) {
        try {
          // Ask for a new domain to import into...
          //
          EnterStringDialog stringDialog =
            new EnterStringDialog(
              shell,
              "", Messages.getString( "MetaEditor.USER_TITLE_SAVE_DOMAIN" ),
              Messages.getString( "MetaEditor.USER_ENTER_DOMAIN_NAME" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          final String domainName = stringDialog.open();
          if ( domainName != null ) {
            int id = SWT.YES;
            if ( CWM.exists( domainName ) ) {
              MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
              mb.setMessage( Messages.getString( "MetaEditor.USER_DOMAIN_EXISTS_OVERWRITE" ) ); //$NON-NLS-1$
              mb.setText( Messages.getString( "MetaEditor.USER_TITLE_DOMAIN_EXISTS" ) ); //$NON-NLS-1$
              id = mb.open();
            }
            if ( id == SWT.YES ) {
              CWM delCwm = CWM.getInstance( domainName );
              delCwm.removeDomain();
            } else {
              return; // no selected.
            }

            final ArrayList<Exception> exceptionList = new ArrayList<Exception>();
            Runnable runnable = new Runnable() {
              public void run() {
                try {
                  // Now create a new domain...
                  CWM cwmInstance = CWM.getInstance( domainName );

                  // import it all...
                  cwmInstance.importFromXMI( filename );

                  // convert to a schema
                  schemaMeta = cwmSchemaFactory.getSchemaMeta( cwmInstance );
                } catch ( Exception e ) {
                  exceptionList.add( e );
                }
              }
            };

            BusyIndicator.showWhile( Display.getCurrent(), runnable );

            if ( exceptionList.size() == 0 ) {
              // Here, we are getting a whole new model, so
              // rebuild the whole tree
              refreshTree();
            } else {
              new ErrorDialog(
                shell,
                Messages.getString( "MetaEditor.USER_TITLE_ERROR_SAVE_DOMAIN" ),
                Messages.getString( "MetaEditor.USER_ERROR_LOADING_DOMAIN" ),
                (Exception) exceptionList.get( 0 ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        } catch ( Exception e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "MetaEditor.USER_TITLE_ERROR_SAVE_DOMAIN" ),
            Messages.getString( "MetaEditor.USER_ERROR_LOADING_DOMAIN" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }

  }

  public void open() {
    // Set the shell size, based upon previous time...
    WindowProperty winprop = props.getScreen( shell.getText() );
    if ( winprop != null ) {
      winprop.setShell( shell );
    } else {
      shell.pack();
    }

    shell.open();

    // Perhaps the transformation contains elements at startup?
    if ( schemaMeta.nrTables() > 0 || schemaMeta.nrDatabases() > 0 ) {
      refreshTree();
      refreshAll(); // Do a complete refresh then...
    }
  }

  public boolean readAndDispatch() {
    try {
      return disp.readAndDispatch();
    } catch ( Exception e ) {
      return false;
    }
  }

  public void dispose() {
    try {
      CWM.quitAndSync();
      disp.dispose();
    } catch ( Exception e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "MetaEditor.USER_TITLE_ERROR_STOPPING_REPOSITORY" ),
        Messages.getString( "MetaEditor.USER_ERROR_STOPPING_REPOSITORY" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public boolean isDisposed() {
    return disp.isDisposed();
  }

  public void sleep() {
    disp.sleep();
  }

  public void initMenu() {
    mBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( mBar );

    // main File menu...
    mFile = new MenuItem( mBar, SWT.CASCADE );
    mFile.setText( Messages.getString( "MetaEditor.USER_FILE" ) ); //$NON-NLS-1$
    msFile = new Menu( shell, SWT.DROP_DOWN );

    mFile.setMenu( msFile );

    miFileNew = new MenuItem( msFile, SWT.CASCADE );
    miFileNew.setText( Messages.getString( "MetaEditor.USER_NEW" ) ); //$NON-NLS-1$

    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    miFileNew.setMenu( fileMenu );

    miNewDomain = new MenuItem( fileMenu, SWT.CASCADE );
    miNewDomain.setText( Messages.getString( "MetaEditor.USER_NEW_DOMAIN_MENU" ) ); //$NON-NLS-1$
    miNewDomain.addListener( SWT.Selection, lsDomainNew );

    new MenuItem( fileMenu, SWT.SEPARATOR );
    miNewConnection = new MenuItem( fileMenu, SWT.CASCADE );
    miNewConnection.setText( Messages.getString( "MetaEditor.USER_NEW_CONNECTION_MENU" ) ); //$NON-NLS-1$
    miNewConnection.addListener( SWT.Selection, lsConnectionNew );

    miNewPTable = new MenuItem( fileMenu, SWT.CASCADE );
    miNewPTable.setText( Messages.getString( "MetaEditor.USER_NEW_PHYSICAL_TABLE_MENU" ) ); //$NON-NLS-1$
    miNewPTable.addListener( SWT.Selection, lsPTableNew );

    new MenuItem( fileMenu, SWT.SEPARATOR );
    miNewBTable = new MenuItem( fileMenu, SWT.CASCADE );
    miNewBTable.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_TABLE_MENU" ) ); //$NON-NLS-1$
    miNewBTable.addListener( SWT.Selection, lsBTableNew );

    miNewBModel = new MenuItem( fileMenu, SWT.CASCADE );
    miNewBModel.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_MODEL_MENU" ) ); //$NON-NLS-1$
    miNewBModel.addListener( SWT.Selection, lsBModelNew );

    miNewRel = new MenuItem( fileMenu, SWT.CASCADE );
    miNewRel.setText( Messages.getString( "MetaEditor.USER_NEW_RELATIONSHIP_MENU" ) ); //$NON-NLS-1$
    miNewRel.addListener( SWT.Selection, lsRelationNew );

    miNewCat = new MenuItem( fileMenu, SWT.CASCADE );
    miNewCat.setText( Messages.getString( "MetaEditor.USER_NEW_CATEGORY_MENU" ) ); //$NON-NLS-1$
    miNewCat.addListener( SWT.Selection, lsCategoryNew );

    miFileOpen = new MenuItem( msFile, SWT.CASCADE );
    miFileOpen.setText( Messages.getString( "MetaEditor.USER_OPEN" ) ); //$NON-NLS-1$
    miFileOpen.setAccelerator( SWT.MOD1 | 'o' );
    miFileOpen.addListener( SWT.Selection, lsFileOpen );

    miFileSave = new MenuItem( msFile, SWT.CASCADE );
    miFileSave.setText( Messages.getString( "MetaEditor.USER_SAVE" ) ); //$NON-NLS-1$
    miFileSave.setAccelerator( SWT.MOD1 | 'p' );
    miFileSave.addListener( SWT.Selection, lsFileSave );

    miFileSaveAs = new MenuItem( msFile, SWT.CASCADE );
    miFileSaveAs.setText( Messages.getString( "MetaEditor.USER_SAVE_AS" ) ); //$NON-NLS-1$
    miFileSaveAs.addListener( SWT.Selection, lsFileSaveAs );

    new MenuItem( msFile, SWT.SEPARATOR );
    miFileImport = new MenuItem( msFile, SWT.CASCADE );
    miFileImport.setText( Messages.getString( "MetaEditor.USER_IMPORT" ) ); //$NON-NLS-1$
    miFileImport.setAccelerator( SWT.MOD1 | 'i' );
    miFileImport.addListener( SWT.Selection, lsFileImport );

    miFileExport = new MenuItem( msFile, SWT.CASCADE );
    miFileExport.setText( Messages.getString( "MetaEditor.USER_EXPORT" ) ); //$NON-NLS-1$
    miFileExport.setAccelerator( SWT.MOD1 | 'e' );
    miFileExport.addListener( SWT.Selection, lsFileExport );

    miPublish = new MenuItem( msFile, SWT.CASCADE );
    miPublish.setText( Messages.getString( "MetaEditor.PUBLISH" ) ); //$NON-NLS-1$
    miPublish.addListener( SWT.Selection, lsPublish );

    new MenuItem( msFile, SWT.SEPARATOR );
    miFileDelete = new MenuItem( msFile, SWT.CASCADE );
    miFileDelete.setText( Messages.getString( "MetaEditor.USER_DELETE_DOMAIN" ) ); //$NON-NLS-1$
    miFileDelete.addListener( SWT.Selection, lsFileDelete );

    new MenuItem( msFile, SWT.SEPARATOR );
    miFilePrint = new MenuItem( msFile, SWT.CASCADE );
    miFilePrint.setText( Messages.getString( "MetaEditor.USER_PRINT" ) ); //$NON-NLS-1$
    miFilePrint.setAccelerator( SWT.MOD1 | 'p' );
    miFilePrint.addListener( SWT.Selection, lsFilePrint );

    new MenuItem( msFile, SWT.SEPARATOR );
    miFileQuit = new MenuItem( msFile, SWT.CASCADE );
    miFileQuit.setText( Messages.getString( "MetaEditor.USER_QUIT" ) ); //$NON-NLS-1$
    miFileQuit.addListener( SWT.Selection, lsFileQuit );

    miFileSep3 = new MenuItem( msFile, SWT.SEPARATOR );
    addMenuLast();

    // main Edit menu...
    mEdit = new MenuItem( mBar, SWT.CASCADE );
    mEdit.setText( Messages.getString( "MetaEditor.USER_EDIT" ) ); //$NON-NLS-1$
    msEdit = new Menu( shell, SWT.DROP_DOWN );
    mEdit.setMenu( msEdit );

    miEditProperties = new MenuItem( msEdit, SWT.CASCADE );
    miEditProperties.setText( Messages.getString( "MetaEditor.USER_EDIT_PROPS" ) ); //$NON-NLS-1$
    miEditProperties.addListener( SWT.Selection, lsEditProperties );
    miEditProperties.setEnabled( false );

    miEditOptions = new MenuItem( msEdit, SWT.CASCADE );
    miEditOptions.setText( Messages.getString( "MetaEditor.USER_EDIT_OPTIONS" ) ); //$NON-NLS-1$
    miEditOptions.addListener( SWT.Selection, lsEditOptions );

    new MenuItem( msEdit, SWT.SEPARATOR );
    miEditUnselectAll = new MenuItem( msEdit, SWT.CASCADE );
    miEditUnselectAll.setText( Messages.getString( "MetaEditor.USER_CLEAR_SELECTION" ) ); //$NON-NLS-1$
    miEditUnselectAll.setAccelerator( SWT.ESC );
    miEditUnselectAll.addListener( SWT.Selection, lsEditUnselectAll );

    miEditSelectAll = new MenuItem( msEdit, SWT.CASCADE );
    miEditSelectAll.setText( Messages.getString( "MetaEditor.USER_SELECT_ALL_STEPS" ) ); //$NON-NLS-1$
    miEditSelectAll.setAccelerator( SWT.MOD1 | 'a' );
    miEditSelectAll.addListener( SWT.Selection, lsEditSelectAll );

    new MenuItem( msEdit, SWT.SEPARATOR );
    miEditRefresh = new MenuItem( msEdit, SWT.CASCADE );
    miEditRefresh.setText( Messages.getString( "MetaEditor.USER_REFRESH" ) ); //$NON-NLS-1$
    miEditRefresh.setAccelerator( SWT.F5 );
    miEditRefresh.addListener( SWT.Selection, lsEditRefresh );

    // Tools
    mTools = new MenuItem( mBar, SWT.CASCADE );
    mTools.setText( Messages.getString( "MetaEditor.USER_TOOLS" ) ); //$NON-NLS-1$
    msTools = new Menu( shell, SWT.DROP_DOWN );
    mTools.setMenu( msTools );

    miSecurityService = new MenuItem( msTools, SWT.CASCADE );
    miSecurityService.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_SECURITY_SERVICE" ) ); //$NON-NLS-1$
    miSecurityService.addListener( SWT.Selection, lsSecurityService );

    new MenuItem( msTools, SWT.SEPARATOR );
    miLocalesEditor = new MenuItem( msTools, SWT.CASCADE );
    miLocalesEditor.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_LOCALES" ) ); //$NON-NLS-1$
    miLocalesEditor.addListener( SWT.Selection, lsEditLocales );

    miConceptEditor = new MenuItem( msTools, SWT.CASCADE );
    miConceptEditor.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_CONCEPTS" ) ); //$NON-NLS-1$
    miConceptEditor.addListener( SWT.Selection, lsEditConcepts );

    miCategoryEditor = new MenuItem( msTools, SWT.CASCADE );
    miCategoryEditor.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_CATEGORYS" ) ); //$NON-NLS-1$
    miCategoryEditor.addListener( SWT.Selection, lsEditCategories );

    new MenuItem( msTools, SWT.SEPARATOR );
    miLogging = new MenuItem( msTools, SWT.CASCADE );
    miLogging.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_LOGGING" ) ); //$NON-NLS-1$

    // new MenuItem(msTools, SWT.SEPARATOR);
    // MenuItem miPopAD = new MenuItem(msTools, SWT.CASCADE);
    //    miPopAD.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_DISTRIBUTE")); //$NON-NLS-1$
    // mPopAD = new Menu(miPopAD);
    //
    // MenuItem miPopALeft = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopALeft.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_LEFT")); //$NON-NLS-1$
    // miPopALeft.addListener(SWT.Selection, lsAlignLeft);
    //
    // MenuItem miPopARight = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopARight.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_RIGHT")); //$NON-NLS-1$
    // miPopARight.addListener(SWT.Selection, lsAlignRight);
    //
    // MenuItem miPopATop = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopATop.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_TOP")); //$NON-NLS-1$
    // miPopATop.addListener(SWT.Selection, lsAlignTop);
    //
    // MenuItem miPopABottom = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopABottom.setText(Messages.getString("MetaEditorGraph.USER_ALIGN_BOTTOM")); //$NON-NLS-1$
    // miPopABottom.addListener(SWT.Selection, lsAlignBottom);
    //
    // new MenuItem(mPopAD, SWT.SEPARATOR);
    // MenuItem miPopDHoriz = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopDHoriz.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_HORIZ")); //$NON-NLS-1$
    // miPopDHoriz.addListener(SWT.Selection, lsDistribHoriz);
    //
    // MenuItem miPopDVertic = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopDVertic.setText(Messages.getString("MetaEditorGraph.USER_DISTRIBUTE_VERT")); //$NON-NLS-1$
    // miPopDVertic.addListener(SWT.Selection, lsDistribVert);
    //
    // new MenuItem(mPopAD, SWT.SEPARATOR);
    // MenuItem miPopSSnap = new MenuItem(mPopAD, SWT.CASCADE);
    //    miPopSSnap.setText(Messages.getString("MetaEditorGraph.USER_SNAP_TO_GRID", Integer.toString(Const
    // .GRID_SIZE))); //$NON-NLS-1$
    // miPopAD.setMenu(mPopAD);
    //
    // miPopSSnap.addSelectionListener(new SelectionAdapter() {
    // public void widgetSelected(SelectionEvent e) {
    // metaEditorGraph.snaptogrid(Const.GRID_SIZE);
    // }
    // });

    miLogging.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        tabfolder.setSelection( 2 );
      }
    } );

    // main Help menu...
    mHelp = new MenuItem( mBar, SWT.CASCADE );
    mHelp.setText( Messages.getString( "MetaEditor.USER_HELP" ) ); //$NON-NLS-1$
    msHelp = new Menu( shell, SWT.DROP_DOWN );

    mHelp.setMenu( msHelp );
    miHelpAbout = new MenuItem( msHelp, SWT.CASCADE );
    miHelpAbout.setText( Messages.getString( "MetaEditor.USER_ABOUT" ) ); //$NON-NLS-1$
    miHelpAbout.addListener( SWT.Selection, lsHelpAbout );
  }

  /**
   * @return
   */
  private Listener getMainListener() {
    if ( mainListener == null ) {
      mainListener = new Listener() {
        public void handleEvent( Event e ) {
          BusinessModel activeModel = schemaMeta.getActiveModel();
          boolean hasActiveModel = false;
          int nrSelected = 0;
          if ( activeModel != null ) {
            hasActiveModel = true;
            nrSelected = activeModel.nrSelected();
          }
          // Enable/disable menus that rely on having an active model
          miNewBTable.setEnabled( hasActiveModel );

          // Enable/disable menus that rely on having more than 1
          // graph item selected
          // mPopAD.setEnabled(nrSelected > 1);
        }
      };
    }
    return mainListener;
  }

  private void addMenuLast() {
    int idx = msFile.indexOf( miFileSep3 );
    int max = msFile.getItemCount();

    // Remove everything until end...
    for ( int i = max - 1; i > idx; i-- ) {
      MenuItem mi = msFile.getItem( i );
      mi.dispose();
    }

    // Previously loaded files...
    String[] lf = props.getLastFiles();

    for ( int i = 0; i < lf.length; i++ ) {
      MenuItem miFileLast = new MenuItem( msFile, SWT.CASCADE );
      char chr = (char) ( '1' + i );
      int accel = SWT.MOD1 | chr;
      miFileLast.setText( "&" + chr + "  " + lf[ i ] + " \tCTRL-" + chr ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      miFileLast.setAccelerator( accel );
      final String fn = lf[ i ];

      Listener lsFileLast = new Listener() {
        public void handleEvent( Event e ) {
          if ( showChangedWarning() ) {
            if ( readData( fn ) ) {
              schemaMeta.clearChanged();
              setDomainName( fn );
              metaEditorGraph.control = false;
            } else {
              MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
              mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_OPENING_DOMAIN", fn ) ); //$NON-NLS-1$
              mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
              mb.open();
            }
          }
        }
      };
      miFileLast.addListener( SWT.Selection, lsFileLast );
    }
  }

  private void initToolBar() {
    // First get the toolbar images
    // Make sure that any images we get are disposed of down below in the
    // DisposeListener
    final Image imFileNew =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "new.png" ) ); //$NON-NLS-1$
    final Image imFileOpen =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "open.png" ) ); //$NON-NLS-1$
    final Image imFileSave =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "save.png" ) ); //$NON-NLS-1$
    final Image imFileSaveAs =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "saveas.png" ) ); //$NON-NLS-1$
    final Image imFilePrint =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "print.png" ) ); //$NON-NLS-1$
    final Image imSQL =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "SQLbutton.png" ) ); //$NON-NLS-1$
    final Image imConceptEdit =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "concept-editor.png" ) ); //$NON-NLS-1$
    final Image imLocaleEdit =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "locale-editor.png" ) ); //$NON-NLS-1$
    final Image imCategoryEdit =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "category-editor.png" ) ); //$NON-NLS-1$
    final Image imPropertyEdit =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "property-editor.png" ) ); //$NON-NLS-1$
    final Image imAlignLeft =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "align-left.png" ) ); //$NON-NLS-1$
    final Image imAlignRight =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "align-right.png" ) ); //$NON-NLS-1$
    final Image imAlignTop =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "align-top.png" ) ); //$NON-NLS-1$
    final Image imAlignBottom =
      new Image( disp, getClass().getResourceAsStream( Const.IMAGE_DIRECTORY + "align-bottom.png" ) ); //$NON-NLS-1$

    // Can't seem to get the transparency correct for this image!
    ImageData idSQL = imSQL.getImageData();
    int sqlPixel = idSQL.palette.getPixel( new RGB( 255, 255, 255 ) );
    idSQL.transparentPixel = sqlPixel;
    final Image imSQL2 = new Image( disp, idSQL );

    tBar = new ToolBar( shell, SWT.HORIZONTAL | SWT.FLAT );
    tBar.addListener( SWT.MouseEnter, getMainListener() );

    final Menu fileMenus = new Menu( shell, SWT.NONE );

    // Add the new file toolbar items dropdowns
    miNewDomainTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewDomainTB.setText( Messages.getString( "MetaEditor.USER_NEW_DOMAIN_MENU" ) ); //$NON-NLS-1$
    miNewDomainTB.addListener( SWT.Selection, lsDomainNew );

    new MenuItem( fileMenus, SWT.SEPARATOR );
    miNewConnectionTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewConnectionTB.setText( Messages.getString( "MetaEditor.USER_NEW_CONNECTION_MENU" ) ); //$NON-NLS-1$
    miNewConnectionTB.addListener( SWT.Selection, lsConnectionNew );

    miNewPTableTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewPTableTB.setText( Messages.getString( "MetaEditor.USER_NEW_PHYSICAL_TABLE_MENU" ) ); //$NON-NLS-1$
    miNewPTableTB.addListener( SWT.Selection, lsPTableNew );

    new MenuItem( fileMenus, SWT.SEPARATOR );
    miNewBTableTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewBTableTB.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_TABLE_MENU" ) ); //$NON-NLS-1$
    miNewBTableTB.addListener( SWT.Selection, lsBTableNew );

    miNewBModelTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewBModelTB.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_MODEL_MENU" ) ); //$NON-NLS-1$
    miNewBModelTB.addListener( SWT.Selection, lsBModelNew );

    miNewRelTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewRelTB.setText( Messages.getString( "MetaEditor.USER_NEW_RELATIONSHIP_MENU" ) ); //$NON-NLS-1$
    miNewRelTB.addListener( SWT.Selection, lsRelationNew );

    miNewCatTB = new MenuItem( fileMenus, SWT.CASCADE );
    miNewCatTB.setText( Messages.getString( "MetaEditor.USER_NEW_CATEGORY_MENU" ) ); //$NON-NLS-1$
    miNewCatTB.addListener( SWT.Selection, lsCategoryNew );

    final ToolItem tiFileNew = new ToolItem( tBar, SWT.DROP_DOWN );

    tiFileNew.setImage( imFileNew );
    tiFileNew.setToolTipText( Messages.getString( "MetaEditor.USER_NEW" ) ); //$NON-NLS-1$
    // Handles creating a drop down on top of the button if the user clicks
    // on the drop down arrow
    tiFileNew.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        if ( e.detail == SWT.ARROW ) {
          ToolItem item = (ToolItem) e.widget;
          Rectangle rect = item.getBounds();
          org.eclipse.swt.graphics.Point p =
            item.getParent().toDisplay( new org.eclipse.swt.graphics.Point( rect.x, rect.y ) );
          fileMenus.setLocation( p.x, p.y + rect.height );
          fileMenus.setVisible( true );
        } else {
          newFile();
        }
      }
    } );

    final ToolItem tiFileOpen = new ToolItem( tBar, SWT.PUSH );
    tiFileOpen.setImage( imFileOpen );
    tiFileOpen.setToolTipText( Messages.getString( "MetaEditor.USER_OPEN_" ) ); //$NON-NLS-1$
    tiFileOpen.addListener( SWT.Selection, lsFileOpen );

    final ToolItem tiFileSave = new ToolItem( tBar, SWT.PUSH );
    tiFileSave.setImage( imFileSave );
    tiFileSave.setToolTipText( Messages.getString( "MetaEditor.USER_SAVE_" ) ); //$NON-NLS-1$
    tiFileSave.addListener( SWT.Selection, lsFileSave );

    final ToolItem tiFileSaveAs = new ToolItem( tBar, SWT.PUSH );
    tiFileSaveAs.setImage( imFileSaveAs );
    tiFileSaveAs.setToolTipText( Messages.getString( "MetaEditor.USER_SAVE_AS_" ) ); //$NON-NLS-1$
    tiFileSaveAs.addListener( SWT.Selection, lsFileSaveAs );

    new ToolItem( tBar, SWT.SEPARATOR );
    final ToolItem tiFilePrint = new ToolItem( tBar, SWT.PUSH );
    tiFilePrint.setImage( imFilePrint );
    tiFilePrint.setToolTipText( Messages.getString( "MetaEditor.USER_PRINT_TEXT" ) ); //$NON-NLS-1$
    tiFilePrint.addListener( SWT.Selection, lsFilePrint );

    new ToolItem( tBar, SWT.SEPARATOR );
    final ToolItem tiSQL = new ToolItem( tBar, SWT.PUSH );
    tiSQL.setImage( imSQL2 );
    tiSQL.setToolTipText( Messages.getString( "MetaEditor.USER_TEST_Q_AND_R" ) ); //$NON-NLS-1$
    tiSQL.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        testQR();
      }
    } );

    final ToolItem tiConceptEdit = new ToolItem( tBar, SWT.PUSH );
    tiConceptEdit.setImage( imConceptEdit );
    tiConceptEdit.setToolTipText( Messages.getString( "MetaEditor.USER_CONCEPT_EDITOR" ) ); //$NON-NLS-1$
    tiConceptEdit.addListener( SWT.Selection, lsEditConcepts );

    final ToolItem tiLocaleEdit = new ToolItem( tBar, SWT.PUSH );
    tiLocaleEdit.setImage( imLocaleEdit );
    tiLocaleEdit.setToolTipText( Messages.getString( "MetaEditor.USER_LOCALE_EDITOR" ) ); //$NON-NLS-1$
    tiLocaleEdit.addListener( SWT.Selection, lsEditLocales );

    final ToolItem tiCategoryEdit = new ToolItem( tBar, SWT.PUSH );
    tiCategoryEdit.setImage( imCategoryEdit );
    tiCategoryEdit.setToolTipText( Messages.getString( "MetaEditor.USER_CATEGORY_EDITOR" ) ); //$NON-NLS-1$
    tiCategoryEdit.addListener( SWT.Selection, lsEditCategories );

    new ToolItem( tBar, SWT.SEPARATOR );
    tiEditProperties = new ToolItem( tBar, SWT.PUSH );
    tiEditProperties.setImage( imPropertyEdit );
    tiEditProperties.setToolTipText( Messages.getString( "MetaEditor.USER_EDIT_PROPERTIES" ) ); //$NON-NLS-1$
    tiEditProperties.addListener( SWT.Selection, lsEditProperties );
    tiEditProperties.setEnabled( false );

    tBar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent e ) {
        imFileNew.dispose();
        imFileOpen.dispose();
        imFileSave.dispose();
        imFileSaveAs.dispose();
        imFilePrint.dispose();
        imSQL.dispose();
        imSQL2.dispose();
        imConceptEdit.dispose();
        imLocaleEdit.dispose();
        imCategoryEdit.dispose();
        imPropertyEdit.dispose();
        imAlignLeft.dispose();
        imAlignRight.dispose();
        imAlignTop.dispose();
        imAlignBottom.dispose();
      }
    } );

    tBar.addKeyListener( defKeys );
    tBar.addKeyListener( modKeys );
    tBar.pack();
    FormData fdBar = new FormData();
    fdBar.left = new FormAttachment( 0, 0 );
    fdBar.top = new FormAttachment( 0, 0 );
    tBar.setLayoutData( fdBar );
  }

  private void initTree() {
    SashForm leftsplit = new SashForm( sashform, SWT.VERTICAL );
    leftsplit.setLayout( new FillLayout() );

    // Main: the top left tree containing connections, physical tables,
    // business models, etc.
    Composite compMain = new Composite( leftsplit, SWT.NONE );
    compMain.setLayout( new FillLayout() );

    // Now set up the main tree (top left part of the screen)
    int treeFlags = SWT.BORDER;
    if ( Const.isOSX() ) {
      treeFlags |= SWT.SINGLE;
    } else {
      treeFlags |= SWT.MULTI;
    }
    treeViewer = new TreeViewer( compMain, treeFlags );
    treeViewer.setContentProvider( new TreeContentProvider() );
    treeViewer.setLabelProvider( new ConceptLabelProvider() );
    mainTreeNode = new SchemaMetaTreeNode( null, schemaMeta );
    mainTreeNode.addTreeNodeChangeListener( (ITreeNodeChangedListener) treeViewer.getContentProvider() );

    treeViewer.getTree().setHeaderVisible( true );

    // Show the concept in an extra column next to the tree
    TreeColumn mainObject = new TreeColumn( treeViewer.getTree(), SWT.LEFT );
    mainObject.setText( "" ); //$NON-NLS-1$
    mainObject.setWidth( 200 );

    TreeColumn mainConcept = new TreeColumn( treeViewer.getTree(), SWT.LEFT );
    mainConcept.setText( Messages.getString( "MetaEditor.USER_PARENT_CONCEPT" ) ); //$NON-NLS-1$
    mainConcept.setWidth( 200 );

    treeViewer.getTree().setBackground( GUIResource.getInstance().getColorBackground() );

    treeViewer.getTree().addSelectionListener( this ); // double click somewhere in the tree...

    addDragSourceToTree( treeViewer.getTree() );
    addDropTargetToTree( treeViewer.getTree() );

    // Add tree memories to the trees.
    TreeMemory.addTreeListener( treeViewer.getTree(), STRING_MAIN_TREE );

    // Keyboard shortcuts!
    treeViewer.getTree().addKeyListener( defKeys );
    treeViewer.getTree().addKeyListener( modKeys );

  }

  private void addDropTargetToTree( final Tree tree ) {
    // Drag & Drop for tables etc.
    Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };
    DropTarget ddTarget = new DropTarget( tree, DND.DROP_MOVE );
    ddTarget.setTransfer( ttypes );
    ddTarget.addDropListener( new DropTargetListener() {
      public void dragEnter( DropTargetEvent event ) {
      }

      public void dragLeave( DropTargetEvent event ) {
      }

      public void dragOperationChanged( DropTargetEvent event ) {
      }

      public void dragOver( DropTargetEvent event ) {
      }

      public void drop( DropTargetEvent event ) {
        BusinessModel activeModel = schemaMeta.getActiveModel();
        String activeLocale = schemaMeta.getActiveLocale();

        // no data to copy, indicate failure in event.detail
        if ( event.data == null || activeModel == null ) {
          event.detail = DND.DROP_NONE;
          return;
        }

        try {
          //
          // Where exactly did we drop in the tree?
          ConceptTreeNode targetNode = (ConceptTreeNode) event.item.getData();
          // We expect a Drag and Drop container... (encased in XML &
          // Base64)
          DragAndDropContainer container = (DragAndDropContainer) event.data;

          // Prevent the user from dropping nodes from a different
          // model
          if ( activeModelTreeNode.findNode( targetNode.getDomainObject() ) == null ) {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_SHARING_ACROSS_MODELS" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
            mb.open();
            return;
          }

          // Prevent the user from dropping a business table or column
          // into
          // any other branch than the business view branch
          boolean isAppropriateForBusinessView =
            ( ( container.getType() == DragAndDropContainer.TYPE_BUSINESS_TABLE ) || ( container.getType()
              == DragAndDropContainer.TYPE_BUSINESS_COLUMN ) );

          if ( isAppropriateForBusinessView
            && ( !( targetNode instanceof CategoryTreeNode ) && !( targetNode instanceof BusinessViewTreeNode )
            && !( targetNode instanceof BusinessViewTreeNode ) ) ) {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_DRAG_TO_VIEW" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
            mb.open();
            return;
          }

          // Prevent the user from dropping a business table or column
          // into
          // any other branch than the business view branch
          boolean isAppropriateForBusinessModel = ( container.getType() == DragAndDropContainer.TYPE_PHYSICAL_TABLE );

          if ( isAppropriateForBusinessModel && !( targetNode instanceof BusinessTablesTreeNode ) ) {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_DRAG_TO_MODEL" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
            mb.open();
            return;
          }

          // Prevent the user from dropping physical columns...
          if ( container.getType() == DragAndDropContainer.TYPE_PHYSICAL_COLUMN ) {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_CANT_DROP_PHYSICAL_COLUMN" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
            mb.open();
            return;
          }

          // Retrieve the category that the drop was aimed at.
          BusinessCategory parentCategory = null;
          if ( targetNode instanceof CategoryTreeNode ) {
            parentCategory = ( (CategoryTreeNode) targetNode ).getCategory();
          } else if ( targetNode instanceof BusinessViewTreeNode ) {
            parentCategory = ( (BusinessViewTreeNode) targetNode ).getRootCategory();
          } else {
            parentCategory = activeModel.getRootCategory();
          }

          // Block sub-categories & columns in the root for now, until
          // Ad-hoc & MDR follow
          //
          if ( ( container.getType() == DragAndDropContainer.TYPE_BUSINESS_TABLE && !parentCategory.isRootCategory() )
            || ( container.getType() == DragAndDropContainer.TYPE_BUSINESS_COLUMN && parentCategory
            .isRootCategory() ) ) {
            MessageBox mb = new MessageBox( shell, SWT.CLOSE | SWT.ICON_INFORMATION );
            mb.setMessage( Messages.getString( "MetaEditor.USER_CATEGORY_COLUMN_SUPPORT" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "MetaEditor.USER_SORRY" ) ); //$NON-NLS-1$
            mb.open();
            return;
          }

          switch ( container.getType() ) {
            // Drag physical table onto metaEditorGraph:
            // 0) Look up the referenced Physical Table name, if it
            // exists continue
            // 1) If there is an active business model use that one, if
            // not ask name, create one, edit it
            // 2) Create the business table based on the physical table,
            // edit
            // 3) Place the business table on the selected coordinates.
            //
            case DragAndDropContainer.TYPE_PHYSICAL_TABLE:
              PhysicalTable physicalTable = getSchemaMeta().findPhysicalTable( container.getData() ); // 0)
              if ( physicalTable != null ) {
                BusinessModel businessModel = getSchemaMeta().getActiveModel();
                if ( businessModel == null ) {
                  businessModel = newBusinessModel(); // 1)
                }

                if ( businessModel != null ) {
                  BusinessTable businessTable = newBusinessTable( physicalTable ); // 2)
                  if ( businessTable != null ) {
                    refreshAll();
                  }
                }
              }
              break;
            //
            // Drag business table in categories: make business table
            // name a new category
            case DragAndDropContainer.TYPE_BUSINESS_TABLE:
              BusinessTable businessTable = activeModel.findBusinessTable( container.getData() );
              if ( businessTable != null ) {
                BusinessCategory businessCategory =
                  businessTable.generateCategory( schemaMeta.getActiveLocale(), activeModel.getRootCategory()
                    .getBusinessCategories() );

                // Add the category to the business model or
                // category
                parentCategory.addBusinessCategory( businessCategory );
                activeModelTreeNode.getBusinessViewRoot().addDomainChild( businessCategory );

                refreshAll();
              }
              break;

            case DragAndDropContainer.TYPE_BUSINESS_COLUMN:
              String columnID = container.getData();
              BusinessColumn businessColumn = activeModel.findBusinessColumn( columnID );
              if ( businessColumn != null ) {

                // Make sure that we are not trying to add a
                // physical table from a
                // different connection than the active model's
                // connection
                if ( !activeModel.verify( businessColumn.getPhysicalColumn() ) ) {
                  MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
                  mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
                  mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_CANNOT_USE_COLUMN", //$NON-NLS-1$
                    businessColumn.getName( schemaMeta.getActiveLocale() ), activeModel.getDisplayName( schemaMeta
                      .getActiveLocale() ), activeModel.getConnection().getName() ) );
                  mb.open();
                  return;
                }

                BusinessColumn existing = activeModel.getRootCategory().findBusinessColumn( columnID ); // search by id
                if ( existing != null && businessColumn.equals( existing ) ) {
                  MessageBox mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_WARNING );
                  mb.setMessage( Messages.getString( "MetaEditor.USER_BUSINESS_COLUMN_EXISTS" ) ); //$NON-NLS-1$
                  mb.setText( Messages.getString( "MetaEditor.USER_WARNING" ) ); //$NON-NLS-1$
                  int answer = mb.open();
                  if ( answer == SWT.NO ) {
                    return;
                  }
                }

                // Add the column to the parentCategory
                parentCategory.addBusinessColumn( businessColumn );
                synchronize( parentCategory );
                refreshAll();
              }
              break;

            //
            // Nothing we can use: give an error!
            //
            default:
              MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
              mb.setMessage( Messages.getString(
                "MetaEditor.USER_CANT_PUT_IN_CATEGORIES_TREE", container.getData().toString() ) ); //$NON-NLS-1$
              mb.setText( Messages.getString( "MetaEditor.USER_SORRY" ) ); //$NON-NLS-1$
              mb.open();
              return;
          }
        } catch ( Exception e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "MetaEditor.USER_TITLE_ERROR_DND" ), Messages.getString( "MetaEditor.USER_ERROR_DND" ),
            e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      public void dropAccept( DropTargetEvent event ) {
      }
    } );

  }

  public void setActiveBusinessModel( SelectionEvent e ) {
    ITreeNode dataNode = (ITreeNode) e.item.getData();
    // Walk up the current node looking for a BusinessModel
    while ( !( dataNode instanceof BusinessModelTreeNode ) && dataNode.getParent() != null ) {
      dataNode = dataNode.getParent();
    }

    if ( dataNode instanceof BusinessModelTreeNode ) {
      setActiveBusinessModel( ( (BusinessModelTreeNode) dataNode ).getBusinessModel() );
      activeModelTreeNode = (BusinessModelTreeNode) dataNode;
    }
  }

  public void setActiveBusinessModel( BusinessModel businessModel ) {
    if ( businessModel != null ) {
      schemaMeta.setActiveModel( businessModel );
      refreshGraph();
      if ( metaEditorOlap != null ) {
        metaEditorOlap.refreshScreen();
      }
    }
  }

  private void addDragSourceToTree( final Tree fTree ) {
    // Drag & Drop for steps

    Transfer[] ttypes = new Transfer[] { XMLTransfer.getInstance() };

    DragSource ddSource = new DragSource( fTree, DND.DROP_MOVE );
    ddSource.setTransfer( ttypes );
    ddSource.addDragListener( new DragSourceListener() {
      public void dragStart( DragSourceEvent event ) {
      }

      public void dragSetData( DragSourceEvent event ) {
        TreeItem[] ti = fTree.getSelection();
        String data = null;
        int type = 0;

        if ( ti.length == 1 ) { // ensure we've only got one thing selected
          ConceptTreeNode node = (ConceptTreeNode) ti[ 0 ].getData();
          data = node.getId();
          type = node.getDragAndDropType();
          if ( type == 0 || Const.isEmpty( data ) ) {
            event.doit = false;
            return; // ignore anything else you drag.
          }

          DragAndDropContainer container = new DragAndDropContainer( type, data );
          event.data = container;
        } else {
          // Nothing got dragged, only can happen on OSX :-)
          event.doit = false;
        }
      }

      public void dragFinished( DragSourceEvent event ) {
      }
    } );

  }

  /**
   * Only one selected item possible
   *
   * @param e
   */
  private void updateMenusAndToolbars( SelectionEvent e ) {
    final TreeItem ti = (TreeItem) e.item;
    final ConceptTreeNode node = (ConceptTreeNode) ti.getData();

    log.logDebug( Messages.getString( "MetaEditor.DEBUG_CLICKED_ON", ti.getText() ) ); //$NON-NLS-1$

    if ( mainMenu == null ) {
      mainMenu = new Menu( shell, SWT.POP_UP );
    } else {
      MenuItem[] items = mainMenu.getItems();
      for ( int i = 0; i < items.length; i++ ) {
        items[ i ].dispose();
      }
    }

    boolean enableProperties =
      ( ( node instanceof BusinessColumnTreeNode ) || ( node instanceof BusinessModelTreeNode )
        || ( node instanceof BusinessTableTreeNode ) || ( node instanceof CategoryTreeNode )
        || ( node instanceof PhysicalColumnTreeNode ) || ( node instanceof DatabaseMetaTreeNode )
        || ( node instanceof PhysicalTableTreeNode ) );
    tiEditProperties.setEnabled( enableProperties );
    miEditProperties.setEnabled( enableProperties );

    if ( node instanceof ConnectionsTreeNode ) {
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_TEXT" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newConnection();
        }
      } );
      MenuItem miCache = new MenuItem( mainMenu, SWT.PUSH );
      miCache.setText( Messages.getString( "MetaEditor.USER_TITLE_CLEAR_CACHE" ) ); //$NON-NLS-1$
      miCache.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          clearDBCache();
        }
      } );

    } else if ( node instanceof BusinessModelsTreeNode ) {

      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_MODEL_TEXT" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newBusinessModel();
        }
      } );

    } else if ( node instanceof DatabaseMetaTreeNode ) { // We clicked on a database node

      final DatabaseMeta databaseMeta = ( (DatabaseMetaTreeNode) node ).getDatabaseMeta();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_TEXT" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newConnection();
        }
      } );
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editConnection( databaseMeta );
          treeViewer.update( node, null );
        }
      } );
      MenuItem miDupe = new MenuItem( mainMenu, SWT.PUSH );
      miDupe.setText( Messages.getString( "MetaEditor.USER_DUPLICATE_TEXT" ) ); //$NON-NLS-1$
      miDupe.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          dupeConnection( databaseMeta );
        }
      } );
      MenuItem miDel = new MenuItem( mainMenu, SWT.PUSH );
      miDel.setText( Messages.getString( "MetaEditor.USER_DELETE_TEXT" ) ); //$NON-NLS-1$
      miDel.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          delConnection( databaseMeta );
        }
      } );
      new MenuItem( mainMenu, SWT.SEPARATOR );
      MenuItem miMImp = new MenuItem( mainMenu, SWT.PUSH );
      miMImp.setText( Messages.getString( "MetaEditor.USER_IMPORT_MULTIPLE_TABLES" ) ); //$NON-NLS-1$
      miMImp.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          importMultipleTables( databaseMeta );
          node.sync();
        }
      } );
      MenuItem miMImpExpl = new MenuItem( mainMenu, SWT.PUSH );
      miMImpExpl.setText( Messages.getString( "MetaEditor.USER_IMPORT_FROM_EXPLORER" ) ); //$NON-NLS-1$
      miMImpExpl.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          importTables( databaseMeta );
          node.sync();
        }
      } );
      new MenuItem( mainMenu, SWT.SEPARATOR );
      MenuItem miCache = new MenuItem( mainMenu, SWT.PUSH );
      miCache.setText( Messages.getString( "MetaEditor.USER_CLEAR_DB_CACHE", ti.getText() ) ); //$NON-NLS-1$
      miCache.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          clearDBCache();
        }
      } );
      new MenuItem( mainMenu, SWT.SEPARATOR );
      MenuItem miSQL = new MenuItem( mainMenu, SWT.PUSH );
      miSQL.setText( Messages.getString( "MetaEditor.USER_SQL_EDITOR" ) ); //$NON-NLS-1$
      miSQL.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          sqlSelected( databaseMeta );
        }
      } );
      MenuItem miExpl = new MenuItem( mainMenu, SWT.PUSH );
      miExpl.setText( Messages.getString( "MetaEditor.USER_EXPLORE" ) ); //$NON-NLS-1$
      miExpl.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          exploreDB();
        }
      } );

    } else if ( node instanceof PhysicalTableTreeNode ) { // We clicked on a physical table

      final PhysicalTable physicalTable = (PhysicalTable) ( (PhysicalTableTreeNode) node ).getDomainObject();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_PHYSICAL_TABLETEXT" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          importTables( physicalTable.getDatabaseMeta() );
          ( (ConceptTreeNode) node.getParent() ).sync();
        }
      } );
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editPhysicalTable( physicalTable );
          treeViewer.update( node, null );
        }
      } );
      MenuItem miDel = new MenuItem( mainMenu, SWT.PUSH );
      miDel.setText( Messages.getString( "MetaEditor.USER_DELETE_TEXT" ) ); //$NON-NLS-1$
      miDel.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          delPhysicalTable( physicalTable );
        }
      } );
    } else if ( node instanceof PhysicalColumnTreeNode ) {
      final PhysicalColumn physicalColumn = (PhysicalColumn) ( (PhysicalColumnTreeNode) node ).getDomainObject();
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editPhysicalColumn( physicalColumn );
        }
      } );

    } else if ( node instanceof BusinessModelTreeNode ) {
      final BusinessModelsTreeNode parentNode = (BusinessModelsTreeNode) node.getParent();
      final BusinessModelTreeNode treeNode = (BusinessModelTreeNode) node;
      final BusinessModel businessModel = ( (BusinessModelTreeNode) node ).getBusinessModel();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_MODEL_INSTANCE" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newBusinessModel();
        }
      } );
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editBusinessModel( businessModel, node );
        }
      } );

      new MenuItem( mainMenu, SWT.SEPARATOR );

      MenuItem miUp = new MenuItem( mainMenu, SWT.PUSH );
      miUp.setText( Messages.getString( "MetaEditor.USER_MOVE_UP" ) ); //$NON-NLS-1$
      miUp.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          parentNode.moveChildUp( treeNode );
        }
      } );

      MenuItem miDown = new MenuItem( mainMenu, SWT.PUSH );
      miDown.setText( Messages.getString( "MetaEditor.USER_MOVE_DOWN" ) ); //$NON-NLS-1$
      miDown.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          parentNode.moveChildDown( treeNode );
        }
      } );

      new MenuItem( mainMenu, SWT.SEPARATOR );

      MenuItem miDelete = new MenuItem( mainMenu, SWT.PUSH );
      miDelete.setText( Messages.getString( "MetaEditor.USER_DELETE_TEXT" ) ); //$NON-NLS-1$
      miDelete.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          deleteBusinessModel( businessModel );
        }
      } );
    } else if ( node instanceof BusinessTablesTreeNode ) {
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_TABLE" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newBusinessTable( null );
        }
      } );
    } else if ( node instanceof RelationshipsTreeNode ) {
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_RELATIONSHIP" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newRelationship();
        }
      } );
    } else if ( node instanceof BusinessViewTreeNode ) {
      final BusinessCategory businessCategory = ( (BusinessViewTreeNode) node ).getRootCategory();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_CATEGORY" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          newBusinessCategory( businessCategory );
        }
      } );

      MenuItem miCategoryEditor = new MenuItem( mainMenu, SWT.PUSH );
      miCategoryEditor.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_CATEGORYS" ) ); //$NON-NLS-1$
      miCategoryEditor.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          editBusinessCategories();
        }
      } );
    } else if ( node instanceof BusinessTableTreeNode ) {
      final BusinessTable businessTable = (BusinessTable) ( (BusinessTableTreeNode) node ).getDomainObject();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_BUSINESS_TABLE" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newBusinessTable( null );
        }
      } );
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editBusinessTable( businessTable, node );
        }
      } );
      MenuItem miDuplicate = new MenuItem( mainMenu, SWT.PUSH );
      miDuplicate.setText( Messages.getString( "MetaEditor.USER_DUPLICATE" ) ); //$NON-NLS-1$
      miDuplicate.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          dupeBusinessTable( businessTable );
        }
      } );
      MenuItem miDel = new MenuItem( mainMenu, SWT.PUSH );
      miDel.setText( Messages.getString( "MetaEditor.USER_DELETE_TEXT" ) ); //$NON-NLS-1$
      miDel.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          delBusinessTable( businessTable );
        }
      } );

    } else if ( node instanceof BusinessColumnTreeNode ) {

      final BusinessColumn businessColumn = (BusinessColumn) ( (BusinessColumnTreeNode) node ).getDomainObject();
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editBusinessColumn( businessColumn, (BusinessColumnTreeNode) node );
          treeViewer.update( node.getParent(), null );
          treeViewer.update( node, null );
        }
      } );

      if ( node.getParent() instanceof CategoryTreeNode ) {
        final BusinessColumnTreeNode currentTreeNode = (BusinessColumnTreeNode) node;
        final CategoryTreeNode parentTreeNode = (CategoryTreeNode) node.getParent();

        new MenuItem( mainMenu, SWT.SEPARATOR );

        MenuItem miUp = new MenuItem( mainMenu, SWT.PUSH );
        miUp.setText( Messages.getString( "MetaEditor.USER_MOVE_UP" ) ); //$NON-NLS-1$
        miUp.addListener( SWT.Selection, new Listener() {
          public void handleEvent( Event ev ) {
            parentTreeNode.moveChildUp( currentTreeNode );
          }
        } );

        MenuItem miDown = new MenuItem( mainMenu, SWT.PUSH );
        miDown.setText( Messages.getString( "MetaEditor.USER_MOVE_DOWN" ) ); //$NON-NLS-1$
        miDown.addListener( SWT.Selection, new Listener() {
          public void handleEvent( Event ev ) {
            parentTreeNode.moveChildDown( currentTreeNode );
          }
        } );
      }

    } else if ( node instanceof RelationshipTreeNode ) {
      final RelationshipMeta relationshipMeta = (RelationshipMeta) ( (RelationshipTreeNode) node ).getDomainObject();
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_RELATIONSHIP" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          newRelationship();

        }
      } );
      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          editRelationship( relationshipMeta );
          treeViewer.update( node, null );
        }
      } );
      MenuItem miDel = new MenuItem( mainMenu, SWT.PUSH );
      miDel.setText( Messages.getString( "MetaEditor.USER_DELETE_TEXT" ) ); //$NON-NLS-1$
      miDel.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          delRelationship( relationshipMeta );
        }
      } );

    } else if ( node instanceof CategoryTreeNode ) {

      final ConceptTreeNode treeNode = (CategoryTreeNode) node;
      final ConceptTreeNode conceptParentNode = (ConceptTreeNode) node.getParent();

      final BusinessModel activeModel = schemaMeta.getActiveModel();
      if ( activeModel == null ) {
        return;
      }

      Object parentNode = node.getParent();
      final BusinessCategory currentCategory = ( (CategoryTreeNode) node ).getCategory();
      BusinessCategory tmpCategory = null;
      if ( parentNode instanceof CategoryTreeNode ) {
        tmpCategory = ( (CategoryTreeNode) parentNode ).getCategory();
      } else {
        tmpCategory = ( (BusinessViewTreeNode) parentNode ).getRootCategory();
      }
      final BusinessCategory parentCategory = tmpCategory;

      // Get the actual parent and current category
      MenuItem miNew = new MenuItem( mainMenu, SWT.PUSH );
      miNew.setText( Messages.getString( "MetaEditor.USER_NEW_CATEGORY" ) ); //$NON-NLS-1$
      miNew.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          newBusinessCategory( currentCategory );
        }
      } );

      MenuItem miCategoryEditor = new MenuItem( mainMenu, SWT.PUSH );
      miCategoryEditor.setText( Messages.getString( "MetaEditor.USER_CONFIGURE_CATEGORYS" ) ); //$NON-NLS-1$
      miCategoryEditor.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          editBusinessCategories();
        }
      } );

      MenuItem miEdit = new MenuItem( mainMenu, SWT.PUSH );
      miEdit.setText( Messages.getString( "MetaEditor.USER_EDIT_TEXT" ) ); //$NON-NLS-1$
      miEdit.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          editBusinessCategory( currentCategory, node );
          treeViewer.update( node, null );
        }
      } );

      MenuItem miDelete = new MenuItem( mainMenu, SWT.PUSH );
      miDelete.setText( Messages.getString( "MetaEditor.USER_REMOVE_CATEGORY" ) ); //$NON-NLS-1$
      miDelete.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          delBusinessCategory( parentCategory, currentCategory );
        }
      } );

      new MenuItem( mainMenu, SWT.SEPARATOR );

      MenuItem miUp = new MenuItem( mainMenu, SWT.PUSH );
      miUp.setText( Messages.getString( "MetaEditor.USER_MOVE_UP" ) ); //$NON-NLS-1$
      miUp.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          conceptParentNode.moveChildUp( treeNode );
        }
      } );

      MenuItem miDown = new MenuItem( mainMenu, SWT.PUSH );
      miDown.setText( Messages.getString( "MetaEditor.USER_MOVE_DOWN" ) ); //$NON-NLS-1$
      miDown.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event ev ) {
          conceptParentNode.moveChildDown( treeNode );
        }
      } );
    }

    final ConceptUtilityInterface[] utilityInterfaces = getSelectedConceptUtilityInterfacesInMainTree();
    if ( utilityInterfaces.length > 0 ) {
      if ( mainMenu.getItemCount() > 0 ) {
        new MenuItem( mainMenu, SWT.SEPARATOR );
      }

      MenuItem miSetConcept = new MenuItem( mainMenu, SWT.PUSH );
      miSetConcept.setText( Messages.getString( "MetaEditor.USER_SET_PARENT_CONCEPT" ) ); //$NON-NLS-1$
      miSetConcept.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          setParentConcept( utilityInterfaces );
          treeViewer.refresh( mainTreeNode );
        }
      } );

      MenuItem miClearConcept = new MenuItem( mainMenu, SWT.PUSH );
      miClearConcept.setText( Messages.getString( "MetaEditor.USER_CLEAR_PARENT_CONCEPT" ) ); //$NON-NLS-1$
      miClearConcept.addListener( SWT.Selection, new Listener() {
        public void handleEvent( Event evt ) {
          clearParentConcept( utilityInterfaces );
          treeViewer.refresh( mainTreeNode );
        }
      } );

      // MenuItem miRemoveProperty = new MenuItem(mainMenu, SWT.PUSH);
      // miRemoveProperty.setText(Messages.getString("MetaEditor.USER_REMOVE_CHILD_PROPERTIES"));
      // //$NON-NLS-1$
      // miRemoveProperty.addListener(SWT.Selection, new Listener() {
      // public void handleEvent(Event evt) {
      // removeChildProperties(utilityInterfaces);
      // treeViewer.refresh(mainTreeNode);
      // }
      // });

    }

    treeViewer.getTree().setMenu( mainMenu );
  }

  /**
   * Add a new business category to the specified parent.
   */
  public void newBusinessCategory( BusinessCategory parentCategory ) {

    if ( ( !parentCategory.isRootCategory() && ( schemaMeta.getActiveModel() != null ) ) ) {
      parentCategory = schemaMeta.getActiveModel().getRootCategory();
    }
    // Block for now, until Ad-hoc & MDR follow

    BusinessCategory businessCategory = new BusinessCategory();
    businessCategory.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( parentCategory
      .getBusinessCategories() ) );

    while ( true ) {
      BusinessCategoryDialog dialog = new BusinessCategoryDialog( shell, businessCategory, schemaMeta );
      if ( dialog.open() == Window.OK ) {
        // Add this to the parent.
        try {
          parentCategory.addBusinessCategory( businessCategory );
          if ( activeModelTreeNode != null ) {
            activeModelTreeNode.getBusinessViewRoot().addDomainChild( businessCategory );
          }
          break;
        } catch ( ObjectAlreadyExistsException e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_ERROR_BUSINESS_CATEGORY_EXISTS", businessCategory.getId() ),
            e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      } else {
        break;
      }
    }
  }

  public void delBusinessCategory( BusinessCategory parentCategory, BusinessCategory businessCategory ) {
    int index = parentCategory.indexOfBusinessCategory( businessCategory );
    if ( index >= 0 ) {
      parentCategory.removeBusinessCategory( index );
      if ( activeModelTreeNode != null ) {
        activeModelTreeNode.getBusinessViewRoot().removeDomainChild( businessCategory );
      }
    }
  }

  public void editBusinessCategory( BusinessCategory businessCategory, ConceptTreeNode node ) {
    if ( businessCategory != null ) {
      BusinessCategory newBusCategory = (BusinessCategory) businessCategory.clone();
      BusinessCategoryDialog dialog = new BusinessCategoryDialog( shell, newBusCategory, schemaMeta );
      int res = dialog.open();

      if ( Window.OK == res ) {

        // Clear the properties
        businessCategory.getConcept().clearChildProperties();

        // Copy concept changes
        businessCategory.getConcept().getChildPropertyInterfaces().putAll(
          newBusCategory.getConcept().getChildPropertyInterfaces() );

        try {
          businessCategory.setId( newBusCategory.getId() );
        } catch ( ObjectAlreadyExistsException e ) {
          MessageDialog.openError( this.shell, Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString(
            "The id '{0}' is already in use.", newBusCategory.getId() ) );
        }
      }

      if ( node != null ) {
        node.sync();
      } else {
        synchronize( businessCategory );
      }
      refreshAll();
    }
    // BusinessCategoryDialog dialog = new BusinessCategoryDialog(shell,
    // businessCategory, schemaMeta.getLocales(),
    // schemaMeta.getSecurityReference());
    // if (dialog.open() != null) {
    // // refresh it all...
    // refreshAll();
    // }

  }

  public void editBusinessCategories() {
    BusinessModel activeModel = schemaMeta.getActiveModel();

    if ( activeModel != null ) {
      CategoryEditorDialog dialog = new CategoryEditorDialog( shell, activeModel, schemaMeta );
      dialog.open();
      if ( activeModelTreeNode != null ) {
        activeModelTreeNode.getBusinessViewRoot().prune();
      }
    }
  }

  public BusinessTable newBusinessTable( PhysicalTable physicalTable ) {

    String activeLocale = schemaMeta.getActiveLocale();
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if ( activeModel == null ) {
      return null;
    }

    if ( physicalTable == null ) {
      ListSelectionDialog comboDialog =
        new ListSelectionDialog(
          shell,
          Messages.getString( "MetaEditor.USER_SELECT_PHYSICAL_TABLE_MESSAGE" ),
          Messages.getString( "MetaEditor.USER_TITLE_SELECT_PHYSICAL_TABLE" ), //$NON-NLS-1$ //$NON-NLS-2$
          schemaMeta.getTables().toArray() );
      comboDialog.open();
      physicalTable = (PhysicalTable) comboDialog.getSelection();
      if ( physicalTable == null ) {
        return null;
      }
    }

    // Make sure that we are not trying to add a physical table from a
    // different connection than the active model's connection
    if ( physicalTable != null ) {
      if ( !activeModel.verify( physicalTable ) ) {
        MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
        mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
        mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_CANNOT_USE_TABLE", //$NON-NLS-1$
          physicalTable.getName( schemaMeta.getActiveLocale() ), activeModel.getDisplayName( schemaMeta
            .getActiveLocale() ), activeModel.getConnection().getName() ) );
        mb.open();

        return null;
      }
    }

    // Create a business table with a new ID and localized name
    BusinessTable businessTable = new BusinessTable( null, physicalTable );

    // copy all localized names from physical table to new business table
    Locales locales = schemaMeta.getLocales();
    Iterator locIter = locales.getLocaleList().iterator();
    while ( locIter.hasNext() ) {
      LocaleInterface loc = (LocaleInterface) locIter.next();
      String tableName = ""; //$NON-NLS-1$
      if ( physicalTable != null ) {
        tableName = physicalTable.getDisplayName( loc.getCode() );
      }
      businessTable.getConcept().setName( loc.getCode(), tableName );
    }

    try {
      businessTable.setId( BusinessTable.proposeId( activeLocale, businessTable, physicalTable, activeModel
        .getBusinessTables() ) );
    } catch ( ObjectAlreadyExistsException e1 ) {
      // No listeners yet, nothing to catch
    }

    // Add a unique ID enforcer...
    businessTable.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( activeModel.getBusinessTables() ) );

    // Add columns to this if we have a physical table to import from...
    if ( physicalTable != null ) {
      // copy the physical columns to the business columns...
      for ( int i = 0; i < physicalTable.nrPhysicalColumns(); i++ ) {
        PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn( i );
        BusinessColumn businessColumn = new BusinessColumn( physicalColumn.getId(), physicalColumn, businessTable );

        // Add a unique ID enforcer...
        businessColumn.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( activeModel
          .getAllBusinessColumns() ) );

        // We're done, add the business column.
        try {
          // Propose a new ID
          businessColumn.setId( BusinessColumn.proposeId( activeLocale, businessTable, physicalColumn, activeModel
            .getAllBusinessColumns() ) );
          businessTable.addBusinessColumn( businessColumn );
        } catch ( ObjectAlreadyExistsException e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_ERROR_BUSINESS_COLUMN_EXISTS", businessColumn.getId() ),
            e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }

    if ( businessTable != null ) {
      BusinessTableDialog td = new BusinessTableDialog( shell, businessTable, schemaMeta );
      int res = td.open();

      if ( Window.OK == res ) {

        try {
          activeModel.addBusinessTable( businessTable );
          if ( activeModelTreeNode != null ) {
            activeModelTreeNode.getBusinessTablesRoot().addDomainChild( businessTable );
          }
          refreshGraph();
          return businessTable;
        } catch ( ObjectAlreadyExistsException e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_ERROR_BUSINESS_TABLE_EXISTS", businessTable.getId() ),
            e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
    return null;

  }

  public void delBusinessTable( BusinessTable businessTable ) {
    if ( businessTable != null ) {
      BusinessModel activeModel = schemaMeta.getActiveModel();
      if ( activeModel == null ) {
        return;
      }
      // First delete the relationships it uses.
      RelationshipMeta[] relationships = activeModel.findRelationshipsUsing( businessTable );
      for ( int i = 0; i < relationships.length; i++ ) {
        int idx = activeModel.indexOfRelationship( relationships[ i ] );
        if ( idx >= 0 ) {
          activeModel.removeRelationship( idx );
          if ( activeModelTreeNode != null ) {
            activeModelTreeNode.getRelationshipsRoot().removeDomainChild( relationships[ i ] );
          }
        }
      }

      int idx = activeModel.indexOfBusinessTable( businessTable );
      activeModel.removeBusinessTable( idx );
      if ( activeModelTreeNode != null ) {
        activeModelTreeNode.getBusinessTablesRoot().removeDomainChild( businessTable );
      }
      // call refresh all to refresh the rest of the UI - does not refresh
      // the tree
      refreshAll();
    }
  }

  private void initTabs() {
    Composite child = new Composite( sashform, SWT.BORDER );
    child.setLayout( new FillLayout() );

    tabfolder = new CTabFolder( child, SWT.BORDER );
    tabfolder.setSimple( false );

    CTabItem tiTabsGraph = new CTabItem( tabfolder, SWT.NONE );
    tiTabsGraph.setText( Messages.getString( "MetaEditor.USER_GRAPHICAL_VIEW" ) ); //$NON-NLS-1$
    tiTabsGraph.setToolTipText( Messages.getString( "MetaEditor.USER_GRAPHICAL_VIEW_TEXT" ) ); //$NON-NLS-1$

    // CTabItem tiTabsConcept = new CTabItem(tabfolder, SWT.NULL);
    // tiTabsConcept.setText(Messages.getString("MetaEditor.USER_CONCEPTS"));
    // //$NON-NLS-1$
    // tiTabsConcept.setToolTipText(Messages.getString("MetaEditor.USER_CONCEPTS_TEXT"));
    // //$NON-NLS-1$

    CTabItem tiTabsLocale = new CTabItem( tabfolder, SWT.NULL );
    tiTabsLocale.setText( Messages.getString( "MetaEditor.USER_LOCALES" ) ); //$NON-NLS-1$
    tiTabsLocale.setToolTipText( Messages.getString( "MetaEditor.USER_LOCALES_TEXT" ) ); //$NON-NLS-1$

    CTabItem tiTabsLog = new CTabItem( tabfolder, SWT.NULL );
    tiTabsLog.setText( Messages.getString( "MetaEditor.USER_LOG_VIEW" ) ); //$NON-NLS-1$
    tiTabsLog.setToolTipText( Messages.getString( "MetaEditor.USER_LOG_VIEW_TEXT" ) ); //$NON-NLS-1$

    metaEditorGraph = new MetaEditorGraph( tabfolder, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND, this );
    metaEditorGraph.addListener( SWT.MouseExit, getMainListener() );
    // metaEditorConcept = new MetaEditorConcepts(tabfolder, SWT.NONE,
    // this);
    metaEditorLocales = new MetaEditorLocales( tabfolder, SWT.NONE, this );
    metaEditorLog = new MetaEditorLog( tabfolder, SWT.NONE, null );

    tiTabsGraph.setControl( metaEditorGraph );
    // tiTabsConcept.setControl(metaEditorConcept);
    tiTabsLocale.setControl( metaEditorLocales );
    tiTabsLog.setControl( metaEditorLog );

    // toggleOlapTab();

    tabfolder.setSelection( 0 );

    sashform.addKeyListener( defKeys );
    sashform.addKeyListener( modKeys );
  }

  private void toggleOlapTab() {
    if ( metaEditorOlap == null ) {
      tiTabsOlap = new CTabItem( tabfolder, SWT.NULL );
      tiTabsOlap.setText( Messages.getString( "MetaEditor.USER_OLAP" ) ); //$NON-NLS-1$
      tiTabsOlap.setToolTipText( Messages.getString( "MetaEditor.USER_OLAP_TEXT" ) ); //$NON-NLS-1$
      metaEditorOlap = new MetaEditorOlap( tabfolder, SWT.NONE, this );
      tiTabsOlap.setControl( metaEditorOlap );
    } else {
      tiTabsOlap.setControl( null );
      tiTabsOlap.dispose();
      metaEditorOlap.dispose();
      metaEditorOlap = null;
    }
  }

  private boolean readData( String domainName ) {
    try {
      props.addLastFile( LastUsedFile.FILE_TYPE_SCHEMA, domainName, "", false, "" ); //$NON-NLS-1$ //$NON-NLS-2$
      saveSettings();
      addMenuLast();

      // Get a new cwm instance for the selected model...
      if ( cwm != null ) {
        cwm.removeFromList();
      }
      cwm = CWM.getInstance( domainName );

      // Read some data from the domain...
      schemaMeta = cwmSchemaFactory.getSchemaMeta( cwm );

      refreshTree();
      refreshAll();
      return true;
    } catch ( Exception e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "MetaEditor.USER_TITLE_ERROR_READING_DOMAIN" ),
        Messages.getString( "MetaEditor.USER_ERROR_READING_DOMAIN" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }
  }

  /*
   * public void newSelected() { BusinessModel activeModel = schemaMeta.getActiveModel(); if (activeModel == null)
   * return;
   * 
   * log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_NEW_SELECTED")); //$NON-NLS-1$ // Determine
   * what menu we selected from...
   * 
   * TreeItem ti[] = treeViewer.getTree().getSelection(); // Then call newConnection or newTrans if (ti.length >= 1) {
   * String name = ti[0].getText(); TreeItem parent = ti[0].getParentItem(); if (parent == null) {
   * log.logDebug(APPLICATION_NAME, Messages.getString("MetaEditor.DEBUG_ELEMENT_HAS_NO_PARENT")); //$NON-NLS-1$ if
   * (name.equalsIgnoreCase(STRING_CONNECTIONS)) newConnection(); if (name.equalsIgnoreCase(STRING_RELATIONSHIPS))
   * newRelationship(); if (name.equalsIgnoreCase(STRING_BUSINESS_TABLES)) { MessageBox mb = new MessageBox(shell,
   * SWT.OK | SWT.ICON_INFORMATION); mb.setMessage(Messages.getString("MetaEditor.USER_IMPORT_TABLES_VIA_CONNECTIONS"));
   * //$NON-NLS-1$ mb.setText(Messages.getString("MetaEditor.USER_TITLE_IMPORT_TABLES")); //$NON-NLS-1$ mb.open(); } }
   * else { String section = parent.getText(); log.logDebug(APPLICATION_NAME,
   * Messages.getString("MetaEditor.DEBUG_ELEMENT_HAS_PARENT", section)); //$NON-NLS-1$ if
   * (section.equalsIgnoreCase(STRING_CONNECTIONS)) newConnection(); } } }
   */

  public void doubleClickedMain() {
    // Determine what tree-item we selected from...

    TreeItem[] ti = treeViewer.getTree().getSelection();
    if ( ti.length == 1 ) { // ensure we've only got one thing selected
      ConceptTreeNode node = (ConceptTreeNode) ti[ 0 ].getData();
      final String itemText = ti[ 0 ].getText();
      if ( node instanceof LabelTreeNode ) { // We clicked on one of the labels... not an actual object
        if ( itemText.equals( STRING_CONNECTIONS ) ) {
          newConnection();
        } else if ( itemText.equals( STRING_BUSINESS_MODELS ) ) {
          newBusinessModel();
        }
      } else if ( node instanceof DatabaseMetaTreeNode ) {
        DatabaseMeta databaseMeta = ( (DatabaseMetaTreeNode) node ).getDatabaseMeta();
        editConnection( databaseMeta );
      } else if ( node instanceof PhysicalTableTreeNode ) {
        PhysicalTable physicalTable = (PhysicalTable) ( (PhysicalTableTreeNode) node ).getDomainObject();
        editPhysicalTable( physicalTable );
      } else if ( node instanceof PhysicalColumnTreeNode ) {
        PhysicalColumn physicalColumn = (PhysicalColumn) ( (PhysicalColumnTreeNode) node ).getDomainObject();
        editPhysicalColumn( physicalColumn );
      } else if ( node instanceof BusinessModelTreeNode ) {
        BusinessModel businessModel = ( (BusinessModelTreeNode) node ).getBusinessModel();
        editBusinessModel( businessModel, node );
      } else if ( node instanceof BusinessTablesTreeNode ) {
        newBusinessTable( null );
      } else if ( node instanceof RelationshipsTreeNode ) {
        newRelationship();
      } else if ( node instanceof BusinessTableTreeNode ) {
        BusinessTable businessTable = (BusinessTable) ( (BusinessTableTreeNode) node ).getDomainObject();
        editBusinessTable( businessTable, node );
      } else if ( node instanceof RelationshipTreeNode ) {
        RelationshipMeta relationship = (RelationshipMeta) ( (RelationshipTreeNode) node ).getDomainObject();
        editRelationship( relationship );
      } else if ( node instanceof BusinessColumnTreeNode ) {
        BusinessColumn businessColumn = ( (BusinessColumnTreeNode) node ).getBusinessColumn();
        editBusinessColumn( businessColumn, (BusinessColumnTreeNode) node );
      } else if ( node instanceof CategoryTreeNode ) {
        BusinessCategory businessCategory = ( (CategoryTreeNode) node ).getCategory();
        if ( businessCategory.isRootCategory() ) {
          editBusinessCategories();
        } else {
          editBusinessCategory( businessCategory, node );
        }
      } else if ( node instanceof BusinessViewTreeNode ) {
        BusinessCategory businessCategory = ( (BusinessViewTreeNode) node ).getRootCategory();
        if ( businessCategory.isRootCategory() ) {
          editBusinessCategories();
        } else {
          editBusinessCategory( businessCategory, node );
        }
      }
      treeViewer.update( node, null );
    }
  }

  public BusinessModel newBusinessModel() {
    String id = null;
    // returns valid id, and semi-random number used for id generation
    // ...mimics old behavior as closely as possible.
    String[] ids = generateBusinessModelId();

    BusinessModel businessModel = new BusinessModel( ids[ 0 ] );
    businessModel.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( schemaMeta.getBusinessModels() ) );
    businessModel.setName( schemaMeta.getActiveLocale(), "Model " + ids[ 1 ] ); //$NON-NLS-1$

    BusinessModel newBusModel = (BusinessModel) businessModel.clone();
    BusinessModelDialog dialog = new BusinessModelDialog( shell, SWT.NONE, newBusModel, schemaMeta );
    int res = dialog.open();

    if ( Window.OK == res ) {

      // Clear the properties
      businessModel.getConcept().clearChildProperties();

      // Copy concept changes
      businessModel.getConcept().getChildPropertyInterfaces().putAll(
        newBusModel.getConcept().getChildPropertyInterfaces() );

      try {
        businessModel.setId( newBusModel.getId() );
        businessModel.setConnection( newBusModel.getConnection() );
        schemaMeta.addModel( businessModel );
        mainTreeNode.getBusinessModelsRoot().addDomainChild( businessModel );
        schemaMeta.setActiveModel( businessModel );
        activeModelTreeNode = (BusinessModelTreeNode) mainTreeNode.getBusinessModelsRoot().findNode( businessModel );
        refreshAll();

        return businessModel;
      } catch ( ObjectAlreadyExistsException e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "General.USER_TITLE_ERROR" ),
          Messages.getString( "MetaEditor.USER_ERROR_BUSINESS_MODEL_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    return null;
  }

  public void editBusinessModel( BusinessModel businessModel ) {
    editBusinessModel( businessModel, null );
  }

  public void editBusinessModel( BusinessModel businessModel, ConceptTreeNode node ) {
    if ( businessModel != null ) {
      BusinessModel newBusModel = (BusinessModel) businessModel.clone();
      BusinessModelDialog dialog = new BusinessModelDialog( shell, SWT.NONE, newBusModel, schemaMeta );
      int res = dialog.open();

      if ( Window.OK == res ) {

        // Clear the properties
        businessModel.getConcept().clearChildProperties();

        // Copy concept changes
        businessModel.getConcept().getChildPropertyInterfaces().putAll(
          newBusModel.getConcept().getChildPropertyInterfaces() );

        try {
          businessModel.setId( newBusModel.getId() );
        } catch ( ObjectAlreadyExistsException e ) {
          MessageDialog
            .openError( this.shell, Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString( //$NON-NLS-1$
              "The id '{0}' is already in use.", newBusModel.getId() ) );
        }

        businessModel.setConnection( newBusModel.getConnection() );

      }

      if ( node != null ) {
        node.sync();
      } else {
        synchronize( businessModel );
      }
      refreshAll();
    }
  }

  public void deleteBusinessModel( BusinessModel businessModel ) {
    if ( businessModel != null ) {
      MessageBox box = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION );
      box.setText( Messages.getString( "General.USER_TITLE_WARNING" ) ); //$NON-NLS-1$
      box.setMessage( Messages.getString(
        "MetaEditor.USER_DELETE_BUSINESS_MODEL",
        businessModel.getDisplayName( schemaMeta.getActiveLocale() ) ) ); //$NON-NLS-1$
      int answer = box.open();
      if ( answer == SWT.YES ) {
        schemaMeta.removeBusinessModel( businessModel );
        schemaMeta.setActiveModel( null );
        mainTreeNode.getBusinessModelsRoot().removeDomainChild( businessModel );
        refreshAll();
      }
    }
  }

  public void sqlSelected( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      SQLEditor sql = new SQLEditor( shell, SWT.NONE, databaseMeta, DBCache.getInstance(), "" ); //$NON-NLS-1$
      sql.open();
    }
  }

  public void editConnection( DatabaseMeta db ) {
    if ( db != null ) {
      DatabaseDialog con = new DatabaseDialog( shell, db );
      con.open();
    }
    setShellText();
  }

  public void dupeConnection( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      try {
        int pos = schemaMeta.indexOfDatabase( databaseMeta );
        DatabaseMeta newdb = (DatabaseMeta) databaseMeta.clone();
        String dupename = Messages.getString( "MetaEditor.USER_COPY_OF", databaseMeta.getName() ); //$NON-NLS-1$
        newdb.setName( dupename );
        schemaMeta.addDatabase( pos + 1, newdb );

        DatabaseDialog con = new DatabaseDialog( shell, newdb );
        String newname = con.open();
        if ( newname != null ) { // null: CANCEL
          schemaMeta.removeDatabaseMeta( pos + 1 );
          schemaMeta.addDatabase( pos + 1, newdb );
        }
        mainTreeNode.getConnectionsRoot().addDomainChild( newdb );
      } catch ( ObjectAlreadyExistsException e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "General.USER_TITLE_ERROR" ),
          Messages.getString( "MetaEditor.USER_ERROR_CONNECTION_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  public void delConnection( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      schemaMeta.removeDatabaseMeta( databaseMeta );
      mainTreeNode.getConnectionsRoot().removeDomainChild( databaseMeta );
    }
    setShellText();
  }

  private void editPhysicalColumn( PhysicalColumn physicalColumn ) {
    if ( physicalColumn != null ) {
      PhysicalTableDialog td = new PhysicalTableDialog( shell, physicalColumn, schemaMeta );
      int res = td.open();
      if ( Window.OK == res ) {
        refreshGraph();
        mainTreeNode.sync();
        setShellText();
      }
    }
  }

  private void syncPhysicalTable( PhysicalTable origPhysicalTable, PhysicalTable newPhysicalTable ) {

    // It's important to preserve the ConceptInterface instances (rather
    // than replacing them), as the instance references are important to
    // the inheritance chain among the concept business objects.

    ConceptInterface originalInterface = origPhysicalTable.getConcept();
    originalInterface.clearChildProperties();
    originalInterface.getChildPropertyInterfaces().putAll( newPhysicalTable.getConcept().getChildPropertyInterfaces() );

    for ( int i = 0; i < origPhysicalTable.nrPhysicalColumns(); i++ ) {
      PhysicalColumn newColumn = newPhysicalTable.getPhysicalColumn( i );
      PhysicalColumn oldColumn = origPhysicalTable.getPhysicalColumn( i );
      try {
        oldColumn.setId( newColumn.getId() );
      } catch ( ObjectAlreadyExistsException e ) {
        log.logDebug(
          "This should not happen as this exception would already have been caught earlier..." ); //$NON-NLS-1$
      }
      ConceptInterface originalInt = oldColumn.getConcept();
      originalInt.clearChildProperties();
      originalInt.getChildPropertyInterfaces().putAll( newColumn.getConcept().getChildPropertyInterfaces() );
    }

    refreshGraph();
    mainTreeNode.sync();
    setShellText();
  }

  public void editPhysicalTable( PhysicalTable physicalTable ) {
    if ( physicalTable != null ) {
      PhysicalTableDialog td = new PhysicalTableDialog( shell, physicalTable, schemaMeta );
      if ( td.open() == Window.OK ) {
        refreshGraph();
        mainTreeNode.sync();
        setShellText();
      }
    }
  }

  public void dupePhysicalTable( PhysicalTable physicalTable ) {
    if ( physicalTable != null ) {
      log.logDebug( Messages.getString( "MetaEditor.DEBUG_DUPLICATE_TABLE", physicalTable.getId() ) ); //$NON-NLS-1$

      PhysicalTable newTable = (PhysicalTable) physicalTable.clone();
      if ( newTable != null ) {
        try {
          String newname = physicalTable.getId() + " (copy)"; //$NON-NLS-1$
          int nr = 2;
          while ( schemaMeta.findPhysicalTable( newname ) != null ) {
            newname = physicalTable.getId() + " (copy " + nr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            nr++;
          }
          newTable.setId( newname );
          schemaMeta.addTable( newTable );
          mainTreeNode.sync();
          refreshGraph();
        } catch ( ObjectAlreadyExistsException e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_PHYSICAL_TABLE_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
  }

  public void delPhysicalTable( String name ) {
    PhysicalTable physicalTable = schemaMeta.findPhysicalTable( schemaMeta.getActiveLocale(), name );
    delPhysicalTable( physicalTable );
    // mainTreeNode.sync();
  }

  public void delPhysicalTable( PhysicalTable physicalTable ) {
    log
      .logDebug( Messages
        .getString(
          "MetaEditor.DEBUG_DELETE_TABLE", physicalTable == null ? "null"
            : physicalTable.getName( schemaMeta.getActiveLocale() ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    if ( physicalTable != null ) {
      int pos = schemaMeta.indexOfTable( physicalTable );
      schemaMeta.removeTable( pos );
      for ( int i = schemaMeta.nrBusinessModels() - 1; i >= 0; i-- ) {
        BusinessModel ri = schemaMeta.getModel( i );
        ri.deletePhysicalTableReferences( physicalTable );
      }
      mainTreeNode.sync();
      refreshGraph();
    } else {
      log.logDebug( Messages.getString( "MetaEditor.DEBUG_CANT_FIND_TABLE", "null" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void editRelationship( RelationshipMeta relationship ) {
    if ( relationship != null ) {
      String name = relationship.toString();
      BusinessModel activeModel = schemaMeta.getActiveModel();
      if ( activeModel == null ) {
        return;
      }
      RelationshipDialog rd = new RelationshipDialog( shell, SWT.NONE, log, relationship, activeModel );
      if ( rd.open() != null ) {
        String newname = relationship.toString();

        if ( !name.equalsIgnoreCase( newname ) ) {
          treeViewer.update( mainTreeNode, null );
        }
        refreshGraph(); // color, nr of copies...
      }
    }
    setShellText();
  }

  public void delRelationship( RelationshipMeta relationship ) {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if ( activeModel == null ) {
      return;
    }
    activeModel.removeRelationship( relationship );
    if ( activeModelTreeNode != null ) {
      activeModelTreeNode.getRelationshipsRoot().removeDomainChild( relationship );
    }
    refreshGraph();
  }

  public void newRelationship() {
    newRelationship( null, null );
  }

  public void newRelationship( BusinessTable from, BusinessTable to ) {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if ( activeModel == null ) {
      return;
    }

    RelationshipMeta relationship = new RelationshipMeta();
    relationship.setTableFrom( from );
    relationship.setTableTo( to );
    RelationshipDialog dialog =
      new RelationshipDialog( shell, SWT.NONE, log, relationship, schemaMeta.getActiveModel() );
    if ( dialog.open() != null ) {
      activeModel.addRelationship( relationship );
      if ( activeModelTreeNode != null ) {
        activeModelTreeNode.getRelationshipsRoot().addDomainChild( relationship );
      }
      refreshGraph();
    }
  }

  public void newConnection() {
    DatabaseMeta db = new DatabaseMeta();
    DatabaseDialog con = new DatabaseDialog( shell, db );
    String con_name = con.open();
    if ( con_name != null ) {
      try {
        schemaMeta.addDatabase( db );
        mainTreeNode.getConnectionsRoot().addDomainChild( db );
        importMultipleTables( db );
        synchronize( db );
      } catch ( ObjectAlreadyExistsException e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "General.USER_TITLE_ERROR" ),
          Messages.getString( "MetaEditor.USER_ERROR_DATABASE_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  public boolean showChangedWarning() {
    return showChangedWarning( Messages.getString( "MetaEditor.USER_DOMAIN_CHANGED" ) ); //$NON-NLS-1$
  }

  public boolean showChangedWarning( String message ) {
    boolean answer = true;
    if ( schemaMeta.hasChanged() ) {
      MessageBox mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_WARNING | SWT.APPLICATION_MODAL );
      mb.setMessage( message );
      mb.setText( Messages.getString( "General.USER_TITLE_WARNING" ) ); //$NON-NLS-1$
      answer = mb.open() == SWT.YES;
    }
    return answer;
  }

  public void openFile() {
    if ( showChangedWarning() ) {
      try {
        // Get the available models in the CWM repository
        String[] domainNames = CWM.getDomainNames();

        // Show a dialog to select a model
        EnterSelectionDialog selectionDialog =
          new EnterSelectionDialog( shell, domainNames, Messages.getString( "MetaEditor.USER_SELECT_DOMAIN" ),
            //$NON-NLS-1$
            Messages.getString( "MetaEditor.USER_SELECT_DOMAIN" ) ); //$NON-NLS-1$
        String domainName = selectionDialog.open();
        if ( domainName != null ) {
          readData( domainName );
        }
      } catch ( CWMException e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "MetaEditor.USER_TITLE_ERROR_GETTING_DOMAINS" ),
          Messages.getString( "MetaEditor.USER_ERROR_GETTING_DOMAINS" ), //$NON-NLS-1$ //$NON-NLS-2$
          e );
      }
    }
  }

  public void newFile() {
    boolean goAhead = false;
    if ( schemaMeta.hasChanged() ) {
      MessageBox mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING );
      mb.setMessage( Messages.getString( "MetaEditor.USER_DOMAIN_CHANGED_SAVE" ) ); //$NON-NLS-1$
      mb.setText( Messages.getString( "General.USER_TITLE_WARNING" ) ); //$NON-NLS-1$
      int answer = mb.open();
      switch ( answer ) {
        case SWT.YES:
          goAhead = saveFile();
          break;
        case SWT.NO:
          goAhead = true;
          break;
        case SWT.CANCEL:
          goAhead = false;
          break;
      }
    } else {
      goAhead = true;
    }

    if ( goAhead ) {
      schemaMeta.clear();
      schemaMeta.addDefaults();
      schemaMeta.clearChanged();
      setDomainName( "" );
      refreshTree();
      refreshAll();
    }
  }

  public boolean quitFile() {
    boolean retval = true;

    log.logDetailed( Messages.getString( "MetaEditor.INFO_QUIT_APPLICATION" ) ); //$NON-NLS-1$
    saveSettings();
    if ( schemaMeta.hasChanged() ) {
      MessageBox mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING );
      mb.setMessage( Messages.getString( "MetaEditor.USER_FILE_CHANGED_SAVE" ) ); //$NON-NLS-1$
      mb.setText( Messages.getString( "General.USER_TITLE_WARNING" ) ); //$NON-NLS-1$
      int answer = mb.open();

      switch ( answer ) {
        case SWT.YES:
          saveFile();
          dispose();
          break;
        case SWT.NO:
          dispose();
          break;
        case SWT.CANCEL:
          retval = false;
          break;
      }
    } else {
      dispose();
    }
    return retval;
  }

  public boolean saveFile() {
    log.logDetailed( Messages.getString( "MetaEditor.INFO_SAVE_FILE" ) ); //$NON-NLS-1$
    if ( schemaMeta.domainName != null && !schemaMeta.domainName.trim().isEmpty() ) {
      return save( schemaMeta.domainName );
    } else {
      return saveFileAs();
    }
  }

  public boolean saveFileAs() {
    try {
      log.logBasic( Messages.getString( "MetaEditor.INFO_SAVE_FILE_AS" ) ); //$NON-NLS-1$

      EnterStringDialog dialog =
        new EnterStringDialog(
          shell,
          "", Messages.getString( "MetaEditor.USER_TITLE_SAVE_DOMAIN_NAME" ),
          Messages.getString( "MetaEditor.USER_SAVE_DOMAIN_NAME" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      dialog.setMandatory( true );
      String domainName = dialog.open();

      if ( domainName != null ) {
        int id = SWT.YES;
        if ( CWM.exists( domainName ) ) {
          MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
          mb.setMessage( Messages.getString( "MetaEditor.USER_DOMAIN_EXISTS" ) ); //$NON-NLS-1$
          mb.setText( Messages.getString( "MetaEditor.USER_TITLE_DOMAIN_EXISTS" ) ); //$NON-NLS-1$
          id = mb.open();
        }
        if ( id == SWT.YES ) {
          save( domainName );
          setDomainName( domainName );
          return true;
        }
      }
    } catch ( Exception e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "MetaEditor.USER_TITLE_ERROR_SAVING_DOMAIN" ),
        Messages.getString( "MetaEditor.USER_ERROR_SAVING_DOMAIN_SEVERE" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return false;
  }

  private boolean save( String domainName ) {
    try {
      // Save the schema in the MDR
      SchemaSaveProgressDialog dialog = new SchemaSaveProgressDialog( shell, domainName, schemaMeta );
      cwm = dialog.open();

      // Handle last opened files...
      props.addLastFile( LastUsedFile.FILE_TYPE_SCHEMA, domainName, Const.FILE_SEPARATOR, false, "" ); //$NON-NLS-1$
      saveSettings();
      addMenuLast();

      schemaMeta.clearChanged();
      setShellText();
      log.logDebug( Messages.getString( "MetaEditor.DEBUG_FILE_WRITTEN_TO_REPOSITORY", domainName ) ); //$NON-NLS-1$
      return true;
    } catch ( Exception e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString( "MetaEditor.USER_ERROR_SAVING_DOMAIN" ),
        e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return false;
  }

  public void deleteFile() {
    try {
      // Get the available domains in the CWM repository
      String[] domainNames = CWM.getDomainNames();

      // Show a dialog to select a model
      EnterSelectionDialog selectionDialog =
        new EnterSelectionDialog( shell, domainNames, Messages.getString( "MetaEditor.USER_DELETE_DOMAIN" ),
          //$NON-NLS-1$
          Messages.getString( "MetaEditor.USER_SELECT_DOMAIN_FOR_DELETE" ) ); //$NON-NLS-1$
      String domainName = selectionDialog.open();
      if ( domainName != null ) {
        MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
        mb.setMessage( Messages.getString( "MetaEditor.USER_DELETE_DOMAIN_CONFIRM" ) ); //$NON-NLS-1$
        mb.setText( Messages.getString( "MetaEditor.USER_SURE_CONFIRM" ) ); //$NON-NLS-1$
        int answer = mb.open();
        if ( answer == SWT.YES ) {
          CWM delCwm = CWM.getInstance( domainName );
          delCwm.removeDomain();
          if ( domainName.equalsIgnoreCase( schemaMeta.getDomainName() ) ) {
            schemaMeta.clear();
            schemaMeta.addDefaults();
            schemaMeta.clearChanged();
            setDomainName( null );
            refreshTree();
            refreshAll();
          }
        }
      }
    } catch ( Throwable e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "MetaEditor.USER_TITLE_ERROR_RETRIEVING_DOMAIN_LIST" ),
        Messages.getString( "MetaEditor.USER_ERROR_RETRIEVING_DOMAIN_LIST" ), //$NON-NLS-1$ //$NON-NLS-2$
        new Exception( e ) );
    }
  }

  public void helpAbout() {
    try {
      AboutDialog aboutDialog = new AboutDialog( this.shell );
      aboutDialog.open();
    } catch ( Exception e ) {
      log.logError( "Error opening about dialog", e );
    }
  }

  public void editUnselectAll() {
    if ( schemaMeta.getActiveModel() == null ) {
      return;
    }

    schemaMeta.getActiveModel().unselectAll();
    metaEditorGraph.redraw();
  }

  public void editSelectAll() {
    if ( schemaMeta.getActiveModel() == null ) {
      return;
    }

    schemaMeta.getActiveModel().selectAll();
    metaEditorGraph.redraw();
  }

  public void editOptions() {
    EnterOptionsDialog eod = new EnterOptionsDialog( shell, props );
    if ( eod.open() != null ) {
      props.saveProps();
      loadSettings();
      changeLooks();
    }
  }

  public void editSelectedProperties() {
    Object selectedItem = ( (StructuredSelection) treeViewer.getSelection() ).getFirstElement();
    if ( selectedItem instanceof BusinessColumnTreeNode ) {
      BusinessColumnTreeNode businessColumnTreeNode = (BusinessColumnTreeNode) selectedItem;
      editBusinessColumn( businessColumnTreeNode.getBusinessColumn(), businessColumnTreeNode );
    } else if ( selectedItem instanceof BusinessModelTreeNode ) {
      BusinessModelTreeNode businessModelTreeNode = (BusinessModelTreeNode) selectedItem;
      editBusinessModel( businessModelTreeNode.getBusinessModel() );
    } else if ( selectedItem instanceof BusinessTableTreeNode ) {
      BusinessTableTreeNode businessTableTreeNode = (BusinessTableTreeNode) selectedItem;
      editBusinessTable( (BusinessTable) businessTableTreeNode.getDomainObject() );
    } else if ( selectedItem instanceof CategoryTreeNode ) {
      CategoryTreeNode categoryTreeNode = (CategoryTreeNode) selectedItem;
      editBusinessCategory( categoryTreeNode.getCategory(), (CategoryTreeNode) selectedItem );
    } else if ( selectedItem instanceof PhysicalColumnTreeNode ) {
      PhysicalColumnTreeNode physicalColumnTreeNode = (PhysicalColumnTreeNode) selectedItem;
      editPhysicalColumn( (PhysicalColumn) physicalColumnTreeNode.getDomainObject() );
    } else if ( selectedItem instanceof PhysicalTableTreeNode ) {
      PhysicalTableTreeNode physicalTableTreeNode = (PhysicalTableTreeNode) selectedItem;
      editPhysicalTable( (PhysicalTable) physicalTableTreeNode.getDomainObject() );
    } else if ( selectedItem instanceof DatabaseMetaTreeNode ) { // We clicked on a database node
      DatabaseMeta databaseMeta = ( (DatabaseMetaTreeNode) selectedItem ).getDatabaseMeta();
      editConnection( databaseMeta );
      treeViewer.update( selectedItem, null );
    }
  }

  public int getTreePosition( TreeItem ti, String item ) {
    if ( ti != null ) {
      TreeItem[] items = ti.getItems();
      for ( int x = 0; x < items.length; x++ ) {
        if ( items[ x ].getText().equalsIgnoreCase( item ) ) {
          return x;
        }
      }
    }
    return -1;
  }

  public void refreshAll() {
    refreshGraph();
    // metaEditorConcept.refreshTree();
    // metaEditorConcept.refreshScreen();
    metaEditorLocales.refreshScreen();
    if ( metaEditorOlap != null ) {
      metaEditorOlap.refreshScreen();
    }
  }

  public void refreshTree() {
    mainTreeNode = new SchemaMetaTreeNode( null, schemaMeta );
    mainTreeNode.addTreeNodeChangeListener( (ITreeNodeChangedListener) treeViewer.getContentProvider() );

    // This next line is only necessary so that the nodes are realized ahead
    // of time, in order for the tree to reflect
    // changes from the graph, regardless of whether the tree was expanded
    // or not...
    mainTreeNode.sync();

    treeViewer.setInput( mainTreeNode );

    // And this line is to prevent a bug where the viewer will display
    // duplicate nodes when setInput() is called
    treeViewer.refresh();

    if ( mainTreeNode.getBusinessModelsRoot().hasChildren() ) {
      activeModelTreeNode = (BusinessModelTreeNode) mainTreeNode.getBusinessModelsRoot().getChildren().get( 0 );
    }
  }

  public void synchronize( Object businessObject ) {
    ConceptTreeNode node = mainTreeNode.findNode( businessObject );
    node.sync();
  }

  public static final void addTreeCategories( TreeItem tiParent, BusinessCategory parentCategory, String locale,
                                              boolean hiddenToo ) {
    // Draw the categories tree...
    for ( int i = 0; i < parentCategory.nrBusinessCategories(); i++ ) {
      BusinessCategory businessCategory = parentCategory.getBusinessCategory( i );
      ConceptInterface concept = businessCategory.getConcept();

      TreeItem tiCategory = new TreeItem( tiParent, SWT.NONE );
      String name = businessCategory.getDisplayName( locale );
      tiCategory.setText( 0, name );
      if ( concept != null && concept.findFirstParentConcept() != null ) {
        tiCategory.setText( 1, concept.findFirstParentConcept().getName() );
      }
      tiCategory.setForeground( GUIResource.getInstance().getColorBlack() );

      // First add the sub-categories...
      addTreeCategories( tiCategory, businessCategory, locale, hiddenToo );
    }

    // Then add the business columns...
    for ( int c = 0; c < parentCategory.nrBusinessColumns(); c++ ) {
      BusinessColumn businessColumn = parentCategory.getBusinessColumn( c );

      if ( hiddenToo || !businessColumn.isHidden() ) {
        ConceptInterface concept = businessColumn.getConcept();

        TreeItem tiColumn = new TreeItem( tiParent, SWT.NONE );
        tiColumn.setText( 0, businessColumn.getDisplayName( locale ) );
        if ( concept != null && concept.findFirstParentConcept() != null ) {
          tiColumn.setText( 1, concept.findFirstParentConcept().getName() );
        }
        tiColumn.setForeground( GUIResource.getInstance().getColorBlue() );
      }
    }
  }

  public void refreshGraph() {
    metaEditorGraph.redraw();
    // Update the active model node, as the connection info
    // can dynamically change depending on the addition or
    // removal of business tables, changing the label on this node ...
    if ( activeModelTreeNode != null ) {
      treeViewer.update( activeModelTreeNode, null );
    }
    setShellText();
  }

  public DatabaseMeta getConnection( String name ) {
    int i;

    for ( i = 0; i < schemaMeta.nrDatabases(); i++ ) {
      DatabaseMeta ci = schemaMeta.getDatabase( i );
      if ( ci.getName().equalsIgnoreCase( name ) ) {
        return ci;
      }
    }
    return null;
  }

  public void setShellText() {
    String fname = schemaMeta.domainName;
    if ( shell.isDisposed() ) {
      return;
    }
    if ( fname != null && !fname.trim().isEmpty() ) {
      shell.setText( APPLICATION_NAME
        + " - " + fname + ( schemaMeta.hasChanged() ? Messages.getString( "MetaEditor.USER_CHANGED" )
        : "" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } else {
      shell.setText( APPLICATION_NAME
        + ( schemaMeta.hasChanged() ? Messages.getString( "MetaEditor.USER_CHANGED" )
        : "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void setDomainName( String domainName ) {
    schemaMeta.domainName = domainName;

    setShellText();
  }

  private void printFile() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    if ( activeModel == null ) {
      return;
    }

    PrintSpool ps = new PrintSpool();
    Printer printer = ps.getPrinter( shell );

    // Create an image of the screen
    Point max = activeModel.getMaximum();

    // Image img_screen = new Image(trans, max.x, max.y);
    // img_screen.dispose();

    PaletteData pal = ps.getPaletteData();

    ImageData imd = new ImageData( max.x, max.y, printer.getDepth(), pal );
    Image img = new Image( printer, imd );

    GC img_gc = new GC( img );

    // Clear the background first, fill with background color...
    Color bg = new Color( printer, props.getBackgroundRGB() );
    img_gc.setForeground( bg );
    img_gc.fillRectangle( 0, 0, max.x, max.y );
    bg.dispose();

    // Draw the transformation...
    metaEditorGraph.drawSchema( img_gc );

    // ShowImageDialog sid = new ShowImageDialog(shell, transMeta.props,
    // img);
    // sid.open();

    ps.printImage( shell, img );

    img_gc.dispose();
    img.dispose();
    ps.dispose();
  }

  public void saveSettings() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    props.setLogLevel( log.getLogLevel().getCode() );
    props.setSashWeights( sashform.getWeights() );
    props.saveProps();
  }

  public void loadSettings() {
    log.setLogLevel( LogLevel.getLogLevelForCode( props.getLogLevel() ) );

    GUIResource.getInstance().reload();

    DBCache.getInstance().setActive( props.useDBCache() );
  }

  public void changeLooks() {
    treeViewer.getTree().setBackground( GUIResource.getInstance().getColorBackground() );
    metaEditorGraph.newProps();

    refreshAll();
  }

  public void clearDBCache() {
    // Determine what menu we selected from...

    TreeItem[] ti = treeViewer.getTree().getSelection();

    // Then call editConnection or editStep or editTrans
    if ( ti.length == 1 ) {
      String name = ti[ 0 ].getText();
      TreeItem parent = ti[ 0 ].getParentItem();
      if ( parent != null ) {
        String type = parent.getText();
        if ( type.equalsIgnoreCase( STRING_CONNECTIONS ) ) {
          DBCache.getInstance().clear( name );
        }
      } else {
        if ( name.equalsIgnoreCase( STRING_CONNECTIONS ) ) {
          DBCache.getInstance().clear( null );
        }
      }
    }
  }

  public void importTables( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      DatabaseExplorerDialog std =
        new DatabaseExplorerDialog( shell, SWT.NONE, databaseMeta, schemaMeta.databases.getList(), false );
      if ( std.open() ) {
        String schemaName = std.getSchemaName();
        String tableName = std.getTableName();

        Database database = new Database( databaseMeta );
        try {
          database.connect();

          importTableDefinition( database, schemaName, tableName );
        } catch ( KettleException e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_ERROR_READING_TABLE_FIELDS", tableName ) //$NON-NLS-1$ //$NON-NLS-2$
              + ( ( schemaName != null ) ? ( "(schema=" + schemaName + ")" ) : "" ),
            e ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } finally {
          if ( database != null ) {
            database.disconnect();
          }
        }
      }
    }
  }

  String[] getSchemas( Database database, DatabaseMeta databaseMeta ) throws KettleDatabaseException {
    // This is a hack for PMD-907. A NPE can be thrown on getSchema JDBC's implementations.
    String[] schemas = null;
    Exception ex = null;
    try {
      schemas = database.getSchemas();
    } catch ( Exception e ) {
      // This can happen on shitty implementation of JDBC. We'll try the catalogs.
      ex = e;
    }

    if ( ArrayUtils.isEmpty( schemas ) ) {
      // MySQL doesn't report schema names. If we call get Catalogs, we get all the schemas, even those for which the
      // current user doesn't have permissions. we'll use the DB name instead, as configured in the JDBC URL.
      // Else try the catalogs instead. Some DBs call them catalogs.
      schemas =
        ( databaseMeta.isMySQLVariant() ) ? new String[] { databaseMeta.getDatabaseName() } : database.getCatalogs();
    }

    if ( ArrayUtils.isEmpty( schemas ) && ex != null ) {
      // If we couldn't figure neither the schemas or catalogs and we have cached an exception, throw that.
      throw new KettleDatabaseException( ex );
    }
    return schemas;
  }

  Map<String, String[]> getTablesBySchemas( Database database, DatabaseMeta databaseMeta, String[] schemas )
    throws KettleDatabaseException {
    Map<String, String[]> tableMap = new LinkedHashMap<String, String[]>();
    String preferredSchemaName =
      String.valueOf( databaseMeta.getAttributes().get( BaseDatabaseMeta.ATTRIBUTE_PREFERRED_SCHEMA_NAME ) );
    for ( String schema : schemas ) {
      if ( StringUtils.isBlank( preferredSchemaName ) || StringUtils.isNotBlank( preferredSchemaName )
        && schema.equals( preferredSchemaName ) ) {
        for ( String tableName : database.getTablenames( schema, false ) ) {
          String fullName = databaseMeta.getQuotedSchemaTableCombination( schema, tableName );
          tableMap.put( fullName, new String[] { schema, tableName } );
        }
      }
    }
    return tableMap;
  }

  public void importMultipleTables( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      Database database = null;
      try {
        database = new Database( databaseMeta );
        database.connect();

        // Get the list of tables...
        // We need unique names for the UI and schema,table for the import
        String[] schemas = getSchemas( database, databaseMeta );
        Map<String, String[]> tableMap = getTablesBySchemas( database, databaseMeta, schemas );

        Set<String> nameSet = tableMap.keySet();
        String[] tableNames = nameSet.toArray( new String[ nameSet.size() ] );

        // Select from it...
        EnterSelectionDialog dialog =
          new EnterSelectionDialog(
            shell,
            tableNames,
            Messages.getString( "MetaEditor.USER_TITLE_IMPORT_TABLES" ),
            Messages.getString( "MetaEditor.USER_SELECT_IMPORT_TABLES" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setMulti( true );
        if ( dialog.open() != null ) {
          int[] indexes = dialog.getSelectionIndeces();
          for ( int i = 0; i < indexes.length; i++ ) {
            String tableName = tableNames[ indexes[ i ] ];
            String[] tableDesc = tableMap.get( tableName );
            importTableDefinition( database, tableDesc[ 0 ], tableDesc[ 1 ] );
          }
        }

      } catch ( Exception e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "General.USER_TITLE_ERROR" ),
          Messages.getString( "MetaEditor.USER_ERROR_IMPORTING_PHYSICAL_TABLES" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      } finally {
        if ( database != null ) {
          database.disconnect();
        }
      }
    }
  }

  private void importTableDefinition( Database database, String schemaName, String tableName ) throws KettleException {
    UniqueList<PhysicalColumn> fields = new UniqueArrayList<PhysicalColumn>();

    String id = tableName;
    String tablename = tableName;

    // Remove
    id = Const.toID( tableName );

    // Set the id to a certain standard...
    id = Settings.getPhysicalTableIDPrefix() + id;
    if ( Settings.isAnIdUppercase() ) {
      id = id.toUpperCase();
    }

    if ( schemaMeta.findPhysicalTable( id ) != null ) {
      // find a new name for the table: add " 2", " 3", " 4", ... to name:
      int copy = 2;
      String newname = id + " " + copy; //$NON-NLS-1$
      while ( schemaMeta.findPhysicalTable( newname ) != null ) {
        copy++;
        newname = id + " " + copy; //$NON-NLS-1$
      }
      id = newname;
    }

    PhysicalTable physicalTable = new PhysicalTable( id, schemaName, tableName, database.getDatabaseMeta(), fields );

    // Also set a localized description...
    String niceName = beautifyName( tablename );
    physicalTable.getConcept().setName( schemaMeta.getActiveLocale(), niceName );

    DatabaseMeta dbMeta = database.getDatabaseMeta();
    String schemaTableCombination = dbMeta.getQuotedSchemaTableCombination( schemaName, tableName );

    RowMetaInterface row = database.getTableFields( schemaTableCombination );

    if ( row != null && row.size() > 0 ) {
      for ( int i = 0; i < row.size(); i++ ) {
        ValueMetaInterface v = row.getValueMeta( i );
        PhysicalColumn physicalColumn = importPhysicalColumnDefinition( v, physicalTable );
        try {
          fields.add( physicalColumn );
        } catch ( ObjectAlreadyExistsException e ) {
          // Don't add this column
          // TODO: show error dialog.
        }
      }
    }
    String upper = tablename.toUpperCase();

    if ( upper.startsWith( "D_" ) || upper.startsWith( "DIM" ) || upper.endsWith( "DIM" ) ) {
      physicalTable.setTableType( TableTypeSettings.DIMENSION ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    if ( upper.startsWith( "F_" ) || upper.startsWith( "FACT" ) || upper.endsWith( "FACT" ) ) {
      physicalTable.setTableType( TableTypeSettings.FACT ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    try {
      schemaMeta.addTable( physicalTable );
    } catch ( ObjectAlreadyExistsException e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "General.USER_TITLE_ERROR" ),
        Messages.getString( "MetaEditor.USER_ERROR_PHYICAL_TABLE_EXISTS", physicalTable.getId() ),
        e ); //$NON-NLS-1$ //$NON-NLS-2$
    }

  }

  private PhysicalColumn importPhysicalColumnDefinition( ValueMetaInterface v, PhysicalTable physicalTable ) {
    // The id
    String id = Settings.getPhysicalColumnIDPrefix() + v.getName();
    if ( Settings.isAnIdUppercase() ) {
      id = id.toUpperCase();
    }

    // The name of the column in the database
    String dbname = v.getName();

    // The field type?
    FieldTypeSettings fieldType = FieldTypeSettings.guessFieldType( v.getName() );

    // Create a physical column.
    PhysicalColumn physicalColumn =
      new PhysicalColumn( v.getName(), dbname, fieldType, AggregationSettings.NONE, physicalTable );

    // Set the localised name...
    String niceName = beautifyName( v.getName() );
    physicalColumn.setName( schemaMeta.getActiveLocale(), niceName );

    // Set the parent concept to the base concept...
    physicalColumn.getConcept().setParentInterface( schemaMeta.findConcept( Settings.getConceptNameBase() ) );

    // The data type...
    DataTypeSettings dataTypeSettings = getDataTypeSettings( v );
    physicalColumn.setDataType( dataTypeSettings );

    return physicalColumn;
  }

  private static final String beautifyName( String name ) {
    return StringUtils.capitalize( name.replace( "_", " " ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  private DataTypeSettings getDataTypeSettings( ValueMetaInterface v ) {
    DataTypeSettings dataTypeSettings = new DataTypeSettings( DataTypeSettings.DATA_TYPE_STRING );
    switch ( v.getType() ) {
      case ValueMetaInterface.TYPE_BIGNUMBER:
      case ValueMetaInterface.TYPE_INTEGER:
      case ValueMetaInterface.TYPE_NUMBER:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_NUMERIC );
        break;

      case ValueMetaInterface.TYPE_BINARY:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_BINARY );
        break;

      case ValueMetaInterface.TYPE_BOOLEAN:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_BOOLEAN );
        break;

      case ValueMetaInterface.TYPE_DATE:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_DATE );
        break;

      case ValueMetaInterface.TYPE_STRING:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_STRING );
        break;

      case ValueMetaInterface.TYPE_NONE:
        dataTypeSettings.setType( DataTypeSettings.DATA_TYPE_UNKNOWN );
        break;

      default:
        break;
    }
    dataTypeSettings.setLength( v.getLength() );
    dataTypeSettings.setPrecision( v.getPrecision() );

    return dataTypeSettings;
  }

  public void exploreDB() {
    // Determine what menu we selected from...

    TreeItem[] ti = treeViewer.getTree().getSelection();

    // Then call editConnection or editStep or editTrans
    if ( ti.length == 1 ) {
      String name = ti[ 0 ].getText();
      TreeItem parent = ti[ 0 ].getParentItem();
      if ( parent != null ) {
        String type = parent.getText();
        if ( type.equalsIgnoreCase( STRING_CONNECTIONS ) ) {
          DatabaseMeta dbinfo = schemaMeta.findDatabase( name );
          if ( dbinfo != null ) {
            DatabaseExplorerDialog std =
              new DatabaseExplorerDialog( shell, SWT.NONE, dbinfo, schemaMeta.databases.getList(), true );
            std.open();
          } else {
            MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
            mb.setMessage( Messages.getString( "MetaEditor.USER_ERROR_CANT_FIND_CONNECTION" ) ); //$NON-NLS-1$
            mb.setText( Messages.getString( "General.USER_TITLE_ERROR" ) ); //$NON-NLS-1$
            mb.open();
          }
        }
      } else {
        if ( name.equalsIgnoreCase( STRING_CONNECTIONS ) ) {
          DBCache.getInstance().clear( null );
        }
      }
    }
  }

  public String toString() {
    return this.getClass().getName();
  }

  public static void main( String[] args ) throws Exception {

    KettleEnvironment.init( false );

    System
      .setProperty( "java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory" ); //$NON-NLS-1$ //$NON-NLS-2$
    System.setProperty( "org.osjava.sj.root", "simple-jndi" ); //$NON-NLS-1$ //$NON-NLS-2$
    System.setProperty( "org.osjava.sj.delimiter", "/" ); //$NON-NLS-1$ //$NON-NLS-2$

    LogChannel log = new LogChannel( APPLICATION_NAME );
    Display.setAppName( APPLICATION_NAME );
    Display display = new Display();

    if ( !Props.isInitialized() ) {
      Const.checkPentahoMetadataDirectory();
      PropsUI.init( display, Const.getPropertiesFile() ); // things to remember...
    }

    Window.setDefaultImage( Constants.getImageRegistry( Display.getCurrent() ).get( "pentaho-icon" ) ); //$NON-NLS-1$

    Splash splash = new Splash( display );

    final MetaEditor win = new MetaEditor( log, display );

    // Read kettle transformation specified on command-line?
    if ( args.length == 1 && !Const.isEmpty( args[ 0 ] ) ) {
      if ( CWM.exists( args[ 0 ] ) ) { // Only try to load the domain if it exists.
        win.cwm = CWM.getInstance( args[ 0 ] );
        CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
        win.schemaMeta = cwmSchemaFactory.getSchemaMeta( win.cwm );
        win.setDomainName( args[ 0 ] );
        win.schemaMeta.clearChanged();
      } else {
        win.newFile();
      }
    } else {
      if ( win.props.openLastFile() ) {
        String[] lastfiles = win.props.getLastFiles();
        if ( lastfiles.length > 0 ) {
          try {
            if ( CWM.exists( lastfiles[ 0 ] ) ) { // Only try to load the domain if it exists.
              win.cwm = CWM.getInstance( lastfiles[ 0 ] );
              CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
              win.schemaMeta = cwmSchemaFactory.getSchemaMeta( win.cwm );
              win.setDomainName( lastfiles[ 0 ] );
              win.schemaMeta.clearChanged();
            } else {
              win.newFile();
            }
          } catch ( Exception e ) {
            log.logError(
              Messages.getString( "MetaEditor.ERROR_0001_CANT_CHECK_DOMAIN_EXISTENCE", e.toString() ) ); //$NON-NLS-1$
            log.logError( Const.getStackTracker( e ) );
          }
        } else {
          win.newFile();
        }
      } else {
        win.newFile();
      }
    }

    if ( !Splash.isMacOS() ) {
      splash.hide();
    }

    win.open();
    while ( !win.isDisposed() ) {
      if ( !win.readAndDispatch() ) {
        win.sleep();
      }
    }
    win.dispose();

    // Close the logfile...
    System.exit( 0 );
  }

  /**
   * @return the schemaMeta
   */
  public SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  /**
   * @param schemaMeta the schemaMeta to set
   */
  public void setSchemaMeta( SchemaMeta schemaMeta ) {
    this.schemaMeta = schemaMeta;
  }

  public void editBusinessTable( BusinessTable businessTable ) {
    editBusinessTable( businessTable, null );
  }

  private void editBusinessColumn( BusinessColumn businessColumn, BusinessColumnTreeNode node ) {
    try {
      if ( businessColumn != null ) {
        BusinessTableDialog td = new BusinessTableDialog( shell, businessColumn, schemaMeta );
        int res = td.open();
        if ( Window.OK == res ) {
          if ( node != null ) {
            node.sync();
          } else {
            synchronize( businessColumn );
          }
          refreshAll();
        }
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell,
        Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString( "MetaEditor.USER_TITLE_DEMO_ERROR" ),
        e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void syncBusinessTables( BusinessTable origBusinessTable, BusinessTable newBusinessTable ) {
    // It's important to preserve the ConceptInterface instances (rather
    // than replacing them), as the instance references are important to
    // the inheritance chain among the concept business objects.

    ConceptInterface originalInterface = origBusinessTable.getConcept();
    originalInterface.clearChildProperties();
    originalInterface.getChildPropertyInterfaces().putAll( newBusinessTable.getConcept().getChildPropertyInterfaces() );

    origBusinessTable.setPhysicalTable( newBusinessTable.getPhysicalTable() );

    for ( int i = origBusinessTable.nrBusinessColumns() - 1; i >= 0; i-- ) {
      origBusinessTable.removeBusinessColumn( i );
    }

    Iterator iter = newBusinessTable.getBusinessColumns().iterator();
    while ( iter.hasNext() ) {
      BusinessColumn column = (BusinessColumn) iter.next();
      try {
        origBusinessTable.addBusinessColumn( column );
      } catch ( ObjectAlreadyExistsException e ) {
        e.printStackTrace();
        log.logDebug(
          "This should not happen as this exception would already have been caught earlier..." ); //$NON-NLS-1$
      }
    }
  }

  /**
   * TODO mlowery move this business save logic to a method for reuse
   */
  private void editBusinessTable( BusinessTable businessTable, ConceptTreeNode node ) {
    try {
      if ( businessTable != null ) {

        BusinessTableDialog td = new BusinessTableDialog( shell, businessTable, schemaMeta );
        int res = td.open();

        if ( Window.OK == res ) {
          if ( node != null ) {
            node.sync();
          } else {
            synchronize( businessTable );
          }
          refreshAll();

        }
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell,
        Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString( "MetaEditor.USER_TITLE_DEMO_ERROR" ),
        e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void dupeBusinessTable( BusinessTable businessTable ) {
    try {
      if ( businessTable != null ) {
        log.logDebug( Messages.getString( "MetaEditor.DEBUG_DUPLICATE_TABLE", businessTable.getId() ) ); //$NON-NLS-1$

        BusinessModel activeModel = schemaMeta.getActiveModel();

        // This should be a unique clone of the business table AND it's columns...
        BusinessTable newTable =
          businessTable.cloneUnique( schemaMeta.getActiveLocale(), activeModel.getBusinessTables(), activeModel
            .getAllBusinessColumns() );

        try {

          activeModel.addBusinessTable( newTable );

        } catch ( ObjectAlreadyExistsException e ) {

          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_BUSINESS_TABLE_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$

        }

        if ( activeModelTreeNode != null ) {
          activeModelTreeNode.getBusinessTablesRoot().addDomainChild( newTable );
        }

        refreshGraph();

      }
    } catch ( Exception e ) {
      new ErrorDialog(
        shell,
        Messages.getString( "General.USER_TITLE_ERROR" ),
        Messages.getString( "MetaEditor.USER_BUSINESS_TABLE_NAME_EXISTS" ), e ); //$NON-NLS-1$ //$NON-NLS-2$

    }
  }

  /**
   * Test Query & Reporting
   */
  protected void testQR() {
    try {
      // If the domain is not the same as the previous: clear the previous query.
      // Just as a precaution.
      //
      QueryBuilderDialog queryBuilderDialog = null;
      if ( query == null || query.getSchemaMeta() == null || query.getSchemaMeta().getDomainName() == null
        || !query.getSchemaMeta().getDomainName().equals( schemaMeta.getDomainName() ) ) {
        queryBuilderDialog = new QueryBuilderDialog( shell, schemaMeta );
      } else {
        BusinessModel origModel = query.getModel();
        BusinessModel businessModel = null;
        List<BusinessModel> businessModels = schemaMeta.getBusinessModels().getList();
        for ( Iterator iter = businessModels.iterator(); iter.hasNext(); ) {
          BusinessModel tmpModel = (BusinessModel) iter.next();
          if ( origModel.getId().equals( tmpModel.getId() ) ) {
            businessModel = tmpModel;
          }
        }
        if ( businessModel != null ) {
          query.setSchemaMeta( schemaMeta );
          query.setModel( businessModel );
          queryBuilderDialog = new QueryBuilderDialog( shell, schemaMeta, query );
        } else {
          queryBuilderDialog = new QueryBuilderDialog( shell, schemaMeta );
        }
      }
      if ( queryBuilderDialog.open() == Window.OK ) {
        query = queryBuilderDialog.getMqlQuery();
        saveQuery();
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell,
        Messages.getString( "MetaEditor.USER_TITLE_DEMO_ERROR" ), Messages.getString( "MetaEditor.USER_DEMO_ERROR" ),
        e ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  // protected void testQR() {
  // try {
  // QueryDialog queryDialog = new QueryDialog(shell, schemaMeta, query);
  // MQLQuery lastQuery = queryDialog.open();
  // if (lastQuery != null) {
  // query = lastQuery;
  // saveQuery();
  // }
  // /*
  // * query = MakeSelectionDemo.executeDemo(shell, props, query, false); //
  // Don't shut down, let it be. if
  // * (query!=null) { saveQuery(); }
  // */
  // } catch (Exception e) {
  // new ErrorDialog(shell,
  // Messages.getString("MetaEditor.USER_TITLE_DEMO_ERROR"),
  // Messages.getString("MetaEditor.USER_DEMO_ERROR"), e); //$NON-NLS-1$
  // //$NON-NLS-2$
  // }
  // }

  private void saveQuery() {
    try {
      if ( query != null ) {
        FileUtil.saveAsXml( Const.getQueryFile(), query.getXML() );
      }
    } catch ( Exception e ) {
      log.logError( Messages.getString( "MetaEditor.ERROR_0002_CANT_SAVE_QUERY" ) + e.toString() ); //$NON-NLS-1$
      log.logError( Const.getStackTracker( e ) );
    }
  }

  private void loadQuery() {
    try {
      File file = new File( Const.getQueryFile() );
      if ( !file.exists() ) {
        return;
      }
      FileInputStream fileInputStream = new FileInputStream( file );
      byte[] bytes = new byte[ (int) file.length() ];
      fileInputStream.read( bytes );
      fileInputStream.close();

      query =
        MQLQueryFactory.getMQLQuery( new String( bytes, Const.XML_ENCODING ), null, Const.XML_ENCODING,
          cwmSchemaFactory );
    } catch ( Exception e ) {
      log.logBasic( Messages.getString( "MetaEditor.ERROR_0003_CANT_LOAD_QUERY", e.toString() ) ); //$NON-NLS-1$
    }
  }

  public void editSecurityService() {
    SecurityDialog dialog = new SecurityDialog( shell, schemaMeta.getSecurityReference().getSecurityService() );

    // SecurityServiceDialog dialog = new SecurityServiceDialog(shell,
    // schemaMeta.getSecurityReference()
    // .getSecurityService());
    if ( dialog.open() == IDialogConstants.OK_ID ) {
      // try to grab it from the security service if it exists...
      SecurityService securityService = schemaMeta.getSecurityReference().getSecurityService();
      if ( securityService != null ) {
        try {
          schemaMeta.setSecurityReference( new SecurityReference( securityService ) );
        } catch ( Throwable e ) {
          new ErrorDialog(
            shell,
            Messages.getString( "General.USER_TITLE_ERROR" ),
            Messages.getString( "MetaEditor.USER_ERROR_LOADING_SECURITY_INFORMATION" ), //$NON-NLS-1$ //$NON-NLS-2$
            new Exception( e ) );
        }
      }

      refreshAll();
    }
  }

  public void getMondrianModel() {
    BusinessModel activeModel = schemaMeta.getActiveModel();
    String locale = schemaMeta.getActiveLocale();

    if ( activeModel != null ) {
      try {
        String xml = activeModel.getMondrianModel( locale );

        EnterTextDialog dialog =
          new EnterTextDialog(
            shell,
            Messages.getString( "MetaEditor.USER_TITLE_MONDRIAN_XML" ),
            Messages.getString( "MetaEditor.USER_MONDRIAN_XML" ), xml ); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.open();
      } catch ( Exception e ) {
        new ErrorDialog(
          shell,
          Messages.getString( "MetaEditor.USER_TITLE_MODEL_ERROR" ),
          Messages.getString( "MetaEditor.USER_MONDRIAN_MODEL_ERROR" ), e ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  /**
   * @return the selected concept utility interfaces...
   */
  public ConceptUtilityInterface[] getSelectedConceptUtilityInterfacesInMainTree() {
    List<Object> list = new ArrayList<Object>();
    TreeItem[] selection = treeViewer.getTree().getSelection();

    for ( int i = 0; i < selection.length; i++ ) {
      TreeItem treeItem = selection[ i ];
      ConceptTreeNode node = (ConceptTreeNode) treeItem.getData();
      if ( node instanceof PhysicalTableTreeNode ) {
        list.add( ( (PhysicalTableTreeNode) node ).getDomainObject() );
      } else if ( node instanceof PhysicalColumnTreeNode ) {
        list.add( ( (PhysicalColumnTreeNode) node ).getDomainObject() );
      } else if ( node instanceof BusinessModelTreeNode ) {
        list.add( ( (BusinessModelTreeNode) node ).getDomainObject() );
      } else if ( node instanceof BusinessTableTreeNode ) {
        list.add( ( (BusinessTableTreeNode) node ).getDomainObject() );
      } else if ( node instanceof CategoryTreeNode ) {
        list.add( ( (CategoryTreeNode) node ).getDomainObject() );
      } else if ( node instanceof BusinessColumnTreeNode ) {
        list.add( ( (BusinessColumnTreeNode) node ).getDomainObject() );
      } else if ( node instanceof BusinessViewTreeNode ) {
        BusinessModelTreeNode modelNode = (BusinessModelTreeNode) node.getParent();
        BusinessModel model = (BusinessModel) modelNode.getDomainObject();
        BusinessCategory category = model.getRootCategory();
        if ( category != null ) {
          list.add( category );
        }
      }
    }

    return list.toArray( new ConceptUtilityInterface[ list.size() ] );
  }

  protected void setParentConcept( ConceptUtilityInterface[] utilityInterfaces ) {
    String[] concepts = schemaMeta.getConceptNames();

    // Ask the user to pick a parent concept...
    EnterSelectionDialog dialog =
      new EnterSelectionDialog( shell, concepts, Messages.getString( "MetaEditor.USER_TITLE_SELECT_PARENT_CONCEPT" ),
        //$NON-NLS-1$
        Messages.getString( "MetaEditor.USER_SELECT_PARENT_CONCEPT" ) ); //$NON-NLS-1$
    String conceptName = dialog.open();
    if ( conceptName != null ) {
      ConceptInterface parentInterface = schemaMeta.findConcept( conceptName );

      for ( int u = 0; u < utilityInterfaces.length; u++ ) {
        utilityInterfaces[ u ].getConcept().setParentInterface( parentInterface );
        utilityInterfaces[ u ].setChanged();
      }

      refreshAll();
    }
  }

  protected void clearParentConcept( ConceptUtilityInterface[] utilityInterfaces ) {
    for ( int u = 0; u < utilityInterfaces.length; u++ ) {

      // If this concept's parent interface is null, then the parent
      // interface the user is trying to remove is inherited... and can't
      // be removed here. Throw a message to tell them to remove the
      // parent concept from the inherited counterpart.

      if ( utilityInterfaces[ u ].getConcept().getParentInterface() == null ) {
        MessageDialog.openWarning( this.shell, Messages.getString( "MetaEditor.USER_TITLE_CANT_CLEAR_PARENT_CONCEPT" ),
          //$NON-NLS-1$
          Messages.getString( "MetaEditor.USER_CANT_CLEAR_PARENT_CONCEPT" ) //$NON-NLS-1$
            + Messages.getString( "MetaEditor.USER_CANT_CLEAR_PARENT_CONCEPT_2" ) //$NON-NLS-1$
            + Messages.getString( "MetaEditor.USER_CANT_CLEAR_PARENT_CONCEPT_3" ) ); //$NON-NLS-1$
      }
      utilityInterfaces[ u ].getConcept().setParentInterface( null );
      utilityInterfaces[ u ].setChanged();
    }

    refreshAll();
  }

  protected void removeChildProperties( ConceptUtilityInterface[] utilityInterfaces ) {
    // First we need a distinct list of all property IDs...
    Map<String, String> all = new Hashtable<String, String>();
    for ( int u = 0; u < utilityInterfaces.length; u++ ) {
      String[] ids = utilityInterfaces[ u ].getConcept().getChildPropertyIDs();
      for ( int i = 0; i < ids.length; i++ ) {
        all.put( ids[ i ], "" ); //$NON-NLS-1$
      }
    }
    Set<String> keySet = all.keySet();
    String[] ids = keySet.toArray( new String[ keySet.size() ] );
    String[] names = new String[ ids.length ];

    // Get the descriptions to show...
    for ( int i = 0; i < ids.length; i++ ) {
      names[ i ] = PredefinedVsCustomPropertyHelper.getDescription( ids[ i ] );
    }

    // Ask the user to pick the child properties to delete...
    EnterSelectionDialog dialog =
      new EnterSelectionDialog(
        shell,
        names,
        Messages.getString( "MetaEditor.USER_TITLE_DELETE_PROPERTIES" ),
        Messages.getString( "MetaEditor.USER_DELETE_PROPERTIES" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    String conceptName = dialog.open();
    if ( conceptName != null ) {

      for ( int u = 0; u < utilityInterfaces.length; u++ ) {
        ConceptInterface concept = utilityInterfaces[ u ].getConcept();

        int[] idxs = dialog.getSelectionIndeces();
        for ( int i = 0; i < idxs.length; i++ ) {
          ConceptPropertyInterface property = concept.getChildProperty( ids[ idxs[ i ] ] );
          if ( property != null ) {
            concept.removeChildProperty( property );
            utilityInterfaces[ u ].setChanged();
          }
        }
      }

      refreshAll();
    }
  }

  public void widgetDefaultSelected( SelectionEvent e ) {
    if ( e.getSource() == treeViewer.getTree() ) {
      doubleClickedMain();
    }
  }

  public void widgetSelected( SelectionEvent e ) {
    if ( e.getSource() == treeViewer.getTree() ) {
      updateMenusAndToolbars( e );
      setActiveBusinessModel( e );
    }
  }

  private String[] generateBusinessModelId() {

    int idNum = schemaMeta.nrBusinessModels();

    String prefix = Settings.getBusinessModelIDPrefix() + "model_"; //$NON-NLS-1$
    if ( Settings.isAnIdUppercase() ) {
      prefix = prefix.toUpperCase();
    }

    String id = null;

    boolean found = true;

    while ( found ) {

      found = false;
      id = prefix + ( ++idNum ); //$NON-NLS-1$

      // Can't use schemaMeta.findModel(id)... the compare fails if the case is different,
      // but the objectexistsexception is thrown regardless of case...
      for ( int i = 0; i < schemaMeta.nrBusinessModels(); i++ ) {
        BusinessModel businessModel = schemaMeta.getModel( i );
        if ( businessModel.getId().equalsIgnoreCase( id ) ) {
          found = true;
          continue;
        }
      }
    }

    return new String[] { id, Integer.toString( idNum ) };

  }

  public void exportLocale( String locale ) throws Exception {
    FileDialog dialog = new FileDialog( shell, SWT.SAVE );
    dialog.setFilterExtensions( new String[] { "*.properties", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    dialog.setFilterNames( new String[] {
      Messages.getString( "MetaEditor.USER_PROPERTIES_FILES" ),
      Messages.getString( "MetaEditor.USER_ALL_FILES" ) } ); //$NON-NLS-1$ //$NON-NLS-2$
    dialog.setFileName( "metadata_" + locale + ".properties" ); //$NON-NLS-1$
    String filename = dialog.open();

    if ( filename != null ) {
      File file = new File( filename );

      boolean writeToFile = true;

      if ( file.exists() ) {
        int result = SWT.NO;
        MessageBox mb = new MessageBox( shell, SWT.NO | SWT.YES | SWT.ICON_WARNING );
        mb.setMessage( Messages.getString( "MetaEditor.USER_PROPERTIES_FILE_EXISTS_OVERWRITE" ) ); //$NON-NLS-1$
        mb.setText( Messages.getString( "MetaEditor.USER_TITLE_PROPERTIES_FILE_EXISTS" ) ); //$NON-NLS-1$
        result = mb.open();
        if ( result == SWT.NO ) {
          writeToFile = false;
        }
      }

      if ( writeToFile ) {
        LocaleExportProgressDialog progDialog = new LocaleExportProgressDialog( shell, schemaMeta, locale, filename );
        progDialog.open();
      }
    }
  }

}
