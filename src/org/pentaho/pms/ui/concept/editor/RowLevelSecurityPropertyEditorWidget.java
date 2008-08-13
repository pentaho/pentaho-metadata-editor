package org.pentaho.pms.ui.concept.editor;

import static org.pentaho.pms.schema.security.RowLevelSecurity.Type.GLOBAL;
import static org.pentaho.pms.schema.security.RowLevelSecurity.Type.NONE;
import static org.pentaho.pms.schema.security.RowLevelSecurity.Type.ROLEBASED;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.SecurityOwner;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.RowLevelSecurity.Type;

/**
 * Widget to represent the row level security concept property.
 * 
 * TODO This class reaches into all kinds of data structures instead of properly listening to events. (Widgets that
 * compose this widget provide access to their internal data structures, etc.  Bad!
 * 
 * @author mlowery
 */
public class RowLevelSecurityPropertyEditorWidget extends AbstractPropertyEditorWidget {

  // ~ Static fields/initializers ====================================================================================== 

  private static final Log logger = LogFactory.getLog(RowLevelSecurityPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Button noneRadio;

  private Button globalRadio;

  private Button roleBasedRadio;

  private RlsGlobalConstraintWidget globalWidget;

  private RlsRoleBasedConstraintWidget roleBasedWidget;

  private SecurityReference securityReference;

  // ~ Constructors ====================================================================================================

  public RowLevelSecurityPropertyEditorWidget(final Composite parent, final int style,
      final IConceptModel conceptModel, final String propertyId, final Map context,
      final SecurityReference securityReference) {
    super(parent, style, conceptModel, propertyId, context, true);
    this.securityReference = securityReference;
    createContents();
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created RowLevelSecurityPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  @Override
  public void cleanup() {
    // TODO Auto-generated method stub 

  }

  @Override
  protected void createContents(Composite parent) {
    noneRadio = new Button(parent, SWT.RADIO);
    noneRadio.setText("None");
    FormData fdNone = new FormData();
    fdNone.left = new FormAttachment(0, 0);
    fdNone.top = new FormAttachment(0, 0);
    fdNone.right = new FormAttachment(100, 0);
    noneRadio.setLayoutData(fdNone);
    noneRadio.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
        updateRadioButtons(NONE);
      }

      public void widgetSelected(SelectionEvent e) {
        updateRadioButtons(NONE);
      }

    });

    globalRadio = new Button(parent, SWT.RADIO);
    globalRadio.setText("Enforce Global Constraint:");
    FormData fdGlobal = new FormData();
    fdGlobal.left = new FormAttachment(0, 0);
    fdGlobal.top = new FormAttachment(noneRadio, 10);
    fdGlobal.right = new FormAttachment(100, 0);
    globalRadio.setLayoutData(fdGlobal);
    globalRadio.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
        updateRadioButtons(GLOBAL);
      }

      public void widgetSelected(SelectionEvent e) {
        updateRadioButtons(GLOBAL);
      }

    });

    globalWidget = new RlsGlobalConstraintWidget(parent, SWT.NONE);
    FormData fdGlobalWidget = new FormData();
    fdGlobalWidget.left = new FormAttachment(0, 20);
    fdGlobalWidget.top = new FormAttachment(globalRadio, 0);
    fdGlobalWidget.right = new FormAttachment(100, 0);
    globalWidget.setLayoutData(fdGlobalWidget);

    roleBasedRadio = new Button(parent, SWT.RADIO);
    roleBasedRadio.setText("Role Based Constraints:");
    FormData fdRoleBased = new FormData();
    fdRoleBased.left = new FormAttachment(0, 0);
    fdRoleBased.top = new FormAttachment(globalWidget, 10);
    fdRoleBased.right = new FormAttachment(100, 0);
    roleBasedRadio.setLayoutData(fdRoleBased);
    roleBasedRadio.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
        updateRadioButtons(ROLEBASED);
      }

      public void widgetSelected(SelectionEvent e) {
        updateRadioButtons(ROLEBASED);
      }

    });
    Map<SecurityOwner, String> constraintMap = ((RowLevelSecurity) getProperty().getValue())
        .getRoleBasedConstraintMap();
    roleBasedWidget = new RlsRoleBasedConstraintWidget(parent, SWT.NONE, securityReference, constraintMap);
    FormData fdRoleBasedWidget = new FormData();
    fdRoleBasedWidget.left = new FormAttachment(0, 20);
    fdRoleBasedWidget.top = new FormAttachment(roleBasedRadio, 0);
    fdRoleBasedWidget.right = new FormAttachment(100, 0);
    roleBasedWidget.setLayoutData(fdRoleBasedWidget);
  }

  protected void updateRadioButtons(Type t) {
    switch (t) {
      case GLOBAL:
        globalWidget.setEnabled(true);
        roleBasedWidget.setEnabled(false);
        break;
      case ROLEBASED:
        globalWidget.setEnabled(false);
        roleBasedWidget.setEnabled(true);
        break;
      default:
        globalWidget.setEnabled(false);
        roleBasedWidget.setEnabled(false);
        break;
    }
  }

  @Override
  protected boolean isValid() {
    // TODO finish this
    return true;

  }

  @Override
  public void refresh() {
    refreshOverrideButton();
    noneRadio.setEnabled(isEditable());
    globalRadio.setEnabled(isEditable());
    roleBasedRadio.setEnabled(isEditable());
    globalWidget.setEnabled(isEditable());
    roleBasedWidget.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }

  public Object getValue() {
    RowLevelSecurity rls = new RowLevelSecurity();
    if (globalRadio.getSelection()) {
      rls.setType(GLOBAL);
    } else if (roleBasedRadio.getSelection()) {
      rls.setType(ROLEBASED);
    } else {
      rls.setType(NONE);
    }

    // TODO get global formula and set on rls

    rls.setRoleBasedConstraintMap(roleBasedWidget.getRoleBasedConstraintMap());
    return rls;
  }

  protected void setValue(final Object value) {
    if (value instanceof RowLevelSecurity) {
      RowLevelSecurity rls = (RowLevelSecurity) value;
      if (rls.getType() == GLOBAL) {
        globalRadio.setSelection(true);
      } else if (rls.getType() == ROLEBASED) {
        roleBasedRadio.setSelection(true);
      } else {
        noneRadio.setSelection(true);
      }
      updateRadioButtons(rls.getType());
    }
  }

}
