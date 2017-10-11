@echo off

REM This program is free software; you can redistribute it and/or modify it under the
REM terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
REM Foundation.
REM
REM You should have received a copy of the GNU Lesser General Public License along with this
REM program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
REM or from the Free Software Foundation, Inc.,
REM 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
REM
REM This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
REM without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
REM See the GNU Lesser General Public License for more details.
REM
REM Copyright (c) 2013-2017 Hitachi Vantara..  All rights reserved.

echo MetaDataDebug is to support you in finding unusual errors and start problems.
echo -

REM this will always output the the system console using the debug mode
set CONSOLE=1

echo This starts MetaData Editor with a console output with the following options:
echo -
echo Pause after the termination?
echo (helps in finding start problems and unusual crashes of the JVM)
choice /C NYC /N /M "Pause? (Y=Yes, N=No, C=Cancel)"
if errorlevel == 3 exit
if errorlevel == 2 set PAUSE=1

echo -
echo Set logging level to Debug? (default: Basic logging)
choice /C NYC /N /M "Debug? (Y=Yes, N=No, C=Cancel)"
if errorlevel == 3 exit
if errorlevel == 2 set DEBUG_OPTIONS=/level:Debug

echo -
echo Redirect console output to metaDataEditorDebug.txt in the actual metadata-editor directory?
choice /C NYC /N /M "Redirect to metaDataEditorDebug.txt? (Y=Yes, N=No, C=Cancel)"
if errorlevel == 3 exit
if errorlevel == 2 set REDIRECT=1
REM We need to disable the pause in this case otherwise the user does not see the pause message
if errorlevel == 2 set PAUSE=

echo -
echo Launching metadata-editor: "%~dp0metadata-editor.bat" %OPTIONS%
if not "%REDIRECT%"=="1" "%~dp0metadata-editor.bat" %SPTIONS%
if "%REDIRECT%"=="1" echo Console output gets redirected to "%~dp0metaDataEditorDebug.txt"
if "%REDIRECT%"=="1" "%~dp0metadata-editor.bat" %OPTIONS% >>"%~dp0metaDataEditorDebug.txt"
