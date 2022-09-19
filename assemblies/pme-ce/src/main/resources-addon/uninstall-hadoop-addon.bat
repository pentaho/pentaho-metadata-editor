@echo offz

REM *****************************************************************************
REM
REM Pentaho Data Integration
REM
REM Copyright (C) 2006 - 2022 by Hitachi Vantara : http://www.hitachivantara.com
REM
REM *****************************************************************************
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with
REM the License. You may obtain a copy of the License at
REM
REM    http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM
REM *****************************************************************************

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

