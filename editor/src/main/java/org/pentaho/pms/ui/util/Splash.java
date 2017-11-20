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

package org.pentaho.pms.ui.util;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.ui.locale.Messages;
import org.pentaho.pms.util.VersionHelper;

/**
 * Displays the Kettle splash screen
 * 
 * @author Matt
 * @since  14-mrt-2005
 */
public class Splash {
  private Shell splash;

  public Splash( final Display display ) {
    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();

    final Image splashImage = GUIResource.getInstance().getImageMetaSplash(); // new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "MetaSplash.png"));
    final Image splashIcon  = GUIResource.getInstance().getImageIcon(); // new Image(display, getClass().getResourceAsStream(Const.IMAGE_DIRECTORY + "icon.png"));

    splash = new Shell( display, SWT.NONE /*SWT.ON_TOP*/ );
    splash.setImage( splashIcon );
    splash.setText( Messages.getString( "Splash.USER_APP_TITLE" ) ); //$NON-NLS-1$


    FormLayout splashLayout = new FormLayout();
    splash.setLayout( splashLayout );

    Canvas canvas = new Canvas( splash, SWT.NO_BACKGROUND );

    FormData fdCanvas = new FormData();
    fdCanvas.left   = new FormAttachment( 0, 0 );
    fdCanvas.top    = new FormAttachment( 0, 0 );
    fdCanvas.right  = new FormAttachment( 100, 0 );
    fdCanvas.bottom = new FormAttachment( 100, 0 );
    canvas.setLayoutData( fdCanvas );

    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent e ) {
        e.gc.drawImage( splashImage, 0, 0 );
        e.gc.setBackground( new Color( e.display, new RGB( 255, 255, 255 ) ) );
        // Updates for PMD-190 - Use version helper to display version information
        VersionHelper helper = new VersionHelper();
        e.gc.setForeground( new Color( display, 65, 65, 65 ) );
        Font font = new Font( e.display, "Sans", 10, SWT.BOLD ); //$NON-NLS-1$
        e.gc.setFont( font );
        e.gc.setAntialias( SWT.ON );
        e.gc.drawString( Messages.getString( "Splash.VERSION_INFO", helper.getVersionInformation( Splash.class, false ) ), 294, 220, true );
        font = new Font( e.display, "Sans", 8, SWT.NONE ); //$NON-NLS-1$
        e.gc.setFont( font );
        e.gc.drawString( Messages.getString( "MetaEditor.USER_HELP_PENTAHO_CORPORATION", "" + ( ( new Date() ).getYear() + 1900 ) ), 294, 260, true ); //$NON-NLS-1$
        for ( int i = 1; i <= 11; i++ ) {
          e.gc.drawString( Messages.getString( "Splash.LICENSE_LINE_" + i ), 294, 270 + i * 12, true ); //$NON-NLS-1$
        }
      }
    } );

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
}
