#!/bin/bash
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2028-08-13
# ******************************************************************************

mkdir -p -- "$PWD/.uninstalled"
INSTALLEDDIR=$PWD/.uninstalled
mkdir -p -- $INSTALLEDDIR/classes

if [ -e $PWD/classes/kettle-lifecycle-listeners.xml ];then mv $PWD/classes/kettle-lifecycle-listeners.xml $INSTALLEDDIR/classes/kettle-lifecycle-listeners.xml; fi
if [ -e $PWD/classes/kettle-registry-extensions.xml ];then mv $PWD/classes/kettle-registry-extensions.xml $INSTALLEDDIR/classes/kettle-registry-extensions.xml; fi

if [ -e $PWD/drivers ];then mv $PWD/drivers $INSTALLEDDIR/drivers ; fi  
if [ -e $PWD/system ];then mv $PWD/system $INSTALLEDDIR/system ; fi
mkdir -p -- $INSTALLEDDIR/libext/pentaho
mv "$PWD"/libext/pentaho/org.apache.karaf*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/org.osgi.core*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pdi-osgi-bridge-core*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pentaho-osgi-utils-api*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pentaho-service-coordinator*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/shim-api-core*.jar $INSTALLEDDIR/libext/pentaho/