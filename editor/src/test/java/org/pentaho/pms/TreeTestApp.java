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


package org.pentaho.pms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.ui.jface.tree.ITreeNodeChangedListener;
import org.pentaho.pms.ui.jface.tree.TreeContentProvider;
import org.pentaho.pms.ui.tree.BusinessModelTreeNode;
import org.pentaho.pms.ui.tree.ConceptLabelProvider;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.ui.util.GUIResource;
import org.pentaho.pms.util.Settings;

public class TreeTestApp extends ApplicationWindow {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(TreeTestApp.class);
  private PropsUI props;

  // ~ Instance fields =================================================================================================
  
    private  BusinessModel model;
    private String locale;
    
  // ~ Constructors ====================================================================================================

  public TreeTestApp() {
    super(null);
  }

  // ~ Methods =========================================================================================================

  public void run() {
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }

  private void init(){
    if (!PropsUI.isInitialized()) {
      Const.checkPentahoMetadataDirectory();
      PropsUI.init(this.getShell().getDisplay(), Const.getPropertiesFile()); // things to remember...
    }
    props = PropsUI.getInstance();
    GUIResource.getInstance().reload();
    DBCache.getInstance().setActive(props.useDBCache());    

    CWM cwm = CWM.getInstance("Steel Wheels");
    CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
    SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
    
    this.model = schemaMeta.getActiveModel();
    this.locale = schemaMeta.getActiveLocale();

  }

  protected Point getInitialSize() {
    return new Point(500, 500);
  }

  protected Control createContents(final Composite parent) {
    FillLayout fillLayout = new FillLayout ();
    Composite composite0 = new Composite(parent, SWT.NONE);
    composite0.setLayout (fillLayout);

    TreeViewer modelViewer = new TreeViewer(composite0, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    
    TreeColumn mainObject = new TreeColumn(modelViewer.getTree(), SWT.LEFT);
    mainObject.setText(""); //$NON-NLS-1$
    mainObject.setWidth(200);

    TreeColumn mainConcept = new TreeColumn(modelViewer.getTree(), SWT.LEFT);
    mainConcept.setText(Messages.getString("MetaEditor.USER_PARENT_CONCEPT")); //$NON-NLS-1$
    mainConcept.setWidth(200);

    init();
    
    modelViewer.setContentProvider(new TreeContentProvider());
    modelViewer.setLabelProvider(new ConceptLabelProvider());
    BusinessModelTreeNode root = new BusinessModelTreeNode(null, model, locale);
    root.addTreeNodeChangeListener((ITreeNodeChangedListener) modelViewer.getContentProvider());
    modelViewer.setInput(root);
    modelViewer.refresh();

    return parent;
  }

  public static void main(final String[] args) {
    
    new TreeTestApp().run();
  }
}
