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

package org.pentaho.pms.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.pms.schema.SchemaMeta;

@RunWith( MockitoJUnitRunner.class )
public class MetaEditorTest {

  private static final String[] EXPECTED_SCHEMAS = new String[] { "Schema1", "Schema2" };
  private static final String[] EXPECTED_TABLE_NAMES = new String[] { "Table1", "Table2" };
  private static final String PREFFERED_SCHEMA_NAME = EXPECTED_SCHEMAS[0];

  private MetaEditor metaEditor = new MetaEditor();

  @Mock
  private Database database;

  @Mock
  private DatabaseMeta meta;

  @Test( expected = KettleDatabaseException.class )
  public void testGetSchemasWithException() throws KettleDatabaseException {
    when( database.getSchemas() ).thenThrow( new KettleDatabaseException() );

    metaEditor.getSchemas( database, meta );
  }

  @Test( expected = KettleDatabaseException.class )
  public void testGetSchemasWithCtalogsException() throws KettleDatabaseException {
    when( database.getSchemas() ).thenReturn( null );
    when( database.getCatalogs() ).thenThrow( new KettleDatabaseException() );

    metaEditor.getSchemas( database, meta );
  }

  @Test
  public void testGetSchemasMySql() {
    try {
      when( database.getSchemas() ).thenReturn( null );
      when( meta.isMySQLVariant() ).thenReturn( true );
      when( meta.getDatabaseName() ).thenReturn( EXPECTED_SCHEMAS[0] );

      String[] schemas = metaEditor.getSchemas( database, meta );

      assertArrayEquals( new String[] { EXPECTED_SCHEMAS[0] }, schemas );
    } catch ( KettleDatabaseException e ) {
      fail( "Should not send KettleDatabaseException" );
    }
  }

  @Test
  public void testGetSchemasWithCtalogs() {
    try {
      when( database.getSchemas() ).thenReturn( null );
      when( database.getCatalogs() ).thenReturn( EXPECTED_SCHEMAS );

      String[] schemas = metaEditor.getSchemas( database, meta );

      assertArrayEquals( EXPECTED_SCHEMAS, schemas );
    } catch ( KettleDatabaseException e ) {
      fail( "Should not send KettleDatabaseException" );
    }
  }

  @Test
  public void testGetSchemas() {
    try {
      when( database.getSchemas() ).thenReturn( EXPECTED_SCHEMAS );

      String[] schemas = metaEditor.getSchemas( database, meta );

      assertArrayEquals( EXPECTED_SCHEMAS, schemas );
    } catch ( KettleDatabaseException e ) {
      fail( "Should not send KettleDatabaseException" );
    }
  }

  @Test
  public void testGetTablesBySchemas() {
    try {
      Map<String, String[]> expectedTables = mockForGetTables( StringUtils.EMPTY );

      Map<String, String[]> tables = metaEditor.getTablesBySchemas( database, meta, EXPECTED_SCHEMAS );

      assertEqualsTableMaps( expectedTables, tables );
    } catch ( KettleDatabaseException e ) {
      fail( "Should not send KettleDatabaseException" );
    }
  }

  @Test
  public void testGetTablesByPrefferedSchema() {
    try {
      Map<String, String[]> expectedTables = mockForGetTables( PREFFERED_SCHEMA_NAME );

      Map<String, String[]> tables = metaEditor.getTablesBySchemas( database, meta, EXPECTED_SCHEMAS );

      assertEqualsTableMaps( expectedTables, tables );
    } catch ( KettleDatabaseException e ) {
      fail( "Should not send KettleDatabaseException" );
    }
  }

  @Test
  public void testNewFile() {
    MetaEditor me = Mockito.spy( metaEditor );
    Mockito.doAnswer( new Answer<Void>() {
      public Void answer( InvocationOnMock invocation ) {
        return null;
      }
    } ).when( me ).setShellText();
    Mockito.doAnswer( new Answer<Void>() {
      public Void answer( InvocationOnMock invocation ) {
        return null;
      }
    } ).when( me ).refreshTree();
    Mockito.doAnswer( new Answer<Void>() {
      public Void answer( InvocationOnMock invocation ) {
        return null;
      }
    } ).when( me ).refreshAll();
    me.setSchemaMeta( mock( SchemaMeta.class ) );
    me.newFile();
    assertEquals( me.getSchemaMeta().domainName, "" );
  }

  private Map<String, String[]> mockForGetTables( String prefferedSchemaName ) throws KettleDatabaseException {
    Map<String, String[]> expectedTables = new LinkedHashMap<String, String[]>();
    Properties expectedProperties = mock( Properties.class );
    when( expectedProperties.get( BaseDatabaseMeta.ATTRIBUTE_PREFERRED_SCHEMA_NAME ) ).thenReturn( prefferedSchemaName );
    when( meta.getAttributes() ).thenReturn( expectedProperties );
    String[] shemas =
      ( StringUtils.isBlank( prefferedSchemaName ) ) ? EXPECTED_SCHEMAS : new String[] { prefferedSchemaName };
    for ( String schema : shemas ) {
      when( database.getTablenames( schema, false ) ).thenReturn( EXPECTED_TABLE_NAMES );
      for ( String tableName : EXPECTED_TABLE_NAMES ) {
        String keyName = schema + tableName;
        when( meta.getQuotedSchemaTableCombination( schema, tableName ) ).thenReturn( keyName );
        expectedTables.put( keyName, new String[] { schema, tableName } );
      }
    }
    return expectedTables;
  }

  private void assertEqualsTableMaps( Map<String, String[]> expectedMap, Map<String, String[]> actualMap ) {
    assertNotNull( actualMap );
    assertEquals( expectedMap.keySet(), actualMap.keySet() );
    for ( Entry<String, String[]> values : expectedMap.entrySet() ) {
      assertEquals( values.getValue().length, actualMap.get( values.getKey() ).length );
      for ( int i = 0; i < values.getValue().length; i++ ) {
        assertEquals( values.getValue()[i], actualMap.get( values.getKey() )[i] );
      }
    }
  }
}
