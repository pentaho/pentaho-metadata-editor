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

package org.pentaho.pms.ui.concept.editor;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

/**
 * Dialog for editing the base sets of concepts from which all other concepts inherit.
 * 
 * <p>A note about <code>lastSelection</code>:</p>
 * <p>lastSelection keeps track of the last concept to which we successfully transitioned. Why is this necessary?
 * Because state on the screen may be invalid (e.g. user entered bogus value) then we must stop the transition, alert
 * the user, and switch back to the offending screen. Here, "screen" is a card in a stack layout.</p> 
 * 
 * @author mlowery
 */
public class ConceptEditorDialog extends Dialog {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(ConceptEditorDialog.class);

  // ~ Instance fields =================================================================================================

  protected String activeLocale;

  protected Composite detailsComposite;

  protected StackLayout stackLayout;

  protected Composite cardComposite;

  protected Map propertyEditorContext = new HashMap();

  protected ConceptModelRegistry conceptModelRegistry = new ConceptModelRegistry();

  protected Map<ConceptInterface, Composite> cards = new HashMap<ConceptInterface, Composite>();

  protected Control defaultCard;

  private Text conceptNameField;

  private IConceptTreeModel conceptTreeModel;

  private ISelectionChangedListener conceptTreeSelectionChangedListener;

  private ConceptTreeWidget conceptTree;

  private PropertyWidgetManager2 propertyWidgetManager;

  private ConceptInterface lastSelection;

  // ~ Constructors ====================================================================================================

  public ConceptEditorDialog(final Shell parent, final IConceptTreeModel conceptTreeModel) {
    super(parent);
    propertyEditorContext.put("locales", conceptTreeModel.getSchemaMeta().getLocales());
    this.conceptTreeModel = conceptTreeModel;
  }

