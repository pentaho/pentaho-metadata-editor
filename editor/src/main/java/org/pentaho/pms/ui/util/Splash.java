/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.pms.ui.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
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
 *
 */
public class Splash {

  private static final Log logger = LogFactory.getLog( Splash.class );

  private static final String FONT_TYPE = "Helvetica";

  private static final String LICENSE_FILE_PATH = "./LICENSE.TXT";

  private Shell shell;
  private Font verFont;
  private Font licFont;

  private int textHorizontalPosition = 300;


  public Splash( Display display ) {
    this( display, new Shell( display, SWT.APPLICATION_MODAL ) );
  }

  protected Splash( Display display, Shell splashShell ) {

    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();

    verFont = new Font( display, FONT_TYPE, 14, SWT.BOLD );

    shell = splashShell;

    Image aboutScreenImage = GUIResource.getInstance().getImageMetaSplash();
    shell.setImage( aboutScreenImage );

    shell.setText( Messages.getString( "MetaEditor.USER_HELP_METADATA_EDITOR" ) );

    shell.addPaintListener( e -> {

      e.gc.drawImage( aboutScreenImage, 0, 0 );

      String fullVersionText = Messages.getString( "MetaEditor.USER_HELP_VERSION" );
      String buildVersion = BuildVersion.getInstance().getVersion();
      if ( StringUtils.ordinalIndexOf( buildVersion, ".", 2 ) > 0 ) {
        fullVersionText = fullVersionText + " " + buildVersion.substring( 0, StringUtils.ordinalIndexOf( buildVersion, ".", 2 ) );
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
          new BufferedReader( new FileReader( LICENSE_FILE_PATH ));

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
      for ( FontData fontData : display.getFontList(null, true) ) {
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


    shell.addDisposeListener( disposeEvent -> {

      //dispose of the image only once the program is closed
      if( ( (Shell) disposeEvent.widget ).getParent() == null ) {
        aboutScreenImage.dispose();
      }
      verFont.dispose();
      licFont.dispose();

    } );


    Rectangle bounds = aboutScreenImage.getBounds();
    int x = ( displayBounds.width - bounds.width ) / 2;
    int y = ( displayBounds.height - bounds.height ) / 2;

    shell.setSize( bounds.width, bounds.height );
    shell.setLocation( x, y );

    shell.open();

    if ( isMacOS() ) {  // This forces the splash screen to display on the Mac.
      long endTime = System.currentTimeMillis() + 5000; // 5 second delay... can you read the splash that fast?
      while ( !shell.isDisposed() && endTime > System.currentTimeMillis() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
    }

    TimerTask timerTask = new TimerTask() {

      @Override
      public void run() {
        try {
          shell.redraw();
        } catch ( Throwable e ) {
          // ignore.
        }
      }

    };

    final Timer timer = new Timer();
    timer.schedule( timerTask, 0, 100 );

    shell.addDisposeListener( arg0 -> timer.cancel() );

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

  public void dispose() {
    if ( !shell.isDisposed() ) {
      shell.dispose();
    }
  }

  public void hide() {
    if ( !shell.isDisposed() ) {
      shell.setVisible( false );
    }
  }

  public void show() {
    if ( !shell.isDisposed() ) {
      shell.setVisible( true );
    }
  }

  public static boolean isMacOS() {
    String osName = System.getProperty( "os.name" ).toLowerCase();
    return osName.startsWith( "mac os x" );
  }

}
/*
  public Splash( final Display display ) {
    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();

    final Image splashImage = GUIResource.getInstance().getImageMetaSplash();
    final Image splashIcon = GUIResource.getInstance().getImageIcon();

    splash = new Shell( display, SWT.NONE );
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
  */
