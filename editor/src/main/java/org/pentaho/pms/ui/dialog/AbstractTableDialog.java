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


package org.pentaho.pms.ui.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.ui.concept.editor.ConceptModelRegistry;
import org.pentaho.pms.ui.concept.editor.Constants;
import org.pentaho.pms.ui.concept.editor.IConceptModel;
import org.pentaho.pms.ui.concept.editor.ITableModel;
import org.pentaho.pms.ui.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.ui.concept.editor.PropertyWidgetManager2;
import org.pentaho.pms.ui.concept.editor.TableColumnTreeWidget;

/**
 * Parent of <code>PhysicalTableDialog</code> and <code>BusinessTableDialog</code>.
 * 
 * <p>A note about <code>lastSelection</code>:</p>
 * <p>lastSelection keeps track of the last concept to which we successfully transitioned. Why is this necessary?
 * Because state on the screen may be invalid (e.g. user entered bogus value) then we must stop the transition, alert
 * the user, and switch back to the offending screen. Here, "screen" is a card in a stack layout.</p> 
 * 
 * @author mlowery
 */
public abstract class AbstractTableDialog extends Dialog implements ISelectionChangedListener {

  protected ITableModel tableModel;

  protected String activeLocale;

  protected Composite detailsComposite;

  protected StackLayout stackLayout;

  protected Composite cardComposite;

  protected Map<String,Locales> propertyEditorContext = new HashMap<String,Locales>();

  protected ConceptModelRegistry conceptModelRegistry = new ConceptModelRegistry();

  protected Map cards = new HashMap();

  protected Control defaultCard;

  protected TableColumnTreeWidget tableColumnTree;

  protected SchemaMeta schemaMeta;

  protected ToolItem viewButton;
  
  protected Text conceptIdText;

  private static final Log logger = LogFactory.getLog(AbstractTableDialog.class);
  
  protected ConceptUtilityInterface initialTableOrColumnSelection;

  protected ToolItem delButton;
  
  protected ConceptUtilityInterface lastSelection;
  
  protected Composite conceptEditor;
  
  PropertyWidgetManager2 propertyWidgetManager;
  PropertyNavigationWidget propertyNavigationWidget;
  
  public AbstractTableDialog(Shell parent) {
    super(parent);
  }

