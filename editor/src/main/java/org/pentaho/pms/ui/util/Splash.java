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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.version.BuildVersion;
import org.pentaho.pms.ui.locale.Messages;

/**
 * Displays the splash and about screen
 */
public class Splash {

  private static final Log logger = LogFactory.getLog( Splash.class );

  private static final String FONT_TYPE = "Helvetica";
  private static final String LICENSE_FILE_PATH = "./LICENSE.TXT";

  private boolean isStartup;
  private boolean isMouseDown = false;
  private boolean isMouseMoveInProgress = false;
  private int xPos = 0;
  private int yPos = 0;

  private Shell shell;
  private Font verFont;
  private Font licFont;
  private Display display;
  private Image aboutScreenImage;

  private int textHorizontalPosition = 300;


  public Splash( Display display ) {
    this( display, new Shell( display, SWT.APPLICATION_MODAL ), true );
  }

  protected Splash( Display display, Shell splashShell ) {
    this( display, splashShell, false );
  }

  protected Splash( Display display, Shell splashShell, boolean isStartup ) {
    this.display = display;
    this.isStartup = isStartup;

    verFont = new Font( display, FONT_TYPE, 14, SWT.BOLD );

    shell = splashShell;

    aboutScreenImage = GUIResource.getInstance().getImageMetaSplash();
    shell.setImage( aboutScreenImage );
    shell.setText( Messages.getString( "MetaEditor.USER_HELP_METADATA_EDITOR" ) );

    setupShellDimensions();

    setupShellListeners();

    shell.open();

    if ( isStartup ) { //show splash for 5s on startup
      long endTime = System.currentTimeMillis() + 5000;
      while ( !shell.isDisposed() && endTime > System.currentTimeMillis() ) {
        if ( isMacOS() && !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      shell.dispose();
    }
  }

  private void setupShellDimensions() {
    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
    Rectangle bounds = aboutScreenImage.getBounds();
    int x = ( displayBounds.width - bounds.width ) / 2;
    int y = ( displayBounds.height - bounds.height ) / 2;

    shell.setSize( bounds.width, bounds.height );
    shell.setLocation( x, y );
  }

  private void setupShellListeners() {
    setupShellPaintListener();

    shell.addMouseListener( new MouseListener() {

      @Override
      public void mouseUp( MouseEvent e ) {
        if ( isMouseMoveInProgress ) {
          isMouseDown = false;
          isMouseMoveInProgress = false;
        } else {
          shell.dispose();
        }
      }

      @Override
      public void mouseDown( MouseEvent e ) {
        isMouseDown = true;
        xPos = e.x;
        yPos = e.y;
      }

      @Override public void mouseDoubleClick( MouseEvent mouseEvent ) {
        // Do Nothing
      }

    } );
    shell.addMouseMoveListener( e -> {
      if ( isMouseDown ) {
        isMouseMoveInProgress = true;
        shell.setLocation( shell.getLocation().x + ( e.x - xPos ),
          shell.getLocation().y + ( e.y - yPos ) );
      }
    } );

    shell.addDisposeListener( disposeEvent -> {

      //dispose of the image only once the program is closed
      if ( !isStartup && ( (Shell) disposeEvent.widget ).getParent() == null ) {
        aboutScreenImage.dispose();
      }
      verFont.dispose();
      licFont.dispose();

    } );
  }

  private void setupShellPaintListener() {
    shell.addPaintListener( e -> {

      e.gc.drawImage( aboutScreenImage, 0, 0 );

      String fullVersionText = Messages.getString( "MetaEditor.USER_HELP_VERSION" );
      String buildVersion = BuildVersion.getInstance().getVersion();
      if ( StringUtils.ordinalIndexOf( buildVersion, ".", 2 ) > 0 ) {
        fullVersionText =
          fullVersionText + " " + buildVersion.substring( 0, StringUtils.ordinalIndexOf( buildVersion, ".", 2 ) );
      } else {
        fullVersionText = fullVersionText + " " + buildVersion;
      }
      e.gc.setFont( verFont );
      e.gc.setForeground( new Color( display, 65, 65, 65 ) );
      e.gc.drawText( fullVersionText, textHorizontalPosition, 80, true );


      String inputStringDate = BuildVersion.getInstance().getBuildDate();
      String outputStringDate = "";
      SimpleDateFormat inputFormat = null;
      SimpleDateFormat outputFormat = null;

      if ( inputStringDate.matches( "^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$" ) ) {
        inputFormat = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss.SSS" );
      }
      if ( inputStringDate.matches( "^\\d{4}-\\d{1,2}-\\d{1,2}\\_\\d{1,2}-\\d{2}-\\d{2}$" ) ) {
        inputFormat = new SimpleDateFormat( "yyyy-MM-dd_hh-mm-ss" );
      }
      if ( inputStringDate.matches( "^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}.\\d{2}.\\d{2}$" ) ) {
        inputFormat = new SimpleDateFormat( "yyyy-MM-dd hh.mm.ss" );
      }
      outputFormat = new SimpleDateFormat( "MMMM d, yyyy hh:mm:ss" );
      try {
        if ( inputFormat != null ) {
          Date date = inputFormat.parse( inputStringDate );
          outputStringDate = outputFormat.format( date );
        } else {
          // If date isn't correspond to formats above just show date in origin format
          outputStringDate = inputStringDate;
        }
      } catch ( ParseException pe ) {
        // Just show date in origin format
        outputStringDate = inputStringDate;
      }

      String version = buildVersion;

      String buildDate = Messages.getString( "MetaEditor.USER_HELP_BUILD_DATE" ) + " " + outputStringDate;

      e.gc.setForeground( new Color( display, 65, 65, 65 ) );
      e.gc.setFont( new Font( display, FONT_TYPE, 10, SWT.NORMAL ) );

      e.gc.drawText( version, textHorizontalPosition, 105, true );
      e.gc.drawText( buildDate, textHorizontalPosition, 122, true );


      StringBuilder sb = new StringBuilder();
      String line;
      try {
        BufferedReader reader =
          new BufferedReader( new FileReader( LICENSE_FILE_PATH ) );

        while ( ( line = reader.readLine() ) != null ) {
          sb.append( line + System.getProperty( "line.separator" ) );
        }
      } catch ( IOException ex ) {
        sb.append( String.format( "Error reading license file from product directory: \"%s\"", LICENSE_FILE_PATH ) );
        logger.error( Messages.getString( "MetaEditor.USER_HELP_LICENSE_TEXT_NOT_FOUND" ), ex );
      }
      String licenseText = sb.toString();

      // try using the desired font size for the license text
      java.util.List<String> fontsAvailable = new ArrayList<String>();
      for ( FontData fontData : display.getFontList( null, true ) ) {
        fontsAvailable.add( fontData.getName() );
      }

      String licFontName = FONT_TYPE;
      int licFontSize = 10;
      // try to find a monospace font since SWT doesn't support logical 'Monospaced'
      boolean fontFound = false;
      if ( fontsAvailable.contains( "Courier New" ) ) {
        licFontName = "Courier New";
        fontFound = true;
      }
      if ( fontsAvailable.contains( "Courier" ) && !fontFound ) {
        licFontName = "Courier";
        fontFound = true;
      }
      if ( fontsAvailable.contains( "adobe-courier" ) && !fontFound ) {
        licFontName = "adobe-courier";
        fontFound = true;
      }
      if ( fontsAvailable.contains( "Lucida Console" ) && !fontFound ) {
        licFontName = "Lucida Console";
        fontFound = true;
      }
      if ( fontsAvailable.contains( "Monospace" ) && !fontFound ) {
        licFontName = "Monospace";
        fontFound = true;
      }

      licFont = new Font( display, licFontName, licFontSize, SWT.NORMAL );
      e.gc.setFont( licFont );
      e.gc.setForeground( new Color( display, 65, 65, 65 ) );

      while ( !willLicenseTextFit( licenseText, e.gc ) ) {
        licFontSize--;
        if ( licFont != null ) {
          licFont.dispose();
        }
        licFont = new Font( e.display, licFontName, licFontSize, SWT.NORMAL );
        e.gc.setFont( licFont );
      }

      e.gc.drawText( licenseText, textHorizontalPosition, 150, true );

    } );
  }

  public static boolean isMacOS() {
    String osName = System.getProperty( "os.name" ).toLowerCase();
    return osName.startsWith( "mac os x" );
  }

  // determine if the license text will fit the allocated space
  private boolean willLicenseTextFit( String licenseText, GC gc ) {
    Point splashSize = shell.getSize();
    Point licenseDrawLocation = new Point( 300, 150 );
    Point requiredSize = gc.textExtent( licenseText );

    int width = splashSize.x - licenseDrawLocation.x;
    int height = splashSize.y - licenseDrawLocation.y;

    boolean fitsVertically = width >= requiredSize.x;
    boolean fitsHorizontally = height >= requiredSize.y;

    return ( fitsVertically && fitsHorizontally );
  }
}
