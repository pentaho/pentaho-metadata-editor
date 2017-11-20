@REM This program is free software; you can redistribute it and/or modify it under the
@REM terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
@REM Foundation.
@REM
@REM You should have received a copy of the GNU Lesser General Public License along with this
@REM program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
@REM or from the Free Software Foundation, Inc.,
@REM 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
@REM
@REM This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
@REM without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
@REM See the GNU Lesser General Public License for more details.
@REM
@REM Copyright (c) 2009-2017 Hitachi Vantara.  All rights reserved.

REM ${project.name}
REM ${project.version}.${build.number}
REM Copyright © ${project.inceptionYear} ${project.organization.name}
REM Classpath is built by launcher. See ..\launcher\launcher.properties.

@echo off
setlocal 

cd /D %~dp0

REM Special console/debug options when called from meta-editor.bat or meta-editorebug.bat
if "%CONSOLE%"=="1" set PENTAHO_JAVA=java
if not "%CONSOLE%"=="1" set PENTAHO_JAVA=javaw
set IS64BITJAVA=0

call "%~dp0set-pentaho-env.bat"

REM **************************************************
REM   Platform Specific SWT       **
REM **************************************************

REM The following line is predicated on the 64-bit Sun
REM java output from -version which
REM looks like this (at the time of this writing):
REM
REM java version "1.6.0_17"
REM Java(TM) SE Runtime Environment (build 1.6.0_17-b04)
REM Java HotSpot(TM) 64-Bit Server VM (build 14.3-b01, mixed mode)
REM
REM Below is a logic to find the directory where java can found. We will
REM temporarily change the directory to that folder where we can run java there
pushd "%_PENTAHO_JAVA_HOME%"
if exist java.exe goto USEJAVAFROMPENTAHOJAVAHOME
cd bin
if exist java.exe goto USEJAVAFROMPENTAHOJAVAHOME
popd
pushd "%_PENTAHO_JAVA_HOME%\jre\bin"
if exist java.exe goto USEJAVAFROMPATH
goto USEJAVAFROMPATH
:USEJAVAFROMPENTAHOJAVAHOME
FOR /F %%a IN ('.\java.exe -version 2^>^&1^|%windir%\system32\find /C "64-Bit"') DO (SET /a IS64BITJAVA=%%a)
GOTO CHECK32VS64BITJAVA
:USEJAVAFROMPATH
FOR /F %%a IN ('java -version 2^>^&1^|find /C "64-Bit"') DO (SET /a IS64BITJAVA=%%a)
GOTO CHECK32VS64BITJAVA
:CHECK32VS64BITJAVA


IF %IS64BITJAVA% == 1 GOTO :USE64

:USE32
REM ===========================================
REM Using 32bit Java, so include 32bit SWT Jar
REM ===========================================
set LIBSPATH=libswt\win32
GOTO :CONTINUE
:USE64
REM ===========================================
REM Using 64bit java, so include 64bit SWT Jar
REM ===========================================
set LIBSPATH=libswt\win64
set SWTJAR=..\libswt\win64
:CONTINUE
popd

REM **********************
REM   Collect arguments
REM **********************

set _cmdline=
:TopArg
if %1!==! goto EndArg
set _cmdline=%_cmdline% %1
shift
goto TopArg
:EndArg

REM ******************************************************************
REM ** Set java runtime options                                     **
REM ** Change 2048m to higher values in case you run out of memory  **
REM ** or set the PENTAHO_JAVA_OPTIONS environment variable         **
REM ******************************************************************

if "%PENTAHO_JAVA_OPTIONS%"=="" set PENTAHO_JAVA_OPTIONS="-Xms1024m" "-Xmx2048m"

set OPT=%PENTAHO_JAVA_OPTIONS% "-Djava.library.path=%LIBSPATH%" 
rem **** USE THIS LINE IF REMOTE DEBUGGING (port 5105) IS REQUIRED***
REM set OPT=%OPT% -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5105

REM ***************
REM ** Run...    **
REM ***************

REM Eventually call java instead of javaw and do not run in a separate window
if not "CONSOLE%"=="1" set START_OPTION=start "Pentaho Metadata Editor"

@echo on
%START_OPTION% "%_PENTAHO_JAVA%" %OPT% -jar launcher\pentaho-application-launcher.jar -lib ..\%LIBSPATH% %_cmdline%
@echo off
if "%PAUSE%"=="1" pause
