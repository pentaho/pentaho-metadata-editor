rem ---------------------------------------------------------------------------
rem Find a suitable Java
rem ---------------------------------------------------------------------------

if not "%PENTAHO_JAVA%" == "" goto gotPentahoJava
set __LAUNCHER=java.exe
goto checkPentahoJavaHome

:gotPentahoJava
set __LAUNCHER=%PENTAHO_JAVA%
goto checkPentahoJavaHome

:checkPentahoJavaHome
if not "%PENTAHO_JAVA_HOME%" == "" goto gotPentahoJavaHome
if not "%JAVA_HOME%" == "" goto gotJdkHome
if not "%JRE_HOME%" == "" goto gotJreHome
goto tryValueFromCaller 

:gotPentahoJavaHome
echo DEBUG: Using PENTAHO_JAVA_HOME
set _PENTAHO_JAVA_HOME=%PENTAHO_JAVA_HOME%
set _PENTAHO_JAVA=%_PENTAHO_JAVA_HOME%\bin\%__LAUNCHER%
goto end

:gotJdkHome
echo DEBUG: Using JAVA_HOME
set _PENTAHO_JAVA_HOME=%JAVA_HOME%
set _PENTAHO_JAVA=%_PENTAHO_JAVA_HOME%\bin\%__LAUNCHER%
goto end

:gotJreHome
echo DEBUG: Using JRE_HOME
set _PENTAHO_JAVA_HOME=%JRE_HOME%
set _PENTAHO_JAVA=%_PENTAHO_JAVA_HOME%\bin\%__LAUNCHER%
goto end

:tryValueFromCaller
if not !%1!==!! goto gotValueFromCaller
goto :gotPath

:gotValueFromCaller
echo DEBUG: Using value (%~1) from calling script
set _PENTAHO_JAVA_HOME=%~1
set _PENTAHO_JAVA=%_PENTAHO_JAVA_HOME%\bin\%__LAUNCHER%
goto end

:gotPath
echo WARNING: Using java from path
set _PENTAHO_JAVA_HOME=
set _PENTAHO_JAVA=%__LAUNCHER%

goto end

:end

echo DEBUG: _PENTAHO_JAVA_HOME=%_PENTAHO_JAVA_HOME%
echo DEBUG: _PENTAHO_JAVA=%_PENTAHO_JAVA%