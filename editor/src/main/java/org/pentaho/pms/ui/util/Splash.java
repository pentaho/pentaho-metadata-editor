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
 * Copyright (c) 2002-2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.pms.ui.util;

import java.time.LocalDateTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.util.VersionHelper;

/**
 * Displays the Kettle splash screen
 *
 * @author Matt
 * @since 14-mrt-2005
 */
public class Splash {
  private Shell splash;

  public Splash( final Display display ) {
    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();

    final Image splashImage = GUIResource.getInstance().getImageMetaSplash();
    final Image splashIcon = GUIResource.getInstance().getImageIcon();

    splash = new Shell( display, SWT.NONE /*SWT.ON_TOP*/ );
    splash.setImage( splashIcon );
    splash.setText( Messages.getString( "Splash.USER_APP_TITLE" ) ); //$NON-NLS-1$


    FormLayout splashLayout = new FormLayout();
    splash.setLayout( splashLayout );

    Control above = splash;
    int left = 290;

    VersionHelper helper = new VersionHelper();
    Label versionLbl = new Label( splash, SWT.NONE );
    above = placeBelow( versionLbl, above, left, 160 );

    versionLbl.setFont( new Font( splash.getDisplay(), "Sans", 10, SWT.BOLD ) );
    versionLbl.setText( Messages.getString( "Splash.VERSION_INFO", helper.getVersionInformation( Splash.class, false ) ) );
    Label copyrightLbl1 = new Label( splash, SWT.NONE );
    above = placeBelow( copyrightLbl1, above, left, 5 );

    String year = "" + LocalDateTime.now().getYear();
    copyrightLbl1.setText( Messages.getString( "MetaEditor.USER_HELP_PENTAHO_CORPORATION", year ) );
    Font copyrightFont = new Font( splash.getDisplay(), "Sans", 8, SWT.NONE );
    copyrightLbl1.setFont( copyrightFont );

    splash.setBackgroundImage( splashImage );
    splash.setBackgroundMode( SWT.INHERIT_DEFAULT );
    Link license = new Link( splash, SWT.NONE );
    license.setText( Messages.getString( "Splash.LICENSE" ) );
    license.setFont( new Font( splash.getDisplay(), "Sans", 9, SWT.NONE  ) );

    above = placeBelow( license, above, left, 10 );

    splash.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent arg0 ) {
        splashImage.dispose();
      }
    } );
    Rectangle bounds = splashImage.getBounds();
    int x = ( displayBounds.width - bounds.width ) / 2;
    int y = ( displayBounds.height - bounds.height ) / 2;

    splash.setSize( bounds.width, bounds.height );
    splash.setLocation( x, y );

    splash.open();

    if ( isMacOS() ) {
      long endTime = System.currentTimeMillis() + 5000; // 5 second delay... can you read the splash that fast?
      while ( splash != null && !splash.isDisposed() && endTime > System.currentTimeMillis() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      splash.close();
    }
  }

  private static Control placeBelow( Control toPlace, Control above, int x, int yOffset ) {
    FormData fd = new FormData();
    fd.left = new FormAttachment( 0, x );
    fd.top = new FormAttachment( above, yOffset );
    toPlace.setLayoutData( fd );
    return toPlace;
  }

  public void dispose() {
    if ( !splash.isDisposed() ) {
      splash.dispose();
    }
  }

  public void hide() {
    splash.setVisible( false );
  }

  public void show() {
    splash.setVisible( true );
  }

  public static boolean isMacOS() {
    String osName = System.getProperty( "os.name" ).toLowerCase();
    return osName.startsWith( "mac os x" );
  }
}
