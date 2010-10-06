@echo off

REM **************************************************
REM ** Make sure we use the correct J2SE version!   **
REM ** Uncomment the PATH line in case of trouble   **
REM **************************************************
cd /D %~dp0
REM set PATH=C:\j2sdk1.4.2_01\bin;.;%PATH%

set CLASSPATH=.

REM ******************
REM   Core Library
REM ******************
REM This will get the versioned pentaho-meta.jar
FOR %%F IN (lib\*.jar) DO call :addcp %%F
REM set CLASSPATH=%CLASSPATH%;lib\pentaho-meta.jar

REM **********************
REM   External Libraries
REM **********************

REM Loop the libext directory and add the classpath.
REM The following command would only add the last jar: FOR %%F IN (libext\*.jar) DO call set CLASSPATH=%CLASSPATH%;%%F
REM So the circumvention with a subroutine solves this ;-)

FOR %%F IN (libext\*.jar) DO call :addcp %%F
FOR %%F IN (libext\commons\*.jar) DO call :addcp %%F
FOR %%F IN (libext\JDBC\*.jar) DO call :addcp %%F
FOR %%F IN (libext\meta\*.jar) DO call :addcp %%F
FOR %%F IN (libext\misc\*.jar) DO call :addcp %%F
FOR %%F IN (libext\pentaho\*.jar) DO call :addcp %%F

goto extlibe

:addcp
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:extlibe

REM *****************
REM   SWT Libraries
REM *****************

set CLASSPATH=%CLASSPATH%;libswt\runtime.jar
set CLASSPATH=%CLASSPATH%;libswt\jface.jar
set CLASSPATH=%CLASSPATH%;libswt\common.jar
set CLASSPATH=%CLASSPATH%;libswt\commands.jar

REM **************************************************
REM   Platform Specific SWT       **
REM **************************************************

set PENTAHO_JAVA=javaw
call "%~dp0set-pentaho-env.bat"

echo "%_PENTAHO_JAVA%"

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
:CONTINUE
popd

set CLASSPATH=%CLASSPATH%;%LIBSPATH%\swt.jar

REM ******************************************************************
REM ** Set java runtime options                                     **
REM ** Change 128m to higher values in case you run out of memory.  **
REM ******************************************************************

set OPT=-Xmx256m -cp %CLASSPATH% -Djava.library.path=%LIBSPATH%

REM ***************
REM ** Run...    **
REM ***************

start "Pentaho Metadata Editor" "%_PENTAHO_JAVA%" %OPT% org.pentaho.pms.ui.MetaEditor %1 %2 %3 %4 %5 %6 %7 %8 %9

REM *****************************************************************************
REM If you are having trouble launching the application, comment out the 
REM "start..."  line above, and uncomment the next 2 lines below . 
REM This will allow you to see any exceptions that may be preventing the 
REM application from starting successfully ...

REM "%_PENTAHO_JAVA%" %OPT% org.pentaho.pms.ui.MetaEditor %1 %2 %3 %4 %5 %6 %7 %8 %9
REM pause
REM ******************************************************************************
