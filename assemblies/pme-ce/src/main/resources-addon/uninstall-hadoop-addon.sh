#!/bin/bash

# *****************************************************************************
#
# Pentaho Data Integration
#
# Copyright (C) 2006 - 2022 by Hitachi Vantara : http://www.hitachivantara.com
#
# *****************************************************************************
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with
# the License. You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# *****************************************************************************
mkdir -p -- "$PWD/.uninstalled"
INSTALLEDDIR=$PWD/.uninstalled
mkdir -p -- $INSTALLEDDIR/classes

if [ -e $PWD/classes/kettle-lifecycle-listeners.xml ];then mv $PWD/classes/kettle-lifecycle-listeners.xml $INSTALLEDDIR/classes/kettle-lifecycle-listeners.xml; fi
if [ -e $PWD/classes/kettle-registry-extensions.xml ];then mv $PWD/classes/kettle-registry-extensions.xml $INSTALLEDDIR/classes/kettle-registry-extensions.xml; fi

if [ -e $PWD/drivers ];then mv $PWD/drivers $INSTALLEDDIR/drivers ; fi  
if [ -e $PWD/system ];then mv $PWD/system $INSTALLEDDIR/system ; fi
mkdir -p -- $INSTALLEDDIR/libext/pentaho
mv "$PWD"/libext/pentaho/org.apache.karaf*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/osgi.core*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pdi-osgi-bridge-core*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pentaho-osgi-utils-api*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/pentaho-service-coordinator*.jar $INSTALLEDDIR/libext/pentaho/
mv "$PWD"/libext/pentaho/shim-api-core*.jar $INSTALLEDDIR/libext/pentaho/