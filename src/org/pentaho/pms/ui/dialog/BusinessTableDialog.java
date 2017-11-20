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

package org.pentaho.pms.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.metadata.util.Util;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.ui.concept.editor.BusinessTableModel;
import org.pentaho.pms.ui.concept.editor.PropertyNavigationWidget;
import org.pentaho.pms.ui.concept.editor.PropertyWidgetManager2;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueList;

public class BusinessTableDialog extends AbstractTableDialog implements SelectionListener {

  private static final Log logger = LogFactory.getLog(BusinessTableDialog.class);

  private Combo physicalTableText;

  private Label physicalTableLabel;

  private BusinessTable businessTable;
  
  private List<BusinessColumn> otherBusinessColumns = new ArrayList<BusinessColumn>();

  HashMap<Object, Object> modificationsMap = new HashMap<Object, Object>();

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.getString("BusinessTableDialog.USER_BUSINESS_TABLE_PROPERTIES"));
  }

  public BusinessTableDialog(Shell parent, BusinessColumn businessColumn, SchemaMeta schemaMeta) {
    super(parent);
    BusinessTable originalBusinessTable = businessColumn.getBusinessTable();
    businessTable = (BusinessTable) originalBusinessTable.clone();
    BusinessTableModel tableModel = new BusinessTableModel(businessTable, schemaMeta.getActiveModel());
    initModificationsMap(originalBusinessTable, businessTable);
    init(tableModel, schemaMeta, businessColumn);
    otherBusinessColumns = getOtherBusinessColumns(originalBusinessTable);

  }

  public BusinessTableDialog(Shell parent, BusinessTable originalBusinessTable, SchemaMeta schemaMeta) {
    super(parent);
    businessTable = (BusinessTable) originalBusinessTable.clone();
    BusinessTableModel tableModel = new BusinessTableModel(businessTable, schemaMeta.getActiveModel());
    initModificationsMap(originalBusinessTable, businessTable);
    init(tableModel, schemaMeta, businessTable);
    otherBusinessColumns = getOtherBusinessColumns(originalBusinessTable);

  }

  private void initModificationsMap(BusinessTable origBusinessTable, BusinessTable workingBusinessTable) {
    modificationsMap.put(workingBusinessTable, origBusinessTable);
    List workingBusinessColumns = workingBusinessTable.getBusinessColumns().getList();
    for (Iterator workingIter = workingBusinessColumns.iterator(); workingIter.hasNext();) {
      BusinessColumn workingBusinessColumn = (BusinessColumn) workingIter.next();
      List origBusinessColumns = origBusinessTable.getBusinessColumns().getList();
      for (Iterator origIter = origBusinessColumns.iterator(); origIter.hasNext();) {
        BusinessColumn origBusinessColumn = (BusinessColumn) origIter.next();
        if (origBusinessColumn.equals(workingBusinessColumn)) {
          modificationsMap.put(workingBusinessColumn, origBusinessColumn);
          break;
        }
      }
    }
  }

  protected void addColumnPressed() {
    PhysicalTable physicalTable = schemaMeta.findPhysicalTable(physicalTableText.getText());
    if (physicalTable != null) {
      AddBusinessColumnDialog dialog = new AddBusinessColumnDialog(getShell(), tableModel, schemaMeta.getActiveLocale());
      dialog.open();
    } else {
      showMissingPhysicalTableError();
    }
  }

  private void showMissingPhysicalTableError() {
    MessageDialog.openError(getShell(), "Error", "You must select a physical table first.");
  }

  protected void okPressed() {
    boolean hasErrors = popupValidationErrorDialogIfNecessary();
    if ( !hasErrors ) {
      try {
        if ( lastSelection != null ) {
          String id = conceptIdText.getText();
          if ( id.trim().length() == 0 || !Util.validateId( id ) ) {
            MessageDialog.openError( getShell(), Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString(
              "BusinessTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText() ) );
            tableColumnTree.setSelection( new StructuredSelection( lastSelection ) );
            conceptIdText.forceFocus();
            conceptIdText.selectAll();
          } else {
            // if selection is business column, also verify current column ids aren't being used
            if ( lastSelection instanceof BusinessColumn ) {
              validateBusinessColumnUniqueness();
            }
            lastSelection.setId( conceptIdText.getText() );
            updateOriginalBusinessTable();
            super.okPressed();
          }
        } else {
          updateOriginalBusinessTable();
          super.okPressed();
        }
      } catch ( ObjectAlreadyExistsException e ) {
        if ( logger.isErrorEnabled() ) {
          logger.error( "an exception occurred", e );
        }
        MessageDialog.openError( getShell(), Messages.getString( "General.USER_TITLE_ERROR" ), Messages.getString(
          "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", conceptIdText.getText() ) );
      }
    }
  }

  public void selectionChanged(SelectionChangedEvent e) {
    if (lastSelection != null && lastSelection.equals(((StructuredSelection) e.getSelection()).getFirstElement())) {
      return;
    }

    boolean hasErrors = popupValidationErrorDialogIfNecessary();
    if (!hasErrors) {
      if (lastSelection != null) {
        try {
          String id = conceptIdText.getText();
          if (id.trim().length() == 0) {
            MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
                "BusinessTableDialog.USER_ERROR_INVALID_ID", conceptIdText.getText()));
            tableColumnTree.setSelection(new StructuredSelection(lastSelection));
            conceptIdText.forceFocus();
            conceptIdText.selectAll();
          } else {
            // if selection is business column, also verify current column ids aren't being used
            if (lastSelection instanceof BusinessColumn) {
              validateBusinessColumnUniqueness();
            }
            lastSelection.setId(conceptIdText.getText());
            super.selectionChanged(e);
          }
        } catch (ObjectAlreadyExistsException e1) {
          if (logger.isErrorEnabled()) {
            logger.error("an exception occurred", e1);
          }
          MessageDialog.openError(getShell(), Messages.getString("General.USER_TITLE_ERROR"), Messages.getString(
              "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", conceptIdText.getText()));
        }
      } else {
        super.selectionChanged(e);
      }
    } else {
      // set selection back where it was
      if (!lastSelection.equals(((StructuredSelection) e.getSelection()).getFirstElement())) {
        tableColumnTree.setSelection(new StructuredSelection(lastSelection));
      }
    }

  }
  
  /**
   * Returns all business columns
   *
   * @return a UniqueList of all business columns in this model
   */
  public List<BusinessColumn> getOtherBusinessColumns(BusinessTable table) {
    List<BusinessColumn> columns = new ArrayList<BusinessColumn>();
    for (int i = 0; i < schemaMeta.getActiveModel().nrBusinessTables(); i++) {
      BusinessTable businessTable = schemaMeta.getActiveModel().getBusinessTable(i);
      if (!table.equals(businessTable)) {
        for (int j = 0; j < businessTable.nrBusinessColumns(); j++) {
          columns.add(businessTable.getBusinessColumn(j));
        }
      }
    }
    return columns;
  }
  
  private void validateBusinessColumnUniqueness() throws ObjectAlreadyExistsException {
    for (int i =0; i < otherBusinessColumns.size(); i++) {
      ConceptUtilityBase base = (ConceptUtilityBase) otherBusinessColumns.get(i);
      if (base != lastSelection && base.getId().equals(conceptIdText.getText())) {
        // This is a problem...
        throw new ObjectAlreadyExistsException(Messages.getString(
            "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", conceptIdText.getText())); //$NON-NLS-1$
      }
    }
    UniqueList bizCols = businessTable.getBusinessColumns();
    for (int i =0; i < bizCols.size(); i++) {
      ConceptUtilityBase base = (ConceptUtilityBase) bizCols.get(i);
      if (base != lastSelection && base.getId().equals(conceptIdText.getText())) {
        // This is a problem...
        throw new ObjectAlreadyExistsException(Messages.getString(
            "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", conceptIdText.getText())); //$NON-NLS-1$
      }
    }
  }

  private void updateOriginalBusinessTable() {
    // Find the original physical table.
    BusinessTable origTable = null;
    for (Iterator iterator = modificationsMap.values().iterator(); iterator.hasNext() && (origTable == null);) {
      Object target = iterator.next();
      if (target instanceof BusinessTable) {
        origTable = (BusinessTable) target;
      }
    }

    // Remove any columns from the original physical table that were removed from the working copy.
    ArrayList<Map.Entry> entriesToRemove = new ArrayList<Map.Entry>();
    Set<Map.Entry<Object, Object>> entrySet = modificationsMap.entrySet();
    for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
      boolean found = false;
      Map.Entry entry = (Map.Entry) iterator.next();
      if (entry.getKey() instanceof BusinessColumn) {
        ConceptUtilityInterface[] workingColumns = tableModel.getColumns();
        for (int i = 0; (i < workingColumns.length) && !found; i++) {
          found = (workingColumns[i] == entry.getKey());
        }
        if (!found) {
          BusinessColumn column = origTable.findBusinessColumn(((BusinessColumn) entry.getValue()).getId());
          // check if any complex joins reference it and warn about them
          warnCannotUpdateRelations(getAffectedRelationships(column));
          int index = origTable.indexOfBusinessColumn(column);
          origTable.removeBusinessColumn(index);
          entriesToRemove.add(entry);
        }
      }
    }
    entrySet.removeAll(entriesToRemove);

    // Update the remaining columns in the physical table with the working info.
    for (Iterator iterator = modificationsMap.entrySet().iterator(); iterator.hasNext();) {
      Map.Entry entry = (Map.Entry) iterator.next();
      ConceptUtilityInterface origConcept = (ConceptUtilityInterface) entry.getValue();
      ConceptUtilityInterface workingConcept = (ConceptUtilityInterface) entry.getKey();
      if (!StringUtils.equals(origConcept.getId(), workingConcept.getId())) {
        List<BusinessModel.RelationFormulaUpdate> updated = updateComplexRelationships(origConcept, workingConcept);
        try {
          origConcept.setId(workingConcept.getId());
        } catch (ObjectAlreadyExistsException e) {
          // This should not happen as this exception would already have been caught earlier...
        }
        if (updated != null) {
          // test updated relation after change
          checkRelationshipUpdates(updated);
        }
      }
      origConcept.getConcept().clearChildProperties();
      origConcept.getConcept().getChildPropertyInterfaces().putAll(
          workingConcept.getConcept().getChildPropertyInterfaces());
    }

    // Add any columns from the working table that don't exist in the original table.
    ConceptUtilityInterface[] workingColumns = tableModel.getColumns();
    for (int i = 0; i < workingColumns.length; i++) {
      boolean found = false;
      for (Iterator iterator = modificationsMap.keySet().iterator(); iterator.hasNext() && !found;) {
        found = (workingColumns[i] == iterator.next());
      }
      if (!found) {
        try {
          origTable.addBusinessColumn((BusinessColumn) workingColumns[i]);
        } catch (ObjectAlreadyExistsException e) {
          // This should not happen as this exception would already have been caught earlier...
        }
      }
    }
  }

  /**
   * @param updated
   */
  private void checkRelationshipUpdates(List<BusinessModel.RelationFormulaUpdate> updated) {
      Map<RelationshipMeta, String> relationsToTest = new HashMap<RelationshipMeta, String>();
      Collection<RelationshipMeta> badRelations = new HashSet<RelationshipMeta>();
      for (BusinessModel.RelationFormulaUpdate update : updated) {
        if (update.getErrors().isEmpty()) {
          // still need to test it
          relationsToTest.put(update.getRelationship(), update.getFormulaBefore());
        }
        else {
          // not good
          badRelations.add(update.getRelationship());
        }
      }
      for (RelationshipMeta relation : relationsToTest.keySet()) {
        try {
          // did the update go well?
          relation.getComplexJoinFormula(schemaMeta.getActiveModel()).parseAndValidate();
        } catch (Exception e) {
          // nope, undo
          relation.setComplexJoin(relationsToTest.get(relation));
          badRelations.add(relation);
        }
      }
      warnCannotUpdateRelations(badRelations);
  }

  private void warnCannotUpdateRelations(Collection<RelationshipMeta> badRelations) {
    if (!badRelations.isEmpty()) {
      String msg = Messages.getString("BusinessTableDialog.CANNOT_UPDATE_COMPLEX_JOIN_DESC");
      for (RelationshipMeta relationship : badRelations) {
        //just give out a warning that manual intervention is required
        msg += System.getProperty("line.separator");
        msg += relationship.toString();
      }
      MessageDialog.openError(getShell() ,  Messages.getString("BusinessTableDialog.CANNOT_UPDATE_COMPLEX_JOIN_TITLE"), msg);
    }
  }

  private Collection<RelationshipMeta> getAffectedRelationships(BusinessColumn column) {
    return schemaMeta.getActiveModel().getAffectedComplexRelationships(column);
  }

  private List<BusinessModel.RelationFormulaUpdate> updateComplexRelationships(ConceptUtilityInterface origConcept, ConceptUtilityInterface workingConcept) {
    List<BusinessModel.RelationFormulaUpdate> updated = null;
    if (origConcept instanceof BusinessTable) {
      return schemaMeta.getActiveModel().updateComplexRelationships((BusinessTable) origConcept, (BusinessTable) workingConcept);
    }
    else if (origConcept instanceof BusinessColumn) {
      return schemaMeta.getActiveModel().updateComplexRelationships((BusinessColumn) origConcept, (BusinessColumn) workingConcept);
    }
    return null;
  }

  protected void editConcept(ConceptUtilityInterface cu) {
    if (cu instanceof BusinessTable) {
      physicalTableLabel.setVisible(true);
      physicalTableText.setVisible(true);
    } else {
      physicalTableLabel.setVisible(false);
      physicalTableText.setVisible(false);
    }

    super.editConcept(cu);
  }

  protected Composite createConceptEditor() {

    Composite conceptEditor = new Composite(cardComposite, SWT.NONE);
    conceptEditor.setLayout(new FillLayout());

    Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
    group.setText("Properties");
    group.setLayout(new GridLayout());

    SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
    s0.SASH_WIDTH = 5;

    Composite leftComposite = new Composite(s0, SWT.NONE);
    leftComposite.setLayout(new GridLayout());
    Label wlId = new Label(leftComposite, SWT.RIGHT);
    wlId.setText(Messages.getString("PhysicalTableDialog.USER_NAME_ID")); //$NON-NLS-1$
    conceptIdText = new Text(leftComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    conceptIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    propertyNavigationWidget = new PropertyNavigationWidget(leftComposite, SWT.NONE);
    propertyNavigationWidget.setLayoutData(new GridData(GridData.FILL_BOTH));

    Composite rightComposite = new Composite(s0, SWT.NONE);
    rightComposite.setLayout(new GridLayout());
    physicalTableLabel = new Label(rightComposite, SWT.RIGHT);
    physicalTableLabel.setText(Messages.getString("BusinessTableDialog.USER_PHYSICAL_TABLE")); //$NON-NLS-1$
    physicalTableText = new Combo(rightComposite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
    physicalTableText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    int selectedIndex = -1;
    for (int i = 0; i < schemaMeta.nrTables(); i++) {
      physicalTableText.add(schemaMeta.getTable(i).getId());
      if (null != businessTable.getPhysicalTable()
          && businessTable.getPhysicalTable().getId().equals(schemaMeta.getTable(i).getId())) {
        selectedIndex = i;
      }
    }
    if (-1 != selectedIndex) {
      physicalTableText.select(selectedIndex);
    }

    physicalTableText.addSelectionListener(this);

    propertyWidgetManager = new PropertyWidgetManager2(rightComposite, SWT.NONE, propertyEditorContext, schemaMeta
        .getSecurityReference());
    propertyWidgetManager.setLayoutData(new GridData(GridData.FILL_BOTH));

    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 20;
    s0.setLayoutData(gridData);
    s0.setWeights(new int[] { 1, 2 });

    if (tableModel.getId() != null) {
      conceptIdText.setText(tableModel.getId());
      if (initialTableOrColumnSelection == null) {
        conceptIdText.selectAll();
      }
    }

    return conceptEditor;
  }

  public void widgetDefaultSelected(SelectionEvent arg0) { }

  public void widgetSelected(SelectionEvent arg0) {
    tableModel.setParent(schemaMeta.findPhysicalTable(physicalTableText.getText()));
  }

}
