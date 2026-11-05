package org.pentaho.pms.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

import org.junit.Test;

public class JFaceCveRegressionTest {

  private static final int[] MIN_FIXED_VERSION = { 3, 31, 0 };

  @Test
  public void rejectsVulnerable330() {
    assertTrue( compareToMinimum( "3.30.0" ) < 0 );
  }

  @Test
  public void acceptsFixed331() {
    assertTrue( compareToMinimum( "3.31.0" ) >= 0 );
  }

  @Test( expected = AssertionError.class )
  public void rejectsMissingVersion() {
    compareToMinimum( null );
  }

  @Test( expected = AssertionError.class )
  public void rejectsMalformedVersion() {
    compareToMinimum( "4garbage.0.0" );
  }

  @Test
  public void jfaceBundleIsAtOrAboveCveFixedVersion() throws Exception {
    List<String> bundleVersions = resolveJFaceBundleVersions();
    assertFalse( "org.eclipse.jface bundle was not found on the classpath", bundleVersions.isEmpty() );
    for ( String bundleVersion : bundleVersions ) {
      assertTrue(
        "Vulnerable org.eclipse.jface " + bundleVersion + " (all found: " + bundleVersions
          + ") is below the CVE-2023-4218 fixed line (3.31.0); bump jface.version.",
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
          assertNotNull( "org.eclipse.jface bundle has no Bundle-Version", version );
          versions.add( version );
        }
      }
    }
    return versions;
  }

  private static int compareToMinimum( String actualVersion ) {
    assertTrue( "Invalid org.eclipse.jface Bundle-Version: " + actualVersion,
      actualVersion != null && actualVersion.matches( "\\d+\\.\\d+\\.\\d+(?:\\.[A-Za-z0-9_-]+)?" ) );
    String[] parts = actualVersion.split( "\\." );
    for ( int index = 0; index < MIN_FIXED_VERSION.length; index++ ) {
      int actual = Integer.parseInt( parts[ index ] );
      if ( actual != MIN_FIXED_VERSION[ index ] ) {
        return Integer.compare( actual, MIN_FIXED_VERSION[ index ] );
      }
    }
    return 0;
  }
}