  protected void init(ITableModel tableModel, SchemaMeta schemaMeta, ConceptUtilityInterface selectedTableOrColumn) {
    this.tableModel = tableModel;
    this.schemaMeta = schemaMeta;
    Locales locales = schemaMeta.getLocales();
    activeLocale = locales.getActiveLocale();
    propertyEditorContext.put("locales", locales);
    initialTableOrColumnSelection = selectedTableOrColumn;
  }
  
  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Point getInitialSize() {
    return new Point(1200, 800);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new GridLayout(2, true));
    GridData gridData = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gridData);

    SashForm s0 = new SashForm(container, SWT.HORIZONTAL);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    gridData.heightHint = 20;
    gridData.minimumWidth = 500;
    s0.setLayoutData(gridData);


    detailsComposite = new Composite(s0, SWT.NONE);
    detailsComposite.setLayout(new GridLayout(2, false));

    Label wlList = new Label(detailsComposite, SWT.NONE);
    wlList.setText(Messages.getString("PhysicalTableDialog.USER_SUBJECT")); //$NON-NLS-1$

    ToolBar tb = new ToolBar(detailsComposite, SWT.FLAT);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalAlignment = SWT.END;
    tb.setLayoutData(gridData);

    viewButton = new ToolItem(tb, SWT.CHECK);

    viewButton.setToolTipText("Show IDs");
    viewButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("show-id-button"));
    viewButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        changeViewPressed();
      }
    });

    ToolItem sep = new ToolItem(tb, SWT.SEPARATOR);

    ToolItem addButton = new ToolItem(tb, SWT.PUSH);

    addButton.setToolTipText(Messages.getString("PhysicalTableDialog.USER_ADD_NEW_COLUMN")); //$NON-NLS-1$
    addButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-add-button"));
    addButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        addColumnPressed();
      }
    });

    delButton = new ToolItem(tb, SWT.PUSH);
    delButton.setToolTipText(Messages.getString("PhysicalTableDialog.USER_DELETE_COLUMN")); //$NON-NLS-1$
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("column-del-button"));
    delButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        delColumnPressed();
      }
    });

    tableColumnTree = new TableColumnTreeWidget(detailsComposite, SWT.SINGLE | SWT.BORDER, tableModel, true, schemaMeta.getActiveLocale());
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    tableColumnTree.getTree().setLayoutData(gridData);

    tableColumnTree.addSelectionChangedListener(this);

    cardComposite = new Composite(s0, SWT.NONE);
    stackLayout = new StackLayout();
    cardComposite.setLayout(stackLayout);

    defaultCard = new DefaultCard(cardComposite, SWT.NONE);
    conceptEditor = createConceptEditor();

    showConceptUtility(null);

    s0.setWeights(new int[] { 1, 3 });

    if (initialTableOrColumnSelection != null) {
      tableColumnTree.setSelection(new StructuredSelection(initialTableOrColumnSelection));
      conceptIdText.forceFocus();
      conceptIdText.selectAll();
    }

    return c0;
  }

  protected abstract void addColumnPressed();

  protected void okPressed() {
    cleanup();
    super.okPressed();
  }

  protected void cleanup() {
    tableColumnTree.removeSelectionChangedListener(this);
  }

  protected void delColumnPressed() {
    // get the currently selected column
    TreeSelection treeSel = (TreeSelection) tableColumnTree.getSelection();
    ConceptUtilityInterface conceptHolder = (ConceptUtilityInterface) treeSel.getFirstElement();
    boolean delete = MessageDialog.openConfirm(getShell(), "Confirm Column Delete",
        "Are you sure you want to delete the column with id '" + conceptHolder.getId() + "'?");
    if (delete) {
      tableModel.removeColumn(conceptHolder.getId());
    }
  }

  protected void changeViewPressed() {
    tableColumnTree.showId(viewButton.getSelection());
    if (viewButton.getSelection()) {
      viewButton.setToolTipText("Show Names");
    } else {
      viewButton.setToolTipText("Show IDs");
    }
  }

  protected void showConceptUtility(ConceptUtilityInterface cu) {
    if (cu == null) {
      if (stackLayout.topControl != defaultCard) {
        stackLayout.topControl = defaultCard;
        cardComposite.layout();
      }
    } else {
      if (stackLayout.topControl == defaultCard){
        stackLayout.topControl = conceptEditor;
        cardComposite.layout();
      }
      editConcept(cu);
    }
    lastSelection = cu;
  }
  
  protected abstract Composite createConceptEditor();
  
  protected void cancelPressed() {
    cleanup();
    super.cancelPressed();
  }

  /**
   * The card that shows when there is no selection in the table-column tree.
   */
  private class DefaultCard extends Composite {

    public DefaultCard(final Composite parent, final int style) {
      super(parent, style);
      createContents();
    }

    private void createContents() {
      setLayout(new GridLayout());
      Label lab0 = new Label(this, SWT.CENTER);
      lab0.setText("Select the table or any of its columns to begin editing properties.");
      GridData gd = new GridData();
      gd.verticalAlignment = GridData.CENTER;
      gd.horizontalAlignment = GridData.CENTER;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      lab0.setLayoutData(gd);
    }

  }

  public void selectionChanged(SelectionChangedEvent e) {
    if (!e.getSelection().isEmpty()) {
      TreeSelection treeSel = (TreeSelection) e.getSelection();
      if (treeSel.getFirstElement() instanceof ConceptUtilityInterface) {
        ConceptUtilityInterface cu = (ConceptUtilityInterface) treeSel.getFirstElement();
        if (tableModel.isColumn(cu)) {
          delButton.setEnabled(true);
        } else {
          delButton.setEnabled(false);
        }
        showConceptUtility(cu);
      } else {
        showConceptUtility(null);
      }
    } else {
      showConceptUtility(null);
    }
  }

  protected void configureShell(Shell arg0) {
    super.configureShell(arg0);
    arg0.setImage(null);
  }
  
  protected void editConcept(ConceptUtilityInterface cu) {
    ConceptInterface concept = cu.getConcept();
    IConceptModel conceptModel = conceptModelRegistry.getConceptModel(concept);
    
    propertyNavigationWidget.removeSelectionChangedListener(propertyWidgetManager);
    
    propertyNavigationWidget.setConceptModel(conceptModel);
    propertyWidgetManager.setConceptModel(conceptModel);
    
    propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
    
    conceptIdText.setText(cu.getId());
  }
  
  protected boolean popupValidationErrorDialogIfNecessary() {
    List<String> errorMessages = propertyWidgetManager.validateWidgets();
    if (errorMessages.isEmpty()) {
      return false;
    } else {
      StringBuilder buf = new StringBuilder();
      for (String errorMessage : errorMessages) {
        buf.append(errorMessage + "\n");
      }
      MessageDialog.openError(getShell(), "Errors", buf.toString());
      return true;
    }
  }
}