  // ~ Methods =========================================================================================================

  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE);
  }

  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText("Concept Editor");
  }

  protected Point getInitialSize() {
    return new Point(1000, 800);
  }

  protected final Control createDialogArea(final Composite parent) {
    Composite c0 = (Composite) super.createDialogArea(parent);

    Composite container = new Composite(c0, SWT.NONE);
    container.setLayout(new FormLayout());
    GridData gdContainer = new GridData(GridData.FILL_BOTH);
    container.setLayoutData(gdContainer);

    SashForm s0 = new SashForm(container, SWT.HORIZONTAL);
    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(0, 0);
    fdSash.right = new FormAttachment(100, 0);
    fdSash.bottom = new FormAttachment(100, 0);
    s0.setLayoutData(fdSash);

    Composite c12 = new Composite(s0, SWT.NONE);
    c12.setLayout(new FormLayout());

    Composite placeholderComposite = new Composite(c12, SWT.NONE);
    FormData fdDetailsComposite = new FormData();
    fdDetailsComposite.top = new FormAttachment(0, 0);
    fdDetailsComposite.left = new FormAttachment(0, 0);
    fdDetailsComposite.right = new FormAttachment(100, -5);
    fdDetailsComposite.bottom = new FormAttachment(100, 0);
    placeholderComposite.setLayoutData(fdDetailsComposite);

    placeholderComposite.setLayout(new FormLayout());

    conceptTree = new ConceptTreeWidget(placeholderComposite, SWT.NONE, conceptTreeModel, true);

    FormData fdlList = new FormData();
    fdlList.left = new FormAttachment(0, 0);
    fdlList.top = new FormAttachment(0, 0);
    fdlList.right = new FormAttachment(100, 0);
    fdlList.bottom = new FormAttachment(100, 0);
    conceptTree.setLayoutData(fdlList);

    conceptTreeSelectionChangedListener = new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent e) {

        if (lastSelection != null && lastSelection.equals(((StructuredSelection) e.getSelection()).getFirstElement())) {
          return;
        }

        boolean hasErrors = popupValidationErrorDialogIfNecessary();
        if (!hasErrors) {
          if (logger.isDebugEnabled()) {
            logger.debug("heard conceptTree selection changed event: " + e);
            logger.debug("attempting to swap cards");
          }
          if (!e.getSelection().isEmpty()) {
            TreeSelection treeSel = (TreeSelection) e.getSelection();
            if (treeSel.getFirstElement() instanceof ConceptInterface) {
              ConceptInterface cu = (ConceptInterface) treeSel.getFirstElement();
              swapCard(cu);
            } else {
              swapCard(null);
            }
          }
        } else {
          // set selection back where it was
          if (!lastSelection.equals(((TreeSelection) e.getSelection()).getFirstElement())) {
            conceptTree.setSelection(new StructuredSelection(lastSelection));
          }
        }
      }
    };

    conceptTree.addSelectionChangedListener(conceptTreeSelectionChangedListener);

    Composite spacer = new Composite(s0, SWT.NONE);
    spacer.setLayout(new FormLayout());

    cardComposite = new Composite(spacer, SWT.NONE);

    FormData fdCardComposite = new FormData();
    fdCardComposite.top = new FormAttachment(0, 0);
    fdCardComposite.left = new FormAttachment(0, 5);
    fdCardComposite.right = new FormAttachment(100, 0);
    fdCardComposite.bottom = new FormAttachment(100, 0);
    cardComposite.setLayoutData(fdCardComposite);

    stackLayout = new StackLayout();
    cardComposite.setLayout(stackLayout);

    defaultCard = new DefaultCard(cardComposite, SWT.NONE);

    swapCard(null);

    s0.setWeights(new int[] { 1, 3 });

    return c0;
  }

  protected void okPressed() {
    boolean hasErrors = popupValidationErrorDialogIfNecessary();
    if (!hasErrors) {
      try {
        conceptTreeModel.save();
      } catch (ObjectAlreadyExistsException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
        MessageDialog.openError(getShell(), "Error", "There was an error during save.");

      }
      cleanup();
      super.okPressed();
    }
  }

  protected void cleanup() {
    conceptTree.removeSelectionChangedListener(conceptTreeSelectionChangedListener);
  }

  private void swapCard(final ConceptInterface concept) {
    if (null == concept) {
      stackLayout.topControl = defaultCard;
    } else {
      if (null == cards.get(concept)) {
        IConceptModel conceptModel = conceptModelRegistry.getConceptModel(concept);

        Composite conceptEditor = new Composite(cardComposite, SWT.NONE);
        conceptEditor.setLayout(new FillLayout());

        Group group = new Group(conceptEditor, SWT.SHADOW_OUT);
        group.setText("Properties");
        group.setLayout(new FillLayout());
        SashForm s0 = new SashForm(group, SWT.HORIZONTAL);
        s0.SASH_WIDTH = 10;
        PropertyNavigationWidget propertyNavigationWidget = new PropertyNavigationWidget(s0, SWT.NONE);
        propertyNavigationWidget.setConceptModel(conceptModel);
        propertyWidgetManager = new PropertyWidgetManager2(s0, SWT.NONE, propertyEditorContext, conceptTreeModel
            .getSchemaMeta().getSecurityReference());
        propertyWidgetManager.setConceptModel(conceptModel);
        propertyNavigationWidget.addSelectionChangedListener(propertyWidgetManager);
        s0.setWeights(new int[] { 1, 2 });
        cards.put(concept, conceptEditor);
      }
      stackLayout.topControl = (Control) cards.get(concept);
    }
    lastSelection = concept;
    cardComposite.layout();
  }

  protected void cancelPressed() {
    cleanup();
    super.cancelPressed();
  }

  /**
   * The card that shows when there is no selection in the concept selection tree.
   */
  private class DefaultCard extends Composite {

    public DefaultCard(final Composite parent, final int style) {
      super(parent, style);
      createContents();
    }

    private void createContents() {
      setLayout(new GridLayout());
      Label lab0 = new Label(this, SWT.CENTER);
      lab0.setText("Select a concept to begin editing properties.");
      GridData gd = new GridData();
      gd.verticalAlignment = GridData.CENTER;
      gd.horizontalAlignment = GridData.CENTER;
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      lab0.setLayoutData(gd);
    }

  }

  /**
   * Unfortunate duplication of code. (Same method is in AbstractTableDialog.)
   */
  protected boolean popupValidationErrorDialogIfNecessary() {
    // if propertyWidgetManager is null, then we are not currently displaying a card; we displaying the defaultCard
    if (propertyWidgetManager == null) {
      return false;
    }
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
