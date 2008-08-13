package org.pentaho.pms.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.ui.concept.editor.AvailSecurityOwnersTableViewer;
import org.pentaho.pms.ui.concept.editor.Constants;

public class RoleBasedConstraintDialog extends TitleAreaDialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(RoleBasedConstraintDialog.class);

  public static enum Mode {
    ADD, EDIT
  }

  // ~ Instance fields ===================================================================================================

  private Mode mode;

  private AvailSecurityOwnersTableViewer availableOwnersViewer;

  private SecurityReference securityReference;

  private List<SecurityOwner> usedOwners;

  private SecurityOwner ownerToEdit;

  private Text formulaField;

  private Label ownerField;

  private String formulaToEdit;

  private TableSelectionListener tableSelectionListener;

  private FormulaModifyListener formulaModifyListener;

  /**
   * Saved when the user clicks OK.
   */
  private String formula;
  
  /**
   * Saved when the user clicks OK.
   */
  private List<SecurityOwner> addedOwners;
  
  // ~ Constructors ======================================================================================================

  public RoleBasedConstraintDialog(Shell parentShell, SecurityReference securityReference,
      List<SecurityOwner> usedOwners) {
    super(parentShell);
    this.usedOwners = usedOwners;
    this.securityReference = securityReference;
    this.mode = Mode.ADD;
  }

  /**
   * Puts the dialog in EDIT mode.
   */
  public RoleBasedConstraintDialog(Shell parentShell, SecurityOwner ownerToEdit, String formulaToEdit) {
    super(parentShell);
    this.ownerToEdit = ownerToEdit;
    this.formulaToEdit = formulaToEdit;
    this.mode = Mode.EDIT;
  }

  // ~ Methods ===========================================================================================================

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    if (mode == Mode.ADD) {
      shell.setText("Add");
    } else {
      shell.setText("Edit");
    }
  }

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);
    Composite c1 = new Composite(c0, SWT.NONE);
    c1.setLayoutData(new GridData(GridData.FILL_BOTH));
    c1.setLayout(new FormLayout());
    if (mode == Mode.ADD) {
      setTitle("Add Rules");
      setMessage("Select one or more roles then enter a formula.");
    } else {
      setTitle("Edit Rule");
      setMessage("Edit the formula associated with a role.");
    }

    if (mode == Mode.ADD) {
      Label availableLabel = new Label(c1, SWT.NULL);
      availableLabel.setText("Available:");
      FormData fdAvailLabel = new FormData();
      fdAvailLabel.left = new FormAttachment(0, 10);
      fdAvailLabel.top = new FormAttachment(0, 10);
      availableLabel.setLayoutData(fdAvailLabel);

      availableOwnersViewer = new AvailSecurityOwnersTableViewer(c1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI
          | SWT.H_SCROLL | SWT.V_SCROLL, securityReference, usedOwners);
      FormData fdAvailOwners = new FormData();
      fdAvailOwners.left = new FormAttachment(0, 10);
      fdAvailOwners.top = new FormAttachment(availableLabel, 10);
      fdAvailOwners.right = new FormAttachment(40, 0);
      fdAvailOwners.bottom = new FormAttachment(100, -10);
      availableOwnersViewer.getTable().setLayoutData(fdAvailOwners);
    } else {
      Label ownerLabel = new Label(c1, SWT.NULL);
      ownerLabel.setText("Owner:");
      FormData fdOwnerLabel = new FormData();
      fdOwnerLabel.left = new FormAttachment(0, 10);
      fdOwnerLabel.top = new FormAttachment(0, 10);
      ownerLabel.setLayoutData(fdOwnerLabel);

      Label icon = new Label(c1, SWT.NULL);
      if (ownerToEdit.getOwnerType() == SecurityOwner.OWNER_TYPE_ROLE) {
        icon.setImage(Constants.getImageRegistry(Display.getCurrent()).get("role-icon"));
      } else {
        icon.setImage(Constants.getImageRegistry(Display.getCurrent()).get("user-icon"));
      }
      FormData fdIcon = new FormData();
      fdIcon.left = new FormAttachment(0, 10);
      fdIcon.top = new FormAttachment(ownerLabel, 10);
      icon.setLayoutData(fdIcon);

      ownerField = new Label(c1, SWT.NULL);
      FormData fdOwnerField = new FormData();
      fdOwnerField.left = new FormAttachment(icon, 5);
      fdOwnerField.top = new FormAttachment(ownerLabel, 10);
      fdOwnerField.right = new FormAttachment(40, 0);
      ownerField.setLayoutData(fdOwnerField);
      ownerField.setText(ownerToEdit.getOwnerName());
    }

    Label formulaLabel = new Label(c1, SWT.NULL);
    formulaLabel.setText("Constraint:");
    FormData fdFormulaLabel = new FormData();
    fdFormulaLabel.left = new FormAttachment(mode == Mode.ADD ? availableOwnersViewer.getTable() : ownerField, 10);
    fdFormulaLabel.top = new FormAttachment(0, 10);
    formulaLabel.setLayoutData(fdFormulaLabel);

    formulaField = new Text(c1, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    FormData fdFormulaField = new FormData();
    fdFormulaField.left = new FormAttachment(mode == Mode.ADD ? availableOwnersViewer.getTable() : ownerField, 10);
    fdFormulaField.top = new FormAttachment(formulaLabel, 10);
    fdFormulaField.right = new FormAttachment(100, -10);
    fdFormulaField.bottom = new FormAttachment(100, -10);
    formulaField.setLayoutData(fdFormulaField);
    if (mode == Mode.EDIT) {
      formulaField.setText(formulaToEdit);
    }

    formulaField.setFont(Constants.getFontRegistry(Display.getCurrent()).get("formula-editor-font"));

    if (mode == Mode.ADD) {
      tableSelectionListener = new TableSelectionListener();
      availableOwnersViewer.getTable().addSelectionListener(tableSelectionListener);
    }

    formulaModifyListener = new FormulaModifyListener();
    formulaField.addModifyListener(formulaModifyListener);

    //    predefinedButton = new Button(c1, SWT.RADIO);
    //    FormData fdPreDefButton = new FormData();
    //    fdPreDefButton.left = new FormAttachment(0, 10);
    //    fdPreDefButton.top = new FormAttachment(0, 10);
    //    predefinedButton.setLayoutData(fdPreDefButton);
    //
    //    predefinedButton.setText("Add a pre-defined property");
    //    predefinedButton.addSelectionListener(new DisableFieldsListener());
    //    propertyTree = new PropertyTreeWidget(c1, PropertyTreeWidget.SHOW_UNUSED, false);
    //    propertyTree.setConceptModel(conceptModel);
    //    propertyTree.addSelectionChangedListener(new ISelectionChangedListener() {
    //      public void selectionChanged(final SelectionChangedEvent e) {
    //        validatePredefined();
    //      }
    //
    //    });
    //
    //    customButton = new Button(c1, SWT.RADIO);
    //
    //    FormData fdTree = new FormData();
    //    fdTree.left = new FormAttachment(0, 10);
    //    fdTree.top = new FormAttachment(predefinedButton, 10);
    //    fdTree.right = new FormAttachment(100, -10);
    //    fdTree.bottom = new FormAttachment(65, 0);
    //    propertyTree.getTree().setLayoutData(fdTree);
    //
    //    FormData fdCustomButton = new FormData();
    //    fdCustomButton.left = new FormAttachment(0, 10);
    //    fdCustomButton.top = new FormAttachment(propertyTree.getTree(), 10);
    //    customButton.setLayoutData(fdCustomButton);
    //
    //    customButton.setText("Add a custom property");
    //    customButton.addSelectionListener(new DisableFieldsListener());
    //
    //    Composite c2 = new Composite(c1, SWT.NONE);
    //    FormData fdC2 = new FormData();
    //    fdC2.left = new FormAttachment(0, 0);
    //    fdC2.top = new FormAttachment(customButton, 10);
    //    fdC2.right = new FormAttachment(100, 0);
    //    c2.setLayoutData(fdC2);
    //
    //    c2.setLayout(new FormLayout());
    //
    //    Label lab1 = new Label(c2, SWT.RIGHT);
    //    idField = new Text(c2, SWT.BORDER);
    //    Label lab2 = new Label(c2, SWT.RIGHT);
    //    typeField = new Combo(c2, SWT.NONE | SWT.READ_ONLY);
    //
    //    lab1.setText("ID:");
    //    FormData fdLab1 = new FormData();
    //    fdLab1.left = new FormAttachment(0, 10);
    //    fdLab1.top = new FormAttachment(idField, 0, SWT.CENTER);
    //    lab1.setLayoutData(fdLab1);
    //
    //    // default to predefined property (the other radio group)
    //    idField.setEnabled(false);
    //
    //    idField.addModifyListener(new ModifyListener() {
    //
    //      public void modifyText(ModifyEvent e) {
    //        validateCustom();
    //      }
    //
    //    });
    //    FormData fdIdField = new FormData();
    //    fdIdField.left = new FormAttachment(lab1, 10);
    //    fdIdField.right = new FormAttachment(100, -10);
    //    idField.setLayoutData(fdIdField);
    //
    //    lab2.setText("Type:");
    //    FormData fdLab2 = new FormData();
    //    fdLab2.left = new FormAttachment(0, 10);
    //    fdLab2.top = new FormAttachment(typeField, 0, SWT.CENTER);
    //    lab2.setLayoutData(fdLab2);
    //
    //    // default to predefined property (the other radio group)
    //    typeField.setEnabled(false);
    //    FormData fdTypeField = new FormData();
    //    fdTypeField.left = new FormAttachment(lab2, 10);
    //    fdTypeField.right = new FormAttachment(100, -10);
    //    fdTypeField.top = new FormAttachment(idField, 10);
    //    typeField.setLayoutData(fdTypeField);
    //
    //    comboViewer = new ComboViewer(typeField);
    //    comboViewer.setContentProvider(new IStructuredContentProvider() {
    //      public Object[] getElements(final Object inputElement) {
    //        List ul = (List) inputElement;
    //        return ul.toArray();
    //      }
    //
    //      public void dispose() {
    //        if (logger.isDebugEnabled()) {
    //          logger.debug("Disposing ...");
    //        }
    //      }
    //
    //      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    //        if (logger.isDebugEnabled()) {
    //          logger.debug("Input changed: old=" + oldInput + ", new=" + newInput);
    //        }
    //      }
    //    });
    //
    //    List<Object> list2 = new ArrayList<Object>();
    //    list2.add("");
    //    list2.addAll(Arrays.asList(ConceptPropertyType.propertyTypes));
    //
    //    comboViewer.setInput(list2);
    //
    //    comboViewer.setLabelProvider(new LabelProvider() {
    //      public Image getImage(Object element) {
    //        return null;
    //      }
    //
    //      public String getText(Object element) {
    //        if (element instanceof ConceptPropertyType) {
    //          ConceptPropertyType type = (ConceptPropertyType) element;
    //          if (logger.isDebugEnabled()) {
    //            logger.debug("desc: " + type.getDescription());
    //          }
    //          return type.getDescription();
    //        } else {
    //          if (logger.isDebugEnabled()) {
    //            logger.debug("obj class: " + element.getClass());
    //          }
    //          return "";
    //        }
    //      }
    //    });
    //
    //    comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
    //
    //      public void selectionChanged(SelectionChangedEvent e) {
    //        validateCustom();
    //      }
    //
    //    });
    //
    //    predefinedButton.setSelection(true);
    return c0;
  }

  protected void validate() {
    if (mode == Mode.ADD) {
      if (availableOwnersViewer.getTable().getSelection() != null
          && availableOwnersViewer.getTable().getSelection().length > 0
          && StringUtils.isNotBlank(formulaField.getText())) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
      } else {
        getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
    } else {
      // in edit mode, only enable OK button if the formula is not blank AND they have made changes to the original 
      // formula
      if (StringUtils.isNotBlank(formulaField.getText()) && !formulaToEdit.equals(formulaField.getText())) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
      } else {
        getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
    }

  }

  private void validatePredefined() {
    //    IStructuredSelection structuredSelection = (StructuredSelection)propertyTree.getSelection();
    //    Object objectSelected = structuredSelection.getFirstElement();
    //    if (!(objectSelected instanceof PropertyTreeWidget.GroupNode)) {
    //      setErrorMessage(null);
    //      getButton(IDialogConstants.OK_ID).setEnabled(true);
    //    } else {
    //      setErrorMessage("Please select a property within a group.");
    //      getButton(IDialogConstants.OK_ID).setEnabled(false);
    //    }
  }

  private void validateCustom() {
    //    if (StringUtils.isBlank(idField.getText())) {
    //      setErrorMessage("Please enter an ID.");
    //      getButton(IDialogConstants.OK_ID).setEnabled(false);
    //      return;
    //    } else if (isPredefinedPropertyId(idField.getText())) {
    //      setErrorMessage("The ID entered cannot be a pre-defined property ID. Please enter a different ID.");
    //      getButton(IDialogConstants.OK_ID).setEnabled(false);
    //      return;
    //    }  else {
    //      setErrorMessage(null);
    //      getButton(IDialogConstants.OK_ID).setEnabled(true);
    //    }
    //
    //    IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
    //    if (selection.getFirstElement() instanceof ConceptPropertyType) {
    //      setErrorMessage(null);
    //      getButton(IDialogConstants.OK_ID).setEnabled(true);
    //    } else {
    //      setErrorMessage("Please select a type.");
    //      getButton(IDialogConstants.OK_ID).setEnabled(false);
    //    }
  }

  //  /**
  //   * Returns true if the given id is a pre-defined property id.
  //   */
  //  private boolean isPredefinedPropertyId(final String propertyId) {
  //    String[] propertyIds = DefaultPropertyID.getDefaultPropertyIDs();
  //    for (int i = 0; i < propertyIds.length; i++) {
  //      if (propertyIds[i].equals(propertyId)) {
  //        return true;
  //      }
  //    }
  //    return false;
  //  }

  protected Control createContents(Composite parent) {
    // start with the OK button disabled
    Control c = super.createContents(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    return c;
  }

  protected Point getInitialSize() {
    return new Point(570, 500);
  }

  protected void okPressed() {
    // save what's changed in the dialog for when user calls getFormula or getAddedOwners
    if (mode == Mode.ADD) {
      List<SecurityOwner> addedOwners = new ArrayList<SecurityOwner>();
      IStructuredSelection sel = (IStructuredSelection) availableOwnersViewer.getSelection();
      Object[] selectedItems = sel.toArray();
      for (int i = 0; i < selectedItems.length; i++) {
        SecurityOwner owner = (SecurityOwner) selectedItems[i];
        addedOwners.add(owner);
      }
      this.addedOwners = addedOwners;
    }
    formula = formulaField.getText();
    super.okPressed();
  }

  private class TableSelectionListener implements SelectionListener {

    public void widgetDefaultSelected(SelectionEvent e) {
      validate();
    }

    public void widgetSelected(SelectionEvent e) {
      validate();
    }

  }

  private class FormulaModifyListener implements ModifyListener {

    public void modifyText(ModifyEvent e) {
      validate();
    }

  }

  public String getFormula() {
    return formula;
  }

  public List<SecurityOwner> getAddedOwners() {
    return addedOwners;
  }

}