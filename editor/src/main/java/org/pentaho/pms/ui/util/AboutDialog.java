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
 import org.eclipse.swt.events.MouseEvent;
 import org.eclipse.swt.events.MouseListener;
 import org.eclipse.swt.events.MouseMoveListener;
 import org.eclipse.swt.widgets.Dialog;
 import org.eclipse.swt.widgets.Shell;
 
 
 public class AboutDialog extends Dialog {
 
   private boolean blnMouseDown = false;
   private boolean blnMouseMoveInProgress = false;
   private int xPos = 0;
   private int yPos = 0;
 
   public AboutDialog( Shell parent ) {
     super( parent );
   }
 
   public void open() throws Exception {

     Shell splashShell = new Shell( getParent(), SWT.APPLICATION_MODAL + SWT.RESIZE );
     final Splash splash = new Splash( getParent().getDisplay(), splashShell );

     splashShell.addMouseListener( new MouseListener() {
 
       @Override
       public void mouseUp( MouseEvent arg0 ) {
         if ( blnMouseMoveInProgress ) {
           blnMouseDown = false;
           blnMouseMoveInProgress = false;
         } else {
           splash.dispose();
         }
       }
 
       @Override
       public void mouseDown( MouseEvent e ) {
         blnMouseDown = true;
         xPos = e.x;
         yPos = e.y;
       }
 
       @Override
       public void mouseDoubleClick( MouseEvent arg0 ) {
       }
 
     } );
 
     splashShell.addMouseMoveListener( new MouseMoveListener() {
 
       @Override
       public void mouseMove( MouseEvent e ) {
         // TODO Auto-generated method stub
         if ( blnMouseDown ) {
           blnMouseMoveInProgress = true;
           splashShell.setLocation( splashShell.getLocation().x + ( e.x - xPos ),
             splashShell.getLocation().y + ( e.y - yPos ) );
         }
       }
     } );
   }
 
 }