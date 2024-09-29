@echo offz
REM ******************************************************************************
REM
REM Pentaho
REM
REM Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
REM
REM Use of this software is governed by the Business Source License included
REM in the LICENSE.TXT file.
REM
REM Change Date: 2028-08-13
REM ******************************************************************************

SET CURRENTFOLDER=%~dp0
md %CURRENTFOLDER%\.uninstalled
set UNINSTALLEDFOLDER=%CURRENTFOLDER%\.uninstalled
md %UNINSTALLEDFOLDER%\classes
if exist %CURRENTFOLDER%\classes\kettle-lifecycle-listeners.xml move %CURRENTFOLDER%\classes\kettle-lifecycle-listeners.xml %UNINSTALLEDFOLDER%\classes\kettle-lifecycle-listeners.xml
if exist %CURRENTFOLDER%\classes\kettle-registry-extensions.xml move %CURRENTFOLDER%\classes\kettle-registry-extensions.xml %UNINSTALLEDFOLDER%\classes\kettle-registry-extensions.xml
if exist %CURRENTFOLDER%\drivers move %CURRENTFOLDER%\drivers %UNINSTALLEDFOLDER%\drivers
if exist %CURRENTFOLDER%\system move %CURRENTFOLDER%\system %UNINSTALLEDFOLDER%\system
md %UNINSTALLEDFOLDER%\libext\pentaho
move "%CURRENTFOLDER%\libext\pentaho\org.apache.karaf*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\
move "%CURRENTFOLDER%\libext\pentaho\org.osgi.core*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\
move "%CURRENTFOLDER%\libext\pentaho\pdi-osgi-bridge-core*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\
move "%CURRENTFOLDER%\libext\pentaho\pentaho-osgi-utils-api*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\
move "%CURRENTFOLDER%\libext\pentaho\pentaho-service-coordinator*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\
move "%CURRENTFOLDER%\libext\pentaho\shim-api-core*.jar" %UNINSTALLEDFOLDER%\libext\pentaho\

