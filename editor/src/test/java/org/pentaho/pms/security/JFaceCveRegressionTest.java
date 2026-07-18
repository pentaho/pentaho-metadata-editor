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


package org.pentaho.pms.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

import org.junit.Test;

/**
 * Regression guard for CVE-2023-4218 (Eclipse JFace XXE, CWE-611).
 *
 * <p>Fails if the {@code org.eclipse.jface} bundle resolved on the classpath is older than the
 * first fixed version on the current line (3.29.0). This encodes the remediation invariant so the
 * vulnerable 3.22.0 cannot silently creep back via {@code jface.version}. RED on 3.22.0, GREEN on
 * 3.31.0.</p>
 */
public class JFaceCveRegressionTest {

  /** First fixed version on the current JFace line for CVE-2023-4218. */
  private static final int[] MIN_FIXED_VERSION = { 3, 29, 0 };

  @Test
  public void jfaceBundleIsAtOrAboveCveFixedVersion() throws Exception {
    List<String> bundleVersions = resolveJFaceBundleVersions();
    assertFalse( "org.eclipse.jface bundle was not found on the classpath", bundleVersions.isEmpty() );
    // Check EVERY jface bundle on the classpath (order-independent): if any copy is below the
    // fixed line the guard must fail, regardless of which one Maven resolution would win.
    for ( String bundleVersion : bundleVersions ) {
      assertTrue(
        "Vulnerable org.eclipse.jface " + bundleVersion + " (all found: " + bundleVersions
          + ") is below the CVE-2023-4218 fixed line (3.29.0); bump jface.version.",
        compareToMinimum( bundleVersion ) >= 0 );
    }
  }

  private static List<String> resolveJFaceBundleVersions() throws Exception {
    List<String> versions = new ArrayList<>();
    Enumeration<URL> manifests =
      JFaceCveRegressionTest.class.getClassLoader().getResources( "META-INF/MANIFEST.MF" );
    while ( manifests.hasMoreElements() ) {
      try ( InputStream in = manifests.nextElement().openStream() ) {
        Manifest manifest = new Manifest( in );
        String symbolicName = manifest.getMainAttributes().getValue( "Bundle-SymbolicName" );
        if ( symbolicName != null && "org.eclipse.jface".equals( symbolicName.split( ";" )[ 0 ].trim() ) ) {
          String version = manifest.getMainAttributes().getValue( "Bundle-Version" );
          if ( version != null ) {
            versions.add( version );
          }
        }
      }
    }
    return versions;
  }

  private static int compareToMinimum( String actualVersion ) {
    String[] parts = actualVersion.split( "\\." );
    for ( int i = 0; i < MIN_FIXED_VERSION.length; i++ ) {
      int actual = i < parts.length ? leadingInt( parts[ i ] ) : 0;
      if ( actual != MIN_FIXED_VERSION[ i ] ) {
        return actual - MIN_FIXED_VERSION[ i ];
      }
    }
    return 0;
  }

  private static int leadingInt( String value ) {
    int end = 0;
    while ( end < value.length() && Character.isDigit( value.charAt( end ) ) ) {
      end++;
    }
    return end == 0 ? 0 : Integer.parseInt( value.substring( 0, end ) );
  }
}
